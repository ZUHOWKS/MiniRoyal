package fr.zuhowks.miniroyal.map.chests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ChestRegistry {
    private List<Location> chestLocations;

    public ChestRegistry() {
        this.chestLocations = new ArrayList<>();
    }
    public ChestRegistry(List<Location> chestLocations) {
        this.chestLocations = chestLocations;
    }

    public boolean addChest(Location chestLocation) {
        return this.chestLocations.add(chestLocation);
    }

    public boolean removeChest(Location chestLocation) {
        return this.chestLocations.remove(chestLocation);
    }

    public List<Location> getChestLocations() {
        return chestLocations;
    }

    public void setChestLocations(List<Location> chestLocations) {
        this.chestLocations = chestLocations;
    }

    public void setBlockChests() {
        for (Location location : this.chestLocations) {
            location.getBlock().setType(Material.CHEST);
        }
    }

    public void removeBlockChest() {
        for (Location location : this.chestLocations) {
            location.getBlock().setType(Material.AIR);
        }
    }
}
