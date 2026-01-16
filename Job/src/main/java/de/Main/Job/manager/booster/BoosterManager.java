package de.Main.Job.manager.booster;

import de.Main.Job.GUI.booster.BoosterGUI;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static de.Main.JobPlugin.activeBooster;
import static de.Main.JobPlugin.prefix;


public class BoosterManager implements Listener {

    private final JavaPlugin plugin;
    DBM dbm;
    BoosterGUI gui;

    private BukkitRunnable task;
    private NamespacedKey firekworkKey = new NamespacedKey(JobPlugin.getPlugin(JobPlugin.class), "booster_firework");
    private boolean taskStarted = false;
    private boolean isBoosterHostServer = false;

    public BoosterManager(JavaPlugin plugin, DBM dbm) {
        this.plugin = plugin;
        this.dbm = dbm;
        isBoosterHostServer = plugin.getConfig().getBoolean("boosterHostServer", false);
    }

    public void start() {
        if (taskStarted) return;
        taskStarted = true;
        if (task != null) return;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getOpenInventory() != null &&
                            player.getOpenInventory().getTopInventory() != null &&
                            player.getOpenInventory().getTitle().equals(BoosterGUI.boosterGUIName)) {

                        BoosterGUI gui = new BoosterGUI(dbm);
                        gui.updateTime(player.getOpenInventory().getTopInventory(), player);
                        player.updateInventory();
                    }
                }
                if (!isBoosterHostServer){
                    return;
                }


                List<String> boosterWorlds = Arrays.asList("farmwelt", "farmwelt_nether", "farmwelt_the_end", "plot");

                for (Booster booster : JobPlugin.boosters) {
                    if (booster.getUuid() != null) {
                        String key = booster.getName() + "#GLOBAL";
                        boolean active = dbm.getBoolean(JobPlugin.activeBooster, key, "ACTIVE", false);
                        int time = dbm.getInt(JobPlugin.activeBooster, key, "TIME", 0);
                        if (active) {
                            if (time < 1) {
                                dbm.remove(JobPlugin.activeBooster, key);
                                for (String worldName : boosterWorlds) {
                                    World world = Bukkit.getWorld(worldName);
                                    if (world != null) {
                                        for (Player player : world.getPlayers()) {
                                            booster.deactivateBooster(new BoosterEffect(player, 0));
                                        }
                                    }
                                }
                            } else {
                                time--;
                                if (time < 0) time = 0;
                                dbm.setInt(JobPlugin.activeBooster, key, "TIME", time);
                            }
                        }
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 0L, 20L); // 20 Ticks = 1 Sekunde
    }


    public String getRemainingTime(String Key) {
        int time = 0;
        String formatted = "0d 0h 0m 0s";

        if (dbm.getBoolean(JobPlugin.activeBooster, Key, "ACTIVE", false)) {
            time = dbm.getInt(JobPlugin.activeBooster, Key, "TIME", 0);
        }

        if (time != 0) {

            int days = time / 86400;
            int hours = (time % 86400) / 3600;
            int minutes = (time % 3600) / 60;
            int seconds = time % 60;

            formatted = String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);

        }

        return formatted;
    }

    public Map<String, Integer> getAllActiveBooster(World world, Player player) {
        Map<String, Integer> activeBooster = new HashMap<>();

        for (Booster boost : JobPlugin.boosters) {


            if (boost.getUuid() != null) {
                String key = Booster.createDbKey(boost, player);
                boolean active = dbm.getBoolean(JobPlugin.activeBooster, key, "LEVEL", false);
                String worldInDb = dbm.getString(JobPlugin.activeBooster, key, "WORLD", "");
                String playerWorld = player.getWorld().getName();

                List<String> boosterWorlds = Arrays.asList("farmwelt", "farmwelt_nether", "farmwelt_the_end", "plot");

                if (active && worldInDb != null) {
                    for(String worldName : boosterWorlds){
                        if(!worldName.equals(worldInDb)){
                            int level = dbm.getInt(JobPlugin.activeBooster, key, "LEVEL", 1);
                            activeBooster.put(key, level);
                        }
                        }


                }
            }


        }

        return activeBooster;
    }


    public double getPlayerGems(Player player) {
        String raw = PlaceholderAPI.setPlaceholders(player, "%bpeco_gems%");
        if (raw == null) return 0.0;
        raw = raw.replaceAll("[^0-9.,]", "").replace(",", ".");
        if (raw.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public void boosterActivated(Player player, Booster booster) {
        BoosterEffectManager boosterEffectmanager = new BoosterEffectManager(dbm, this);

        for (Player target : Bukkit.getOnlinePlayers()) {
            boosterEffectmanager.giveBoosterEffects(player);
            Firework fw = (Firework) player.getWorld().spawnEntity(
                    player.getLocation().add(0, 2, 0),
                    EntityType.FIREWORK_ROCKET
            );

            FireworkMeta meta = fw.getFireworkMeta();
            meta.addEffect(FireworkEffect.builder()
                    .withColor(Color.LIME)
                    .withFade(Color.WHITE)
                    .withFade(Color.GREEN)
                    .with(FireworkEffect.Type.BALL)
                    .trail(true)
                    .build());
            meta.setPower(1);
            fw.setFireworkMeta(meta);

            fw.getPersistentDataContainer().set(firekworkKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);

            fw.detonate();
            fw.setVelocity(fw.getVelocity().zero());


            FileConfiguration config = JobPlugin.getInstance().getConfig();
            String boosterName = booster.getName();
            String key = Booster.createDbKey(booster, player);
            String title = "§c ";
            double level = 0;
            switch (booster.getName()) {
                case "HASTE":
                    level = dbm.getInt(JobPlugin.activeBooster, key, "LEVEL", 1);
                    title = "§c " + boosterName + "§b Auf Stufe §c" + level + "§b aktiviert!";
                    break;
                case "FLY":
                    level = dbm.getInt(JobPlugin.activeBooster, key, "LEVEL", 1);
                    title = "§c " + boosterName + "§b Auf Stufe §c" + level + "§b aktiviert!";
                    break;
                case "XP":
                    level = dbm.getInt(JobPlugin.activeBooster, key, "LEVEL", 1);
                    if(level == 1){
                        level = config.getDouble("booster.xpMultiplicator.1");
                    } else if(level == 2){
                        level = config.getDouble("booster.xpMultiplicator.2");
                    } else if(level == 3){
                        level = config.getDouble("booster.xpMultiplicator.3");
                    }
                    title = "§c +" + level + "00% XP Booster!";
                    title = title.replace(".0", "");

                    break;
                case "MONEY":
                    level = dbm.getInt(JobPlugin.activeBooster, key, "LEVEL", 1);
                    if(level == 1){
                        level = config.getDouble("booster.moneyMultiplicator.1");
                    } else if(level == 2){
                        level = config.getDouble("booster.moneyMultiplicator.2");
                    } else if(level == 3){
                        level = config.getDouble("booster.moneyMultiplicator.3");
                    }
                    title = "§c§l +" + level + "00% Money Booster!";
                    title = title.replace(".0", "");
                    break;
            }
            String subtitle = "§b" + player.getName() + " hat den Booster Aktiviert!";
            target.sendTitle(title, subtitle, 10, 40, 10);
            target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1f);

            player.sendTitle(title, subtitle, 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.2f, 1f);
        }


    }

    @EventHandler
    public void onPlayerDamageByFirework(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getDamager() instanceof Firework fw &&
                    fw.getPersistentDataContainer().has(firekworkKey, org.bukkit.persistence.PersistentDataType.BYTE)) {
                event.setCancelled(true);
            }
        }
    }


    public boolean hasEnoughGems(int gems, Player player) {

        if (getPlayerGems(player) < gems && !player.isOp()) {
            return false;
        } else {
            return true;
        }

    }

    public int calculateTime(String key){
        int time = 1800;
        if(dbm.getInt(JobPlugin.activeBooster, key, "TIME", 0) != 0){
            time = 1800 + dbm.getInt(JobPlugin.activeBooster, key, "TIME", 0);
        }
        return time;
    }

    public boolean canActivateBooster(String key, Player player, int level){


        if(dbm.getInt(activeBooster, key, "LEVEL", 1) != level && dbm.getBoolean(activeBooster, key, "ACTIVE", false)){
            player.sendMessage(prefix + "Du kannst nur Booster mit dem selben Level verlängern!");
            return false;
        }


        return true;
    }

    public void removeGems(Player target, int amount) {
        String command = "syseco remove " + target.getName() + " " + amount;

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

}
