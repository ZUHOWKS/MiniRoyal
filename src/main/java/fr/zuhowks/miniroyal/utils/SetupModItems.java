package fr.zuhowks.miniroyal.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public enum SetupModItems {
    SETUP_LOBBY(Material.GOLD_RECORD, "§6§lSETUP LOBBY", "amr setuplobby",  Color.GRAY + "/amr setuplobby", Color.GRAY + "(Right click in the air)"),
    SET_LOBBY_POS(Material.STICK, "§6§lSET LOBBY POSITION", null, Color.GRAY + "- Right click on block to set position 1", Color.GRAY + "- Left click on block to set position 2"),
    SET_LOBBY_SPAWN(Material.GREEN_RECORD, "§6§lSET LOBBY SPAWN", "amr setuplobby setspawn", Color.GRAY + "/amr lobby setspawn -> Set lobby spawn at your location", Color.GRAY + "(Right click in the air)"),
    SETUP_LOBBY_CONFIRM(Material.SLIME_BALL, "§a§lCONFIRM LOBBY CHANGE", "amr setuplobby confirm", Color.GRAY + "/amr lobby confirm -> Register lobby change", Color.GRAY + "(Right click in the air)"),
    SETUP_LOBBY_CANCEL(Material.REDSTONE, "§c§lCANCEL LOBBY CHANGE", "amr setuplobby cancel", Color.GRAY + "/amr lobby cancel -> Cancel lobby change", Color.GRAY + "(Right click in the air)"),
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
