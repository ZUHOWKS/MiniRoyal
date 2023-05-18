package fr.zuhowks.miniroyal.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lobby {

    private Location pos1;
    private Location pos2;
    private Location spawnLocation;

    public Lobby(Location pos1, Location pos2, Location spawnLocation) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spawnLocation = spawnLocation;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void teleportPlayerToLobby(Player player) {
        player.teleport(spawnLocation);
    }


}
