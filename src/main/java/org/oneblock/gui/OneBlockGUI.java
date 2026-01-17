package org.oneblock.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OneBlockGUI {


    public static String guiName = "§b§lOneBlock";
    public static Inventory inv = Bukkit.createInventory(null, 9, guiName);


    public static Inventory getInv(Player player) {


        return inv;
    }
}