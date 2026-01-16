package de.Main.Job.manager.jobs;


import de.Main.Job.manager.JobManager;
import de.Main.Job.manager.PayoutManager;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;


public class FarmerManager implements Listener {
    DBM dbm;

    private final HashMap<Player, Map<String, Integer>> totalIncomePerJob = new HashMap<>();
    FileConfiguration config = JobPlugin.getInstance().getConfig();
    private JavaPlugin plugin;
    private Economy eco;
    private JobManager jobManager;
    PayoutManager payoutManager;
    public FarmerManager(DBM dbm, JavaPlugin plugin, Economy economy, JobManager jobManager, PayoutManager payoutManager) {
        this.dbm = dbm;
        this.plugin = plugin;
        this.eco = economy;
        this.jobManager = jobManager;
        this.payoutManager = payoutManager;
    }



    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Material minedBlockMaterial = event.getBlock().getType();
        Material blockType = minedBlockMaterial;
        String jobName = jobManager.getJobForBlock(minedBlockMaterial);


        if(jobName == null){return;}

        if (!jobName.equalsIgnoreCase("Farmer")) return;



        Material type = event.getBlock().getType();
        BlockData data = event.getBlock().getBlockData();

        switch (type) {
            case WHEAT -> {
                if (!(data instanceof Ageable ageable) || ageable.getAge() != 7) return;
            }
            case CARROTS -> {
                if (!(data instanceof  Ageable ageable) || ageable.getAge() != 7) return;
            }
            case POTATOES -> {
                if (!(data instanceof  Ageable ageable) || ageable.getAge() != 7) return;
            }
            case BEETROOTS -> {
                if (!(data instanceof Ageable ageable) || ageable.getAge() != 3) return;
            }
            default -> {
                return;
            }
        }


        jobManager.handleJobAction(player, event, jobManager);



    }


    public int gettotalincome(Player player, String job) {
        Map<String, Integer> jobIncome = totalIncomePerJob.get(player);
        if (jobIncome == null) return 0;
        return jobIncome.getOrDefault(job, 0);
    }

}