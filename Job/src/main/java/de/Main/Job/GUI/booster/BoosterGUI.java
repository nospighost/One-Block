package de.Main.Job.GUI.booster;

import de.Main.Job.manager.booster.Booster;
import de.Main.Job.manager.booster.BoosterManager;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class BoosterGUI {
    public static String boosterGUIName = "§bBooster";

    DBM dbm;

    public BoosterGUI(DBM dbm) {
        this.dbm = dbm;
    }

    public Inventory createGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new BoosterHolder(player), 36, boosterGUIName);

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setHideTooltip(true);
        paperMeta.setCustomModelData(1000);
        paper.setItemMeta(paperMeta);

        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, paper);

        ItemStack HASTE = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta hasteMeta = HASTE.getItemMeta();
        hasteMeta.setDisplayName("§6Haste");
        hasteMeta.setCustomModelData(0);
        HASTE.setItemMeta(hasteMeta);

        ItemStack XP = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta XPMeta = XP.getItemMeta();
        XPMeta.setDisplayName("§2XP");
        XPMeta.setCustomModelData(0);
        XP.setItemMeta(XPMeta);

        ItemStack MONEY = new ItemStack(Material.GOLD_INGOT);
        ItemMeta moneyMeta = MONEY.getItemMeta();
        moneyMeta.setDisplayName("§aMoney");
        moneyMeta.setCustomModelData(0);
        MONEY.setItemMeta(moneyMeta);

        ItemStack FLY = new ItemStack(Material.ELYTRA);
        ItemMeta flyMeta = FLY.getItemMeta();
        flyMeta.setDisplayName("§bFly");
        flyMeta.setCustomModelData(0);
        FLY.setItemMeta(flyMeta);

        inv.setItem(11, HASTE);
        inv.setItem(12, XP);
        inv.setItem(14, MONEY);
        inv.setItem(15, FLY);

        inv = updateTime(inv, player);

        return inv;
    }


    public Inventory updateTime(Inventory inv, Player player) {
        BoosterManager boosterManager = new BoosterManager(JobPlugin.getPlugin(JobPlugin.class), dbm);
        Map<String, Integer> activeBooster = boosterManager.getAllActiveBooster(player.getWorld(), player);

        Map<String, Integer> boosterSlots = Map.of(
                "HASTE", 20,
                "XP", 21,
                "MONEY", 23,
                "FLY", 24
        );

        for (Booster booster : JobPlugin.boosters) {
            String name = booster.getName();
            ItemStack grayGlas = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta grayGlasMeta = grayGlas.getItemMeta();
            grayGlasMeta.setHideTooltip(true);
            grayGlas.setItemMeta(grayGlasMeta);

            ItemStack limeGlas = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta limeGlasMeta = limeGlas.getItemMeta();
            limeGlasMeta.setDisplayName("§bDauer:" +
                    boosterManager.getRemainingTime(
                            Booster.createDbKey(booster, player)
                    ));
            if (activeBooster.get(Booster.createDbKey(booster, player)) != null) {
                limeGlas.setAmount(activeBooster.get(Booster.createDbKey(booster, player)));
            }
            limeGlas.setItemMeta(limeGlasMeta);

            int slot = boosterSlots.get(booster.getName());
            inv.setItem(slot, activeBooster.containsKey(Booster.createDbKey(booster, player)) ? limeGlas : grayGlas);
        }

        return inv;
    }
}