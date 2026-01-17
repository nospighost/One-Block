package org.oneblock.utils;

import de.Main.config.ConfigManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.oneblock.OneBlock;

public class ActionBarManager {

    ConfigManager configManager = ConfigManager.getInstance();
    FileConfiguration config;

    public void sendActionBar(Player player) {
        config = OneBlock.getInstance().getConfig();
        if (config.getBoolean("actionBar.enabled")) {
            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            int wave = userDataConfig.getInt("currentWave");
            int missingBlocks = userDataConfig.getInt("missingBlocks");
            boolean islandCompleted = WaveManager.getInstance().allWavesCompleted(player);
            String actionBarMessage = !islandCompleted ? config.getString("actionBar.message") : config.getString("actionBar.completedMessage");
            if (actionBarMessage != null) {
                actionBarMessage = actionBarMessage.replace("%wave%", String.valueOf(wave));
                actionBarMessage = actionBarMessage.replace("%missingBlocks%", String.valueOf(missingBlocks));
                actionBarMessage = actionBarMessage.replace("%name%", config.getString("waves." + wave + ".name"));
                actionBarMessage = actionBarMessage.replace("%totalBlocks%", String.valueOf(config.getInt("waves." + wave + ".blockAmount")));
                actionBarMessage = actionBarMessage.replace("%bar%", getBar(missingBlocks, config.getInt("waves." + wave + ".blockAmount")));


                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage.replace("&", "§")));
            }
        }
    }

    private String getBar(int missingBlocks, int totalBlocks) {

        int totalBars = 10;

        double progress = 1.0 - ((double) missingBlocks / totalBlocks);
        int filledBars = (int) Math.floor(progress * totalBars);

        String filledChar = "§a|";
        String emptyChar = "§7|";

        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < filledBars; i++) {
            bar.append(filledChar);
        }
        for (int i = filledBars; i < totalBars; i++) {
            bar.append(emptyChar);
        }

        return bar.toString();
    }



}
