package org.oneblock.utils;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MessagesManager {

    public static MessagesManager instance;
    private final JavaPlugin plugin;
    private FileConfiguration messages;
    private File messagesFile;

    public MessagesManager(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        init();
    }

    private void init() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

     //  if (!messagesFile.exists()) {
     //      plugin.saveResource("messages.yml", false);
     //  }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path, OfflinePlayer player) {
        String msg = messages.getString(path, "§cMessage fehlt: " + path);

        msg = msg.replace("<prefix>", messages.getString("prefix", "")).replace("%player%", player.getName());

        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
