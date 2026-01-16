package de.Main.Job.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.*;
import de.Main.Job.GUI.booster.BoosterGUI;
import de.Main.Job.manager.JobManager;
import de.Main.Job.manager.booster.Booster;
import de.Main.Job.manager.booster.BoosterEffect;
import de.Main.Job.manager.booster.BoosterManager;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandAlias("Booster")
@CommandPermission("be.booster")
public class BoosterCommand extends BaseCommand {
    DBM dbm;
    JobManager jobManager;
    BoosterManager boosterManager;

    public BoosterCommand(DBM dbm, JobManager jobManager, CommandManager manager, BoosterManager boosterManager) {
        this.dbm = dbm;
        this.jobManager = jobManager;
        this.boosterManager = boosterManager;

        manager.getCommandCompletions().registerAsyncCompletion("activeBooster", c -> {
            Object issuer = c.getIssuer().getIssuer();
            if (!(issuer instanceof Player player)) return java.util.Collections.emptyList();
            return boosterManager.getAllActiveBooster(player.getWorld(), player)
                    .keySet().stream()
                    .map(key -> key.split("#")[0])
                    .distinct()
                    .collect(Collectors.toList());
        });
    }

    @Default
    public void onBoosterCommand(Player player) {
        BoosterGUI boosterGUI = new BoosterGUI(dbm);
        player.openInventory(boosterGUI.createGUI(player));
    }

    @Subcommand("deactivate")
    @CommandCompletion("@activeBooster")
    @CommandPermission("be.booster.activate")
    public void onBoosterActivate(Player player, @Name("Name") String boosterName) {

        for (Booster booster : JobPlugin.boosters) {
            if (booster.getName().equalsIgnoreCase(boosterName)) {
                dbm.remove(JobPlugin.activeBooster, Booster.createDbKey(booster, player));
                booster.deactivateBooster(new BoosterEffect(player, 0));
                player.sendMessage(JobPlugin.prefix + "Der Booster " + boosterName + " wurde deaktiviert.");
            }

        }


    }

}
