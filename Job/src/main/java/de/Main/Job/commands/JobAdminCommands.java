package de.Main.Job.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("jobadmin")
@CommandPermission("be.jobcommand.admin")
public class JobAdminCommands extends BaseCommand {

    private final DBM dbm;
    public static String prefix = "§b§lJobSystem §8» §7";

    public JobAdminCommands(DBM dbm) {
        this.dbm = dbm;
    }

    @Default
    public void onCommand(Player player) {
        player.sendMessage(prefix + "§7/jobadmin set xp|level <Job> <Wert>");
        player.sendMessage(prefix + "§7/jobadmin add xp|level <Job> <Wert>");
        player.sendMessage(prefix + "§7/jobadmin check xp|level <Job>");
    }

    private void setValue(Player player, String job, String type, int value) {
        if (value < 0) {
            player.sendMessage(prefix + "§cDer Wert muss positiv sein!");
            return;
        }
        dbm.setInt(JobPlugin.tableName, player.getUniqueId(), job + "_" + type, value);
        player.sendMessage(prefix + "§a" + job + " " + type + " auf " + value + " gesetzt.");
    }

    private void addValue(Player player, String job, String type, int value, int defaultValue) {
        if (value < 0) {
            player.sendMessage(prefix + "§cDer Wert muss positiv sein!");
            return;
        }
        int current = dbm.getInt(JobPlugin.tableName, player.getUniqueId(), job + "_" + type, defaultValue);
        dbm.setInt(JobPlugin.tableName, player.getUniqueId(), job + "_" + type, current + value);
        player.sendMessage(prefix + "§aNeues Level Auf: Level " + (current + value));
    }

    private void checkValue(Player sender, Player target, String job, String type, int defaultValue) {
        int value = dbm.getInt(JobPlugin.tableName, target.getUniqueId(), job + "_" + type, defaultValue);
        sender.sendMessage(prefix + "§e" + job + " " + type + " von §b" + target.getName() + "§e: §b" + value);
    }


    @Subcommand("set xp Miner")
    @CommandPermission("be.jobcommand.admin.set.xp.miner")
    public void setMinerXP(Player player, @Name("xp") int xp) {
        setValue(player, "Miner", "XP", xp);
    }

    @Subcommand("set level Miner")
    @CommandPermission("be.jobcommand.admin.set.level.miner")
    public void setMinerLVL(Player player, @Name("level") int lvl) {
        setValue(player, "Miner", "LVL", lvl);
    }

    @Subcommand("add xp Miner")
    @CommandPermission("be.jobcommand.admin.add.xp.miner")
    public void addMinerXP(Player player, @Name("xp") int xp) {
        addValue(player, "Miner", "XP", xp, 0);
    }

    @Subcommand("add level Miner")
    @CommandPermission("be.jobcommand.admin.add.level.miner")
    public void addMinerLVL(Player player, @Name("level") int lvl) {
        addValue(player, "Miner", "LVL", lvl, 1);
    }

    @Subcommand("check xp Miner")
    @CommandPermission("be.jobcommand.admin.check.xp.miner")
    public void checkMinerXP(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Miner", "XP", 0);
    }

    @Subcommand("check level Miner")
    @CommandPermission("be.jobcommand.admin.check.level.miner")
    public void checkMinerLVL(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Miner", "LVL", 1);
    }

    // Gräber
    @Subcommand("set xp Gräber")
    @CommandPermission("be.jobcommand.admin.set.xp.gräber")
    public void setGraeberXP(Player player, @Name("xp") int xp) {
        setValue(player, "Gräber", "XP", xp);
    }

    @Subcommand("set level Gräber")
    @CommandPermission("be.jobcommand.admin.set.level.gräber")
    public void setGraeberLVL(Player player, @Name("level") int lvl) {
        setValue(player, "Gräber", "LVL", lvl);
    }

    @Subcommand("add xp Gräber")
    @CommandPermission("be.jobcommand.admin.add.xp.gräber")
    public void addGraeberXP(Player player, @Name("xp") int xp) {
        addValue(player, "Gräber", "XP", xp, 0);
    }

    @Subcommand("add level Gräber")
    @CommandPermission("be.jobcommand.admin.add.level.gräber")
    public void addGraeberLVL(Player player, @Name("level") int lvl) {
        addValue(player, "Gräber", "LVL", lvl, 1);
    }

    @Subcommand("check xp Gräber")
    @CommandPermission("be.jobcommand.admin.check.xp.gräber")
    public void checkGraeberXP(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Gräber", "XP", 0);
    }

    @Subcommand("check level Gräber")
    @CommandPermission("be.jobcommand.admin.check.level.gräber")
    public void checkGraeberLVL(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Gräber", "LVL", 1);
    }

