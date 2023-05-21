package fr.zuhowks.miniroyal.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public enum SetupModItems {
    SETUP_LOBBY(Material.GOLD_RECORD, "§6§lSETUP LOBBY", "amr setuplobby",  Color.GRAY + "/amr setuplobby -> To setup the lobby", Color.GRAY + "(Right click in the air)"),
    SET_LOBBY_POS(Material.STICK, "§6§lSET LOBBY POSITION", null, Color.GRAY + "- Right click on block to set position 1", Color.GRAY + "- Left click on block to set position 2"),
    SET_LOBBY_SPAWN(Material.GREEN_RECORD, "§6§lSET LOBBY SPAWN", "amr setuplobby setspawn", Color.GRAY + "/amr setuplobby setspawn -> Set lobby spawn at your location", Color.GRAY + "(Right click)"),
    SETUP_LOBBY_CONFIRM(Material.SLIME_BALL, "§a§lCONFIRM LOBBY CHANGE", "amr setuplobby confirm", Color.GRAY + "/amr setuplobby confirm -> Register lobby change", Color.GRAY + "(Right click)"),
    SETUP_LOBBY_CANCEL(Material.REDSTONE, "§c§lCANCEL LOBBY CHANGE", "amr setuplobby cancel", Color.GRAY + "/amr setuplobby cancel -> Cancel lobby change", Color.GRAY + "(Right click)"),
    SETUP_MAP(Material.EMPTY_MAP, "§e§lSETUP MAP", "amr setupmap", Color.GRAY + "/amr setupmap -> To setup the map", Color.GRAY + "(Right click)"),
    SET_MAP_POS(Material.STICK, "§e§lSET MAP POSITION", null, Color.GRAY + "- Right click on block to set position 1", Color.GRAY + "- Left click on block to set position 2"),
    SET_MAP_FINISH_ZONE(Material.BEACON, "§e§lSET FINISH ZONE", "amr setupmap finishzone", Color.GRAY + "/amr setupmap confirm -> Register lobby change", Color.GRAY + "(Right click)"),
    SET_MAP_CHEST(Material.CHEST, "§e§lADD CHEST", null, Color.GRAY + "- Right click to add chest"),
    SETUP_MAP_CONFIRM(Material.SLIME_BALL, "§a§lCONFIRM MAP CHANGE", "amr setupmap confirm", Color.GRAY + "/amr setupmap confirm -> Register lobby change", Color.GRAY + "(Right click)"),
    SETUP_MAP_CANCEL(Material.REDSTONE, "§c§lCANCEL MAP CHANGE", "amr setupmap cancel", Color.GRAY + "/amr setupmap cancel -> Cancel lobby change", Color.GRAY + "(Right click)"),
    ;

    private final ItemStack itemStack;
    private final String command;

    private final String displayName;
    SetupModItems(Material material, String displayName, String command, String... lore) {
        this.itemStack = new ItemStack(material, 1);
        this.command = command;
        this.displayName = displayName;
        //Set a basic item meta
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(meta);
    }
    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getCommand() {
        return command;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SetupModItems isInSetupModItems(ItemStack item) {
        for (SetupModItems setupModItems : SetupModItems.values()) {
            if (setupModItems.getItemStack().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                return setupModItems;
            }
        }
        return null;
    }
}
