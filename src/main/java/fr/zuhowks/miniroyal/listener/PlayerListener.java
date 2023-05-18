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
        if (MiniRoyal.getINSTANCE().isInSetupMod(p)) {
            ItemStack itemStack = event.getItem();
            if (itemStack != null) {
                SetupModItems setupModItem = SetupModItems.isInSetupModItems(itemStack);
                if (setupModItem != null) {
                    if (setupModItem.getCommand() != null && !setupModItem.getCommand().equals("")) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            p.chat("/" + setupModItem.getCommand());

                        }
                    } else {
                        String displayName = setupModItem.getItemStack().getItemMeta().getDisplayName();

                        if (displayName.equals(SetupModItems.SET_LOBBY_POS.getDisplayName())) {
                            Block block = event.getClickedBlock();

                            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                MiniRoyal.getINSTANCE().getLobby().setPos1(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Pos 1 as been set !");

                            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                MiniRoyal.getINSTANCE().getLobby().setPos2(block.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Pos 2 as been set !");

                            }
                        }
                    }
                }
            }
        }


    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        event.setCancelled(MiniRoyal.getINSTANCE().isInSetupMod(p));

    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        event.setCancelled(MiniRoyal.getINSTANCE().isInSetupMod(p));

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        event.setCancelled(MiniRoyal.getINSTANCE().isInSetupMod(p));
    }

    @EventHandler
    public void onDrop(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        event.setCancelled(MiniRoyal.getINSTANCE().isInSetupMod(p));
    }
}
