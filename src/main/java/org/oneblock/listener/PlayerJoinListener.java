package org.oneblock.listener;

import de.Main.config.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.oneblock.OneBlock;
import org.oneblock.utils.OneBlockManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinListener implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ConfigManager configManager = ConfigManager.getInstance();
        configManager.createUserData(event.getPlayer());
        OneBlockManager obManager = OneBlockManager.getInstance();
        FileConfiguration config = configManager.getUserDataConfig(event.getPlayer());

        if(config.get("owner_name") == null) {
            config.set("owner_name", event.getPlayer().getName());
        }
        if (config.get("owner_uuid") == null) {
            config.set("owner_uuid", event.getPlayer().getUniqueId().toString());
        }




        if (!obManager.hasIsland(event.getPlayer())) {
            List<String> empty = new ArrayList<>();
            config.set("invitedAdd", empty);
            config.set("invitedTrust", empty);
            config.set("trust", empty);
            config.set("add", empty);
            config.set("denied", empty);
            config.set("obPosition.x", 0);
            config.set("obPosition.y", 0);
            config.set("obPosition.z", 0);
            config.set("obPosition.world", "null");
            config.set("obSettings.visit.allowed", true);
            config.set("hasIsland", false);
            config.set("wavesCompleted", false);
            config.set("missingBlocks", OneBlock.getInstance().getConfig().getInt("waves.1.blockAmount"));
            config.set("currentWave", 1);
        }



        configManager.saveUserData(event.getPlayer(), config);



        if (obManager.hasIsland(event.getPlayer())) {
            obManager.joinIsland(event.getPlayer(), event.getPlayer().getName());
        }

    }
}
