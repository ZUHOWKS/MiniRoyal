package fr.zuhowks.miniroyal.map.chests;

import fr.zuhowks.miniroyal.map.items.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

            BlockState state = location.getBlock().getState();

            Chest chest = new Chest(BlockFace.NORTH);

            int randomFace = (new Random()).nextInt(4);

            if (randomFace == 3) {
                chest = new Chest(BlockFace.EAST);
            } else if (randomFace == 2) {
                chest = new Chest(BlockFace.WEST);
            } else if (randomFace == 1) {
                chest = new Chest(BlockFace.SOUTH);
            }

            state.setData(chest);

            state.update();

        }
    }

    public void removeBlockChest() {
        for (Location location : this.chestLocations) {
            location.getBlock().setType(Material.AIR);
        }
    }
}
