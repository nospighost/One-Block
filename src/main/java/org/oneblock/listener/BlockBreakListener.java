package org.oneblock.listener;

import de.Main.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.oneblock.utils.OneBlockManager;

import java.util.UUID;

public class BlockBreakListener implements Listener {


    ConfigManager configManager = ConfigManager.getInstance();
    OneBlockManager obManager = OneBlockManager.getInstance();

    public BlockBreakListener() {

    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (!obManager.isTrusted(event.getPlayer(), event.getPlayer().getWorld().getName())) {
            if (!obManager.isAdded(event.getPlayer(), event.getPlayer().getWorld().getName()) && !obManager.isOwnerOnline(event.getPlayer())) {
                if(!obManager.isOwner(event.getPlayer())){
                    event.setCancelled(true);
                }
            }
        }


        if (obManager.hasIsland(event.getPlayer())) {
            if (obManager.isOneBlock(event.getBlock().getLocation(), event.getPlayer())) {
                if (event.isCancelled()) {
                    return;
                }
                UUID uuid = UUID.fromString(event.getBlock().getLocation().getWorld().getName().replace("ob_", ""));
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                obManager.handleOneBlockBreak(event.getBlock().getLocation(), player, event);
            }
        }
    }
}




