package org.oneblock.utils;

import de.Main.config.ConfigManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.oneblock.OneBlock;

import java.util.List;
import java.util.UUID;

public class OneBlockManager extends WaveManager {

    ConfigManager configManager;
    ;
    public static OneBlockManager instance;

    public OneBlockManager(ConfigManager configManager) {
        super();
        this.configManager = configManager;
        instance = this;
    }

    public static OneBlockManager getInstance() {
        if (instance == null) new OneBlockManager(ConfigManager.getInstance());
        return instance;
    }

    public boolean hasIsland(Player player) {
        boolean hasIsland = false;

        FileConfiguration userDataConfig = configManager.getUserDataConfig(player);

        if (!userDataConfig.getBoolean("hasIsland")) {
            hasIsland = false;
        } else {
            hasIsland = true;
        }


        return hasIsland;
    }

    public boolean createIsland(Player player) {

        try {
            player.sendMessage(MessagesManager.instance.getMessage("creating-running", player));
            World obWorld = WorldManager.instance.getNewOBWorld(player);
            int x = 0;
            double y = 128.5;
            int z = 0;
            Location spawnLoc = new Location(obWorld, x, y, z);

            /// Starter Blocks
            obWorld.getBlockAt(0, 127, 0).setType(Material.GRASS_BLOCK);
            obWorld.getBlockAt(0, 127, 1).setType(Material.GRASS_BLOCK);
            obWorld.getBlockAt(0, 127, -1).setType(Material.GRASS_BLOCK);
            obWorld.getBlockAt(1, 127, 0).setType(Material.GRASS_BLOCK);
            obWorld.getBlockAt(-1, 127, 0).setType(Material.GRASS_BLOCK);
            /// Starter Blocks

            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            userDataConfig.set("obPosition.x", x);
            userDataConfig.set("obPosition.y", y - 1.5);
            userDataConfig.set("obPosition.z", z);
            userDataConfig.set("obPosition.world", obWorld.getName());
            userDataConfig.set("hasIsland", true);

            userDataConfig.set("obVisitPosition.x", x);
            userDataConfig.set("obVisitPosition.y", y);
            userDataConfig.set("obVisitPosition.z", z);
            userDataConfig.set("obVisitPosition.world", obWorld.getName());

            configManager.saveUserData(player, userDataConfig);

            player.teleport(spawnLoc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    public void deleteIsland(Player player) {
        try {
            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            String worldName = userDataConfig.getString("obVisitPosition.world");
            World obWorld = Bukkit.getWorld(worldName);
            userDataConfig.set("obVisitPosition.x", null);
            userDataConfig.set("obVisitPosition.y", null);
            userDataConfig.set("obVisitPosition.z", null);
            userDataConfig.set("obVisitPosition.world", null);

            userDataConfig.set("obPosition.x", null);
            userDataConfig.set("obPosition.y", null);
            userDataConfig.set("obPosition.z", null);
            userDataConfig.set("obPosition.world", null);

            userDataConfig.set("hasIsland", false);

            configManager.saveUserData(player, userDataConfig);

            player.teleport(Bukkit.getWorld(OneBlock.getInstance().getConfig().getString("worldAfterIslandDeleted")).getSpawnLocation());

            WorldManager.instance.deleteOBWorld(obWorld);

            player.sendMessage(MessagesManager.instance.getMessage("island-delete-success", player));


        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(MessagesManager.instance.getMessage("island-delete-error", player));
        }
    }


    public void joinIsland(Player player, String targetName) {
        try {
            OfflinePlayer ofTarget = Bukkit.getOfflinePlayer(targetName);
            FileConfiguration targetConfig = configManager.getUserDataConfig(ofTarget);
            if (!targetConfig.getBoolean("obSettings.visit.allowed") && !isOwner(player) && !isTrusted(player, ofTarget.getUniqueId().toString()) && !isAdded(player, ofTarget.getUniqueId().toString())) {
                player.sendMessage(MessagesManager.instance.getMessage("visit-not-allowed", player));
                return;
            }

            if (isDenied(player)&& !isOwner(player) && !isTrusted(player, ofTarget.getUniqueId().toString()) && !isAdded(player, ofTarget.getUniqueId().toString())) {
                player.sendMessage(MessagesManager.instance.getMessage("visit-denied", player));
                return;
            }
            int x = targetConfig.getInt("obVisitPosition.x");
            int y = targetConfig.getInt("obVisitPosition.y");
            int z = targetConfig.getInt("obVisitPosition.z");
            World world = Bukkit.getWorld(targetConfig.getString("obVisitPosition.world"));
            Location loc = new Location(world, x, y, z);

            Bukkit.getScheduler().runTaskLater(OneBlock.getPlugin(OneBlock.class), () -> {
                player.teleport(loc);
            }, 5L);

            player.sendMessage(MessagesManager.instance.getMessage("visit-success", player));

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(MessagesManager.instance.getMessage("visit-error", player));
        }


    }


    private boolean isDenied(Player player) {

        if (ConfigManager.getInstance().getUserDataConfig(player).getStringList("denyed").contains(player.getName())) {
            return true;
        }

        return false;
    }


    public void acceptInvite(Player player ,OfflinePlayer target) {


        FileConfiguration userDataConfig = configManager.getUserDataConfig(target);
        try {

            List<String> invitedTrust = userDataConfig.getStringList("invitedTrust");
            List<String> invitedAdd = userDataConfig.getStringList("invitedAdd");

            String uuid = player.getUniqueId().toString();
            if (!invitedTrust.contains(uuid) && !invitedAdd.contains(uuid)) {
                player.sendMessage(MessagesManager.instance.getMessage("island-accept-invitation-notInvited", player));
                return;
            }

            if (userDataConfig.getStringList("invitedTrust").contains(uuid)) {
                invitedTrust.remove(uuid);
                userDataConfig.set("invitedTrust", invitedTrust);
                List<String> trust = userDataConfig.getStringList("trust");
                trust.add(uuid);
                userDataConfig.set("trust", trust);
                configManager.saveUserData(target, userDataConfig);
            }



            if (userDataConfig.getStringList("invitedAdd").contains(uuid)) {
                invitedAdd.remove(uuid);
                userDataConfig.set("invitedAdd", invitedAdd);
                List<String> add = userDataConfig.getStringList("add");
                add.add(uuid);
                userDataConfig.set("add", add);
                configManager.saveUserData(target, userDataConfig);
            }

            player.sendMessage(MessagesManager.instance.getMessage("island-accept-invitation", player));
            target.getPlayer().sendMessage(MessagesManager.instance.getMessage("island-trust-success", player));

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(MessagesManager.instance.getMessage("island-trust-error", player));
        }


    }

    public void inviteUserToIsland(Player player, UUID targetUUID, String type) {
        try {
            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            List<String> invited = userDataConfig.getStringList("invited" + type);

            if (invited.contains(targetUUID.toString())) {
                player.sendMessage(MessagesManager.instance.getMessage("island-invitation-alreadyInvited", Bukkit.getPlayer(targetUUID)));
                return;
            }

            player.sendMessage(MessagesManager.instance.getMessage("island-trust-invitation", Bukkit.getPlayer(targetUUID)));
            Bukkit.getOfflinePlayer(targetUUID).getPlayer().sendMessage(MessagesManager.instance.getMessage("island-trust-invitation-other", player));

            if (type.equals("Trust")) {
                invited.add(targetUUID.toString());
            } else if (type.equalsIgnoreCase("add")) {
                invited.add(targetUUID.toString());
            }

            userDataConfig.set("invited" + type, invited);
            configManager.saveUserData(player, userDataConfig);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(MessagesManager.instance.getMessage("island-trust-invitation-error", player));
        }

    }


    public boolean isOneBlock(Location location, Player player) {
        Location obLoc = new Location(Bukkit.getWorld(configManager.getUserDataConfig(player).getString("obPosition.world")),
                configManager.getUserDataConfig(player).getInt("obPosition.x"),
                configManager.getUserDataConfig(player).getInt("obPosition.y"),
                configManager.getUserDataConfig(player).getInt("obPosition.z"));

        if (location.equals(obLoc)) {
            return true;
        }

        return false;

    }




    public void removePlayer(Player player, String targetName) {
        try {


            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            List<String> add = userDataConfig.getStringList(("add"));
            List<String> trust = userDataConfig.getStringList("trust");

            targetName = Bukkit.getOfflinePlayer(UUID.fromString(targetName)).getUniqueId().toString();

            if (!add.contains(targetName) && !trust.contains(targetName)) {
                player.sendMessage(MessagesManager.instance.getMessage("island-remove-notAMember", player));
                return;
            }

            if (add.contains(targetName)) {
                add.remove(targetName);
                userDataConfig.set("add", add);
            }

            if (trust.contains(targetName)) {
                trust.remove(targetName);
                userDataConfig.set("trust", trust);
            }


            player.sendMessage(MessagesManager.instance.getMessage("island-remove", Bukkit.getOfflinePlayer(UUID.fromString(targetName))));

            configManager.saveUserData(player, userDataConfig);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(MessagesManager.instance.getMessage("island-remove-error", player));
        }

    }

    public void denyPlayer(Player player, String targetName) {
        try {
            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            List<String> denyedList = userDataConfig.getStringList("denied");
            List<String> add = userDataConfig.getStringList("invitedAdd");
            List<String> trust = userDataConfig.getStringList("invitedTrust");
            List<String> invitedTrust = userDataConfig.getStringList("invitedTrust");
            List<String> invitedAdd = userDataConfig.getStringList("invitedAdd");


            if (denyedList.contains(targetName)) {
                player.sendMessage(MessagesManager.instance.getMessage("island-deny-alreadyDenied", player));
                return;
            }

            add.remove(targetName);
            trust.remove(targetName);
            invitedTrust.remove(targetName);
            invitedAdd.remove(targetName);
            denyedList.add(targetName);
            userDataConfig.set("denied", denyedList);
            userDataConfig.set("add", add);
            userDataConfig.set("trust", trust);
            userDataConfig.set("invitedTrust", invitedTrust);
            userDataConfig.set("invitedAdd", invitedAdd);
            configManager.saveUserData(player, userDataConfig);

            player.sendMessage(MessagesManager.instance.getMessage("island-deny-player", player));

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(MessagesManager.instance.getMessage("island-deny-error", player));


        }

    }
    
    


    public boolean isOwner(Player player) {
       if(player.getWorld().getName().contains(player.getUniqueId().toString())){
           return true;
       }
        return false;
    }

    public boolean isTrusted(Player player, String worldName){
        if(player.getWorld().getName().contains(worldName)){
            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            List<String> trust = userDataConfig.getStringList("trust");

            return trust.contains(player.getName());
         }
        return false;
    }

    public boolean isOwnerOnline(Player player){



        return false;
    }

    public boolean isAdded(Player player, String worldName){
        if(player.getWorld().getName().contains(worldName)){
            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            List<String> add = userDataConfig.getStringList("add");
            return add.contains(player.getName());
        }
        return false;
    }


    public void unbanPlayer(Player player, String string) {
        try {

            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);
            List<String> denied = userDataConfig.getStringList("denied");
            denied.remove(string);
            userDataConfig.set("denied", denied);
            configManager.saveUserData(player, userDataConfig);
            player.sendMessage(MessagesManager.instance.getMessage("island-unban-success", player));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(MessagesManager.instance.getMessage("island-unban-error", player));
        }
    }

    public void allowVisit(Player player){
        try {
            FileConfiguration userDataConfig = configManager.getUserDataConfig(player);

            if (userDataConfig.getBoolean("obSettings.visit.allowed")) {
                userDataConfig.set("obSettings.visit.allowed", false);
            } else {
                userDataConfig.set("obSettings.visit.allowed", true);
            }

            configManager.saveUserData(player, userDataConfig);
            player.sendMessage(MessagesManager.instance.getMessage("island-allow-visit-success", player));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(MessagesManager.instance.getMessage("island-allow-visit-error", player));
        }
    }

}
