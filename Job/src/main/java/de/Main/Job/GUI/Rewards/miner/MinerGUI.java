package de.Main.Job.GUI.Rewards.miner;

import de.Main.JobPlugin;
import org.bukkit.entity.Player;


public class MinerGUI {

    private MinerGUI() {

    }

    public static MinerGUI get() {
        return new MinerGUI();
    }

    public void openGUI(Player player, String title) {
        player.openInventory(JobPlugin.getInstance().getRewardManager().loadRewardInventory(player, title, false, true));
    }




}
