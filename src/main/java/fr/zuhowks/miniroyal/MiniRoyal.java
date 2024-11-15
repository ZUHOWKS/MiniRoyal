package fr.zuhowks.miniroyal;

import fr.zuhowks.miniroyal.commands.CommandAdmin;
import fr.zuhowks.miniroyal.commands.CommandPlayer;
import fr.zuhowks.miniroyal.game.GameManager;
import fr.zuhowks.miniroyal.game.GameStatus;
import fr.zuhowks.miniroyal.listener.PlayerListener;
import fr.zuhowks.miniroyal.lobby.Lobby;
import fr.zuhowks.miniroyal.map.MiniRoyalMap;
import fr.zuhowks.miniroyal.map.Zone;
import fr.zuhowks.miniroyal.map.items.Item;
import fr.zuhowks.miniroyal.map.items.ItemPotion;
import fr.zuhowks.miniroyal.map.items.ItemRarity;
import fr.zuhowks.miniroyal.map.items.ItemRegistry;
import fr.zuhowks.miniroyal.utils.BukkitUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class MiniRoyal extends JavaPlugin {


    public static final String prefixMessage = ChatColor.YELLOW +  "[" + ChatColor.AQUA + " MINI-ROYAL " + ChatColor.YELLOW + "]" + ChatColor.RESET + " ";
    private static MiniRoyal INSTANCE;
    private FileConfiguration config;
    private final Map<UUID, ItemStack[]> inventoryRegistry = new HashMap<>(); // For admin setup mod
    private boolean partyIsSetup = false;
    private Lobby lobby;
    private GameManager gameManager;
    private MiniRoyalMap miniRoyalMap;
    private Zone zone;
    private boolean isInGame = false;
    private ItemRegistry itemRegistry;


    @Override
    public void onEnable() {

        INSTANCE = this;

        this.config = this.getConfig();
        saveDefaultConfig();

        // Set party config
        this.partyIsSetup = config.getBoolean("party.isSetup");

        // Set lobby config
        this.lobby = new Lobby((Location) config.get("lobby.pos1"), (Location) config.get("lobby.pos2"), (Location) config.get("lobby.spawn"));

        // Set game config
        this.gameManager = new GameManager(10);

        // Set map config
        this.miniRoyalMap = new MiniRoyalMap((Location) config.get("map.pos1"), (Location) config.get("map.pos2"), (Location) config.get("map.finish-zone-center"), config.getInt("map.finish-zone-radius"));
        this.miniRoyalMap.getChestRegistry().setChestLocations((List<Location>) config.getList("map.chests"));

        // Set zone config
        this.zone = new Zone(this.getMiniRoyalMap());

        // Setup commands
        setupCommands();

        // Add player listener events
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // Set zone simulation
        this.setZoneSimulation();

        // Register items
        this.registerItems();

        // Set game manager scheduler
        this.setGameManagerSchedulers();



    }

    private void setupCommands() {
        CommandAdmin commandAdmin = new CommandAdmin();
        this.getCommand("amr").setExecutor(commandAdmin);
        this.getCommand("amr").setTabCompleter(commandAdmin);

        CommandPlayer commandPlayer = new CommandPlayer();
        this.getCommand("miniroyal").setExecutor(commandPlayer);
        this.getCommand("miniroyal").setTabCompleter(commandPlayer);
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

    public void setGameManagerSchedulers() {


        int scoreboardScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniRoyal.getPlugin(MiniRoyal.class), () -> {
            GameStatus gameStatus = this.gameManager.getGameStatus();
            if (this.gameManager.canLaunchGameCounter()) {
                this.gameManager.updateLobbyScoreboard();
                if (this.gameManager.getCounter() == 0) {
                    this.gameManager.startGame();
                    this.gameManager.launchPlayer();
                }
            } else if (gameStatus == GameStatus.WAITING_ROOM) {
                this.gameManager.updateLobbyScoreboard();
            } else if (gameStatus == GameStatus.IN_GAME) {


                if (this.gameManager.getUuidList().size() < 1) {
                    this.gameManager.finishGame();
                    this.gameManager.updateInGameScoreboard();
                    this.gameManager.resetCounter(30);
                    Player p = (this.gameManager.getUuidList().size() == 1) ? Bukkit.getPlayer(this.gameManager.getUuidList().get(0)) : null;

                    if (p != null) {
                        this.gameManager.sendMessage(prefixMessage + ChatColor.YELLOW + "The Royal Top 1 is " + ChatColor.GOLD + ChatColor.BOLD  + p.getName() + ChatColor.YELLOW + ". Congratulation!", Sound.BLOCK_METAL_BREAK, 2.5f);
                        p.setInvulnerable(true);
                    }

                } else {
                    this.gameManager.updateInGameScoreboard();
                }
            }
        }, 0L, 5L);

        int counterScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniRoyal.getPlugin(MiniRoyal.class), () -> {
            GameStatus gameStatus = this.gameManager.getGameStatus();
            int counter = this.gameManager.getCounter();
            if (this.gameManager.canLaunchGameCounter()) {

                if (gameStatus == GameStatus.WAITING_ROOM_FULL && counter > 10) {
                    this.gameManager.resetCounter(10);
                } else {
                    this.gameManager.deleteSecond(1);
                }

                counter = this.gameManager.getCounter();

                if ((counter <= 5 &&  counter > 0) || counter == 10) {
                    this.gameManager.sendMessage(prefixMessage + ChatColor.YELLOW + "The party begin in " + ChatColor.AQUA + (counter) + " second" + ChatColor.YELLOW + ".", 2 - counter*0.15f);
                }


            } else if (gameStatus == GameStatus.WAITING_ROOM) {
                this.gameManager.resetCounter();

            } else if (gameStatus == GameStatus.IN_GAME) {
                this.gameManager.deleteSecond(1);

                if (counter < 0) {
                    this.zone.subRadius(1);
                }

            } else if (gameStatus == GameStatus.FINISH) {
                if (counter == 30) {
                    this.gameManager.sendMessage(prefixMessage + ChatColor.YELLOW + "The server party restart in " + ChatColor.AQUA + counter + " second" + ChatColor.YELLOW + ".", Sound.BLOCK_GRASS_BREAK ,0.1f);
                } else if (counter == 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.kickPlayer(ChatColor.YELLOW + "The server party restarting... Please wait a few second.");
                    }
                    Bukkit.getServer().shutdown();
                }

                if (counter > 0) {
                    if (counter > 15) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            Location fireworkLoc = p.getLocation();
                            fireworkLoc.add(0, 5, 0);
                            Firework fw = (Firework) p.getLocation().getWorld().spawnEntity(fireworkLoc, EntityType.FIREWORK);
                            FireworkMeta fwm = fw.getFireworkMeta();

                            fwm.setPower(3);
                            fwm.addEffect(FireworkEffect.builder().withColor(Color.fromBGR((int) (Math.random() * (255)), (int) (Math.random() * (255)), (int) (Math.random() * (255)))).flicker(true).build());

                            fw.setFireworkMeta(fwm);
                            fw.detonate();
                        }

                    }

                    this.gameManager.deleteSecond(1);
                }

            }

        }, 0L, 20L);
    }

    public void setZoneSimulation() {
        final Particle particle = Particle.PORTAL;
        AtomicInteger particleNumber = new AtomicInteger(7);
        //Zone simulation
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniRoyal.getPlugin(MiniRoyal.class), () -> {
            if (this.gameManager.getGameStatus() == GameStatus.IN_GAME) {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                final Location center = this.miniRoyalMap.getFinishZoneCenter();

                final double radius = this.zone.getRadius();
                final double radiusCoef = Math.min(50 / radius, 2);

                List<Vector> vectors = new ArrayList<>();

                for (Player p : players) {
                    final Location playerLoc = p.getLocation();
                    final double distRadius = radius - BukkitUtils.getVectorNormInPlanXZ(playerLoc, center);


                    if (0 < distRadius && distRadius < 25 && 0.1 < radius) {
                        Vector playerVec = BukkitUtils.getVector(center, playerLoc);
                        Vector baseVec = new Vector(radius, 0, 0);

                        double angle = BukkitUtils.getAngleInPlanXZ(baseVec, playerVec);
                        final double signedRadius = (playerLoc.getBlockZ() < center.getZ() ? -1 : 1) * radius;

                        particleNumber.addAndGet(-(int) (((distRadius + 1) / 2) - 5));

                        for (int i = -30; i <= 30; i += 5) {
                            for (int j = -3; j < 5; j += 1) {
                                final double x = Math.round((float) (radius * Math.cos(angle + radiusCoef * i * Math.PI / 180) + center.getX()));
                                final double y = playerLoc.getY() + j;
                                final double z = Math.round((float) (signedRadius * Math.sin(angle + radiusCoef * i * Math.PI / 180) + center.getZ()));

                                Vector particleVec = new Vector(x, y, z);
                                if (!vectors.contains(particleVec)) {
                                    vectors.add(particleVec);
                                    playerLoc.getWorld().spawnParticle(particle, x, y, z, (int) (radius / this.zone.getMaxRadius() * particleNumber.get()), 1.2, 10, 1.2);
                                }

                            }
                        }
                    } else if (distRadius < 0 || radius <= 0.1) {
                        for (int i = 0; i <= 360; i += 45) {
                            for (int j = 0; j < 4; j += 1) {
                                final double x = 4 * Math.cos(i * Math.PI / 180) + playerLoc.getX();
                                final double y = playerLoc.getY() + j;
                                final double z = 4 * Math.sin(i * Math.PI / 180) + playerLoc.getZ();

                                playerLoc.getWorld().spawnParticle(particle, x, y, z, (int) (radius / this.zone.getMaxRadius() * particleNumber.get()), 2, 3, 2);
                                p.damage(Math.max(this.zone.getMaxRadius() / (Math.max(radius, 0.1) * 1.25) , 1) * 0.155);

                            }
                        }
                    }
                }
            }
        }, 0L, 10L);
    }

    private void registerItems() {
        this.itemRegistry = this.getMiniRoyalMap().getItemRegistry();

        /*
         * * * * * * * * * *
         * LEGENDARY ITEMS *
         * * * * * * * * * *
         */

        Map<Enchantment, Integer> stickPoweredEnchantments = new HashMap<>();
        stickPoweredEnchantments.put(Enchantment.DAMAGE_ALL, 3);
        stickPoweredEnchantments.put(Enchantment.KNOCKBACK, 2);
        stickPoweredEnchantments.put(Enchantment.DURABILITY, 4);
        this.itemRegistry.addItem(new Item(Material.STICK, ItemRarity.LEGENDARY, 1, "Stick Powered", stickPoweredEnchantments));

        Map<Enchantment, Integer> diamondSwordEnchantments = new HashMap<>();
        diamondSwordEnchantments.put(Enchantment.FIRE_ASPECT, 1);
        diamondSwordEnchantments.put(Enchantment.KNOCKBACK, 1);
        diamondSwordEnchantments.put(Enchantment.DURABILITY, 3);
        this.itemRegistry.addItem(new Item(Material.DIAMOND_SWORD, ItemRarity.LEGENDARY, 2, "Diamond Sword", diamondSwordEnchantments));


        Map<Enchantment, Integer> diamondLegendaryProtectionEnchantments = new HashMap<>();
        diamondLegendaryProtectionEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        diamondLegendaryProtectionEnchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
        diamondLegendaryProtectionEnchantments.put(Enchantment.PROTECTION_FIRE, 1);

        this.itemRegistry.addItem(new Item(Material.DIAMOND_HELMET, ItemRarity.LEGENDARY, 2, "Diamond Helmet", diamondLegendaryProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.DIAMOND_CHESTPLATE, ItemRarity.LEGENDARY, 1, "Diamond Chestplate", diamondLegendaryProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.DIAMOND_LEGGINGS, ItemRarity.LEGENDARY, 1, "Diamond Leggings", diamondLegendaryProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.DIAMOND_BOOTS, ItemRarity.LEGENDARY, 2, "Diamond Boots", diamondLegendaryProtectionEnchantments));



        /*
         * * * * * * * *
         * EPIC  ITEMS *
         * * * * * * * *
         */

        Map<Enchantment, Integer> ironEpicSwordEnchantments = new HashMap<>();
        ironEpicSwordEnchantments.put(Enchantment.DAMAGE_ALL, 2);
        ironEpicSwordEnchantments.put(Enchantment.KNOCKBACK, 2);
        ironEpicSwordEnchantments.put(Enchantment.DURABILITY, 2);
        this.itemRegistry.addItem(new Item(Material.IRON_SWORD, ItemRarity.EPIC, 3, "Iron Sword", ironEpicSwordEnchantments));

        Map<Enchantment, Integer> diamondEpicAxeEnchantments = new HashMap<>();
        diamondEpicAxeEnchantments.put(Enchantment.DAMAGE_ALL, 1);
        diamondEpicAxeEnchantments.put(Enchantment.KNOCKBACK, 1);
        diamondEpicAxeEnchantments.put(Enchantment.DURABILITY, 2);
        this.itemRegistry.addItem(new Item(Material.DIAMOND_AXE, ItemRarity.EPIC, 2, "Diamond Axe", diamondEpicAxeEnchantments));


        Map<Enchantment, Integer> ironEpicProtectionEnchantments = new HashMap<>();
        ironEpicProtectionEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ironEpicProtectionEnchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
        ironEpicProtectionEnchantments.put(Enchantment.PROTECTION_FIRE, 2);
        this.itemRegistry.addItem(new Item(Material.IRON_HELMET, ItemRarity.EPIC, 2, "Iron Helmet", ironEpicProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.IRON_CHESTPLATE, ItemRarity.EPIC, 1, "Iron Chestplate", ironEpicProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.IRON_LEGGINGS, ItemRarity.EPIC, 2, "Iron Leggings", ironEpicProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.IRON_BOOTS, ItemRarity.EPIC, 1, "Iron Boots", ironEpicProtectionEnchantments));

        this.itemRegistry.addItem(new Item(Material.DIAMOND_HELMET, ItemRarity.EPIC, 1, "Diamond Helmet"));
        this.itemRegistry.addItem(new Item(Material.DIAMOND_LEGGINGS, ItemRarity.EPIC, 1, "Diamond Chestplate"));


        this.itemRegistry.addItem(new ItemPotion(Material.SPLASH_POTION, ItemRarity.EPIC, 1, "Poison II Splash", PotionType.POISON, false, true));



        /*
         * * * * * * * *
         * RARE  ITEMS *
         * * * * * * * *
         */

        Map<Enchantment, Integer> ironSwordEnchantments = new HashMap<>();
        ironSwordEnchantments.put(Enchantment.DAMAGE_ALL, 2);
        ironSwordEnchantments.put(Enchantment.DURABILITY, 3);
        this.itemRegistry.addItem(new Item(Material.IRON_SWORD, ItemRarity.RARE, 3, "Iron Sword", ironSwordEnchantments));
        this.itemRegistry.addItem(new Item(Material.DIAMOND_AXE, ItemRarity.RARE, 2, "Diamond Axe"));


        Map<Enchantment, Integer> ironRareChestplateEnchantments = new HashMap<>();
        ironRareChestplateEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ironRareChestplateEnchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
        ironRareChestplateEnchantments.put(Enchantment.PROTECTION_FIRE, 1);
        this.itemRegistry.addItem(new Item(Material.IRON_HELMET, ItemRarity.RARE, 1, "Iron Helmet", ironRareChestplateEnchantments));
        this.itemRegistry.addItem(new Item(Material.IRON_CHESTPLATE, ItemRarity.RARE, 1, "Iron Chestplate", ironRareChestplateEnchantments));
        this.itemRegistry.addItem(new Item(Material.IRON_LEGGINGS, ItemRarity.RARE, 1, "Iron Leggings", ironRareChestplateEnchantments));
        this.itemRegistry.addItem(new Item(Material.IRON_BOOTS, ItemRarity.RARE, 1, "Iron Boots", ironRareChestplateEnchantments));


        this.itemRegistry.addItem(new ItemPotion(Material.POTION, ItemRarity.RARE, 1, "Regen II", PotionType.REGEN, false, true));
        this.itemRegistry.addItem(new ItemPotion(Material.SPLASH_POTION, ItemRarity.RARE, 1, "Poison Splash", PotionType.POISON, false, false));



        /*
         * * * * * * * * *
         * COMMON  ITEMS *
         * * * * * * * * *
         */

        this.itemRegistry.addItem(new Item(Material.IRON_SWORD, ItemRarity.COMMON, 3, "Iron Sword"));
        this.itemRegistry.addItem(new Item(Material.GOLD_AXE, ItemRarity.COMMON, 2, "Diamond Pickaxe"));


        Map<Enchantment, Integer> chainmailCommonProtectionEnchantments = new HashMap<>();
        chainmailCommonProtectionEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        chainmailCommonProtectionEnchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
        chainmailCommonProtectionEnchantments.put(Enchantment.PROTECTION_FIRE, 1);
        chainmailCommonProtectionEnchantments.put(Enchantment.THORNS, 1);

        this.itemRegistry.addItem(new Item(Material.CHAINMAIL_HELMET, ItemRarity.COMMON, 1, "Chainmail Helmet", chainmailCommonProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.CHAINMAIL_CHESTPLATE, ItemRarity.COMMON, 1, "Chainmail Chestplate", chainmailCommonProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.CHAINMAIL_LEGGINGS, ItemRarity.COMMON, 1, "Chainmail Leggings", chainmailCommonProtectionEnchantments));
        this.itemRegistry.addItem(new Item(Material.CHAINMAIL_BOOTS, ItemRarity.COMMON, 1, "Chainmail Boots", chainmailCommonProtectionEnchantments));


        this.itemRegistry.addItem(new ItemPotion(Material.SPLASH_POTION, ItemRarity.COMMON, 1, "Speed I Splash", PotionType.SPEED, false, false));
        this.itemRegistry.addItem(new ItemPotion(Material.POTION, ItemRarity.COMMON, 1, "Speed II", PotionType.SPEED, false, true));
        this.itemRegistry.addItem(new ItemPotion(Material.SPLASH_POTION, ItemRarity.COMMON, 1, "Instant Heal I Splash", PotionType.INSTANT_HEAL, false, false));
        this.itemRegistry.addItem(new ItemPotion(Material.POTION, ItemRarity.COMMON, 1, "Instant Heal II", PotionType.INSTANT_HEAL, false, true));
        this.itemRegistry.addItem(new ItemPotion(Material.POTION, ItemRarity.COMMON, 1, "Regen I", PotionType.REGEN, false, false));

    }

    public void forceDisableSetupModForAll() {
        Map<UUID, ItemStack[]> inventoryRegistry = this.getInventoryRegistry();
        for (UUID uuid : inventoryRegistry.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            p.getInventory().setContents(inventoryRegistry.get(uuid));
            p.updateInventory();
            inventoryRegistry.remove(uuid);
        }
        this.miniRoyalMap.getChestRegistry().removeBlockChest();
    }


    public GameManager getGameManager() {
        return gameManager;
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

    public boolean isInGame() {
        return this.isInGame;
    }

    public void setInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }
}
