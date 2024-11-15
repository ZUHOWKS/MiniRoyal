package fr.zuhowks.miniroyal.map;


import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.map.chests.ChestRegistry;
import fr.zuhowks.miniroyal.map.items.Item;
import fr.zuhowks.miniroyal.map.items.ItemRegistry;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public class MiniRoyalMap {

    private Location pos1;
    private Location pos2;
    private Location finishZoneCenter;
    private int finishZoneRadius;

    private final MiniRoyal INSTANCE = MiniRoyal.getINSTANCE();

    private final ChestRegistry chestRegistry;

    private final ItemRegistry itemRegistry;

    public MiniRoyalMap(Location pos1, Location pos2, Location finishZoneCenter, int finishZoneRadius) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.finishZoneCenter = finishZoneCenter;
        this.finishZoneRadius = finishZoneRadius;
        this.chestRegistry = new ChestRegistry();
        this.itemRegistry = new ItemRegistry();
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public ChestRegistry getChestRegistry() {
        return chestRegistry;
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public Location getFinishZoneCenter() {
        return finishZoneCenter;
    }

    public int getFinishZoneRadius() {
        return finishZoneRadius;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public void setFinishZoneCenter(Location finishZoneCenter) {
        this.finishZoneCenter = finishZoneCenter;
    }

    public void setFinishZoneRadius(int finishZoneRadius) {
        this.finishZoneRadius = finishZoneRadius;
    }

    public void setupMapRegistriesFromFile() {

        FileConfiguration config = INSTANCE.getFileConfiguration();

        this.setPos1((Location) config.get("map.pos1"));
        this.setPos2((Location) config.get("map.pos2"));
        this.setFinishZoneCenter((Location) config.get("map.finish-zone-center"));
        this.getChestRegistry().setChestLocations((List<Location>) config.getList("map.chests"));
        INSTANCE.updateZone();
    }
    public void loadMapFromFile() {
        this.getChestRegistry().removeBlockChest();
        FileConfiguration config = INSTANCE.getFileConfiguration();
        this.setPos1((Location) config.get("map.pos1"));
        this.setPos2((Location) config.get("map.pos2"));
        this.setFinishZoneCenter((Location) config.get("map.finish-zone-center"));
        this.getChestRegistry().setChestLocations((List<Location>) config.getList("map.chests"));
        this.getChestRegistry().setBlockChests();
        INSTANCE.updateZone();
    }

    public void saveMapToFile() {
        this.clearChestInventories();
        this.getChestRegistry().removeBlockChest();
        FileConfiguration config = INSTANCE.getFileConfiguration();
        config.set("map.pos1", this.getPos1());
        config.set("map.pos2", this.getPos2());
        config.set("map.finish-zone-center", this.getFinishZoneCenter());
        config.set("map.chests", this.getChestRegistry().getChestLocations());
        this.getChestRegistry().setBlockChests();
        INSTANCE.saveConfig();
        INSTANCE.updateZone();
    }

    public void setupChestInventories() {
        List<Location> locations = this.chestRegistry.getChestLocations();
        Collections.shuffle(locations);
        for (Location chestLocation : locations) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) chestLocation.getBlock().getState();
            this.itemRegistry.setInventoryItems(chest);
        }
    }

    public void clearChestInventories() {
        for (Location chestLocation : this.chestRegistry.getChestLocations()) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) chestLocation.getBlock().getState();
            chest.getInventory().clear();
        }
    }
}
