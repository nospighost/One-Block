package de.Main.Job.manager.booster;

import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BoosterEffectManager implements Listener {

    private final DBM dbm;
    private final BoosterManager boosterManager;

    public BoosterEffectManager(DBM dbm, BoosterManager boosterManager) {
        this.dbm = dbm;
        this.boosterManager = boosterManager;
        startBoosterTask();
    }

    private void startBoosterTask() {


        Bukkit.getScheduler().runTaskTimer(
                JobPlugin.getPlugin(JobPlugin.class),
                this::applyBoosterEffectsToAll,
                0L,
                120 * 20L
        );
    }

    public void applyBoosterEffectsToAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<String, Integer> activeBooster = boosterManager.getAllActiveBooster(player.getWorld(), player);
            if (activeBooster.isEmpty()) return;
            giveBoosterEffects(player);
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        giveBoosterEffects(event.getPlayer());
    }


    public void giveBoosterEffects(Player player) {
        Map<String, Integer> activeBooster = boosterManager.getAllActiveBooster(player.getWorld(), player);
        for (Booster booster : JobPlugin.boosters) {
            String name = booster.getName();
            String key = Booster.createDbKey(booster, player);
            if (!activeBooster.containsKey(key)) continue;
            int level = activeBooster.get(key);
            int remaining = dbm.getInt(JobPlugin.activeBooster, key, "TIME", 0);

            if (remaining > 1) {
                if (booster.getName().equalsIgnoreCase("HASTE") && level == 1) {
                    level = 2;
                }
                List<String> flyBlackList = new ArrayList<>(Arrays.asList("farmwelt", "farmwelt_nether", "farmwelt_the_end"));
                if(name.equals("FLY")){
                    if(flyBlackList.contains(player.getWorld().getName())){return;}
                }
                booster.activateBooster(new BoosterEffect(player, level));
            } else {
                if(!player.hasPermission("be.fly") || !player.isOp() || !player.hasPermission("essentials.fly") || !player.hasPermission("bp.fly")){
                    player.setAllowFlight(false);
                }
                booster.deactivateBooster(new BoosterEffect(player, level));
            }
        }
    }
}
