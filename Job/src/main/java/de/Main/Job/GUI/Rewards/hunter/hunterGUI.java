package de.Main.Job.GUI.Rewards.hunter;

import de.Main.JobPlugin;
import org.bukkit.entity.Player;


public class hunterGUI {

    private hunterGUI() {

    }

    public static hunterGUI get() {
        return new hunterGUI();
    }

    public void openGUI(Player player, String title) {
        player.openInventory(JobPlugin.getInstance().getRewardManager().loadRewardInventory(player, title, false, true));
    }




}
