package fr.zuhowks.miniroyal;

import fr.zuhowks.miniroyal.commands.CommandAdmin;
import fr.zuhowks.miniroyal.listener.PlayerListener;
import fr.zuhowks.miniroyal.lobby.Lobby;
import fr.zuhowks.miniroyal.map.MiniRoyalMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MiniRoyal extends JavaPlugin {


    public static final String prefixMessage = ChatColor.YELLOW + "" + ChatColor.BOLD +  "[" + ChatColor.AQUA + " MINI-ROYAL " + ChatColor.YELLOW + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";
    private static MiniRoyal INSTANCE;
    private FileConfiguration config;
    private final Map<UUID, ItemStack[]> inventoryRegistry = new HashMap<>(); //For setup mod
    private boolean partyIsSetup;
    private Lobby lobby;
    private MiniRoyalMap miniRoyalMap;
    private boolean isInGame;


    @Override
    public void onEnable() {

        INSTANCE = this;
        this.config = this.getConfig();
        saveDefaultConfig();

        this.partyIsSetup = config.getBoolean("party.isSetup");
        this.lobby = new Lobby((Location) config.get("lobby.pos1"), (Location) config.get("lobby.pos2"), (Location) config.get("lobby.spawn"));
        this.miniRoyalMap = new MiniRoyalMap((Location) config.get("map.pos1"), (Location) config.get("map.pos2"), (Location) config.get("map.finish-zone-center"), config.getInt("map.finish-zone-radius"));

        this.getCommand("amr").setExecutor(new CommandAdmin());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

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
}
