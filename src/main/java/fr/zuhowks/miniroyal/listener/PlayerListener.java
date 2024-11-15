package fr.zuhowks.miniroyal.listener;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.game.GameManager;
import fr.zuhowks.miniroyal.game.GameStatus;
import fr.zuhowks.miniroyal.map.items.Item;
import fr.zuhowks.miniroyal.utils.SetupModItems;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

import static fr.zuhowks.miniroyal.MiniRoyal.prefixMessage;

public class PlayerListener implements Listener {

    private static final MiniRoyal INSTANCE = MiniRoyal.getINSTANCE();
    private static final Map<UUID, Integer> planners = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractEvent event) {

        Player p = event.getPlayer();
        event.setCancelled(INSTANCE.isInSetupMod(p));
        ItemStack itemStack = event.getItem();

        GameStatus gameStatus = INSTANCE.getGameManager().getGameStatus();

        if (event.isCancelled()) {
            if (itemStack != null) {
                SetupModItems setupModItem = SetupModItems.isInSetupModItems(itemStack);
                if (setupModItem != null) {
                    if (setupModItem.getCommand() != null && !setupModItem.getCommand().isEmpty()) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            p.chat("/" + setupModItem.getCommand());

                        }
                    } else {
                        Block block = event.getClickedBlock();
                        if (setupModItem == SetupModItems.SET_LOBBY_POS) {

                            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                INSTANCE.getLobby().setPos1(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 1 as been set !");

                            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                INSTANCE.getLobby().setPos2(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 2 as been set !");

                            }

                        } else if (setupModItem == SetupModItems.SET_MAP_POS) {
                            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                INSTANCE.getMiniRoyalMap().setPos1(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 1 as been set !");

                            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                INSTANCE.getMiniRoyalMap().setPos2(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 2 as been set !");

                            }
                        } else if (setupModItem == SetupModItems.SET_MAP_CHEST) {
                            event.setCancelled(event.getClickedBlock().getType() == Material.CHEST && event.getAction() == Action.RIGHT_CLICK_BLOCK);
                        }
                    }
                }
            }
        }

        if (itemStack != null) {
            if (gameStatus == GameStatus.IN_GAME) {
                if (itemStack.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Planner") && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() ==  Action.RIGHT_CLICK_AIR)) {
                    if (p.getVehicle() == null) {
                        int planner = spawnPlanner(p, itemStack);
                        this.planners.put(p.getUniqueId(), planner);
                    } else if (getDistance(p) <= 3) {
                        p.getInventory().remove(itemStack);

                    }
                }
            } else if (gameStatus == GameStatus.WAITING_ROOM || gameStatus == GameStatus.WAITING_ROOM_FULL) {
                if (itemStack.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Cosmetic") && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() ==  Action.RIGHT_CLICK_AIR)) {
                    p.openInventory(getCosmeticInventory());
                }
            }
        }
    }

    private static Inventory getCosmeticInventory() {
        final Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.YELLOW + "Cosmetic");

        ItemStack plannersItem = new ItemStack(Material.FEATHER);
        ItemMeta plannersItemMeta = plannersItem.getItemMeta();
        plannersItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Planners");
        plannersItem.setItemMeta(plannersItemMeta);

        inventory.setItem(13, plannersItem);
        return inventory;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event != null) {
            if (event.getInventory() != null) {
                if (event.getInventory().getName().equals(ChatColor.YELLOW + "Cosmetic")) {
                    if (event.getCurrentItem() != null) {
                        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
                        if (itemName.equals(ChatColor.LIGHT_PURPLE + "Planners")) {
                            event.getWhoClicked().openInventory(getPlannerComesticInventory(event));

                        } else if (itemName.equals(ChatColor.WHITE + "Chicken Flying")) {
                            INSTANCE.getGameManager().setPlayerPlanner(event.getWhoClicked().getUniqueId(), EntityType.CHICKEN);
                            event.getWhoClicked().openInventory(getPlannerComesticInventory(event));

                        } else if (itemName.equals(ChatColor.YELLOW + "Egg Throwing")) {
                            INSTANCE.getGameManager().setPlayerPlanner(event.getWhoClicked().getUniqueId(), EntityType.EGG);
                            event.getWhoClicked().openInventory(getPlannerComesticInventory(event));

                        } else if (itemName.equals(ChatColor.DARK_PURPLE + "Ender Pearl Mastery")) {
                            INSTANCE.getGameManager().setPlayerPlanner(event.getWhoClicked().getUniqueId(), EntityType.ENDER_PEARL);
                            event.getWhoClicked().openInventory(getPlannerComesticInventory(event));

                        } else if (itemName.equals(ChatColor.LIGHT_PURPLE + "Wakey Wakey Pork...")) {
                            INSTANCE.getGameManager().setPlayerPlanner(event.getWhoClicked().getUniqueId(), EntityType.PIG);
                            event.getWhoClicked().openInventory(getPlannerComesticInventory(event));

                        } else if (itemName.equals(ChatColor.RED + "Come On BEEF!")) {
                            INSTANCE.getGameManager().setPlayerPlanner(event.getWhoClicked().getUniqueId(), EntityType.COW);
                            event.getWhoClicked().openInventory(getPlannerComesticInventory(event));

                        } else if (itemName.equals(ChatColor.YELLOW + "Back")) {
                            event.getWhoClicked().openInventory(getCosmeticInventory());
                        }
                    }

                    event.setCancelled(true);
                }
            }
        }

    }

    private static Inventory getPlannerComesticInventory(InventoryClickEvent event) {
        final Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.YELLOW + "Cosmetic");


        ItemStack chickenPlanner = new ItemStack(Material.COOKED_CHICKEN);
        ItemMeta chickenPlannerMeta = chickenPlanner.getItemMeta();
        chickenPlannerMeta.setDisplayName(ChatColor.WHITE + "Chicken Flying");

        ItemStack eggPlanner = new ItemStack(Material.EGG);
        ItemMeta eggPlannerMeta = eggPlanner.getItemMeta();
        eggPlannerMeta.setDisplayName(ChatColor.YELLOW + "Egg Throwing");

        ItemStack enderPearlPlanner = new ItemStack(Material.ENDER_PEARL);
        ItemMeta enderPearlPlannerMeta = enderPearlPlanner.getItemMeta();
        enderPearlPlannerMeta.setDisplayName(ChatColor.DARK_PURPLE + "Ender Pearl Mastery");

        ItemStack pigPlanner = new ItemStack(Material.PORK);
        ItemMeta pigPlannerMeta = pigPlanner.getItemMeta();
        pigPlannerMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Wakey Wakey Pork...");

        ItemStack cowPlanner = new ItemStack(Material.COOKED_BEEF);
        ItemMeta cowPlannerMeta = pigPlanner.getItemMeta();
        cowPlannerMeta.setDisplayName(ChatColor.RED + "Come On BEEF!");

        EntityType entityType = INSTANCE.getGameManager().getPlayerPlanner(event.getWhoClicked().getUniqueId());

        if (entityType == EntityType.CHICKEN) {
            addGlow(chickenPlannerMeta);

        } else if (entityType == EntityType.EGG) {
            addGlow(eggPlannerMeta);

        } else if (entityType == EntityType.ENDER_PEARL) {
            addGlow(enderPearlPlannerMeta);

        } else if (entityType == EntityType.PIG) {
            addGlow(pigPlannerMeta);

        } else if (entityType == EntityType.COW) {
            addGlow(cowPlannerMeta);

        }

        ItemStack backItem = new ItemStack(Material.BARRIER);
        ItemMeta backItemMeta = backItem.getItemMeta();
        backItemMeta.setDisplayName(ChatColor.YELLOW + "Back");
        backItem.setItemMeta(backItemMeta);

        chickenPlanner.setItemMeta(chickenPlannerMeta);
        eggPlanner.setItemMeta(eggPlannerMeta);
        enderPearlPlanner.setItemMeta(enderPearlPlannerMeta);
        pigPlanner.setItemMeta(pigPlannerMeta);
        cowPlanner.setItemMeta(cowPlannerMeta);

        inventory.setItem(0, chickenPlanner);
        inventory.setItem(1, eggPlanner);
        inventory.setItem(2, enderPearlPlanner);
        inventory.setItem(3, pigPlanner);
        inventory.setItem(4, cowPlanner);
        inventory.setItem(26, backItem);

        return inventory;
    }

    private static void addGlow(ItemMeta itemMeta) {
        itemMeta.addEnchant(Enchantment.LURE, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }


    private static int spawnPlanner(Player p, ItemStack itemStack) {


        Integer planner = planners.remove(p.getUniqueId());

        if (planner != null) {
            Bukkit.getScheduler().cancelTask(planner);
        }

        UUID uuid = p.getUniqueId();

        p.setGliding(true);
        p.setAllowFlight(true);

        Entity plannerEntity = p.getLocation().getWorld().spawnEntity(p.getLocation(), INSTANCE.getGameManager().getPlayerPlanner(uuid));
        plannerEntity.setPassenger(p);
        plannerEntity.setCustomNameVisible(false);
        plannerEntity.setCustomName(ChatColor.GREEN + "Planner");
        plannerEntity.setInvulnerable(true);

        if (plannerEntity instanceof Cow) {
            Cow plannerCow = (Cow) plannerEntity;
            plannerCow.setGliding(true);
        } else if (plannerEntity instanceof Pig) {
            Pig plannerCow = (Pig) plannerEntity;
            plannerCow.setGliding(true);
        }

        return Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniRoyal.getPlugin(MiniRoyal.class), () -> {
            Player playerUpdate = Bukkit.getPlayer(p.getUniqueId());
            if (playerUpdate != null) {
                Location playerLocation = playerUpdate.getLocation();

                Vector direction = playerLocation.getDirection();
                direction.setX(Math.min(0.47, Math.max(-0.47, direction.getX()*0.7)));
                direction.setY(-0.1625);
                direction.setZ(Math.min(0.47, Math.max(-0.47, direction.getZ()*0.7)));

                if (getDistance(playerLocation) > 5) {
                    plannerEntity.setVelocity(direction);
                } else {
                    plannerEntity.remove();
                    p.setGliding(false);
                    if (planners.get(p.getUniqueId()) != null) {
                        Bukkit.getScheduler().cancelTask(planners.remove(p.getUniqueId()));
                    }

                    p.getInventory().remove(itemStack);
                }

                if (playerUpdate.getVehicle() != null) {
                    Location plannerLoc = playerUpdate.getVehicle().getLocation();

                    plannerLoc.setYaw(playerLocation.getYaw() + 0f);
                    plannerLoc.setPitch(playerLocation.getPitch() + 0f);

                    try {
                        methods[1].invoke(methods[0].invoke(playerUpdate.getVehicle()), plannerLoc.getX(), plannerLoc.getY(), plannerLoc.getZ(), plannerLoc.getYaw(), plannerLoc.getPitch());
                    } catch (Exception ex) {
                        // Shouldn't happen, but possibly print some stuff?
                    }

                } else if (planners.get(p.getUniqueId()) != null) {
                    p.setGliding(false);
                    Bukkit.getScheduler().cancelTask(planners.remove(p.getUniqueId()));
                }


            } else {
                plannerEntity.remove();
                p.setGliding(false);
                if (planners.get(p.getUniqueId()) != null) {
                    Bukkit.getScheduler().cancelTask(planners.remove(p.getUniqueId()));
                }
            }


        }, 0L, 1L);
    }

    private static final Method[] methods = ((Supplier<Method[]>) () -> {
        try {
            Method getHandle = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftEntity").getDeclaredMethod("getHandle");
            return new Method[] {
                    getHandle, getHandle.getReturnType().getDeclaredMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class)
            };
        } catch (Exception ex) {
            return null;
        }
    }).get();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (INSTANCE.isInSetupMod(p)) {
            if (INSTANCE.getMiniRoyalMap().getChestRegistry().removeChest(event.getBlock().getLocation())) {
                p.sendMessage(prefixMessage + ChatColor.GREEN + "Chest removed from the map.");
            } else {
                event.setCancelled(true);
            }
        } else {
            GameStatus gameStatus = INSTANCE.getGameManager().getGameStatus();
            event.setCancelled(gameStatus == GameStatus.WAITING_ROOM || gameStatus == GameStatus.WAITING_ROOM_FULL);
        }

    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (INSTANCE.isInSetupMod(p)) {
            ItemStack itemStack = event.getItemInHand();
            if (itemStack != null) {
                SetupModItems setupModItem = SetupModItems.isInSetupModItems(itemStack);
                if (setupModItem == SetupModItems.SET_MAP_CHEST) {
                    INSTANCE.getMiniRoyalMap().getChestRegistry().addChest(event.getBlock().getLocation());
                    p.sendMessage(prefixMessage + ChatColor.GREEN + "Chest added to the map.");
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            GameStatus gameStatus = INSTANCE.getGameManager().getGameStatus();
            event.setCancelled(gameStatus == GameStatus.WAITING_ROOM || gameStatus == GameStatus.WAITING_ROOM_FULL);
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        GameStatus gameStatus = INSTANCE.getGameManager().getGameStatus();
        if (gameStatus == GameStatus.GAME_NOT_SETUP) {
            event.setCancelled(INSTANCE.isInSetupMod(p));
        } else {
            event.setCancelled(!(gameStatus == GameStatus.IN_GAME) && !INSTANCE.getGameManager().getUuidList().contains(p.getUniqueId()));
        }

        if (event.getItemDrop() != null) {
            event.setCancelled(event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Planner"));
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        GameStatus gameStatus = INSTANCE.getGameManager().getGameStatus();
        if (gameStatus == GameStatus.GAME_NOT_SETUP) {
            event.setCancelled(INSTANCE.isInSetupMod(p));
        } else {
            event.setCancelled(!(gameStatus == GameStatus.IN_GAME) && !INSTANCE.getGameManager().getUuidList().contains(p.getUniqueId()));
        }

        if (event.getItem() != null) {
            event.setCancelled(event.getItem().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Planner"));
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player p = event.getPlayer();

        GameManager gameManager = INSTANCE.getGameManager();
        GameMode gameMode = gameManager.joinTheGame(p);
        p.setGameMode(gameMode);


        if (gameManager.getUuidList().contains(p.getUniqueId())) {
            gameManager.sendMessage(prefixMessage + ChatColor.RESET + p.getDisplayName() + ChatColor.YELLOW + " join the party ! " + ChatColor.AQUA + "(" + gameManager.getUuidList().size() + "/" + gameManager.getGameCapacity() + ")", 1);
        }

        GameStatus gameStatus = gameManager.getGameStatus();

        if (!(gameStatus == GameStatus.GAME_NOT_SETUP)) {
            p.sendTitle(ChatColor.AQUA + "Mini-Royal", gameMode == GameMode.SPECTATOR ? ChatColor.WHITE + "SPECTATOR" : "");
            if (!(gameStatus == GameStatus.IN_GAME || gameStatus == GameStatus.FINISH)) {
                INSTANCE.getLobby().teleportPlayerToLobby(p);
            } else {
                Location joinLoc = INSTANCE.getMiniRoyalMap().getFinishZoneCenter();
                joinLoc.setY(65);
                p.teleport(joinLoc);
            }
        }



    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage("");
        INSTANCE.getGameManager().leaveTheGame(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerKickEvent event) {
        event.setLeaveMessage("");
        INSTANCE.getGameManager().leaveTheGame(event.getPlayer().getUniqueId());
    }


    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            GameStatus gameStatus = INSTANCE.getGameManager().getGameStatus();
            event.setCancelled(gameStatus == GameStatus.WAITING_ROOM || gameStatus == GameStatus.WAITING_ROOM_FULL || gameStatus == GameStatus.FINISH);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        GameManager gameManager = INSTANCE.getGameManager();
        Location respawnLoc = null;



        if (p.getKiller() != null) {
            gameManager.addKillToPlayer(p.getKiller().getUniqueId());
            respawnLoc = p.getKiller().getLocation();
            event.setDeathMessage(prefixMessage + coloredDeathMessage(event.getDeathMessage(), p.getName(), p.getKiller().getName()));
        } else {
            event.setDeathMessage(prefixMessage + coloredDeathMessage(event.getDeathMessage(), p.getName(), null));
        }



        if (respawnLoc == null) {
            respawnLoc = getRandomPlayerLocation();
        }

        gameManager.leaveTheGame(p.getUniqueId());

        p.spigot().respawn();
        p.teleport(respawnLoc);
        p.setGameMode(gameManager.joinTheGame(p.getPlayer()));

    }

    private String coloredDeathMessage(String deathMessage, String killedName, String killerName) {

        deathMessage = deathMessage.replace(killedName, ChatColor.RED + killedName + ChatColor.YELLOW);
        if (!(killerName == null || killedName.equals(killerName)))
            deathMessage = deathMessage.replace(killerName, ChatColor.AQUA + killerName + ChatColor.YELLOW);

        return ChatColor.YELLOW + deathMessage;
    }

    @EventHandler
    public void onDamagedEntity(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();

            if (event.getCause() != null) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(planners.containsKey(p.getUniqueId()));
                }
            }
        }
    }


    @EventHandler
    public void onDismountEntity(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            p.setAllowFlight(false);
            p.setGliding(false);
            if (event.getDismounted() != null) {

                if (event.getDismounted().getCustomName().equals(ChatColor.GREEN + "Planner")) {

                    Integer planner = planners.remove(p.getUniqueId());

                    if (planner != null) {
                        Bukkit.getScheduler().cancelTask(planner);
                    }

                    event.getDismounted().remove();

                    fallenSecurity(p);
                }
            }
        }
    }

    public static void fallenSecurity(Player p) {
        int plannerScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniRoyal.getPlugin(MiniRoyal.class), () -> {
            Player playerUpdate = Bukkit.getPlayer(p.getUniqueId());
            if (playerUpdate != null) {
                System.out.println(getDistance(p));
                if (getDistance(playerUpdate) < 20 && getDistance(playerUpdate) > 5) {
                    spawnPlanner(playerUpdate, getPlannerItemStack());
                } else if (getDistance(playerUpdate) <= 5) {
                    playerUpdate.getInventory().remove(getPlannerItemStack());
                    if (planners.get(p.getUniqueId()) != null) {
                        Bukkit.getScheduler().cancelTask(planners.remove(p.getUniqueId()));
                    }
                }
            } else {
                if (planners.get(p.getUniqueId()) != null) {
                    Bukkit.getScheduler().cancelTask(planners.remove(p.getUniqueId()));
                }
            }
        }, 0L, 1L);
        planners.put(p.getUniqueId(), plannerScheduler);
    }

    private static int getDistance(Entity e){
        Location loc = e.getLocation().clone();
        double y = loc.getBlockY();
        int distance = 0;
        for (double i = y; i >= 0; i--){
            loc.setY(i);
            if(loc.getBlock().getType().isSolid())break;
            distance++;
        }
        return distance;
    }

    private static int getDistance(Location loc){
        double y = loc.getBlockY();
        int distance = 0;
        for (double i = y; i >= 0; i--){
            loc.setY(i);
            if(loc.getBlock().getType().isSolid())break;
            distance++;
        }
        return distance;
    }

    public static ItemStack getPlannerItemStack() {
        ItemStack plannerItem = new ItemStack(Material.FEATHER);
        ItemMeta plannerMeta = plannerItem.getItemMeta();
        plannerMeta.setDisplayName(ChatColor.GREEN + "Planner");
        plannerItem.setItemMeta(plannerMeta);

        return plannerItem;
    }

    public static Location getRandomPlayerLocation() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation() != null) {
                return p.getLocation();
            }
        }

        Location location = INSTANCE.getMiniRoyalMap().getFinishZoneCenter();
        location.setY(45);
        return location;
    }
}