package fr.zuhowks.miniroyal.map.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Map;

public class Item {
    final private ItemStack itemStack;
    final private ItemRarity rarity;
    final private int luck; // Represent the luck to drop this item

    public Item(String materialName, String rarity, int luck, String name) {
        this.itemStack = new ItemStack(Material.getMaterial(materialName));
        this.rarity = ItemRarity.valueOf(rarity);
        this.luck = luck;
    }

    public Item(Material material, String rarity, int luck, String name) {
        this.itemStack = new ItemStack(material);
        this.rarity = ItemRarity.valueOf(rarity);
        this.luck = luck;

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(this.rarity.getPrefix() + name);
        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "Rarity: " + this.rarity.getName()));
        this.itemStack.setItemMeta(meta);
    }

    public Item(Material material, ItemRarity rarity, int luck, String name) {
        this.itemStack = new ItemStack(material);
        this.rarity = rarity;
        this.luck = luck;

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(this.rarity.getPrefix() + name);
        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "Rarity: " + this.rarity.getName()));
        this.itemStack.setItemMeta(meta);
    }

    public Item(Material material, ItemRarity rarity, int luck, String name, Map<Enchantment, Integer> enchantments) {
        this.itemStack = new ItemStack(material);
        this.rarity = rarity;
        this.luck = luck;

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(this.rarity.getPrefix() + name);
        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "Rarity: " + this.rarity.getName()));
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        }
        this.itemStack.setItemMeta(meta);
    }

    public int getLuck() {
        return luck;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item item = (Item) obj;
            ItemMeta itemMeta = item.getItemStack().getItemMeta();
            ItemMeta thisItemMeta = this.getItemStack().getItemMeta();
            return thisItemMeta.getDisplayName().equals(itemMeta.getDisplayName()) && thisItemMeta.getEnchants() == itemMeta.getEnchants() && this.getRarity() == item.getRarity() && this.getLuck() == item.getLuck();
        }
        return false;
    }

    public ItemYML getItemYML() {
        return new ItemYML(this.getItemStack().getData().getItemType().name(), this.getItemStack().getItemMeta().getDisplayName(), this.getRarity().name(), this.getLuck(), this.getItemStack().getItemMeta().getEnchants());
    }
}
