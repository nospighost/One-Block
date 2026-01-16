package de.Main.Job.GUI.booster.money;

import de.Main.Job.GUI.booster.BoosterGUI;
import de.Main.Job.manager.booster.Booster;
import de.Main.Job.manager.booster.BoosterEffectManager;
import de.Main.Job.manager.booster.BoosterManager;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static de.Main.JobPlugin.*;


public class MoneyBoosterGUIListener implements Listener {


    BoosterManager boosterManager;
    DBM dbm;
    BoosterEffectManager boosterEffectmanager;
    public FileConfiguration config = JobPlugin.getInstance().getConfig();

    public MoneyBoosterGUIListener(BoosterManager boosterManager, DBM dbm, BoosterEffectManager boosterEffectmanager) {
        this.boosterManager = boosterManager;
        this.dbm = dbm;
        this.boosterEffectmanager = boosterEffectmanager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        int slot = event.getSlot();
        ItemStack clicked = event.getCurrentItem();
        if(clicked == null){return;}

        if (!title.equals(MoneyBoosterGUI.moneyBoosterGUIName)) {
            return;
        }

        event.setCancelled(true);
        String key = Booster.createDbKey(MONEY, player);
        switch (slot) {

            case 12:


                if(!boosterManager.hasEnoughGems(config.getInt("booster.money.1"), player)){player.sendMessage(JobPlugin.prefix + "§cDu hast nicht genug Gems!"); return;}


                if (config.getList("moneyBoosterBlackList").contains(player.getWorld().getName())) {
                    player.sendMessage(prefix + "§c§l Der Money Booster kann in diesem Welt nicht genutzt werden!");
                    return;
                }

                boolean canActivate = boosterManager.canActivateBooster(key, player, 1);
                if (!canActivate) {
                    return;
                }

                HashMap<String, Object> boosterDefaults = new HashMap<>();
                boosterDefaults.put("owner_uuid", key);
                boosterDefaults.put("LEVEL", 1);
                boosterDefaults.put("ACTIVE", true);
                boosterDefaults.put("WORLD", player.getWorld().getName());
                boosterDefaults.put("TIME", boosterManager.calculateTime(key));


                if (dbm.getBoolean(JobPlugin.activeBooster, key, "ACTIVE", false)) {
                    dbm.setInt(JobPlugin.activeBooster, key, "TIME", boosterManager.calculateTime(key));
                } else {
                    dbm.insertDefaultValues(JobPlugin.activeBooster, key, boosterDefaults);
                }


                player.sendMessage(prefix + "§bDu hast den Money Booster auf Stufe 1 Aktiviert!");
                boosterManager.boosterActivated(player, MONEY);
                boosterManager.removeGems(player, config.getInt("booster.money.1"));

                break;


            case 13:

                if(!boosterManager.hasEnoughGems(config.getInt("booster.money.2"), player)){player.sendMessage(JobPlugin.prefix + "§cDu hast nicht genug Gems!"); return;}

                if (config.getList("moneyBoosterBlackList").contains(player.getWorld().getName())) {
                    player.sendMessage(prefix + "§c§l Der Money Booster kann in diesem Welt nicht genutzt werden!");
                    return;
                }

                boolean canActivate2 = boosterManager.canActivateBooster(key, player, 2);
                if (!canActivate2) {
                    return;
                }

                HashMap<String, Object> boosterDefaults2 = new HashMap<>();
                boosterDefaults2.put("owner_uuid", key);
                boosterDefaults2.put("LEVEL", 2);
                boosterDefaults2.put("ACTIVE", true);
                boosterDefaults2.put("WORLD", player.getWorld().getName());
                boosterDefaults2.put("TIME", boosterManager.calculateTime(key));


                if (dbm.getBoolean(JobPlugin.activeBooster, key, "ACTIVE", false)) {
                    dbm.setInt(JobPlugin.activeBooster, key, "TIME", boosterManager.calculateTime(key));
                } else {
                    dbm.insertDefaultValues(JobPlugin.activeBooster, key, boosterDefaults2);
                }


                player.sendMessage(prefix + "§bDu hast den Money Booster auf Stufe 2 Aktiviert!");
                boosterManager.boosterActivated(player, MONEY);

                boosterManager.removeGems(player, config.getInt("booster.money.2"));

                break;

            case 14:

                if(!boosterManager.hasEnoughGems(config.getInt("booster.money.3"), player)){player.sendMessage(JobPlugin.prefix + "§cDu hast nicht genug Gems!"); return;}

                if (config.getList("moneyBoosterBlackList").contains(player.getWorld().getName())) {
                    player.sendMessage(prefix + "§c§l Der Money Booster kann in diesem Welt nicht genutzt werden!");
                    return;
                }

                boolean canActivate3 = boosterManager.canActivateBooster(key, player, 3);
                if (!canActivate3) {
                    return;
                }

                HashMap<String, Object> boosterDefaults3 = new HashMap<>();
                boosterDefaults3.put("owner_uuid", key);
                boosterDefaults3.put("LEVEL", 3);
                boosterDefaults3.put("ACTIVE", true);
                boosterDefaults3.put("WORLD", player.getWorld().getName());
                boosterDefaults3.put("TIME", boosterManager.calculateTime(key));


                if (dbm.getBoolean(JobPlugin.activeBooster, key, "ACTIVE", false)) {
                    dbm.setInt(JobPlugin.activeBooster, key, "TIME", boosterManager.calculateTime(key));
                } else {
                    dbm.insertDefaultValues(JobPlugin.activeBooster, key, boosterDefaults3);
                }



                player.sendMessage(prefix + "§bDu hast den Money Booster auf Stufe 3 Aktiviert!");
                boosterManager.boosterActivated(player, MONEY);
                boosterManager.removeGems(player, config.getInt("booster.money.3"));
                break;


            case 18:

                BoosterGUI boosterGUI = new BoosterGUI(dbm);
                player.openInventory(boosterGUI.createGUI(player));

                    break;
        }


    }





}
