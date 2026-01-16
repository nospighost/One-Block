package de.Main.Job.GUI.booster.listener;

import de.Main.Job.GUI.booster.BoosterHolder;
import de.Main.Job.GUI.booster.fly.FlyBoosterGUI;
import de.Main.Job.GUI.booster.haste.HasteBoosterGUI;
import de.Main.Job.GUI.booster.money.MoneyBoosterGUI;
import de.Main.Job.GUI.booster.xp.xpBoosterGUI;
import de.Main.database.DBM;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BoosterGUIListener implements Listener {

    DBM dbm;

    public BoosterGUIListener(DBM dbm) {
        this.dbm = dbm;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BoosterHolder)) return;

        event.setCancelled(true);
        Player player = ((BoosterHolder) event.getInventory().getHolder()).getPlayer();
        int slot = event.getSlot();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        switch (clicked.getType()) {
            case DIAMOND_PICKAXE:
                HasteBoosterGUI.createGUI();
                player.openInventory(HasteBoosterGUI.getHasteBoosterGUI());
                break;
            case EXPERIENCE_BOTTLE:
                xpBoosterGUI.createGUI();
                player.openInventory(xpBoosterGUI.getXpBoosterGUI());
                break;
            case GOLD_INGOT:
                MoneyBoosterGUI.createGUI();
                player.openInventory(MoneyBoosterGUI.getMoneyBoosterGUI());
                break;
            case ELYTRA:
                FlyBoosterGUI.createGUI();
                player.openInventory(FlyBoosterGUI.getHasteBoosterGUI());
                break;
        }
    }



}
