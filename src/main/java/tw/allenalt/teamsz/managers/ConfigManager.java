package tw.allenalt.teamsz.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tw.allenalt.teamsz.TeamsZ;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {
    
    private final TeamsZ plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    
    public ConfigManager(TeamsZ plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfigs() {
        // Create plugin folder if not exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // Load config.yml
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Load messages.yml
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        plugin.getLogger().info("Configuration files loaded!");
    }
    
    private void saveResource(String resourcePath) {
        File outFile = new File(plugin.getDataFolder(), resourcePath);
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in != null) {
                Files.copy(in, outFile.toPath());
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save " + resourcePath);
            e.printStackTrace();
        }
    }
    
    public void reloadConfigs() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public FileConfiguration getMessages() {
        return messages;
    }
    
    public String getMessage(String path) {
        String message = messages.getString(path, "&cMessage not found: " + path);
        String prefix = messages.getString("general.plugin-prefix", "&8[&bTeamsZ&8] &r");
        return TeamsZ.colorize(message.replace("{prefix}", prefix));
    }
    
    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }
    
    // Config getters
    public int getMaxTeamsPerPlayer() {
        return config.getInt("limits.max-teams-per-player", 1);
    }
    
    public int getMaxMembersPerTeam() {
        return config.getInt("limits.max-members-per-team", 10);
    }
    
    public int getMinTeamNameLength() {
        return config.getInt("limits.min-team-name-length", 3);
    }
    
    public int getMaxTeamNameLength() {
        return config.getInt("limits.max-team-name-length", 16);
    }
    
    public double getCreationCost() {
        return config.getDouble("limits.creation-cost-economy", 0.0);
    }
    
    public boolean isFriendlyFireEnabled() {
        return config.getBoolean("features.friendly-fire-enabled", true);
    }
    
    public boolean isHomeEnabled() {
        return config.getBoolean("features.home.enabled", true);
    }
    
    public int getHomeCooldown() {
        return config.getInt("features.home.cooldown-seconds", 60);
    }
    
    public int getHomeTeleportDelay() {
        return config.getInt("features.home.teleport-delay-seconds", 3);
    }
    
    public boolean isWarpsEnabled() {
        return config.getBoolean("features.warps.enabled", true);
    }
    
    public int getMaxWarpsPerTeam() {
        return config.getInt("features.warps.max-warps-per-team", 5);
    }
    
    public double getStartingBalance() {
        return config.getDouble("features.balance.starting-balance", 0.0);
    }
    
    public int getRequiredRankForPermission(String permission) {
        return config.getInt("ranks.rank-permissions." + permission, 5);
    }
    
    public String getRankName(int level) {
        return config.getString("ranks.rank-" + level + "-name", "Unknown");
    }
}
