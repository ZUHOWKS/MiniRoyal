package fr.zuhowks.miniroyal.commands;

import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.game.GameStatus;
import fr.zuhowks.miniroyal.utils.SetupModItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.StringUtil;

import java.util.*;

import static fr.zuhowks.miniroyal.MiniRoyal.prefixMessage;

public class CommandAdmin implements CommandExecutor, TabCompleter {

    final MiniRoyal INSTANCE = MiniRoyal.getINSTANCE();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            int argsLen = args.length;
            if (p.hasPermission("miniroyal.admin")) {
                GameStatus gameStatus = INSTANCE.getGameManager().getGameStatus();

                if (gameStatus == GameStatus.GAME_NOT_SETUP) {
                    if (INSTANCE.isInSetupMod(p)) {
                        if (argsLen == 1) {
                            if (args[0].equalsIgnoreCase("setupmod")) {
                                PlayerInventory inv = p.getInventory();
                                UUID uuid = p.getUniqueId();

                                Map<UUID, ItemStack[]> inventoryMap = INSTANCE.getInventoryRegistry();
                                inv.setContents(inventoryMap.get(uuid));

                                p.updateInventory();
                                inventoryMap.remove(uuid);

                                if (inventoryMap.isEmpty()) {
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

                            } else {
                                p.sendMessage(prefixMessage + ChatColor.RED + "Perform /amr help to view the list of command");
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

                                } else {
                                    p.sendMessage(prefixMessage + ChatColor.RED + "Perform /amr help to view the list of command");
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

                                } else {
                                    p.sendMessage(prefixMessage + ChatColor.RED + "Perform /amr help to view the list of command");
                                }
                            }
                        }
                    } else if(argsLen == 1) {
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
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Party is already disabled !");

                        } else if (args[0].equalsIgnoreCase("help")) {
                            p.sendMessage(listOfCommands());

                        } else {
                            p.sendMessage(prefixMessage + ChatColor.RED + "Perform /amr help to view the list of command");
                        }

                    } else {
                        p.sendMessage(prefixMessage + ChatColor.RED + "Perform /amr help to view the list of command");
                    }

                } else {
                    if (argsLen == 1) {
                        if (args[0].equalsIgnoreCase("disable")) {

                            INSTANCE.setPartyIsSetup(false);
                            INSTANCE.getFileConfiguration().set("party.isSetup", false);
                            INSTANCE.saveConfig();

                            p.sendMessage(prefixMessage + ChatColor.RED + "Party is now disabled !");

                            for (UUID uuid : INSTANCE.getGameManager().getUuidList()) {
                                Bukkit.getPlayer(uuid).kickPlayer("Party has been disable by administration.");

                            }

                        } else if (args[0].equalsIgnoreCase("enable")) {
                            p.sendMessage(prefixMessage + ChatColor.GREEN + "Party is already enabled !");

                        } else if (args[0].equalsIgnoreCase("help")) {
                            p.sendMessage(listOfCommands());
                        } else {
                            p.sendMessage(prefixMessage + ChatColor.RED + "Perform /amr help to view the list of command");
                        }

                    } else {
                        p.sendMessage(prefixMessage + ChatColor.RED + "Unavailable command. Try: " + ChatColor.AQUA + "/amr help");
                    }
                }

                return true;
            } else {
                p.sendMessage(prefixMessage + ChatColor.RED + "You haven't the required permission to perform this command.");
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            final int argsLen = args.length;

            final List<String> result = new ArrayList<>();

            if (p.hasPermission("miniroyal.admin")) {
                GameStatus gameStatus = INSTANCE.getGameManager().getGameStatus();

                if (gameStatus == GameStatus.GAME_NOT_SETUP) {
                    if (INSTANCE.isInSetupMod(p)) {
                        if (argsLen == 1) {
                            StringUtil.copyPartialMatches(args[0], Arrays.asList("setupmod", "setuplobby", "setupmap"), result);

                        } else if (argsLen == 2) {
                            if (args[0].equalsIgnoreCase("setuplobby")) {
                                StringUtil.copyPartialMatches(args[0], Arrays.asList("setspawn", "setpos", "confirm", "cancel"), result);

                            } else if (args[0].equalsIgnoreCase("setupmap")) {
                                StringUtil.copyPartialMatches(args[0], Arrays.asList("finishzone", "confirm", "cancel"), result);
                            }
                        }
                    } else if (argsLen == 1) {
                        StringUtil.copyPartialMatches(args[0], Arrays.asList("setupmod", "enable", "help"), result);
                    }

                } else {
                    if (argsLen == 1) {
                        StringUtil.copyPartialMatches(args[0], Arrays.asList("disable", "help"), result);
                    }
                }

                return result;

            } else {
                p.sendMessage(prefixMessage + ChatColor.RED + "You haven't the required permission to perform this command.");
            }
        }

        return null;
    }


    private String listOfCommands() {
        return prefixMessage + ChatColor.YELLOW + "List of Mini-Royal admin commands:\n" +
                "  - " + ChatColor.AQUA + "/amr setupmod" + ChatColor.YELLOW + "  to enter/exit in setup mod.\n" +
                "  - " + ChatColor.AQUA + "/amr setuplobby" + ChatColor.YELLOW + "  to enter/exit in setup lobby mod.\n" +
                "  - " + ChatColor.AQUA + "/amr setuplobby setspawn" + ChatColor.YELLOW + "  to set lobby spawn.\n" +
                "  - " + ChatColor.AQUA + "/amr setuplobby setpos" + ChatColor.YELLOW + "  to set lobby position.\n" +
                "  - " + ChatColor.AQUA + "/amr setuplobby confirm" + ChatColor.YELLOW + "  to confirm lobby change.\n" +
                "  - " + ChatColor.AQUA + "/amr setuplobby cancel" + ChatColor.YELLOW + "  to cancel lobby change.\n" +
                "  - " + ChatColor.AQUA + "/amr setupmap finishzone" + ChatColor.YELLOW + "  to enter/exit in setup map mod.\n" +
                "  - " + ChatColor.AQUA + "/amr setupmap confirm" + ChatColor.YELLOW + "  to confirm map change.\n" +
                "  - " + ChatColor.AQUA + "/amr setupmap cancel" + ChatColor.YELLOW + "  to cancel lobby change.\n" +
                "  - " + ChatColor.AQUA + "/amr enable" + ChatColor.YELLOW + "  to enable lobby change.\n" +
                "  - " + ChatColor.AQUA + "/amr disable" + ChatColor.YELLOW + "  to cancel lobby change.\n";
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

