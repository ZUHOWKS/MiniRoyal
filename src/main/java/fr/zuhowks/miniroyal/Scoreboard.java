package fr.zuhowks.miniroyal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class Scoreboard {

    private final ScoreboardManager scoreboardManager;
    private final org.bukkit.scoreboard.Scoreboard scoreboard;
    private Objective objective;
    private final List<Score> scores;

    public Scoreboard() {
        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.scores = new ArrayList<>();
        this.objective = scoreboard.registerNewObjective("test", "dummy");
        this.setupScoreboard();
    }

    private void setupScoreboard() {
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "[  " + ChatColor.AQUA + ChatColor.BOLD + "Mini-Royal" + ChatColor.YELLOW + ChatColor.BOLD + "  ]");
    }

    private void clearScore() {
        this.objective.unregister();
        this.objective = scoreboard.registerNewObjective("test", "dummy");
        this.setupScoreboard();
    }

    private void clearScore(String player) {
        this.objective.unregister();
        this.objective = scoreboard.registerNewObjective(player, "dummy");
        this.setupScoreboard();
    }

    public void updateScoreboardLobby(int nbPlayer, int maxPlayer, String mapName, int counter) {
        this.clearScore();
        this.objective.getScore(ChatColor.YELLOW + "--------------------").setScore(10);
        this.objective.getScore("         ").setScore(9);
        this.objective.getScore(ChatColor.YELLOW + "Player number: " + ChatColor.AQUA + nbPlayer + "/" + maxPlayer).setScore(8);
        this.objective.getScore(ChatColor.YELLOW + "       ").setScore(7);
        this.objective.getScore(ChatColor.YELLOW + "Map: " + ChatColor.AQUA + mapName).setScore(6);
        this.objective.getScore(ChatColor.YELLOW + "     ").setScore(5);
        this.objective.getScore(ChatColor.YELLOW + "Party begin in: " + ChatColor.AQUA + Math.max(0, counter) + "s").setScore(4);
        this.objective.getScore("   ").setScore(3);
        this.objective.getScore(ChatColor.YELLOW + "Mode: " + ChatColor.AQUA + "Mini-Royal Solo").setScore(2);
        this.objective.getScore(" ").setScore(1);
    }

    public org.bukkit.scoreboard.Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void updateScoreboardInGame(String player, int nbPlayer, int playerKills, int counter) {
        this.clearScore(player);
        this.objective.getScore(ChatColor.YELLOW + "--------------------").setScore(10);
        this.objective.getScore("         ").setScore(9);
        this.objective.getScore(ChatColor.YELLOW + "Player remaining: " + ChatColor.AQUA + nbPlayer).setScore(8);
        this.objective.getScore(ChatColor.YELLOW + "       ").setScore(7);
        this.objective.getScore(ChatColor.YELLOW + "Kills: " + ChatColor.AQUA + playerKills).setScore(6);
        this.objective.getScore(ChatColor.YELLOW + "     ").setScore(5);
        this.objective.getScore(ChatColor.YELLOW + "Zone moving in: " + ChatColor.AQUA + Math.max(0, counter) + "s").setScore(4);
        this.objective.getScore("   ").setScore(3);
        this.objective.getScore(ChatColor.YELLOW + "Mode: " + ChatColor.AQUA + "Mini-Royal Solo").setScore(2);
        this.objective.getScore(" ").setScore(1);
    }
}
