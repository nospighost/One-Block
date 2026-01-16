package de.Main.Job.GUI.Rewards.gräber;

import de.Main.JobPlugin;
import org.bukkit.entity.Player;


public class gräberGUI {

    private gräberGUI() {

    }

    public static gräberGUI get() {
        return new gräberGUI();
    }

    public void openGUI(Player player, String title) {
        player.openInventory(JobPlugin.getInstance().getRewardManager().loadRewardInventory(player, title, false, true));
    }




}
