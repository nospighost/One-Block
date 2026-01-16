package de.Main.Job.manager.jobs;


import de.Main.Job.manager.JobManager;
import de.Main.Job.manager.PayoutManager;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class JägerManager implements Listener {
    DBM dbm;

    private final HashMap<Player, Map<String, Integer>> totalIncomePerJob = new HashMap<>();
    FileConfiguration config = JobPlugin.getInstance().getConfig();
    private JavaPlugin plugin;
    private Economy eco;
    private JobManager jobManager;
    PayoutManager payoutManager;

    public JägerManager(DBM dbm, JavaPlugin plugin, Economy economy, JobManager jobManager, PayoutManager payoutManager) {
        this.dbm = dbm;
        this.plugin = plugin;
        this.eco = economy;
        this.jobManager = jobManager;
        this.payoutManager = payoutManager;
    }


    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (!(event.getEntity().getKiller() instanceof Player)) return;
        EntityType entityType = event.getEntityType();
        if (entityType == EntityType.PLAYER) return;
        String jobName = jobManager.getJobForEntity(entityType);
        if (config.getList("jobBlackList").contains(player.getWorld().getName())) return;
        if (jobName == null) return;

        jobManager.handleHunterAction(player, event);

    }

}