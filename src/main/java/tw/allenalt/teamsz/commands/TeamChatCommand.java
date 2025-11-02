package tw.allenalt.teamsz.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.allenalt.teamsz.TeamsZ;
import tw.allenalt.teamsz.models.Team;

public class TeamChatCommand implements CommandExecutor {
    
    private final TeamsZ plugin;
    
    public TeamChatCommand(TeamsZ plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.not-a-player"));
            return true;
        }
        
        Player player = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
        
        if (team == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("command-messages.leave.not-in-team"));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(TeamsZ.colorize("&cUsage: /tc <message>"));
            return true;
        }
        
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        
        String formattedMessage = TeamsZ.colorize(
                "&8[&bTeam Chat&8] &b" + team.getName() + " &7| &e" + 
                team.getRank(player.getUniqueId()).getDisplayName() + " &f" + 
                player.getName() + ": &a" + message.toString().trim()
        );
        
        // Send to all team members
        for (java.util.UUID memberUUID : team.getMembers().keySet()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage(formattedMessage);
            }
        }
        
        return true;
    }
}
