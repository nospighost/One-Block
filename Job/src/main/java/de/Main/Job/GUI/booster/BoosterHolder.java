package de.Main.Job.GUI.booster;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.entity.Player;

public class BoosterHolder implements InventoryHolder {
    private final Player player;

    public BoosterHolder(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
