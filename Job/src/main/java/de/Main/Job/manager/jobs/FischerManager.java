package de.Main.Job.manager.jobs;

import de.Main.Job.manager.ActionBar;
import de.Main.Job.manager.JobManager;
import de.Main.Job.manager.PayoutManager;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class FischerManager implements Listener {
    DBM dbm;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private final HashMap<Player, Map<String, Integer>> totalIncomePerJob = new HashMap<>();
    FileConfiguration config = JobPlugin.getInstance().getConfig();
    private JavaPlugin plugin;
    private Economy eco;
    private JobManager jobManager;
    private BlockBreakManager blockBreakManager;
    private PayoutManager payoutManager;

    public FischerManager(DBM dbm, JavaPlugin plugin, Economy economy, JobManager jobManager, BlockBreakManager blockBreakManager, PayoutManager payoutManager) {
        this.dbm = dbm;
        this.plugin = plugin;
        this.eco = economy;
        this.jobManager = jobManager;
        this.blockBreakManager = blockBreakManager;
        this.payoutManager = payoutManager;
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String jobName = "Fischer";

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        if (!(event.getCaught() instanceof Item)) {
            return;
        }

        if (config.getList("jobBlackList").contains(player.getWorld().getName())) return;


        Item caughtItem = (Item) event.getCaught();
        Material type = caughtItem.getItemStack().getType();

        double currentXP = dbm.getDouble(JobPlugin.tableName, playerUUID, jobName + "_XP", 0);
        int level = dbm.getInt(JobPlugin.tableName, playerUUID, jobName + "_LVL", 1);

        double blockXp = config.getDouble("jobs." + jobName + ".blocks." + type + ".xp", 0);
        double moneyPerBlock = jobManager.getMoneyForMinedBlock(jobName, level);


        double moneyMultiplier = JobPlugin.MONEY.getMultiplier();
        double xpMultiplier = JobPlugin.XP.getMultiplier();

        if (moneyMultiplier == 1) {
            moneyMultiplier = 2;
        }
        if (xpMultiplier == 1) {
            xpMultiplier = 2;
        }
        if (moneyMultiplier != 0) {
            moneyPerBlock = moneyPerBlock * moneyMultiplier;
        }
        if (xpMultiplier != 0) {
            blockXp = blockXp * xpMultiplier;
        }

        if (blockXp > 0) {
            currentXP += blockXp;
            double xpForLevel = jobManager.getXpForLevelUp(level, jobName);

            while (currentXP >= xpForLevel) {
                currentXP -= xpForLevel;
                level++;
                player.sendMessage(JobPlugin.prefix + ChatColor.RED +
                        "Glückwunsch! §aDu bist in dem Job " + "§b" + jobName +
                        " §aaufgestiegen! Nun bist du Level " + level);
                jobManager.userLevelUp(player);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                xpForLevel = jobManager.getXpForLevelUp(level, jobName);
            }

            double xpToNextLevel = xpForLevel - currentXP;
            ActionBar.sendActionBar(decimalFormat, player, jobName, level, blockXp, moneyPerBlock, xpToNextLevel);

            double payoutBefore = dbm.getDouble(JobPlugin.tableName, playerUUID, "payout", 0.0);
            Double payoutAfter = payoutBefore + moneyPerBlock;


            dbm.setDouble(JobPlugin.tableName, playerUUID, "payout", payoutAfter);


            if (!payoutManager.isSchedulerRunning.getOrDefault(player, false)) {
                payoutManager.startPaymentScheduler(player);
            }

            dbm.setDouble(JobPlugin.tableName, playerUUID, jobName + "_XP", currentXP);
            dbm.setDouble(JobPlugin.tableName, playerUUID, jobName + "_LVL", level);
        }
    }


}

