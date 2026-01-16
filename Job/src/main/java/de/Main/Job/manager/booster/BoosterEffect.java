package de.Main.Job.manager.booster;

import org.bukkit.entity.Player;

public class BoosterEffect {

    private Player player;

    private int level;

    public BoosterEffect(Player player, int level) {
        this.level = level;
        this.player = player;
    }

    public int getLevel() {
        return level;
    }

    public Player getPlayer() {
        return player;
    }
}