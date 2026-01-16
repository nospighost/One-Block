package de.Main;

import co.aikar.commands.PaperCommandManager;
import com.plotsquared.core.PlotSquared;
import de.Main.Job.GUI.Rewards.listener.RewardListener;
import de.Main.Job.GUI.maingui.JobGUI;
import de.Main.Job.GUI.booster.fly.FlyBoosterGUIListener;
import de.Main.Job.GUI.booster.haste.HasteBoosterGUIListener;
import de.Main.Job.GUI.booster.money.MoneyBoosterGUIListener;
import de.Main.Job.GUI.booster.xp.xpBoosterGUIListener;
import de.Main.Job.GUI.booster.listener.BoosterGUIListener;
import de.Main.Job.GUI.maingui.JobGUIListener;
import de.Main.Job.commands.BoosterCommand;
import de.Main.Job.commands.JobAdminCommands;
import de.Main.Job.commands.rewardCommand;
import de.Main.Job.manager.*;
import de.Main.Job.manager.booster.Booster;
import de.Main.Job.manager.booster.BoosterEffectManager;
import de.Main.Job.manager.booster.BoosterManager;
import de.Main.Job.manager.booster.MultiplierBooster;
import de.Main.Job.manager.jobs.*;
import de.Main.Job.commands.JobCommand;
import de.Main.api.JobAPI;
import de.Main.database.DBM;
import de.Main.database.SQLDataType;
import de.Main.database.SQLConnection;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class JobPlugin extends JavaPlugin implements JobAPI {
    private static JobPlugin instance;
    private SQLConnection connection;

    DBM dbm;
    DBM dbm2;
    DBM dbm3;
    DBM dbm5;
    public static PayoutManager payoutManager;
    private static Economy economy;
    public static String tableName = "Job";
    public static String doublePlayerTableName = "PartyJob";



    public static String activeBooster = "activeBooster";
    public static final UUID HASTE_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID FLY_UUID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID MONEY_UUID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID XP_UUID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    public static Booster FLY = new Booster(FLY_UUID, "FLY");
    public static Booster HASTE = new Booster(HASTE_UUID, "HASTE");
    public static MultiplierBooster MONEY = new MultiplierBooster(MONEY_UUID, "MONEY");
    public static MultiplierBooster XP = new MultiplierBooster(XP_UUID, "XP");
    public static final Booster[] boosters = {FLY, HASTE, MONEY, XP};

    static {
        FLY.setActivationEffect(effect -> {
            effect.getPlayer().setAllowFlight(true);
        });
        FLY.setDeactivationEffect(effect -> {
            effect.getPlayer().setAllowFlight(false);
        });
        HASTE.setActivationEffect(effect -> {
            effect.getPlayer().removePotionEffect(PotionEffectType.HASTE);
            effect.getPlayer().addPotionEffect(new PotionEffect(
                    PotionEffectType.HASTE,
                    129 * 20,
                    effect.getLevel() - 1,
                    false,
                    false
            ));
        });
        MONEY.setActivationEffect(effect -> {
            MONEY.setMultiplier(effect.getLevel());
        });
        MONEY.setDeactivationEffect(effect -> {
            MONEY.setMultiplier(0);
        });
        XP.setActivationEffect(effect -> {
            XP.setMultiplier(effect.getLevel());
        });
        XP.setDeactivationEffect(effect -> {
            XP.setMultiplier(0);
        });
    }

    public static String rewardTableName = "Reward";
    public static String prefix;
    public static List<String> rewardJobs = List.of(new String[]{"Miner", "Farmer", "Jäger", "Holzfäller", "Gräber", "Fischer", "Builder", "Gourmet"});

    private JobManager jobManager;
    private RewardManager rewardManager;
    PlotSquared plotSquared;
    PaperCommandManager manager;

    public static Economy getEconomy() {
        return economy;
    }

    public static PayoutManager getPayoutManager() {
        return payoutManager;
    }

    @Override
    public void onEnable() {

        instance = this;
        prefix = getConfig().getString("jobPrefix");
        registerDataBase();

        if (!setupEconomy()) {
            getLogger().severe("Vault oder ein Economy-Plugin wurde nicht gefunden! Deaktiviere Plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        plotSquared = PlotSquared.get();

        if (plotSquared == null) {
            getLogger().severe("[Job] PlotSquared wurde nicht gefunden! Plugin deaktiviert.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        manager = new PaperCommandManager(this);

        BoosterManager boosterManager = new BoosterManager(this, dbm);
        BoosterEffectManager boosterEffectmanager = new BoosterEffectManager(dbm, boosterManager);


        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(dbm), this);
        Bukkit.getPluginManager().registerEvents(new JobGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockIdentifier(this), this);
        Bukkit.getPluginManager().registerEvents(new BoosterManager(this, dbm), this);

        BoosterManager manager1 = new BoosterManager(this, dbm);
        manager1.start();

        registerCommands();
        registerRewardListener();
        registerBoosters(boosterManager, boosterEffectmanager);
        registerJobListener(boosterManager);

        createRewards();

        saveDefaultConfig();


    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
        connection.disconnect();

    }

    private void registerCommands() {

        manager.registerCommand(new JobCommand(dbm));
        manager.registerCommand(new JobAdminCommands(dbm));
        manager.registerCommand(new rewardCommand(manager));
    }

    private void registerDataBase() {
        connection = new SQLConnection(getConfig().getString("database.host"),
                getConfig().getInt("database.port"),
                getConfig().getString("database.database"),
                getConfig().getString("database.user")
                ,  getConfig().getString("database.password"));
        HashMap<String, SQLDataType> userdatacolumns = new HashMap<>();
        userdatacolumns.put("owner", SQLDataType.CHAR);
        userdatacolumns.put("owner_uuid", SQLDataType.CHAR);
        userdatacolumns.put("Miner_LVL", SQLDataType.CHAR);
        userdatacolumns.put("Miner_XP", SQLDataType.CHAR);
        userdatacolumns.put("Gräber_LVL", SQLDataType.CHAR);
        userdatacolumns.put("Gräber_XP", SQLDataType.CHAR);
        userdatacolumns.put("Jäger_LVL", SQLDataType.CHAR);
        userdatacolumns.put("Jäger_XP", SQLDataType.CHAR);
        userdatacolumns.put("Fischer_LVL", SQLDataType.CHAR);
        userdatacolumns.put("Fischer_XP", SQLDataType.CHAR);
        userdatacolumns.put("Holzfäller_LVL", SQLDataType.CHAR);
        userdatacolumns.put("Holzfäller_XP", SQLDataType.CHAR);
        userdatacolumns.put("Farmer_LVL", SQLDataType.CHAR);
        userdatacolumns.put("Farmer_XP", SQLDataType.CHAR);
        userdatacolumns.put("Builder_LVL", SQLDataType.CHAR);
        userdatacolumns.put("Builder_XP", SQLDataType.CHAR);
        userdatacolumns.put("Gourmet_LVL", SQLDataType.CHAR);
        userdatacolumns.put("Gourmet_XP", SQLDataType.CHAR);
        userdatacolumns.put("payout", SQLDataType.CHAR);

        dbm = new DBM(connection, tableName, userdatacolumns);


        HashMap<String, SQLDataType> rewardColums = new HashMap<>();
        rewardColums.put("owner", SQLDataType.CHAR);
        rewardColums.put("owner_uuid", SQLDataType.CHAR);
        for (String name : rewardJobs) {
            rewardColums.put(name + "_LVL_5", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_10", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_20", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_30", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_40", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_50", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_60", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_70", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_80", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_90", SQLDataType.BOOLEAN);
            rewardColums.put(name + "_LVL_100", SQLDataType.BOOLEAN);
        }

        dbm2 = new DBM(this.connection, rewardTableName, rewardColums);

        HashMap<String, SQLDataType> partyColumns = new HashMap<>();
        partyColumns.put("owner", SQLDataType.CHAR);
        partyColumns.put("owner_uuid", SQLDataType.CHAR);
        partyColumns.put("member", SQLDataType.CHAR);
        partyColumns.put("invited", SQLDataType.CHAR);

        dbm3 = new DBM(this.connection, doublePlayerTableName, partyColumns);


        HashMap<String, SQLDataType> boosterTable = new HashMap<>();
        boosterTable.put("owner_uuid", SQLDataType.CHAR);
        boosterTable.put("WORLD", SQLDataType.CHAR);
        boosterTable.put("LEVEL", SQLDataType.INT);
        boosterTable.put("TIME", SQLDataType.INT);
        boosterTable.put("ACTIVE", SQLDataType.BOOLEAN);
        dbm5 = new DBM(this.connection, activeBooster, boosterTable);
    }




    private void createRewards() {
        File folder = new File(getDataFolder(), "rewards");
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (String job : rewardJobs) {
            File file = new File(folder, job + ".yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public FileConfiguration getRewardFile(String job) {
        return YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/rewards", job + ".yml"));
    }

    public void saveRewardFile(String job, FileConfiguration fileConfiguration) {
        try {
            fileConfiguration.save(new File(getDataFolder() + "/rewards", job + ".yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerRewardListener() {
        Bukkit.getPluginManager().registerEvents(new RewardListener(dbm), this);
        Bukkit.getPluginManager().registerEvents(new RewardManager(dbm), this);
    }

    private void registerBoosters(BoosterManager boosterManager, BoosterEffectManager boosterEffectmanager) {

        Bukkit.getPluginManager().registerEvents(new BoosterGUIListener(dbm), this);
        Bukkit.getPluginManager().registerEvents(new BoosterEffectManager(dbm, boosterManager), this);
        Bukkit.getPluginManager().registerEvents(new HasteBoosterGUIListener(boosterManager, dbm, boosterEffectmanager), this);
        Bukkit.getPluginManager().registerEvents(new MoneyBoosterGUIListener(boosterManager, dbm, boosterEffectmanager), this);
        Bukkit.getPluginManager().registerEvents(new FlyBoosterGUIListener(boosterManager, dbm, boosterEffectmanager), this);
        Bukkit.getPluginManager().registerEvents(new xpBoosterGUIListener(boosterManager, dbm, boosterEffectmanager), this);
    }


    public void registerJobListener(BoosterManager boosterManager) {
        payoutManager = new PayoutManager(dbm, this, economy);
        jobManager = new JobManager(dbm, payoutManager, boosterManager);
        rewardManager = new RewardManager(dbm);
        manager.registerCommand(new BoosterCommand(dbm, jobManager, manager, boosterManager));
        BlockBreakManager blockBreakManager = new BlockBreakManager(this, jobManager, payoutManager);
        EatManager eatManager = new EatManager(this, jobManager, payoutManager);
        FarmerManager farmerManager = new FarmerManager(dbm, this, economy, jobManager, payoutManager);
        JägerManager jägerManager = new JägerManager(dbm, this, economy, jobManager, payoutManager);
        Bukkit.getPluginManager().registerEvents(blockBreakManager, this);
        Bukkit.getPluginManager().registerEvents(eatManager, this);
        Bukkit.getPluginManager().registerEvents(farmerManager, this);
        Bukkit.getPluginManager().registerEvents(jägerManager, this);
        Bukkit.getPluginManager().registerEvents(new FischerManager(dbm, this, economy, jobManager, blockBreakManager, payoutManager), this);

    }

    public static JobPlugin getInstance() {
        return instance;
    }

    @Override
    public JobManager getJobManager() {
        return Optional.ofNullable(jobManager)
                .orElseThrow(() -> new IllegalStateException("JobManager is null. Plugin may have not been enabled yet."));
    }


    public JobGUI getJobGUI() {
        JobGUI jobGUI = new JobGUI(dbm, JobPlugin.getInstance().getJobManager());
        return Optional.ofNullable(jobGUI)
                .orElseThrow(() -> new IllegalStateException("JobManager is null. Plugin may have not been enabled yet."));
    }

    public RewardManager getRewardManager() {
        return Optional.ofNullable(rewardManager)
                .orElseThrow(() -> new IllegalStateException("RewardManager is null. Plugin may have not been enabled yet."));
    }

    public DBM getDbm() {
        return dbm;
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public DBM getDbm3() {
        return dbm3;
    }


}