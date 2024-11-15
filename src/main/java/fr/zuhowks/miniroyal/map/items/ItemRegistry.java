package fr.zuhowks.miniroyal.map.items;

import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemRegistry {

    private final List<Item> legendaryItems;
    private final List<Item> epicItems;
    private final List<Item> rareItems;
    private final List<Item> commonItems;
    private int legendaryItemsGave = 0;
    private int epicItemsGave = 0;
    private int rareItemsGave = 0;
    private int commonItemsGave = 0;
    private double legendaryLuck = 3;
    private double epicLuck = 14;
    private double rareLuck = 38;

    public ItemRegistry() {
        this.legendaryItems = new ArrayList<>();
        this.epicItems = new ArrayList<>();
        this.rareItems = new ArrayList<>();
        this.commonItems = new ArrayList<>();
    }

    public ItemStack getLegendaryItemStack(int index) {
        return this.legendaryItems.get(index).getItemStack();
    }

    public ItemStack getEpicItemStack(int index) {
        return this.epicItems.get(index).getItemStack();
    }

    public ItemStack getRareItemStack(int index) {
        return this.rareItems.get(index).getItemStack();
    }

    public ItemStack getCommonItemStack(int index) {
        return this.commonItems.get(index).getItemStack();
    }

    public List<ItemYML> getItems() {
        List<ItemYML> items = new ArrayList<>();
        addItemsYML(items, legendaryItems);
        addItemsYML(items, epicItems);
        addItemsYML(items, rareItems);
        addItemsYML(items, commonItems);
        return items;
    }

    private void addItemsYML(List<ItemYML> items, List<Item> rarityItems) {
        for (Item item : legendaryItems) {
            if(!items.contains(item.getItemYML())) {
                items.add(item.getItemYML());
            }
        }
    }

    private void addLegendaryItem(Item item) {
        for (int i=1; i <= item.getLuck(); i++) {
            this.legendaryItems.add(item);
        }
    }

    private void addEpicItem(Item item) {
        for (int i=1; i <= item.getLuck(); i++) {
            this.epicItems.add(item);
        }
    }

    private void addRareItem(Item item) {
        for (int i=1; i <= item.getLuck(); i++) {
            this.rareItems.add(item);
        }
    }

    private void addCommonItem(Item item) {
        for (int i=1; i <= item.getLuck(); i++) {
            this.commonItems.add(item);
        }
    }

    public void addItem(Item item) {
        if (item.getRarity() == ItemRarity.LEGENDARY) {
            this.addLegendaryItem(item);

        } else if (item.getRarity() == ItemRarity.EPIC) {
            this.addEpicItem(item);

        } else if (item.getRarity() == ItemRarity.RARE) {
            this.addRareItem(item);

        } else {
            this.addCommonItem(item);
        }
    }

    public void addItems(List<Item> items) {
        for (Item item : items) {
            this.addItem(item);
        }
    }

    public void setInventoryItems(Chest chest) {

        this.reloadItemsLuck();

        Random rand = new Random();
        int itemsToGive = 3 + rand.nextInt(4);

        for (int i = 1; i <= itemsToGive; i++) {

            int itemSlot = Math.max(0, Math.round((float) ((i * 3 / itemsToGive) - 1))) * 9 + rand.nextInt(9);

            ItemStack item;
            final double itemRarityLuck = rand.nextDouble() * (100) + 1;

            if (itemRarityLuck <= this.legendaryLuck) {
                int itemIndex = rand.nextInt(this.legendaryItems.size());
                item = this.getLegendaryItemStack(itemIndex);
                this.legendaryItemsGave++;

            } else if (itemRarityLuck <= this.epicLuck) {
                int itemIndex = rand.nextInt(this.epicItems.size());
                item = this.getEpicItemStack(itemIndex);
                this.epicItemsGave++;

            } else if (itemRarityLuck <= this.rareLuck) {
                int itemIndex = rand.nextInt(this.rareItems.size());
                item = this.getRareItemStack(itemIndex);
                this.rareItemsGave++;

            } else {
                int itemIndex = rand.nextInt(this.commonItems.size());
                item = this.getCommonItemStack(itemIndex);
                this.commonItemsGave++;
            }
            Inventory inv = chest.getInventory();
            while (!(inv.getItem(itemSlot) == null) && (itemSlot < 26)) {
                itemSlot++;
            }
            inv.setItem(itemSlot, item);
        }
    }

    private void reloadItemsLuck() {

        final double legendaryLuckRatioCoef = 0.61;
        final double epicLuckRatioCoef = 0.83;
        final double rareLuckRatioCoef = 1.27;

        final double rarityBalance = (double) (commonItemsGave - epicItemsGave) / Math.max(1, rareItemsGave - legendaryItemsGave);
        final double rarityBalanceLog = Math.log(Math.max(0.9, (rarityBalance) * 10));

        this.legendaryLuck = 5.35 + rarityBalanceLog * legendaryLuckRatioCoef;
        this.epicLuck = 17.84 + rarityBalanceLog * epicLuckRatioCoef;
        this.rareLuck = 55 + rarityBalanceLog * rareLuckRatioCoef;

    }
}
