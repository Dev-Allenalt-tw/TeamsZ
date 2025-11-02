package tw.allenalt.teamsz.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tw.allenalt.teamsz.TeamsZ;
import tw.allenalt.teamsz.models.Team;

public class TeamPlaceholders extends PlaceholderExpansion {
    
    private final TeamsZ plugin;
    
    public TeamPlaceholders(TeamsZ plugin) {
        this.plugin = plugin;
    }
    
    @Override
    @NotNull
    public String getAuthor() {
        return "Dev_Allenalt_tw";
    }
    
    @Override
    @NotNull
    public String getIdentifier() {
        return "teamsz";
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }
        
        Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
        
        // %teamsz_name%
        if (identifier.equals("name")) {
            return team != null ? team.getName() : "N/A";
        }
        
        // %teamsz_colored_name%
        if (identifier.equals("colored_name")) {
            return team != null ? team.getColoredName() : TeamsZ.colorize("&7N/A");
        }
        
        // %teamsz_rank%
        if (identifier.equals("rank")) {
            if (team == null) return "N/A";
            return team.getRank(player.getUniqueId()).getDisplayName();
        }
        
        // %teamsz_members%
        if (identifier.equals("members")) {
            return team != null ? String.valueOf(team.getMemberCount()) : "0";
        }
        
        // %teamsz_balance%
        if (identifier.equals("balance")) {
            return team != null ? String.valueOf(team.getBalance()) : "0.0";
        }
        
        // %teamsz_owner%
        if (identifier.equals("owner")) {
            if (team == null) return "N/A";
            return org.bukkit.Bukkit.getOfflinePlayer(team.getOwner()).getName();
        }
        
        // %teamsz_pvp%
        if (identifier.equals("pvp")) {
            if (team == null) return "N/A";
            return team.isPvpEnabled() ? "Enabled" : "Disabled";
        }
        
        // %teamsz_open%
        if (identifier.equals("open")) {
            if (team == null) return "N/A";
            return team.isOpen() ? "Open" : "Invite Only";
        }
        
        return null;
    }
}