    // Fischer
    @Subcommand("set xp Fischer")
    @CommandPermission("be.jobcommand.admin.set.xp.fischer")
    public void setFischerXP(Player player, @Name("xp") int xp) {
        setValue(player, "Fischer", "XP", xp);
    }

    @Subcommand("set level Fischer")
    @CommandPermission("be.jobcommand.admin.set.level.fischer")
    public void setFischerLVL(Player player, @Name("level") int lvl) {
        setValue(player, "Fischer", "LVL", lvl);
    }

    @Subcommand("add xp Fischer")
    @CommandPermission("be.jobcommand.admin.add.xp.fischer")
    public void addFischerXP(Player player, @Name("xp") int xp) {
        addValue(player, "Fischer", "XP", xp, 0);
    }

    @Subcommand("add level Fischer")
    @CommandPermission("be.jobcommand.admin.add.level.fischer")
    public void addFischerLVL(Player player, @Name("level") int lvl) {
        addValue(player, "Fischer", "LVL", lvl, 1);
    }

    @Subcommand("check xp Fischer")
    @CommandPermission("be.jobcommand.admin.check.xp.fischer")
    public void checkFischerXP(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Fischer", "XP", 0);
    }

    @Subcommand("check level Fischer")
    @CommandPermission("be.jobcommand.admin.check.level.fischer")
    public void checkFischerLVL(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Fischer", "LVL", 1);
    }

    // Holzfäller
    @Subcommand("set xp Holzfäller")
    @CommandPermission("be.jobcommand.admin.set.xp.holzfäller")
    public void setHolzfaellerXP(Player player, @Name("xp") int xp) {
        setValue(player, "Holzfäller", "XP", xp);
    }

    @Subcommand("set level Holzfäller")
    @CommandPermission("be.jobcommand.admin.set.level.holzfäller")
    public void setHolzfaellerLVL(Player player, @Name("level") int lvl) {
        setValue(player, "Holzfäller", "LVL", lvl);
    }

    @Subcommand("add xp Holzfäller")
    @CommandPermission("be.jobcommand.admin.add.xp.holzfäller")
    public void addHolzfaellerXP(Player player, @Name("xp") int xp) {
        addValue(player, "Holzfäller", "XP", xp, 0);
    }

    @Subcommand("add level Holzfäller")
    @CommandPermission("be.jobcommand.admin.add.level.holzfäller")
    public void addHolzfaellerLVL(Player player, @Name("level") int lvl) {
        addValue(player, "Holzfäller", "LVL", lvl, 1);
    }

    @Subcommand("check xp Holzfäller")
    @CommandPermission("be.jobcommand.admin.check.xp.holzfäller")
    public void checkHolzfaellerXP(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Holzfäller", "XP", 0);
    }

    @Subcommand("check level Holzfäller")
    @CommandPermission("be.jobcommand.admin.check.level.holzfäller")
    public void checkHolzfaellerLVL(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Holzfäller", "LVL", 1);
    }

    // Farmer
    @Subcommand("set xp Farmer")
    @CommandPermission("be.jobcommand.admin.set.xp.farmer")
    public void setFarmerXP(Player player, @Name("xp") int xp) {
        setValue(player, "Farmer", "XP", xp);
    }

    @Subcommand("set level Farmer")
    @CommandPermission("be.jobcommand.admin.set.level.farmer")
    public void setFarmerLVL(Player player, @Name("level") int lvl) {
        setValue(player, "Farmer", "LVL", lvl);
    }

    @Subcommand("add xp Farmer")
    @CommandPermission("be.jobcommand.admin.add.xp.farmer")
    public void addFarmerXP(Player player, @Name("xp") int xp) {
        addValue(player, "Farmer", "XP", xp, 0);
    }

    @Subcommand("add level Farmer")
    @CommandPermission("be.jobcommand.admin.add.level.farmer")
    public void addFarmerLVL(Player player, @Name("level") int lvl) {
        addValue(player, "Farmer", "LVL", lvl, 1);
    }

    @Subcommand("check xp Farmer")
    @CommandPermission("be.jobcommand.admin.check.xp.farmer")
    public void checkFarmerXP(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Farmer", "XP", 0);
    }

    @Subcommand("check level Farmer")
    @CommandPermission("be.jobcommand.admin.check.level.farmer")
    public void checkFarmerLVL(Player sender, @Name("spieler") String spielerName) {
        Player target = Bukkit.getPlayerExact(spielerName);
        if (target == null) {
            sender.sendMessage(prefix + "§cSpieler nicht gefunden!");
            return;
        }
        checkValue(sender, target, "Farmer", "LVL", 1);
    }
}
