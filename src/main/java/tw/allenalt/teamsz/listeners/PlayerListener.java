package tw.allenalt.teamsz.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tw.allenalt.teamsz.TeamsZ;
import tw.allenalt.teamsz.commands.TeamCommand;
import tw.allenalt.teamsz.models.Team;

public class PlayerListener implements Listener {
    
    private final TeamsZ plugin;
    
    public PlayerListener(TeamsZ plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
        
        if (team != null) {
            player.sendMessage(TeamsZ.colorize("&aWelcome back! You are in team: " + team.getColoredName()));
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup any pending metadata
        Player player = event.getPlayer();
        if (player.hasMetadata("TEAMSZ_PENDING_CREATE")) {
            player.removeMetadata("TEAMSZ_PENDING_CREATE", plugin);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();
        
        Team victimTeam = plugin.getTeamManager().getPlayerTeam(victim.getUniqueId());
        Team attackerTeam = plugin.getTeamManager().getPlayerTeam(attacker.getUniqueId());
        
        // Check if both players are in the same team
        if (victimTeam != null && attackerTeam != null && victimTeam.equals(attackerTeam)) {
            // Check if friendly fire is disabled
            if (!victimTeam.isPvpEnabled()) {
                event.setCancelled(true);
                attacker.sendMessage(TeamsZ.colorize("&cYou cannot hurt your teammates!"));
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Check if this is the team creation confirmation GUI
        if (!title.contains("Confirm") || !title.contains("Creation")) {
            return;
        }
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        
        Material clickedItem = event.getCurrentItem().getType();
        
        if (clickedItem == Material.EMERALD_BLOCK) {
            // Confirm creation
            player.closeInventory();
            
            // Get the TeamCommand instance and finalize creation
            TeamCommand teamCommand = new TeamCommand(plugin);
            teamCommand.finalizeTeamCreation(player);
            
        } else if (clickedItem == Material.REDSTONE_BLOCK) {
            // Cancel creation
            player.closeInventory();
            player.removeMetadata("TEAMSZ_PENDING_CREATE", plugin);
            player.sendMessage(TeamsZ.colorize("&cTeam creation cancelled."));
        }
    }
}
