package fr.zuhowks.miniroyal.map.items;

import org.bukkit.ChatColor;

public enum ItemRarity {
    LEGENDARY("" + ChatColor.BOLD + ChatColor.GOLD + "Legendary", "" + ChatColor.BOLD + ChatColor.GOLD + "⌊ " + ChatColor.BOLD + ChatColor.YELLOW + "L" + ChatColor.BOLD + ChatColor.GOLD + " ⌉" + ChatColor.YELLOW + "  "),
    EPIC("" + ChatColor.BOLD + ChatColor.DARK_PURPLE + "Epic", "" + ChatColor.BOLD + ChatColor.DARK_PURPLE + "⌊ " + ChatColor.BOLD + ChatColor.LIGHT_PURPLE + "E" + ChatColor.BOLD + ChatColor.DARK_PURPLE + " ⌉" + ChatColor.LIGHT_PURPLE + "  "),
    RARE("" + ChatColor.BOLD + ChatColor.DARK_AQUA + "Rare", "" + ChatColor.BOLD + ChatColor.DARK_AQUA + "⌊ " + ChatColor.BOLD + ChatColor.AQUA + "R" + ChatColor.BOLD + ChatColor.DARK_AQUA + " ⌉" + ChatColor.AQUA + "  "),
    COMMON("" + ChatColor.BOLD + ChatColor.DARK_GRAY + "Common", "" + ChatColor.BOLD + ChatColor.DARK_GRAY + "⌊ " + ChatColor.BOLD + ChatColor.GRAY + "C" + ChatColor.BOLD + ChatColor.DARK_GRAY + " ⌉" + ChatColor.GRAY + "  ");

    private final String name;
    private final String prefix;

    ItemRarity(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }
}
