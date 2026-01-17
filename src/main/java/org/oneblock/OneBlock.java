package org.oneblock;

import co.aikar.commands.PaperCommandManager;
import de.Main.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.oneblock.command.OneBlockCommands;
import org.oneblock.listener.BlockBreakListener;
import org.oneblock.listener.PlayerJoinListener;
import org.oneblock.utils.MessagesManager;
import org.oneblock.utils.WorldManager;

import java.io.File;

public final class OneBlock extends JavaPlugin {


    public static boolean useDataBase = false;
    public static OneBlock instance;
    private File messagesFile;
    private FileConfiguration messages;
    public MessagesManager messagesManager;
    ConfigManager configManager;

    public OneBlock() {
        instance = this;
    }

    public static OneBlock getInstance() {
        if (instance == null) instance = new OneBlock();
        return instance;
    }


    @Override
    public void onEnable() {
        isDataBaseEnabled();

        saveDefaultConfig();

        createConfigs();
        registerListener();

        WorldManager worldManager = new WorldManager();
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);


        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new OneBlockCommands(configManager, commandManager));
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }


    private void isDataBaseEnabled() {
        useDataBase = getConfig().getBoolean("isDataBaseEnabled");
    }


    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onDisable() {
    }


    public FileConfiguration getMessages() {
        return messages;
    }


    public void loadMessageConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }


    private void createConfigs() {
        loadMessageConfig();
    }

}
