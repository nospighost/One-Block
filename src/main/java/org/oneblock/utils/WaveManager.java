package org.oneblock.utils;

import de.Main.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.oneblock.OneBlock;

import java.util.*;

public class WaveManager extends ActionBarManager {

    private static WaveManager instance;
    private final Random random = new Random();

    WaveManager() {

    }

    public static WaveManager getInstance() {
        if (instance == null) {
            instance = new WaveManager();
        }
        return instance;
    }

    public void handleOneBlockBreak(Location location, OfflinePlayer player, BlockBreakEvent event) {

        FileConfiguration userDataConfig = ConfigManager.getInstance().getUserDataConfig(player);
        int wave = userDataConfig.getInt("currentWave");
        if(wave > getMaxWave()){
            userDataConfig.set("currentWave", getMaxWave());
            userDataConfig.set("missingBlocks", OneBlock.getInstance().getConfig().getInt("waves." + getMaxWave() + ".blockAmount"));
            ConfigManager.getInstance().saveUserData(player, userDataConfig);
            wave = getMaxWave();
        }
        List<Material> blocks = getRandomBlocks(wave);
        List<EntityType> mobs = getRandomMobs(wave);
        boolean spawnMob = random.nextInt(100) < OneBlock.getInstance().getConfig().getInt("mobSpawnChance");

        Bukkit.getScheduler().runTaskLater(OneBlock.getPlugin(OneBlock.class), () -> {
            location.getBlock().setType(blocks.get(random.nextInt(blocks.size())));
        }, 1L);


        if (spawnMob) {
            Location mobLocation = location.clone().add(0, 1, 0);
            mobLocation.getWorld().spawnEntity(mobLocation, mobs.get(random.nextInt(mobs.size())));
        }


        int missingBlocks = userDataConfig.getInt("missingBlocks");
        int newMissingBlocks = missingBlocks - 1;

        userDataConfig.set("missingBlocks", Math.max(newMissingBlocks, 0));

        FileConfiguration config = OneBlock.getInstance().getConfig();
        int maxWave = config.getConfigurationSection("waves").getKeys(false).size();

        if (newMissingBlocks <= 0) {

            if (allWavesCompleted(player)) {
                sendActionBar(event.getPlayer());
                return;
            }

            int nextWave = wave + 1;
            if (nextWave <= maxWave) {
                userDataConfig.set("currentWave", nextWave);
                userDataConfig.set("missingBlocks", config.getInt("waves." + nextWave + ".blockAmount"));
            }
        }

        ConfigManager.getInstance().saveUserData(player, userDataConfig);
        sendActionBar(event.getPlayer());
    }

    private int getMaxWave() {
        return OneBlock.getInstance().getConfig().getConfigurationSection("waves").getKeys(false).size();
    }

    public boolean allWavesCompleted(OfflinePlayer player) {

        FileConfiguration userDataConfig = ConfigManager.getInstance().getUserDataConfig(player);
        int currentWave = userDataConfig.getInt("currentWave");
        int missingBlocks = userDataConfig.getInt("missingBlocks");

        FileConfiguration config = OneBlock.getInstance().getConfig();
        ConfigurationSection wavesSection = config.getConfigurationSection("waves");

        if (wavesSection == null) return false;

        int maxWave = wavesSection.getKeys(false).size();

        if (currentWave < maxWave) return false;

        if (missingBlocks > 0) return false;

        if (!userDataConfig.getBoolean("wavesCompleted")) {
            userDataConfig.set("wavesCompleted", true);
        } else {
            userDataConfig.set("wavesCompleted", false);
        }
        ConfigManager.getInstance().saveUserData(player, userDataConfig);

        return true;
    }



    private List<EntityType> getRandomMobs(int wave) {

        String path = "waves." + wave + ".mobs";
        List<String> blockNames = OneBlock.getInstance().getConfig().getStringList(path);

        List<EntityType> mobs = new ArrayList<>();
        for (String name : blockNames) {
            EntityType type = EntityType.valueOf(name);
            if (type != null) {
                mobs.add(type);
            }
        }
        return mobs;
    }

    private List<Material> getRandomBlocks(int wave) {

        String path = "waves." + wave + ".blocks";
        List<String> blockNames = OneBlock.getInstance().getConfig().getStringList(path);

        List<Material> blocks = new ArrayList<>();
        for (String name : blockNames) {
            Material mat = Material.getMaterial(name);
            if (mat != null) {
                blocks.add(mat);
            }
        }
        return blocks;
    }


}
