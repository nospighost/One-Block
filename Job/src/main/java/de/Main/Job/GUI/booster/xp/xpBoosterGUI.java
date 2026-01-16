package de.Main.Job.GUI.booster.xp;

import de.Main.JobPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class xpBoosterGUI {

    public static String xpBoosterGUIName = "§eXP Booster";
    public static Inventory xpBoosterGUI = Bukkit.createInventory(null, 27, xpBoosterGUIName);
    public xpBoosterGUI() {



    }

    public static void createGUI() {
        FileConfiguration config = JobPlugin.getInstance().getConfig();

        Inventory inv = xpBoosterGUI;

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setCustomModelData(1000);
        paperMeta.setHideTooltip(true);
        paper.setItemMeta(paperMeta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, paper);
        }

        ItemStack level1 = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta1 = level1.getItemMeta();
        meta1.setDisplayName("§c§l+ " + config.getString("booster.xpMultiplicator.1") + "00% xp");
        List<String> lore = new ArrayList<>();
        lore.add("§bPreis: " + config.getString("booster.xp.1") + " Gems");
        meta1.setLore(lore);
        level1.setItemMeta(meta1);

        ItemStack level2 = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta2 = level2.getItemMeta();
        meta2.setDisplayName("§c§l+ " + config.getString("booster.xpMultiplicator.2") + "00% xp");
        List<String> lore1 = new ArrayList<>();
        lore1.add("§bPreis: " + config.getString("booster.xp.2") + " Gems");
        meta2.setLore(lore1);
        level2.setItemMeta(meta2);


        ItemStack level3 = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta3 = level3.getItemMeta();
        meta3.setDisplayName("§c§l+ " + config.getString("booster.xpMultiplicator.3") + "00% xp");
        List<String> lore3 = new ArrayList<>();
        lore3.add("§bPreis: " + config.getString("booster.xp.3") + " Gems");

        meta3.setLore(lore3);
        level3.setItemMeta(meta3);

        ItemStack menu = new ItemStack(Material.PAPER);
        ItemMeta meta = menu.getItemMeta();
        meta.setDisplayName("§cZurück");
        meta.setCustomModelData(1058);
        menu.setItemMeta(meta);

        inv.setItem(18, menu);
        inv.setItem(12, level1);
        inv.setItem(13, level2);
        inv.setItem(14, level3);

    }

    public static Inventory getXpBoosterGUI() {
        return xpBoosterGUI;
    }
}
