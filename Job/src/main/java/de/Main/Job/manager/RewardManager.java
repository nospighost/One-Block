package de.Main.Job.manager;

import de.Main.database.DBM;
import de.Main.JobPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RewardManager implements Listener {

    DBM dbm;
    public static RewardManager instance;
    public RewardManager(DBM dbm) {
        instance = this;
        this.dbm = dbm;
    }


    public static RewardManager getInstance() {
        return instance;
    }

    public boolean hasUnlockedReward(Player player, int level, String jobName) {

        if (dbm.getInt(JobPlugin.tableName, player.getUniqueId(), jobName + "_LVL", 1) >= level) {
            return true;
        } else {
            return false;
        }


    }

    public String hasClaimedRewardString(Player player, int level, String jobName) {
        if (dbm.getBoolean(JobPlugin.rewardTableName, player.getUniqueId(), jobName + "_LVL_" + level, false)) {
            return "§7Bereits Eingefordert";
        } else {
            if (!hasUnlockedReward(player, level, jobName)) {
                return "§cNicht freigeschaltet";
            }
            return "§aKlicke zum Einfordern";

        }
    }

    public boolean hasClaimedReward(Player player, String jobName, int level){
        return dbm.getBoolean(JobPlugin.rewardTableName, player.getUniqueId(), jobName + "_LVL_" + level, false);
    }


    public void openEditRewardGUI(Player player, String job) {
        switch (job) {
            case "Miner":
                player.openInventory(loadRewardInventory(player, job, true, false));
                break;
            case "Farmer":
                player.openInventory(loadRewardInventory(player, job, true, false));
                break;
            case "Jäger":
                player.openInventory(loadRewardInventory(player, job, true, false));
                break;
            case "Holzfläller":
                player.openInventory(loadRewardInventory(player, job, true, false));
                break;
            case "Gräber":
                player.openInventory(loadRewardInventory(player, job, true, false));
                break;
            case "Fischer":
                player.openInventory(loadRewardInventory(player, job, true, false));
                break;
            case "Builder":
                player.openInventory(loadRewardInventory(player, job, true, false));
                break;
            default:
                player.sendMessage(JobPlugin.prefix + "§cUnbekannter Job!");
                break;
        }
    }


    public Inventory loadRewardInventory(Player player, String title, boolean isEdit, boolean openedByJobGUI) {
        Inventory inv;
        if (isEdit) {
            inv = Bukkit.createInventory(null, 45, title + "-Edit");
        } else {
            inv = Bukkit.createInventory(null, 45, title);
        }
        FileConfiguration config = JobPlugin.getInstance().getRewardFile(title);

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setCustomModelData(1000);
        paperMeta.setHideTooltip(true);
        paper.setItemMeta(paperMeta);

        int[] rewardSlots = {10, 12, 14, 16, 28, 30, 32, 34};
        int[] rewardLevels = {5, 10, 20, 30, 40, 50, 60, 70};

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, paper);
        }

        ItemStack backButton = new ItemStack(Material.PAPER);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setCustomModelData(1058);
        backButtonMeta.setDisplayName("§cZurück");
        backButton.setItemMeta(backButtonMeta);
        inv.setItem(36, backButton);

        for (int j = 0; j < rewardSlots.length; j++) {
            int slot = rewardSlots[j];
            int level = rewardLevels[j];
            ItemStack reward = config.getItemStack("level_" + level);

            if (reward == null) {
                inv.setItem(slot, new ItemStack(Material.AIR));
                continue;
            }


            ItemMeta meta = reward.getItemMeta();

            if (openedByJobGUI) {
                String status = hasClaimedRewardString(player, level, title);
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add("");
                lore.add(status);
                meta.setLore(lore);
            }

            reward.setItemMeta(meta);

            inv.setItem(slot, reward);
        }

        return inv;
    }

    private void saveRewardInventory(String title, InventoryCloseEvent event) {
        if (!JobPlugin.rewardJobs.contains(title.replace("-Edit", ""))) return;

        FileConfiguration config = JobPlugin.getInstance().getRewardFile(title.replace("-Edit", ""));

        int[] rewardSlots = {10, 12, 14, 16, 28, 30, 32, 34};
        int[] rewardLevels = {5, 10, 20, 30, 40, 50, 60, 70};

        for (int j = 0; j < rewardSlots.length; j++) {
            int slot = rewardSlots[j];
            int level = rewardLevels[j];
            ItemStack item = event.getInventory().getItem(slot);

            if (item == null) {
                config.set("level_" + level, null);
                continue;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 1000 && item.getType() == Material.PAPER) {
                config.set("level_" + level, null);
            } else {
                config.set("level_" + level, item);
            }


        }

        JobPlugin.getInstance().saveRewardFile(title.replace("-Edit", ""), config);
    }


    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();

        if (!title.contains("-Edit")) {
            return;
        }
        saveRewardInventory(title, event);
    }

}
