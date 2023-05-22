package fr.zuhowks.miniroyal.commands;

import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.utils.SetupModItems;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
                            if (inventoryMap.size() == 0) {
                                INSTANCE.getMiniRoyalMap().getChestRegistry().removeBlockChest();
                            }
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup mod disable !");

                        } else if (args[0].equalsIgnoreCase("setuplobby")) {
                            setupLobby(p.getInventory());
                            p.updateInventory();
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup of the lobby turn on !");

                        } else if (args[0].equalsIgnoreCase("setupmap")) {
                            setupMap(p.getInventory());
                            p.updateInventory();
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup of the map turn on !");
                        }
                    } else if (argsLen == 2) {
                        if (args[0].equalsIgnoreCase("setuplobby")) {
                            if (args[1].equalsIgnoreCase("setspawn")) {
                                INSTANCE.getLobby().setSpawnLocation(p.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Spawn location as been set !" + ChatColor.GRAY + "(/amr setuplobby confirm to apply change)");

                            } else if (args[1].equalsIgnoreCase("confirm")) {
                                INSTANCE.getLobby().saveLobbyToFIle();
                                setupMod(p.getInventory());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Change has been saved successfully !");

                            } else if (args[1].equalsIgnoreCase("cancel")) {
                                INSTANCE.getLobby().loadLobbyFromFile();
                                setupMod(p.getInventory());
                                p.sendMessage(prefixMessage + ChatColor.RED + "Change has been cancelled successfully !");

                            } else if (args[1].equalsIgnoreCase("setpos")) {
                                setupLobbyPosition(p.getInventory());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Use items in your inventory to setup the lobby position." + ChatColor.GRAY + "(/amr setuplobby confirm to apply change)");

                            }
                        } else if (args[0].equalsIgnoreCase("setupmap")) {
                            if (args[1].equalsIgnoreCase("finishzone")) {
                                INSTANCE.getMiniRoyalMap().setFinishZoneCenter(p.getLocation());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Finish zone location as been set !" + ChatColor.GRAY + "(/amr setupmap confirm to apply change)");

                            } else if (args[1].equalsIgnoreCase("confirm")) {
                                INSTANCE.getMiniRoyalMap().saveMapToFile();
                                setupMod(p.getInventory());
                                p.sendMessage(prefixMessage + ChatColor.GREEN + "Change has been saved successfully !");

                            } else if (args[1].equalsIgnoreCase("cancel")) {
                                INSTANCE.getMiniRoyalMap().loadMapFromFile();
                                setupMod(p.getInventory());
                                p.sendMessage(prefixMessage + ChatColor.RED + "Change has been cancelled successfully !");

                            }
                        }
                    }
                } else if (argsLen == 1) {
                    if (args[0].equalsIgnoreCase("setupmod")) {
                        INSTANCE.getInventoryRegistry().put(p.getUniqueId(), p.getInventory().getContents());
                        setupMod(p.getInventory());
                        p.updateInventory();
                        INSTANCE.getMiniRoyalMap().getChestRegistry().setBlockChests();
                        p.sendMessage(prefixMessage + ChatColor.GREEN + "Setup mod activate ! Use items in your inventory to setup the party without command.");
                    } else if (args[0].equalsIgnoreCase("enable")) {
                        INSTANCE.setPartyIsSetup(true);
                        INSTANCE.getFileConfiguration().set("party.isSetup", true);
                        INSTANCE.saveConfig();
                        p.sendMessage(prefixMessage + ChatColor.GREEN + "Party is now enabled !");
                    } else if (args[0].equalsIgnoreCase("disable")) {
                        INSTANCE.setPartyIsSetup(false);
                        INSTANCE.getFileConfiguration().set("party.isSetup", false);
                        INSTANCE.saveConfig();
                        p.sendMessage(prefixMessage + ChatColor.RED + "Party is now disabled !");
                    }
                } else {
                    p.sendMessage(prefixMessage + ChatColor.RED + "Perform /amr help to view the list of command");
                }

                return true;
            }
        }

        return false;
    }

    private void setupMod(PlayerInventory inv) {
        inv.clear();
        inv.setItem(2, SetupModItems.SETUP_LOBBY.getItemStack());
        inv.setItem(6, SetupModItems.SETUP_MAP.getItemStack());

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

    private void setupMap(PlayerInventory inv) {
        inv.clear();
        inv.setItem(0, SetupModItems.SET_MAP_POS.getItemStack());
        inv.setItem(4, SetupModItems.SET_MAP_FINISH_ZONE.getItemStack());
        inv.setItem(6, SetupModItems.SET_MAP_CHEST.getItemStack());
        inv.setItem(7, SetupModItems.SETUP_MAP_CONFIRM.getItemStack());
        inv.setItem(8, SetupModItems.SETUP_MAP_CANCEL.getItemStack());
    }


}

