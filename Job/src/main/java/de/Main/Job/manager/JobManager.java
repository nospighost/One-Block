package de.Main.Job.manager;

import de.Main.Job.GUI.maingui.JobGUI;
import de.Main.Job.manager.booster.BoosterManager;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JobManager {

    DBM dbm;
    FileConfiguration config;
    PayoutManager payoutManager;
    public static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    BoosterManager boosterManager;


    public JobManager(DBM dbm, PayoutManager payoutManager, BoosterManager boosterManager) {
        this.dbm = dbm;
        this.payoutManager = payoutManager;
        this.boosterManager = boosterManager;
        this.config = JobPlugin.getInstance().getConfig();
        startSaveTask();
    }

    private final Map<UUID, Map<String, JobData>> playerJobCache = new ConcurrentHashMap<>();

    private static class JobData {
        double xp;
        int level;
        boolean modified;

        JobData(double xp, int level) {
            this.xp = xp;
            this.level = level;
            this.modified = false;
        }
    }

    private void startSaveTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(JobPlugin.getInstance(), () -> {
            for (Map.Entry<UUID, Map<String, JobData>> entry : playerJobCache.entrySet()) {
                UUID uuid = entry.getKey();
                for (Map.Entry<String, JobData> jobEntry : entry.getValue().entrySet()) {
                    JobData data = jobEntry.getValue();
                    if (data.modified) {
                        String jobName = jobEntry.getKey();
                        dbm.setDouble(JobPlugin.tableName, uuid, jobName + "_XP", data.xp);
                        dbm.setDouble(JobPlugin.tableName, uuid, jobName + "_LVL", data.level);
                        data.modified = false;
                    }
                }
            }
        }, 20 * 30L, 20 * 30L);
    }

    private JobData getJobData(UUID uuid, String jobName) {
        playerJobCache.putIfAbsent(uuid, new HashMap<>());
        Map<String, JobData> jobs = playerJobCache.get(uuid);

        if (!jobs.containsKey(jobName)) {
            double xp = dbm.getDouble(JobPlugin.tableName, uuid, jobName + "_XP", 0);
            int level = dbm.getInt(JobPlugin.tableName, uuid, jobName + "_LVL", 1);
            jobs.put(jobName, new JobData(xp, level));
        }
        return jobs.get(jobName);
    }


    public double getMoneyForMinedBlock(String jobName, int level) {
        double levelMultiPlier;

        switch (jobName) {
            case "Miner":
                levelMultiPlier = 0.15;
                return level * levelMultiPlier;

            case "Gräber":
                levelMultiPlier = 0.10;
                return level * levelMultiPlier;

            case "Holzfäller":
                return calculateWoodcutterMoney(level);

            case "Farmer":
                levelMultiPlier = 0.03;
                return level * levelMultiPlier;

            case "Fischer":
                levelMultiPlier = 10.1;
                return level * levelMultiPlier;

            case "Jäger":
                levelMultiPlier = 0.06;
                return level * levelMultiPlier;

            case "Gourmet":

                levelMultiPlier = 0.05;
                return level * levelMultiPlier;
            default:
                return 0;
        }

    }


    private double calculateWoodcutterMoney(int level) {
        double finalValue;
        double baseValue = 0.5;
        double underLevel30Multiplier = 0.18;
        double overLevel30Multiplier = 0.12;

        if (level <= 30) {
            finalValue = baseValue + (level * underLevel30Multiplier);
        } else {
            finalValue = baseValue + (30 * underLevel30Multiplier) + ((level - 30) * overLevel30Multiplier);
        }


        return finalValue;
    }


    public double getXpForLevelUp(double level, String job) {
        double finalXP = 0.0;
        if(job.equals("Gourmet")){
            if(level >= 20){
                finalXP = 50000;
            } else {
                finalXP = level * 2500;
            }
            return finalXP;
        }
        if (level <= 18) {
            finalXP = level * 1322;
        } else if (level >= 19) {
            finalXP = level * 5432;
        }

        if (level >= 37 && level <= 45) {
            finalXP = 1750000;
        } else if(level > 45){
            finalXP = 1750000 + (level * 1000);
        }
        return finalXP;
    }

    public String getJobForBlock(Material blockType) {
        FileConfiguration config = JobPlugin.getInstance().getConfig();
        ConfigurationSection jobsSection = config.getConfigurationSection("jobs");
        if (jobsSection == null) return null;

        for (String job : jobsSection.getKeys(false)) {
            ConfigurationSection blocksSection = jobsSection.getConfigurationSection(job + ".blocks");
            if (blocksSection != null && blocksSection.contains(blockType.name())) {
                return job;
            }
        }
        return null;
    }


    public String getJobForEntity(EntityType entityType) {
        FileConfiguration config = JobPlugin.getInstance().getConfig();
        for (String job : config.getConfigurationSection("jobs").getKeys(false)) {
            if (config.contains("jobs." + job + ".blocks." + entityType.name())) return job;
        }
        return null;
    }

    public String getJobForFood(Material type) {
        FileConfiguration config = JobPlugin.getInstance().getConfig();
        for (String job : config.getConfigurationSection("jobs").getKeys(false)) {
            if (config.contains("jobs." + job + ".blocks." + type)) return job;
        }
        return null;
    }

    public void userLevelUp(Player player) {
        JobGUI jobGUI = new JobGUI(dbm, JobPlugin.getInstance().getJobManager());
        jobGUI.createJobGUI(player);
    }

    public void addJobAction(Player player, int count, Block block) {

        Material minedBlockMaterial = block.getType();
        String jobName = getJobForBlock(minedBlockMaterial);
        if (jobName == null) return;

        if (block.hasMetadata(BlockIdentifier.PLACED_METADATA_KEY)) return;


        UUID playerUUID = player.getUniqueId();

        Material blockType = minedBlockMaterial;

        JobData jobData = getJobData(playerUUID, jobName);

        double currentXP = jobData.xp;
        int level = jobData.level;

        double tempBlockXp = config.getDouble("jobs." + jobName + ".blocks." + blockType.name() + ".xp", 0);
        double tempMoneyPerBlock = getMoneyForMinedBlock(jobName, level);


        double moneyMultiplier = JobPlugin.MONEY.getMultiplier();
        double xpMultiplier = JobPlugin.XP.getMultiplier();

        if (moneyMultiplier == 1) {
            moneyMultiplier = 2;
        }
        if (xpMultiplier == 1) {
            xpMultiplier = 2;
        }
        if (moneyMultiplier != 0) {
            tempMoneyPerBlock = tempMoneyPerBlock * moneyMultiplier;
        }
        if (xpMultiplier != 0) {
            tempBlockXp = tempBlockXp * xpMultiplier;
        }

        double blockXp = tempBlockXp * count;
        double moneyPerBlock = tempMoneyPerBlock * count;

        if (blockXp > 0) {

            currentXP += blockXp;
            double xpForLevel = getXpForLevelUp(level, jobName);

            while (currentXP >= xpForLevel) {
                currentXP -= xpForLevel;
                level++;
                player.sendMessage(JobPlugin.prefix + ChatColor.RED + "Glückwunsch! §aDu bist in dem Job " + "§b " + jobName + " §a ein Level aufgestiegen! Nun bist du Level " + level);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                xpForLevel = getXpForLevelUp(level, jobName);
                userLevelUp(player);
            }
            double xpToNextLevel = xpForLevel - currentXP;
            ActionBar.sendActionBar(JobManager.decimalFormat, player, jobName, level, blockXp, moneyPerBlock, xpToNextLevel);


            double payoutBefore = dbm.getDouble(JobPlugin.tableName, playerUUID, "payout", 0.0);
            Double payoutAfter = payoutBefore + moneyPerBlock;


            dbm.setDouble(JobPlugin.tableName, playerUUID, "payout", payoutAfter);


            if (!payoutManager.isSchedulerRunning.getOrDefault(player, false)) {
                payoutManager.startPaymentScheduler(player);
            }

            jobData.xp = currentXP;
            jobData.level = level;
            jobData.modified = true;

        }
    }

    public void handleHunterAction(Player player, EntityDeathEvent event) {
        EntityType entityType = event.getEntityType();
        String jobName = getJobForEntity(entityType);
        if (jobName == null) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        JobData jobData = getJobData(playerUUID, jobName);

        double currentXP = jobData.xp;
        int level = jobData.level;

        double blockXp = config.getDouble("jobs." + jobName + ".blocks." + entityType.name() + ".xp", 0);
        double moneyPerBlock = getMoneyForMinedBlock(jobName, level);


        double moneyMultiplier = JobPlugin.MONEY.getMultiplier();
        double xpMultiplier = JobPlugin.XP.getMultiplier();

        if (moneyMultiplier == 1) {
            moneyMultiplier = 2;
        }
        if (xpMultiplier == 1) {
            xpMultiplier = 2;
        }
        if (moneyMultiplier != 0) {
            moneyPerBlock = moneyPerBlock * moneyMultiplier;
        }
        if (xpMultiplier != 0) {
            blockXp = blockXp * xpMultiplier;
        }

        if (blockXp > 0) {

            currentXP += blockXp;
            double xpForLevel = getXpForLevelUp(level, jobName);

            while (currentXP >= xpForLevel) {
                currentXP -= xpForLevel;
                level++;
                player.sendMessage(JobPlugin.prefix + ChatColor.RED + "Glückwunsch! §aDu bist in dem Job " + "§b " + jobName + " §a ein Level aufgestiegen! Nun bist du Level " + level);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                xpForLevel = getXpForLevelUp(level, jobName);
                userLevelUp(player);

            }
            double xpToNextLevel = xpForLevel - currentXP;
            ActionBar.sendActionBar(decimalFormat, player, jobName, level, blockXp, moneyPerBlock, xpToNextLevel);


            double payoutBefore = dbm.getDouble(JobPlugin.tableName, playerUUID, "payout", 0.0);
            Double payoutAfter = payoutBefore + moneyPerBlock;


            dbm.setDouble(JobPlugin.tableName, playerUUID, "payout", payoutAfter);


            if (!payoutManager.isSchedulerRunning.getOrDefault(player, false)) {
                payoutManager.startPaymentScheduler(player);
            }

            jobData.xp = currentXP;
            jobData.level = level;
            jobData.modified = true;
        }
    }
    public void handleFoodAction(Player player, FoodLevelChangeEvent event) {
        String jobName = getJobForFood(event.getItem().getType());
        if (jobName == null) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        JobData jobData = getJobData(playerUUID, jobName);

        double currentXP = jobData.xp;
        int level = jobData.level;

        Material item = event.getItem().getType();
        if(item == null){
            return;
        }
        double blockXp = config.getDouble("jobs." + jobName + ".blocks." + item  + ".xp", 0);
        double moneyPerBlock = getMoneyForMinedBlock(jobName, level);


        double moneyMultiplier = JobPlugin.MONEY.getMultiplier();
        double xpMultiplier = JobPlugin.XP.getMultiplier();

        if (moneyMultiplier == 1) {
            moneyMultiplier = 2;
        }
        if (xpMultiplier == 1) {
            xpMultiplier = 2;
        }
        if (moneyMultiplier != 0) {
            moneyPerBlock = moneyPerBlock * moneyMultiplier;
        }
        if (xpMultiplier != 0) {
            blockXp = blockXp * xpMultiplier;
        }

        if (blockXp > 0) {

            currentXP += blockXp;
            double xpForLevel = getXpForLevelUp(level, jobName);

            while (currentXP >= xpForLevel) {
                currentXP -= xpForLevel;
                level++;
                player.sendMessage(JobPlugin.prefix + ChatColor.RED + "Glückwunsch! §aDu bist in dem Job " + "§b " + jobName + " §a ein Level aufgestiegen! Nun bist du Level " + level);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                xpForLevel = getXpForLevelUp(level, jobName);
                userLevelUp(player);

            }
            double xpToNextLevel = xpForLevel - currentXP;
            ActionBar.sendActionBar(decimalFormat, player, jobName, level, blockXp, moneyPerBlock, xpToNextLevel);


            double payoutBefore = dbm.getDouble(JobPlugin.tableName, playerUUID, "payout", 0.0);
            Double payoutAfter = payoutBefore + moneyPerBlock;


            dbm.setDouble(JobPlugin.tableName, playerUUID, "payout", payoutAfter);


            if (!payoutManager.isSchedulerRunning.getOrDefault(player, false)) {
                payoutManager.startPaymentScheduler(player);
            }

            jobData.xp = currentXP;
            jobData.level = level;
            jobData.modified = true;
        }
    }



    public void handleJobAction(Player player, BlockBreakEvent event, JobManager jobManager) {

        UUID playerUUID = player.getUniqueId();
        Material minedBlockMaterial = event.getBlock().getType();
        Material blockType = minedBlockMaterial;
        String jobName = jobManager.getJobForBlock(minedBlockMaterial);

        if (jobName == null) return;


        JobData jobData = getJobData(playerUUID, jobName);

        if(jobData == null){
            player.sendMessage(JobPlugin.prefix + ChatColor.RED + "jobData is null please report it an administrator");
            return;
        }
        double currentXP = jobData.xp;
        int level = jobData.level;

        double blockXp = config.getDouble("jobs." + jobName + ".blocks." + blockType.name() + ".xp", 0);
        double moneyPerBlock = jobManager.getMoneyForMinedBlock(jobName, level);


        double moneyMultiplier = JobPlugin.MONEY.getMultiplier();
        double xpMultiplier = JobPlugin.XP.getMultiplier();

        if (moneyMultiplier == 1) {
            moneyMultiplier = 2;
        }
        if (xpMultiplier == 1) {
            xpMultiplier = 2;
        }
        if (moneyMultiplier != 0) {
            moneyPerBlock = moneyPerBlock * moneyMultiplier;
        }
        if (xpMultiplier != 0) {
            blockXp = blockXp * xpMultiplier;
        }

        if (blockXp > 0) {

            currentXP += blockXp;
            double xpForLevel = jobManager.getXpForLevelUp(level, jobName);

            while (currentXP >= xpForLevel) {
                currentXP -= xpForLevel;
                level++;
                player.sendMessage(JobPlugin.prefix + ChatColor.RED + "Glückwunsch! §aDu bist in dem Job " + "§b " + jobName + " §a ein Level aufgestiegen! Nun bist du Level " + level);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                xpForLevel = jobManager.getXpForLevelUp(level, jobName);
            }

            double xpToNextLevel = xpForLevel - currentXP;
            ActionBar.sendActionBar(decimalFormat, player, jobName, level, blockXp, moneyPerBlock, xpToNextLevel);


            double payoutBefore = dbm.getDouble(JobPlugin.tableName, playerUUID, "payout", 0.0);
            Double payoutAfter = payoutBefore + moneyPerBlock;


            dbm.setDouble(JobPlugin.tableName, playerUUID, "payout", payoutAfter);


            if (!payoutManager.isSchedulerRunning.getOrDefault(player, false)) {
                payoutManager.startPaymentScheduler(player);
            }

            jobData.xp = currentXP;
            jobData.level = level;
            jobData.modified = true;
        }
    }



}