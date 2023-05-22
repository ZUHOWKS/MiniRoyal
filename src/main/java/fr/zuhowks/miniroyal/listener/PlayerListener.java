package fr.zuhowks.miniroyal.listener;

import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.utils.SetupModItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import static fr.zuhowks.miniroyal.MiniRoyal.prefixMessage;

public class PlayerListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {

        Player p = event.getPlayer();
        event.setCancelled(MiniRoyal.getINSTANCE().isInSetupMod(p));
        if (event.isCancelled()) {
            ItemStack itemStack = event.getItem();
            if (itemStack != null) {
                SetupModItems setupModItem = SetupModItems.isInSetupModItems(itemStack);
                if (setupModItem != null) {
                    if (setupModItem.getCommand() != null && !setupModItem.getCommand().equals("")) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            p.chat("/" + setupModItem.getCommand());

                        }
                    } else {
                        Block block = event.getClickedBlock();
                        if (setupModItem == SetupModItems.SET_LOBBY_POS) {

                            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                MiniRoyal.getINSTANCE().getLobby().setPos1(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 1 as been set !");

                            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                MiniRoyal.getINSTANCE().getLobby().setPos2(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 2 as been set !");

                            }

                        } else if (setupModItem == SetupModItems.SET_MAP_POS) {
                            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                MiniRoyal.getINSTANCE().getMiniRoyalMap().setPos1(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 1 as been set !");

                            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                MiniRoyal.getINSTANCE().getMiniRoyalMap().setPos2(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Position 2 as been set !");

                            }
                        } else if (setupModItem == SetupModItems.SET_MAP_CHEST && event.getClickedBlock().getType() != Material.CHEST) {
                            event.setCancelled(false);
                        }
                    }
                }
            }
        }


    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (MiniRoyal.getINSTANCE().isInSetupMod(p)) {
            if (MiniRoyal.getINSTANCE().getMiniRoyalMap().getChestRegistry().removeChest(event.getBlock().getLocation())) {
                p.sendMessage(prefixMessage + ChatColor.GREEN + "Chest removed from the map.");
            } else {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (MiniRoyal.getINSTANCE().isInSetupMod(p)) {
            ItemStack itemStack = event.getItemInHand();
            if (itemStack != null) {
                SetupModItems setupModItem = SetupModItems.isInSetupModItems(itemStack);
                System.out.println(itemStack.getItemMeta().getDisplayName());
                if (setupModItem == SetupModItems.SET_MAP_CHEST) {
                    MiniRoyal.getINSTANCE().getMiniRoyalMap().getChestRegistry().addChest(event.getBlock().getLocation());
                    p.sendMessage(prefixMessage + ChatColor.GREEN + "Chest added to the map.");
                } else {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        event.setCancelled(MiniRoyal.getINSTANCE().isInSetupMod(p));
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        event.setCancelled(MiniRoyal.getINSTANCE().isInSetupMod(p));
    }
}
