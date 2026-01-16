package de.Main.Job.GUI.maingui;

import de.Main.Job.manager.RewardManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class JobGUIListener implements Listener {

    private boolean itemExists = false;

    // Items als Klassenvariablen
    private ItemStack tdiamond_sword;
    private ItemStack tdiamond_pickaxe;
    private ItemStack tdiamond_axe;
    private ItemStack tdiamond_shovel;
    private ItemStack tdiamond_hoe;
    private ItemStack tfishing_rod;
    private ItemStack tbread;

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        int slot = event.getSlot();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        if (!title.equalsIgnoreCase("§b§lJob")) return;

        event.setCancelled(true);

        if (!itemExists) {
            tdiamond_sword = new ItemStack(Material.DIAMOND_SWORD);
            tdiamond_pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
            tdiamond_axe = new ItemStack(Material.DIAMOND_AXE);
            tdiamond_shovel = new ItemStack(Material.DIAMOND_SHOVEL);
            tdiamond_hoe = new ItemStack(Material.DIAMOND_HOE);
            tfishing_rod = new ItemStack(Material.FISHING_ROD);
            tbread = new ItemStack(Material.BREAD);

            ItemMeta meta;

            meta = tdiamond_sword.getItemMeta();
            meta.setDisplayName("§b§lJäger");
            tdiamond_sword.setItemMeta(meta);

            meta = tdiamond_pickaxe.getItemMeta();
            meta.setDisplayName("§b§lMiner");
            tdiamond_pickaxe.setItemMeta(meta);

            meta = tdiamond_axe.getItemMeta();
            meta.setDisplayName("§b§lHolzfäller");
            tdiamond_axe.setItemMeta(meta);

            meta = tdiamond_shovel.getItemMeta();
            meta.setDisplayName("§b§lGräber");
            tdiamond_shovel.setItemMeta(meta);

            meta = tdiamond_hoe.getItemMeta();
            meta.setDisplayName("§b§lFarmer");
            tdiamond_hoe.setItemMeta(meta);

            meta = tfishing_rod.getItemMeta();
            meta.setDisplayName("§b§lFischer");
            tfishing_rod.setItemMeta(meta);

            meta = tbread.getItemMeta();
            meta.setDisplayName("§b§lGourmet");
            tbread.setItemMeta(meta);

            itemExists = true;
        }

            switch (slot) {
                case 10:
                    player.openInventory(JobGUI.getInstance().createJobGUI2(player, "Jäger", tdiamond_sword));
                    break;
                case 11:
                    player.openInventory(JobGUI.getInstance().createJobGUI2(player, "Miner", tdiamond_pickaxe));
                    break;
                case 12:
                    player.openInventory(JobGUI.getInstance().createJobGUI2(player, "Holzfäller", tdiamond_axe));
                    break;
                case 13:
                    player.openInventory(JobGUI.getInstance().createJobGUI2(player, "Gräber", tdiamond_shovel));
                    break;
                case 14:
                    player.openInventory(JobGUI.getInstance().createJobGUI2(player, "Farmer", tdiamond_hoe));
                    break;
                case 15:
                    player.openInventory(JobGUI.getInstance().createJobGUI2(player, "Fischer", tfishing_rod));
                    break;
                case 16:
                    player.openInventory(JobGUI.getInstance().createJobGUI2(player, "Gourmet", tbread));
                    break;

                case 18:

                    player.openInventory(JobGUI.getInstance().createJobGUI(player));

                    break;

                case 21:

                    String jobName = clicked.getItemMeta().getDisplayName().replace("§e§l", "").replace(" §b§lBelohnungen", "");
                    player.openInventory(RewardManager.getInstance().loadRewardInventory(player, jobName , false, true));
                    break;
            }


    }
}
