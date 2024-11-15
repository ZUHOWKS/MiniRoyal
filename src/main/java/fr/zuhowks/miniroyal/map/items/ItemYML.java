package fr.zuhowks.miniroyal.map.items;

import org.bukkit.enchantments.Enchantment;

import java.util.Map;

public class ItemYML {
    private String materialName;

    private String displayName;
    private String rarity;
    private int luck;
    private Map<Enchantment, Integer> enchantmentIntegerMap;

    public ItemYML(String materialName, String displayName, String rarity, int luck, Map<Enchantment, Integer> enchantmentIntegerMap) {
        this.materialName = materialName;
        this.displayName = displayName;
        this.rarity = rarity;
        this.luck = luck;
        this.enchantmentIntegerMap = enchantmentIntegerMap;
    }

    public String getMaterialName() {
        return materialName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRarity() {
        return rarity;
    }

    public int getLuck() {
        return luck;
    }

    public Map<Enchantment, Integer> getEnchantmentIntegerMap() {
        return enchantmentIntegerMap;
    }
}
