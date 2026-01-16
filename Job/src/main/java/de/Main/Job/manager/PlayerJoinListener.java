package de.Main.Job.manager;

import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    DBM dbm;
    public PlayerJoinListener(DBM dbm) {
        this.dbm = dbm;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HashMap<String, Object> defaultValues = new HashMap<>();
        defaultValues.put("owner", player.getName());
        defaultValues.put("owner_uuid", player.getUniqueId().toString());
        defaultValues.put("Miner_LVL", 1);
        defaultValues.put("Miner_XP", 0.0);
        defaultValues.put("Gräber_LVL", 1);
        defaultValues.put("Gräber_XP", 0.0);
        defaultValues.put("Fischer_LVL", 1);
        defaultValues.put("Fischer_XP", 0.0);
        defaultValues.put("Holzfäller_LVL", 1);
        defaultValues.put("Holzfäller_XP", 0.0);
        defaultValues.put("Jäger_LVL", 1);
        defaultValues.put("Jäger_XP", 0.0);
        defaultValues.put("Farmer_LVL", 1);
        defaultValues.put("Farmer_XP", 0.0);
        defaultValues.put("Builder_LVL", 0.0);
        defaultValues.put("Builder_XP", 0.0);
        defaultValues.put("Gourmet_XP", 0.0);
        defaultValues.put("Gourmet_LVL", 1);
        defaultValues.put("payout", 0.0);


        String playerName = player.getName();
        UUID playerUUID = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(JobPlugin.getPlugin(JobPlugin.class), c -> {
            HashMap<String, Object> rewardDefaultValues = new HashMap<>();
            for (String name : JobPlugin.rewardJobs) {
                rewardDefaultValues.put("owner", playerName);
                rewardDefaultValues.put("owner_uuid", playerUUID.toString());
                rewardDefaultValues.put(name + "_LVL_5", false);
                rewardDefaultValues.put(name + "_LVL_10", false);
                rewardDefaultValues.put(name + "_LVL_20", false);
                rewardDefaultValues.put(name + "_LVL_30", false);
                rewardDefaultValues.put(name + "_LVL_40", false);
                rewardDefaultValues.put(name + "_LVL_50", false);
                rewardDefaultValues.put(name + "_LVL_60", false);
                rewardDefaultValues.put(name + "_LVL_70", false);
                rewardDefaultValues.put(name + "_LVL_80", false);
                rewardDefaultValues.put(name + "_LVL_90", false);
                rewardDefaultValues.put(name + "_LVL_100", false);
            }

            dbm.insertDefaultValues(JobPlugin.rewardTableName, player.getUniqueId(), rewardDefaultValues);
            dbm.insertDefaultValues(JobPlugin.tableName, player.getUniqueId(), defaultValues);
        });

    }

}
