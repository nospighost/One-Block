package de.Main.Job.GUI;

import de.Main.database.DBM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.DIAMOND;


public class BelohnungsGUI implements Listener {

    DBM dbm;

    public BelohnungsGUI (DBM dbm ){
        this.dbm = dbm;
    }

    public static Inventory MinerBelohnung = Bukkit.createInventory(null, 54, "§bMiner Belohnungen");

    public static void createGUIS (){


        ItemStack diamond = new ItemStack(DIAMOND);
        MinerBelohnung.setItem(1, diamond);

    }







}
