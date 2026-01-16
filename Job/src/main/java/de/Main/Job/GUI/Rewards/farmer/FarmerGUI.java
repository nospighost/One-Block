package de.Main.Job.GUI.Rewards.farmer;

import de.Main.JobPlugin;
import org.bukkit.entity.Player;


public class FarmerGUI {

    private FarmerGUI() {

    }

    public static FarmerGUI get() {
        return new FarmerGUI();
    }

    public void openGUI(Player player, String title) {
        player.openInventory(JobPlugin.getInstance().getRewardManager().loadRewardInventory(player, title, false, true));
    }




}
