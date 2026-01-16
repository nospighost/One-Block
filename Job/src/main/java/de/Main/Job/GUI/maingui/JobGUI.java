package de.Main.Job.GUI.maingui;

import de.Main.JobPlugin;
import de.Main.database.DBM;
import de.Main.Job.manager.jobs.BlockBreakManager;
import de.Main.Job.manager.JobManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class JobGUI {

    DBM dbm;


    public int jägerSlot = 10;
    public int minerSlot = 11;
    public int holzfällerSlot = 12;
    public int gräberSlot = 13;
    public int farmerSlot = 14;
    public int fischerSlot = 15;
    public int GourmetSlot = 16;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private BlockBreakManager blockBreakManager;

    JobManager jobManager;
    public static JobGUI instance;

    public JobGUI(DBM dbm, JobManager jobManager) {
        instance = this;
        this.dbm = dbm;
        this.jobManager = jobManager;
        blockBreakManager = new BlockBreakManager(JobPlugin.getPlugin(JobPlugin.class), JobPlugin.getInstance().getJobManager(), JobPlugin.getInstance().getPayoutManager());

    }

    public static JobGUI getInstance() {
        return instance;
    }

    public Inventory createJobGUI2(Player player, String job, ItemStack jobItem) {
        Inventory inv = Bukkit.createInventory(null, 27, "§b§lJob");

        ItemStack grayGlas = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayGlasMeta = grayGlas.getItemMeta();
        grayGlasMeta.setHideTooltip(true);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, grayGlas);
        }


        for (int i = 18; i < 27; i++) {
            inv.setItem(i, grayGlas);
        }

         createPercentBars(player, jobManager, inv, job, jobItem);

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("§cZurück");
        backButton.setItemMeta(backButtonMeta);
        inv.setItem(18, backButton);

        inv.setItem(21, getJobRewardTotem(job));
        inv.setItem(23, getJobTopListTotem(job, 10));

        return inv;
    }

    private ItemStack getJobTopListTotem(String job, int topN) {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lTop " + topN + " §b§lListe");
        List<String> lore = getJobTopList(job, topN);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getJobRewardTotem(String job) {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§l" + job + " §b§lBelohnungen");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§c§lRechtsklick um zu den Belohnungen zu gelangen");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }


    public Inventory createJobGUI(Player player) {
        Inventory mainGUI = Bukkit.createInventory(null, 27, "§b§lJob");
        UUID playerUUID = player.getUniqueId();

        ItemStack tdiamond_sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack tdiamond_pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemStack tdiamond_axe = new ItemStack(Material.DIAMOND_AXE);
        ItemStack tdiamond_shovel = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemStack tdiamond_hoe = new ItemStack(Material.DIAMOND_HOE);
        ItemStack tfishing_rod = new ItemStack(Material.FISHING_ROD);
        ItemStack tbread = new ItemStack(Material.BREAD);


        ItemStack diamond_sword = createPercificJobGUI(tdiamond_sword, "Jäger");
        ItemStack diamond_pickaxe = createPercificJobGUI(tdiamond_pickaxe, "Miner");
        ItemStack diamond_axe = createPercificJobGUI(tdiamond_axe, "Holzfäller");
        ItemStack diamond_shovel = createPercificJobGUI(tdiamond_shovel, "Gräber");
        ItemStack diamond_hoe = createPercificJobGUI(tdiamond_hoe, "Farmer");
        ItemStack fishing_rod = createPercificJobGUI(tfishing_rod, "Fischer");
        ItemStack bread = createPercificJobGUI(tbread, "Gourmet");

        mainGUI.setItem(jägerSlot, diamond_sword);
        mainGUI.setItem(minerSlot, diamond_pickaxe);
        mainGUI.setItem(holzfällerSlot, diamond_axe);
        mainGUI.setItem(gräberSlot, diamond_shovel);
        mainGUI.setItem(farmerSlot, diamond_hoe);
        mainGUI.setItem(fischerSlot, fishing_rod);
        mainGUI.setItem(GourmetSlot, bread);

        Integer arr[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setHideTooltip(true);
        meta.setCustomModelData(1000);
        item.setItemMeta(meta);
        for (Integer i : arr) {
            mainGUI.setItem(i, item);
        }

        return mainGUI;
    }


    public ItemStack createPercificJobGUI(ItemStack item, String jobName) {

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("§b§l" + jobName);
        List<String> lore = new ArrayList<>();
        lore.add("§cRechtsklick §afür weitere Informationen");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public void createPercentBars(Player player, JobManager jobManager, Inventory inv, String jobName, ItemStack jobItem) {
        UUID playerUUID = player.getUniqueId();

        double currentXP = dbm.getDouble(JobPlugin.tableName, playerUUID, jobName + "_XP", 0.0);
        int level = dbm.getInt(JobPlugin.tableName, playerUUID, jobName + "_LVL", 1);
        double levelXP = jobManager.getXpForLevelUp(level, jobName);

        int percent = calculatePercentBars(currentXP, levelXP);
        int maxSlots = 9;
        int filledSlots = (int) Math.round((percent / 100.0) * maxSlots);

        for (int i = 0; i < maxSlots; i++) {
            boolean filled = i < filledSlots;
            inv.setItem(9 + i, createGlassPane(filled, player, jobName));
        }


        inv.setItem(4, jobItem);

        ItemStack statistik = new ItemStack(Material.GOLD_INGOT);
        ItemMeta sMeta = statistik.getItemMeta();
        sMeta.setDisplayName("§b§l " + jobName + " Statistiken");
        statistik.setItemMeta(sMeta);
        inv.setItem(5, statistik);

        ItemStack jobInformation = new ItemStack(Material.EMERALD);
        ItemMeta jiMeta = jobInformation.getItemMeta();
        jiMeta.setDisplayName("§a§l" + jobName + " Informationen");


        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§6§lLevel: " + level);
        lore.add("§bErfahrung: " + currentXP + "/" + decimalFormat.format(JobPlugin.getInstance().getJobManager().getXpForLevelUp(level, jobName)) + " XP");
        lore.add("§eVerdienst: " + decimalFormat.format(JobPlugin.getInstance().getJobManager().getMoneyForMinedBlock(jobName, dbm.getInt(JobPlugin.tableName, playerUUID, jobName + "_LVL", 1))) + "$ / Job Aktion");
        lore.add("");
        jiMeta.setLore(lore);
        jobInformation.setItemMeta(jiMeta);
        inv.setItem(3, jobInformation);

    }

    public ItemStack createGlassPane(boolean filled, Player player, String jobName) {
        Material mat = filled ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        ItemStack pane = new ItemStack(mat, 1);
        ItemMeta meta = pane.getItemMeta();
        double currentXP = dbm.getDouble(JobPlugin.tableName, player.getUniqueId(), jobName + "_XP", 0.0);
        int level = dbm.getInt(JobPlugin.tableName, player.getUniqueId(), jobName + "_LVL", 1);
        double levelXP = jobManager.getXpForLevelUp(level, jobName);

        int percent = calculatePercentBars(currentXP, levelXP);
        if (meta != null) {
            if (filled) {


                meta.setDisplayName("§a" + percent + "% §7(§a" + currentXP + "§7/§a" + levelXP + " XP§7)");

            } else {
                meta.setDisplayName("§c" + percent + "% §7(§c" + currentXP + "§7/§c" + levelXP + " XP§7)");

            }

            pane.setItemMeta(meta);
        }

        return pane;
    }


    private int calculatePercentBars(double currentXP, double levelXP) {

        if (levelXP <= 0) {
            return 0;
        }
        double percent = (currentXP / levelXP) * 100;

        if (percent > 100) percent = 100;
        if (percent < 0) percent = 0;


        return (int) percent;
    }

    public List<String> getJobTopList(String jobName, int topN) {
        Map<UUID, Integer> playerXP = new HashMap<>();
        for (String uuid : dbm.getAllValues(JobPlugin.tableName, "owner_uuid")) {
            int lvl = dbm.getInt(JobPlugin.tableName, uuid, jobName + "_LVL", 0);
            UUID uuid1 = UUID.fromString(uuid);
            playerXP.put(uuid1, lvl);
        }
        return playerXP.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .map(entry -> {
                    String name = dbm.getString(JobPlugin.tableName, entry.getKey(), "owner", "Unbekannt");
                    return "§e" + name + "§7: §b" + "Level: " + entry.getValue();
                })
                .collect(Collectors.toList());
    }


}
