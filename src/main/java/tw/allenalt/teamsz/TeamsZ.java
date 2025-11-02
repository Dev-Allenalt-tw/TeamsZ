package tw.allenalt.teamsz;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tw.allenalt.teamsz.commands.TeamCommand;
import tw.allenalt.teamsz.commands.TeamChatCommand;
import tw.allenalt.teamsz.managers.ConfigManager;
import tw.allenalt.teamsz.managers.TeamManager;
import tw.allenalt.teamsz.listeners.PlayerListener;
import tw.allenalt.teamsz.placeholders.TeamPlaceholders;

public class TeamsZ extends JavaPlugin {
    
    private static TeamsZ instance;
    private ConfigManager configManager;
    private TeamManager teamManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Load configurations
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        
        // Initialize managers
        teamManager = new TeamManager(this);
        
        // Register commands
        getCommand("team").setExecutor(new TeamCommand(this));
        getCommand("tc").setExecutor(new TeamChatCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Register PlaceholderAPI expansion
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TeamPlaceholders(this).register();
            getLogger().info("PlaceholderAPI hook registered!");
        }
        
        // Display startup message
        displayStartupMessage();
    }
    
    @Override
    public void onDisable() {
        if (teamManager != null) {
            teamManager.saveAllTeams();
        }
        getLogger().info("TeamsZ has been disabled!");
    }
    
    private void displayStartupMessage() {
        String version = getDescription().getVersion();
        String author = getDescription().getAuthors().isEmpty() ? "Dev_Allenalt_tw" : getDescription().getAuthors().get(0);
        
        Bukkit.getConsoleSender().sendMessage(colorize("&b&l╔═══════════════════════════════════╗"));
        Bukkit.getConsoleSender().sendMessage(colorize("&b&l║ &f&lTEAMSZ - Team & Clan Management &b&l║"));
        Bukkit.getConsoleSender().sendMessage(colorize("&b&l║ &aVersion: &f" + version + " &b&l║"));
        Bukkit.getConsoleSender().sendMessage(colorize("&b&l║ &aAuthor: &f" + author + " &b&l║"));
        Bukkit.getConsoleSender().sendMessage(colorize("&b&l║ &aStatus: &a&lLoaded Successfully &b&l║"));
        Bukkit.getConsoleSender().sendMessage(colorize("&b&l╚═══════════════════════════════════╝"));
        Bukkit.getConsoleSender().sendMessage(colorize("&6TeamsZ: &fThank you for using this plugin!"));
    }
    
    public static String colorize(String text) {
        return text.replace("&", "§");
    }
    
    public static TeamsZ getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public TeamManager getTeamManager() {
        return teamManager;
    }
}
