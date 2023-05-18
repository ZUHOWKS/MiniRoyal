package fr.zuhowks.miniroyal.commands;

import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.lobby.Lobby;
import fr.zuhowks.miniroyal.utils.SetupModItems;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.UUID;

import static fr.zuhowks.miniroyal.MiniRoyal.prefixMessage;

public class CommandAdmin implements CommandExecutor {

    final MiniRoyal INSTANCE = MiniRoyal.getINSTANCE();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            int argsLen = args.length;
            if (p.hasPermission("miniroyal.admin")) {
                if (INSTANCE.isInSetupMod(p)) {
                    if (argsLen == 1) {
                        if (args[0].equalsIgnoreCase("setupmod")) {
                            PlayerInventory inv = p.getInventory();
                            UUID uuid = p.getUniqueId();
                            Map<UUID, ItemStack[]> inventoryMap = INSTANCE.getInventoryRegistry();


                            inv.setContents(inventoryMap.get(uuid));

                            p.updateInventory();
                            inventoryMap.remove(uuid);

                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup mod disable !");

                        } else if (args[0].equalsIgnoreCase("setuplobby")) {
                            setupLobby(p.getInventory());
                            p.updateInventory();
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup of the lobby turn on !");

                        }
                    } else if (argsLen == 2) {
                        if (args[0].equalsIgnoreCase("setuplobby")) {
                            if (args[1].equalsIgnoreCase("setspawn")) {
                                INSTANCE.getLobby().setSpawnLocation(p.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Spawn location as benn set !");
                            } else if (args[1].equalsIgnoreCase("confirm")) {
                                //TODO: Save Lobby change in yml
                                Lobby l = INSTANCE.getLobby();
                                FileConfiguration config = INSTANCE.getFileConfiguration();
                                config.set("lobby.pos1", l.getPos1());
                                config.set("lobby.pos2", l.getPos2());
                                config.set("lobby.spawn", l.getSpawnLocation());
                                INSTANCE.saveConfig();
                                setupMod(p.getInventory());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Change has been saved successfully !");

                            } else if (args[1].equalsIgnoreCase("cancel")) {
                                Lobby l = INSTANCE.getLobby();
                                FileConfiguration config = INSTANCE.getFileConfiguration();
                                l.setPos1((Location) config.get("lobby.pos1"));
                                l.setPos2((Location) config.get("lobby.pos2"));
                                l.setSpawnLocation((Location) config.get("lobby.spawn"));
                                setupMod(p.getInventory());
                                p.sendMessage(prefixMessage + ChatColor.RED + "Change has been cancelled successfully !");

                            } else if (args[1].equalsIgnoreCase("setpos")) {
                                setupLobbyPosition(p.getInventory());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Use items in your inventory to setup the lobby position.");
                            }
                        }
                    }
                } else if (argsLen == 1) {
                    if (args[0].equalsIgnoreCase("setupmod")) {
                        INSTANCE.getInventoryRegistry().put(p.getUniqueId(), p.getInventory().getContents());
                        setupMod(p.getInventory());
                        p.updateInventory();
                        p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup mod activate ! Use items in your inventory to setup the party without command.");
                    }
                } else {

                }

                return true;
            }
        }

        return false;
    }

    private void setupMod(PlayerInventory inv) {
        inv.clear();
        inv.setItem(0, SetupModItems.SETUP_LOBBY.getItemStack());
        //TODO: ADD ITEM SETUP

    }

    private void setupLobbyPosition(PlayerInventory inv) {
        inv.clear();
        inv.setItem(0, SetupModItems.SET_LOBBY_POS.getItemStack());
        inv.setItem(7, SetupModItems.SETUP_LOBBY_CONFIRM.getItemStack());
        inv.setItem(8, SetupModItems.SETUP_LOBBY_CANCEL.getItemStack());
    }
    private void setupLobby(PlayerInventory inv) {
        setupLobbyPosition(inv);
        inv.setItem(4, SetupModItems.SET_LOBBY_SPAWN.getItemStack());
    }


}

