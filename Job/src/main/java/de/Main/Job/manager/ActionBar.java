package de.Main.Job.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.text.DecimalFormat;


public class ActionBar {

    public static void sendActionBar(DecimalFormat decimalFormat, Player player,
                                     String job, int userLevel, double blockXp, double moneyPerBlock, double xpToNextLevel) {


         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent( (ChatColor.GREEN + "+" + blockXp +
                 " XP | " + ChatColor.BLUE +  "+ " +decimalFormat.format(moneyPerBlock)+ "$ "
                 + ChatColor.GRAY + "| Job: " + job + ChatColor.RED + " | Level: " + userLevel
                 + ChatColor.AQUA + " | Fehlende XP: " + xpToNextLevel)));


   //    String msg = String.format(
   //            "§a+%s XP §7| §b+%s$ §7| §fJob: %s §7| §eLevel: %s §7| §3Fehlende XP: %s",
   //            blockXp,
   //            decimalFormat.format(moneyPerBlock),
   //            job,
   //            userLevel,
   //            xpToNextLevel
   //    );
   //    TextComponent text = new TextComponent(msg)
        //    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }
}
