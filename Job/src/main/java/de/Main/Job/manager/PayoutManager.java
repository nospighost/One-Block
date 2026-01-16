package de.Main.Job.manager;

import de.Main.JobPlugin;
import de.Main.database.DBM;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PayoutManager {

    public final Map<Player, Boolean> isSchedulerRunning = new HashMap<>();
    DBM dbm;
    private Economy eco;
    private JavaPlugin plugin;

    public PayoutManager(DBM dbm, JavaPlugin plugin, Economy eco) {
    this.dbm = dbm;
    this.plugin = plugin;
    this.eco = eco;
    }

    public void startPaymentScheduler(Player player) {
        isSchedulerRunning.put(player, true);


        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            double payoutMoney = dbm.getDouble(JobPlugin.tableName, player.getUniqueId(), "payout", 0.0);

            if (dbm.getDouble(JobPlugin.tableName, player.getUniqueId(), "payout", 0.0) < 0.0) return;

            dbm.setDouble(JobPlugin.tableName, player.getUniqueId(), "payout", 0.0);

            eco.depositPlayer(player, payoutMoney);
            player.sendMessage(JobPlugin.prefix + "§7Du hast deinen Lohn von §b" +  JobManager.decimalFormat.format(payoutMoney) + "$ §7Erhalten!");
            isSchedulerRunning.put(player, false);
        }, 1200L);


    }

}
