package de.Main.Job.manager.jobs;

import de.Main.Job.manager.JobManager;
import de.Main.Job.manager.PayoutManager;
import de.Main.JobPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EatManager implements Listener {

    private JavaPlugin plugin;
    private JobManager jobManager;
    PayoutManager payoutManager;
    FileConfiguration config = JobPlugin.getInstance().getConfig();
    public EatManager(JavaPlugin plugin, JobManager jobManager, PayoutManager payoutManager) {
        this.plugin = plugin;
        this.jobManager = jobManager;
        this.payoutManager = payoutManager;
    }


    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getItem() == null){
            return;
        }
        if(event.getEntity() instanceof Player player){
            String jobName = jobManager.getJobForFood(event.getItem().getType());
            if(jobName == null) return;

            if (config.getList("jobBlackList").contains(player.getWorld().getName())) {
                return;
            }

            jobManager.handleFoodAction(player, event);

        } else {
            return;
        }

    }


}
