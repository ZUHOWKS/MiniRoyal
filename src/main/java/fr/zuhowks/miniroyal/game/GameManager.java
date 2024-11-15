package fr.zuhowks.miniroyal.game;

import fr.zuhowks.miniroyal.MiniRoyal;
import fr.zuhowks.miniroyal.Scoreboard;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static fr.zuhowks.miniroyal.listener.PlayerListener.fallenSecurity;
import static fr.zuhowks.miniroyal.listener.PlayerListener.getPlannerItemStack;

public class GameManager {

    private final List<UUID> uuidList;
    private final Map<UUID, Integer> playerKills;
    private final Map<UUID, EntityType> playerPlanners;
    private final int gameCapacity;
    private int counter;
    private final Scoreboard scoreboard;
    private boolean gameFinished;
    public GameManager(int gameCapacity) {
        this.uuidList = new ArrayList<>();
        this.playerKills = new HashMap<>();
        this.playerPlanners = new HashMap<>();
        this.gameCapacity = gameCapacity;
        this.counter = 30;
        this.scoreboard = new Scoreboard();
    }

    public List<UUID> getUuidList() {
        return this.uuidList;
    }

    public GameMode joinTheGame(Player player) {

        UUID playerUuid = player.getUniqueId();
        GameStatus gameStatus = this.getGameStatus();

        if (gameStatus == GameStatus.WAITING_ROOM && !this.uuidList.contains(playerUuid)) {
            this.uuidList.add(playerUuid);
            player.getInventory().clear();

            ItemStack cosmeticItem = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta cosmeticItemMeta = cosmeticItem.getItemMeta();
            cosmeticItemMeta.setDisplayName(ChatColor.YELLOW + "Cosmetic");
            cosmeticItem.setItemMeta(cosmeticItemMeta);

            ItemStack leaveItem = new ItemStack(Material.BED);
            ItemMeta leaveItemMeta = leaveItem.getItemMeta();
            leaveItemMeta.setDisplayName(ChatColor.YELLOW + "Leave");
            leaveItem.setItemMeta(leaveItemMeta);

            player.getInventory().setItem(0, cosmeticItem);
            player.getInventory().setItem(8, leaveItem);

            this.setPlayerPlanner(playerUuid, EntityType.CHICKEN);

            return GameMode.ADVENTURE;

        } else if (gameStatus != GameStatus.GAME_NOT_SETUP && !this.uuidList.contains(playerUuid)) {
            player.getInventory().clear();
            return GameMode.SPECTATOR;

        } else {
            return player.getGameMode();
        }
    }

    public void setPlayerPlanner(UUID uuid, EntityType entityType) {
        this.playerPlanners.put(uuid, entityType);
    }

    public EntityType getPlayerPlanner(UUID uuid) {
        return this.playerPlanners.get(uuid);
    }

    public void leaveTheGame(UUID uuid) {
        this.uuidList.remove(uuid);
    }

    public int getGameCapacity() {
        return this.gameCapacity;
    }

    public boolean canLaunchGameCounter() {
        GameStatus gameStatus = this.getGameStatus();
        return gameStatus == GameStatus.WAITING_ROOM_FULL || (gameStatus == GameStatus.WAITING_ROOM && this.uuidList.size() >= Math.max(1, this.gameCapacity - 10));
    }

    public GameStatus getGameStatus() {
        if (MiniRoyal.getINSTANCE().partyIsSetup() && !MiniRoyal.getINSTANCE().isInGame()) {
            return this.uuidList.size() < this.gameCapacity ? GameStatus.WAITING_ROOM : GameStatus.WAITING_ROOM_FULL;
        } else if (MiniRoyal.getINSTANCE().isInGame() && !this.gameFinished) {
            return GameStatus.IN_GAME;
        } else if (this.gameFinished) {
            return GameStatus.FINISH;
        } else {
            return GameStatus.GAME_NOT_SETUP;
        }
    }

    public void startGame() {
        MiniRoyal.getINSTANCE().getMiniRoyalMap().getChestRegistry().setBlockChests();
        MiniRoyal.getINSTANCE().getMiniRoyalMap().setupChestInventories();
        MiniRoyal.getINSTANCE().setInGame(true);
    }

    public void finishGame() {
        this.gameFinished = true;
    }

    public void launchPlayer() {
        for (UUID uuid : this.uuidList) {
            Player p = Bukkit.getPlayer(uuid);
            Location location = MiniRoyal.getINSTANCE().getMiniRoyalMap().getFinishZoneCenter();
            location.setX(location.getX() + Math.random() * 50 -  Math.random() * 50);
            location.setY(250);
            location.setZ(location.getZ() + Math.random() * 50 -  Math.random() * 50);
            p.teleport(location);
            p.playSound(location, Sound.BLOCK_ANVIL_LAND, 1.15f, 2f);
            p.getInventory().clear();
            p.getInventory().setItem(8, getPlannerItemStack());
            fallenSecurity(p);
        }

        this.resetCounter(120);
    }

    public void sendMessage(String message, float pitch) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.15f, pitch);
        }
    }

    public void sendMessage(String message, Sound sound, float pitch) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
            p.playSound(p.getLocation(), sound, 1.15f, pitch);
        }
    }

    public void deleteSecond(int second) {
        this.counter -= second;
    }

    public int getCounter() {
        return this.counter;
    }

    public void resetCounter() {
        this.counter = 30;
    }
    public void resetCounter(int resetTo) {
        this.counter = resetTo;
    }

    public void updateLobbyScoreboard() {
        this.scoreboard.updateScoreboardLobby(this.uuidList.size(), gameCapacity, "Japanese Temple", this.counter);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(this.scoreboard.getScoreboard());
        }
    }

    public Integer getPlayerKills(UUID uuid) {
        return this.playerKills.get(uuid);
    }

    public void addKillToPlayer(UUID uuid) {
        if (this.uuidList.contains(uuid)) {
            Integer kills = this.getPlayerKills(uuid);
            this.playerKills.put(uuid, kills == null ? 1 : kills + 1);
        }
    }

    public void updateInGameScoreboard() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            Integer kills = this.getPlayerKills(p.getUniqueId());
            this.scoreboard.updateScoreboardInGame(p.getName(), this.uuidList.size(), kills == null ? 0 : kills, this.counter);
            p.setScoreboard(this.scoreboard.getScoreboard());
        }

    }
}
