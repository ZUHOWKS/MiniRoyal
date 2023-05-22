package fr.zuhowks.miniroyal;

import fr.zuhowks.miniroyal.commands.CommandAdmin;
import fr.zuhowks.miniroyal.listener.PlayerListener;
import fr.zuhowks.miniroyal.lobby.Lobby;
import fr.zuhowks.miniroyal.map.MiniRoyalMap;
import fr.zuhowks.miniroyal.map.Zone;
import fr.zuhowks.miniroyal.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

public final class MiniRoyal extends JavaPlugin {


    public static final String prefixMessage = ChatColor.YELLOW + "" + ChatColor.BOLD +  "[" + ChatColor.AQUA + " MINI-ROYAL " + ChatColor.YELLOW + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";
    private static MiniRoyal INSTANCE;
    private FileConfiguration config;
    private final Map<UUID, ItemStack[]> inventoryRegistry = new HashMap<>(); //For setup mod
    private boolean partyIsSetup = false;
    private Lobby lobby;
    private MiniRoyalMap miniRoyalMap;
    private Zone zone;
    private boolean isInGame = false;


    @Override
    public void onEnable() {

        INSTANCE = this;
        this.config = this.getConfig();
        saveDefaultConfig();

        this.partyIsSetup = config.getBoolean("party.isSetup");
        this.lobby = new Lobby((Location) config.get("lobby.pos1"), (Location) config.get("lobby.pos2"), (Location) config.get("lobby.spawn"));
        this.miniRoyalMap = new MiniRoyalMap((Location) config.get("map.pos1"), (Location) config.get("map.pos2"), (Location) config.get("map.finish-zone-center"), config.getInt("map.finish-zone-radius"));
        this.miniRoyalMap.getChestRegistry().setChestLocations((List<Location>) config.getList("map.chests"));
        this.zone = new Zone(this.getMiniRoyalMap());

        this.getCommand("amr").setExecutor(new CommandAdmin());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        final Particle particle = Particle.PORTAL;

        //Zone simulation
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniRoyal.getPlugin(MiniRoyal.class), () ->{
            if (isInGame) {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                final Location center = this.miniRoyalMap.getFinishZoneCenter();

                final double radius = this.zone.getRadius();
                final double radiusCoef = Math.min(50/radius, 2);
                int particleNumber = 7;

                List<Vector> vectors = new ArrayList<>();

                for (Player p : players) {
                    final Location playerLoc = p.getLocation();
                    final double distRadius = radius - BukkitUtils.getVectorNormInPlanXZ(playerLoc, center);


                    if (0 < distRadius && distRadius < 25 && 0.1 < radius ) {
                        Vector playerVec = BukkitUtils.getVector(center, playerLoc);
                        Vector baseVec = new Vector(radius, 0, 0);

                        double angle = BukkitUtils.getAngleInPlanXZ(baseVec, playerVec);
                        final double signedRadius = (playerLoc.getBlockZ() < center.getZ() ? -1 : 1)*radius;

                        particleNumber -= (((distRadius+1)/2)-5);

                        for (int i=-30; i<=30; i+=5) {
                            for (int j=-3; j<5; j+=1) {
                                final double x = Math.round((float) (radius*Math.cos(angle + radiusCoef*i*Math.PI/180) + center.getX()));
                                final double y = playerLoc.getY() + j;
                                final double z = Math.round((float) (signedRadius*Math.sin(angle + radiusCoef*i*Math.PI/180) + center.getZ()));

                                Vector particleVec = new Vector(x, y, z);
                                if (!vectors.contains(particleVec)) {
                                    vectors.add(particleVec);
                                    playerLoc.getWorld().spawnParticle(particle, x, y, z, (int) (radius/this.zone.getMaxRadius() * particleNumber), 1.2, 10, 1.2);
                                }

                            }
                        }
                    } else if (distRadius < 0 || radius <= 0.1) {
                        for (int i=0; i<=360; i+=45) {
                            for (int j=0; j<4; j+=1) {
                                final double x = 3*Math.cos(i*Math.PI/180) + playerLoc.getX();
                                final double y = playerLoc.getY() + j;
                                final double z = 3*Math.sin(i*Math.PI/180) + playerLoc.getZ();

                                playerLoc.getWorld().spawnParticle(particle, x, y, z, (int) (radius/this.zone.getMaxRadius() * particleNumber), 2, 3, 2);
                                p.damage(Math.max(this.zone.getMaxRadius()/Math.max(radius, 0.1), 5)*1);

                            }
                        }
                    }
                }
            }
        }, 0L, 10L);

    }

    @Override
    public void onDisable() {
        forceDisableSetupModForAll();
    }

    public static MiniRoyal getINSTANCE() {
        return INSTANCE;
    }

    public FileConfiguration getFileConfiguration() {
        return config;
    }

    public Map<UUID, ItemStack[]> getInventoryRegistry() {
        return inventoryRegistry;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public boolean isInSetupMod(Player p) {
        Map<UUID, ItemStack[]> inventoryRegistry = this.getInventoryRegistry();
        for (UUID uuid : inventoryRegistry.keySet()) {
            if (uuid.equals(p.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void forceDisableSetupModForAll() {
        Map<UUID, ItemStack[]> inventoryRegistry = this.getInventoryRegistry();
        for (UUID uuid : inventoryRegistry.keySet()) {
            Bukkit.getPlayer(uuid).chat("/amr setupmod");
        }
    }

    public boolean partyIsSetup() {
        return partyIsSetup;
    }

    public void setPartyIsSetup(boolean bool) {
        partyIsSetup = bool;
    }

    public MiniRoyalMap getMiniRoyalMap() {
        return miniRoyalMap;
    }

    public Zone getZone() {
        return zone;
    }
    public void updateZone() {
        this.zone = new Zone(this.miniRoyalMap);
    }
}
