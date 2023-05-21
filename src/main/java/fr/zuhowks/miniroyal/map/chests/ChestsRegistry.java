package fr.zuhowks.miniroyal.map.chests;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ChestsRegistry {
    private List<Location> chestLocations;

    public ChestsRegistry() {
        this.chestLocations = new ArrayList<>();
    }
    public ChestsRegistry(List<Location> chestLocations) {
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
}
