package de.Main.Job.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.Main.Job.GUI.BelohnungsGUI;
import de.Main.Job.GUI.maingui.JobGUI;
import de.Main.Job.manager.twoplayer.PartyJobManager;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandAlias("job|Job")
@CommandPermission("be.jobcommand")
public class JobCommand extends BaseCommand {
    DBM dbm;

    public JobCommand (DBM dbm ){
        this.dbm = dbm;
    }

    @Default
    public void onJobCommand(Player player) {

        BelohnungsGUI belohnungsGUI = new BelohnungsGUI(dbm);
        belohnungsGUI.createGUIS();

        JobGUI jobGUI = new JobGUI(dbm,  JobPlugin.getInstance().getJobManager());
        Inventory gui = jobGUI.createJobGUI(player);
        player.openInventory(gui);
    }


    @Subcommand("party invite")
    @CommandCompletion("@players")
    @CommandPermission("be.jobcommand.party.invite")
    public void onPartyInvite(Player player, String targetName) {

        PartyJobManager.getInstance().invitePlayer(player, targetName);

    }


    @Subcommand("party accept")
    @CommandPermission("be.jobcommand.party.accept")
    public void onPartyAccept(Player player, String targetName) {
        PartyJobManager.getInstance().acceptInvite(player, targetName);
    }


}
