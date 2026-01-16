package de.Main.Job.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.annotation.*;
import de.Main.JobPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


@CommandAlias("editReward")
@CommandPermission("be.jobcommand.editReward")
public class rewardCommand extends BaseCommand {


    public rewardCommand(CommandManager manager) {
        manager.getCommandCompletions().registerAsyncCompletion("jobs", c ->
                JobPlugin.rewardJobs
        );
    }

    @Default
    @CommandCompletion("@jobs")
    public void onEdit(Player player, String job) {
        JobPlugin.getInstance().getRewardManager().openEditRewardGUI(player, job);
    }


    @Subcommand("advent")
    public void onTestGUI(Player player) {


        Inventory inv = Bukkit.createInventory(null, 54, "uF80A ");
        player.openInventory(inv);


    }





}
