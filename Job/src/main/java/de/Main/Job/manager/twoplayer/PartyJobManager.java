package de.Main.Job.manager.twoplayer;

import com.plotsquared.bukkit.player.BukkitPlayer;
import de.Main.JobPlugin;
import de.Main.database.DBM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PartyJobManager {

    public static PartyJobManager instance;
    DBM dbm = JobPlugin.getInstance().getDbm3();

    public PartyJobManager() {
        instance = this;
    }

    public static PartyJobManager getInstance() {
        return instance;
    }

    public void invitePlayer(Player player, String playerName) {
        HashMap<String, Object> values = new HashMap<>();
        values.put("owner", player.getName());
        values.put("owner_uuid", player.getUniqueId().toString());
        values.put("invited", playerName);
        dbm.insertDefaultValues(JobPlugin.doublePlayerTableName, player.getUniqueId(), values);
        Player target = Bukkit.getPlayer(playerName);
        if (!target.isOnline()) {
            player.sendMessage(JobPlugin.prefix + "§cDer Spieler ist nicht online!");
            return;
        }

        target.sendMessage(JobPlugin.prefix + "§aDu wurdest von §6" + player.getName() + " §aeingeladen, seinem Job beizutreten! Nutze §6/job party accept §aum die Einladung anzunehmen.");
        player.sendMessage(JobPlugin.prefix + "§aDu hast " + target.getName() + " eingeladen, deinem Job beizutreten!");
    }


    public void acceptInvite(Player player, String targetName) {
        HashMap<String, Object> values = new HashMap<>();
        values.put("member", targetName);
        values.put("invited", "");
        values.put("owner", targetName);
        dbm.insertDefaultValues(JobPlugin.doublePlayerTableName, player.getUniqueId(), values);

        if (player.isOnline()) {
            player.sendMessage(JobPlugin.prefix + "§aDu bist nun dem Job von §6" + targetName + " §abeigetreten!");
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (!targetPlayer.isOnline()) {
            return;
        } else {
            targetPlayer.sendMessage(JobPlugin.prefix + "§aDer Spieler §6" + player.getName() + " §ahat deine Einladung angenommen und ist deinem Job beigetreten!");
        }
    }
}
