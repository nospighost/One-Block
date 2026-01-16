package de.Main.Job.GUI.Rewards.holzfäller;

import de.Main.database.DBM;
import de.Main.Job.manager.JobManager;
import de.Main.JobPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class HolzfällerGUI {

    private DBM dbm;
    private JobManager jobManager;
    public static Inventory inv;
    public static String invName = "§bMiner Belohnungen";

    private HolzfällerGUI(JobManager jobManager, DBM dbm) {
        this.jobManager = jobManager;
        this.dbm = dbm;
    }

    public static HolzfällerGUI get() {
        return new HolzfällerGUI(JobPlugin.getInstance().getJobManager(), JobPlugin.getInstance().getDbm());
    }

    public void openGUI(Player player, String title) {
        player.openInventory(JobPlugin.getInstance().getRewardManager().loadRewardInventory(player, title, false, true));
    }




}
