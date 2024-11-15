package fr.zuhowks.miniroyal.map.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemPotion extends Item{
    public ItemPotion(Material material, ItemRarity rarity, int luck, String name, PotionType potionType, boolean extended, boolean upgraded) {
        super(material, rarity, luck, name);
        PotionMeta potionMeta = (PotionMeta) this.getItemStack().getItemMeta();
        potionMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
        this.getItemStack().setItemMeta(potionMeta);
    }
}
