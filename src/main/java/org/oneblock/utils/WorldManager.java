package org.oneblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;

public class WorldManager {


    public static WorldManager instance;

    public WorldManager() {
        instance = this;
    }

    public World getNewOBWorld(Player player) {
        String name = "ob_" + player.getUniqueId();
        WorldCreator wc = new WorldCreator(name);
        wc.generator(new VoidChunkGenerator());
        wc.createWorld();
        return Bukkit.getWorld(name);
    }

    public void deleteOBWorld(World obWorld) {
        try {
            File file = obWorld.getWorldFolder();
            file.delete();
        } catch (Exception e) {

        }

    }
}
