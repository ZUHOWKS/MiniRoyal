package fr.zuhowks.miniroyal.lobby;

import fr.zuhowks.miniroyal.MiniRoyal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Lobby {

    private Location pos1;
    private Location pos2;
    private Location spawnLocation;

    private final MiniRoyal INSTANCE = MiniRoyal.getINSTANCE();

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

    public void saveLobbyToFIle() {
        FileConfiguration config = INSTANCE.getFileConfiguration();
        config.set("lobby.pos1", this.getPos1());
        config.set("lobby.pos2", this.getPos2());
        config.set("lobby.spawn", this.getSpawnLocation());
        INSTANCE.saveConfig();
    }
    public void loadLobbyFromFile() {
        FileConfiguration config = INSTANCE.getFileConfiguration();
        this.setPos1((Location) config.get("lobby.pos1"));
        this.setPos2((Location) config.get("lobby.pos2"));
        this.setSpawnLocation((Location) config.get("lobby.spawn"));
    }


}
