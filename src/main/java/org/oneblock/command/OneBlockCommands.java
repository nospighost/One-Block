package org.oneblock.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import de.Main.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.oneblock.OneBlock;
import org.oneblock.gui.OneBlockGUI;
import org.oneblock.utils.MessagesManager;
import org.oneblock.utils.OneBlockManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


@CommandAlias("oneblock|ob")
public class OneBlockCommands extends BaseCommand {


    ConfigManager configManager;
    OneBlockManager obManager = OneBlockManager.getInstance();
    PaperCommandManager commandManager;

    public OneBlockCommands(ConfigManager configManager, PaperCommandManager commandManager) {
        this.configManager = configManager;
        this.commandManager = commandManager;


        commandManager.getCommandCompletions().registerAsyncCompletion("members", c -> {
            FileConfiguration userDataConfig = configManager.getUserDataConfig(c.getPlayer());
            List<String> members = new ArrayList<>();
            for (String uuid : userDataConfig.getStringList("trust")) {
                members.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            }

            for (String uuid : userDataConfig.getStringList("add")) {
                members.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            }
            return members;
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("invited", c -> {
            return getAllInvited(c.getPlayer());
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("denied", c -> {
            List<String> denied = new ArrayList<>();
            FileConfiguration userDataConfig = configManager.getUserDataConfig(c.getPlayer());
            for (String uuid : userDataConfig.getStringList("denied")) {
                denied.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            }
            return denied;
        });

    }

    private Collection<String> getAllInvited(Player player) {
        List<String> players = new ArrayList<>();

        for(UUID uuid : configManager.getAllUserUUIDs()){
            FileConfiguration config = configManager.getUserDataConfig(Bukkit.getPlayer(uuid));
            if(config.getStringList("invitedTrust").contains(player.getUniqueId().toString())){
                players.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
            if(config.getStringList("invitedAdd").contains(player.getUniqueId().toString())){
                players.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
        }

        return players;
    }

    @Default
    public void onDefault(Player player) {
        player.openInventory(OneBlockGUI.getInv(player));

    }


    @Subcommand("join")
    @CommandPermission("oneblock.join")
    public void onJoinCommand(Player player) {
        obManager.joinIsland(player, player.getName());
    }

    @Subcommand("visit")
    @CommandPermission("oneblock.visit")
    @CommandCompletion("@players")
    public void onVisit(Player player, String targetName) {
        obManager.joinIsland(player, targetName);
    }


    @Subcommand("create")
    @CommandPermission("oneblock.create")
    public void onCreateCommand(Player player) {


        if (obManager.hasIsland(player)) {
            player.sendMessage(MessagesManager.instance.getMessage("already-has-island", player));
            return;
        }

        if (obManager.createIsland(player)) { ///  Create the Island, and returns a boolean, if the island was correctly created
            player.sendMessage(MessagesManager.instance.getMessage("create-success", player));
        } else {
            player.sendMessage(MessagesManager.instance.getMessage("create-error", player));
        }
    }

    @Subcommand("delete")
    @CommandPermission("oneblock.delete")
    private void onDeleteCommand(Player player) {
        obManager.deleteIsland(player);
    }

    @Subcommand("trust")
    @CommandPermission("oneblock.trust")
    @CommandCompletion("@players")
    public void onInviteCommandTrust(Player player, String targetName) {
        obManager.inviteUserToIsland(player, Bukkit.getPlayer(targetName).getUniqueId(), "Trust");

    }


    @Subcommand("add")
    @CommandPermission("oneblock.add")
    @CommandCompletion("@players")
    public void onInviteCommandAdd(Player player, String targetName) {
        obManager.inviteUserToIsland(player, Bukkit.getPlayer(targetName).getUniqueId(), "Add");

    }

    @Subcommand("accept")
    @CommandPermission("oneblock.accept")
    @CommandCompletion("@invited")
    public void onAcceptInviteCommand(Player player, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        obManager.acceptInvite(player, target);
    }

    @Subcommand("remove")
    @CommandPermission("oneblock.remove")
    @CommandCompletion("@members")
    public void onRemoveInviteCommand(Player player, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        obManager.removePlayer(player, target.getUniqueId().toString());

    }

    @Subcommand("deny")
    @CommandPermission("oneblock.deny")
    @CommandCompletion("@players")
    public void onDenyInviteCommand(Player player, String targetName) {
        obManager.denyPlayer(player, player.getUniqueId().toString());
    }

    @Subcommand("unban")
    @CommandPermission("oneblock.unban")
    @CommandCompletion("@denied")
    public void onUnbanCommand(Player player, String targetName) {
        obManager.unbanPlayer(player, player.getUniqueId().toString());
    }

    @Subcommand("allow visit")
    @CommandPermission("oneblock.allowVisit")
    public void onAllowVisitCommand(Player player) {
        obManager.allowVisit(player);
    }

    @Subcommand("reload configs")
    @CommandPermission("oneblock.reload.configs")
    public void onReloadConfigsCommand(Player player) {
        try {
            MessagesManager.instance.reload();
            OneBlock.getInstance().reloadConfig();
            player.sendMessage("§aOneBlock configs wurden neu geladen.");
        } catch (Exception e) {
            e.addSuppressed(e);
            player.sendMessage(MessagesManager.instance.getMessage("reload-error", player));
        }
    }


}
