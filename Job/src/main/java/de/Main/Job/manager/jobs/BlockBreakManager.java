package de.Main.Job.manager.jobs;


import de.Main.Job.manager.BlockIdentifier;
import de.Main.Job.manager.JobManager;
import de.Main.Job.manager.PayoutManager;
import de.Main.JobPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


public class BlockBreakManager implements Listener {

    FileConfiguration config = JobPlugin.getInstance().getConfig();
    private JavaPlugin plugin;
    private JobManager jobManager;
    PayoutManager payoutManager;

    public BlockBreakManager(JavaPlugin plugin, JobManager jobManager, PayoutManager payoutManager) {
        this.plugin = plugin;
        this.jobManager = jobManager;
        this.payoutManager = payoutManager;
    }


    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material minedBlockMaterial = event.getBlock().getType();
        String jobName = jobManager.getJobForBlock(minedBlockMaterial);

        if (config.getList("jobBlackList").contains(player.getWorld().getName())) {
            return;
        }
        if (jobName == null) return;

        if (event.getBlock().hasMetadata(BlockIdentifier.PLACED_METADATA_KEY)) {
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemMeta meta = hand.getItemMeta();
        if (meta != null) {
            NamespacedKey Bohrerkey = new NamespacedKey("customenchants", "bohrer");
            NamespacedKey TimberKey = new NamespacedKey("customenchants", "woodcutter");
            if (meta.getPersistentDataContainer().has(Bohrerkey, PersistentDataType.INTEGER)) return;
            if (meta.getPersistentDataContainer().has(TimberKey, PersistentDataType.INTEGER)) return;
        }
        jobManager.handleJobAction(player, event, jobManager);


    }



}