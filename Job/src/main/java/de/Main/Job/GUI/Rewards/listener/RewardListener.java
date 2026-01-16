package de.Main.Job.GUI.Rewards.listener;

import de.Main.database.DBM;
import de.Main.Job.manager.RewardManager;
import de.Main.JobPlugin;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RewardListener implements Listener {

    private final DBM dbm;
    private RewardManager rewardManager;

    public RewardListener(DBM dbm) {
        this.dbm = dbm;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        int slot = event.getSlot();

        if (!JobPlugin.rewardJobs.contains(title)) {
            return;
        }

        event.setCancelled(true);
        rewardManager = JobPlugin.getInstance().getRewardManager();
        FileConfiguration config = JobPlugin.getInstance().getRewardFile(title);

        switch (slot) {
            case 10 -> handleRewardClick(player, config, 5, title);
            case 12 -> handleRewardClick(player, config, 10, title);
            case 14 -> handleRewardClick(player, config, 20, title);
            case 16 -> handleRewardClick(player, config, 30, title);
            case 28 -> handleRewardClick(player, config, 40, title);
            case 30 -> handleRewardClick(player, config, 50, title);
            case 32 -> handleRewardClick(player, config, 60, title);
            case 34 -> handleRewardClick(player, config, 70, title);
            case 36 -> player.openInventory(JobPlugin.getInstance().getJobGUI().createJobGUI(player));
        }
        if(slot == 36) return;
        player.openInventory(rewardManager.loadRewardInventory(player, title, false, true));
    }

    private void handleRewardClick(Player player, FileConfiguration config, int level, String jobName) {

        if (rewardManager.hasClaimedReward(player, jobName, level)) {
            player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1, 1);
            player.sendMessage("§7Diese Belohnung hast du bereits eingefordert!");
            return;
        }

        if (!rewardManager.hasUnlockedReward(player, level, jobName)) {
            player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1, 1);
            player.sendMessage("§cDiese Belohnung ist noch nicht freigeschaltet!");
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cDein Inventar ist voll!");
            return;
        }

        ItemStack reward = config.getItemStack("level_" + level);
        if (reward != null) {
            player.getInventory().addItem(reward);
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            player.sendMessage("§aDu hast deine Level-" + level + "-Belohnung für " + jobName + " erhalten!");
            dbm.setBoolean(JobPlugin.rewardTableName, player.getUniqueId(), jobName + "_LVL_" + level, true);
        } else {
            player.sendMessage("§cFehler beim Laden der Belohnung für Level " + level + "!");
            player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1, 1);
        }
    }
}
