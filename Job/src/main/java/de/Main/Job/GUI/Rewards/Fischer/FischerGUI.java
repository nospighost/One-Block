package de.Main.Job.GUI.Rewards.Fischer;

import de.Main.JobPlugin;
import org.bukkit.entity.Player;


public class FischerGUI {

    private FischerGUI() {

    }

    public static FischerGUI get() {
        return new FischerGUI();
    }

    public void openGUI(Player player, String title) {
        player.openInventory(JobPlugin.getInstance().getRewardManager().loadRewardInventory(player, title, false, true));
    }




}
