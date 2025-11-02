package tw.allenalt.teamsz.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tw.allenalt.teamsz.TeamsZ;
import tw.allenalt.teamsz.models.Team;
import tw.allenalt.teamsz.models.TeamRank;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamManager {
    
    private final TeamsZ plugin;
    private final Map<String, Team> teams;
    private final Map<UUID, String> playerTeams;
    private final File teamsFolder;
    
    public TeamManager(TeamsZ plugin) {
        this.plugin = plugin;
        this.teams = new HashMap<>();
        this.playerTeams = new HashMap<>();
        this.teamsFolder = new File(plugin.getDataFolder(), "teams");
        
        if (!teamsFolder.exists()) {
            teamsFolder.mkdirs();
        }
        
        loadAllTeams();
    }
    
    public Team createTeam(String name, UUID owner) {
        if (teamExists(name)) {
            return null;
        }
        
        Team team = new Team(name, owner);
        teams.put(name.toLowerCase(), team);
        playerTeams.put(owner, name.toLowerCase());
        
        saveTeam(team);
        return team;
    }
    
    public boolean disbandTeam(String name) {
        Team team = getTeam(name);
        if (team == null) return false;
        
        // Remove all player mappings
        for (UUID member : team.getMembers().keySet()) {
            playerTeams.remove(member);
        }
        
        teams.remove(name.toLowerCase());
        
        // Delete team file
        File teamFile = new File(teamsFolder, name.toLowerCase() + ".yml");
        if (teamFile.exists()) {
            teamFile.delete();
        }
        
        return true;
    }
    
    public Team getTeam(String name) {
        return teams.get(name.toLowerCase());
    }
    
    public Team getPlayerTeam(UUID player) {
        String teamName = playerTeams.get(player);
        return teamName != null ? teams.get(teamName) : null;
    }
    
    public boolean hasTeam(UUID player) {
        return playerTeams.containsKey(player);
    }
    
    public boolean teamExists(String name) {
        return teams.containsKey(name.toLowerCase());
    }
    
    public void addPlayerToTeam(UUID player, Team team) {
        playerTeams.put(player, team.getName().toLowerCase());
        saveTeam(team);
    }
    
    public void removePlayerFromTeam(UUID player) {
        Team team = getPlayerTeam(player);
        if (team != null) {
            team.removeMember(player);
            playerTeams.remove(player);
            saveTeam(team);
        }
    }
    
    public Collection<Team> getAllTeams() {
        return teams.values();
    }
    
    public List<Team> getTopTeams(int limit) {
        List<Team> topTeams = new ArrayList<>(teams.values());
        topTeams.sort((t1, t2) -> Integer.compare(t2.getMemberCount(), t1.getMemberCount()));
        return topTeams.subList(0, Math.min(limit, topTeams.size()));
    }
    
    // Save/Load methods
    public void saveTeam(Team team) {
        File teamFile = new File(teamsFolder, team.getName().toLowerCase() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(teamFile);
        
        config.set("name", team.getName());
        config.set("owner", team.getOwner().toString());
        config.set("creationDate", team.getCreationDate());
        config.set("color", team.getColor());
        config.set("pvpEnabled", team.isPvpEnabled());
        config.set("isOpen", team.isOpen());
        config.set("balance", team.getBalance());
        
        // Save members
        List<String> membersList = new ArrayList<>();
        for (Map.Entry<UUID, TeamRank> entry : team.getMembers().entrySet()) {
            membersList.add(entry.getKey().toString() + ":" + entry.getValue().name());
        }
        config.set("members", membersList);
        
        // Save allies
        config.set("allies", new ArrayList<>(team.getAllies()));
        
        // Save home
        if (team.hasHome()) {
            config.set("home", team.getHome());
        }
        
        // Save warps
        for (Map.Entry<String, org.bukkit.Location> entry : team.getWarps().entrySet()) {
            config.set("warps." + entry.getKey(), entry.getValue());
        }
        
        try {
            config.save(teamFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save team: " + team.getName());
            e.printStackTrace();
        }
    }
    
    public void saveAllTeams() {
        for (Team team : teams.values()) {
            saveTeam(team);
        }
    }
    
    private void loadAllTeams() {
        if (!teamsFolder.exists()) return;
        
        File[] files = teamsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;
        
        for (File file : files) {
            loadTeam(file);
        }
        
        plugin.getLogger().info("Loaded " + teams.size() + " teams!");
    }
    
    private void loadTeam(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        String name = config.getString("name");
        UUID owner = UUID.fromString(config.getString("owner"));
        
        Team team = new Team(name, owner);
        team.setColor(config.getString("color", "&b"));
        team.setPvpEnabled(config.getBoolean("pvpEnabled", false));
        team.setOpen(config.getBoolean("isOpen", false));
        team.setBalance(config.getDouble("balance", 0.0));
        
        // Load members
        List<String> membersList = config.getStringList("members");
        for (String memberData : membersList) {
            String[] parts = memberData.split(":");
            UUID uuid = UUID.fromString(parts[0]);
            TeamRank rank = TeamRank.valueOf(parts[1]);
            team.getMembers().put(uuid, rank);
            playerTeams.put(uuid, name.toLowerCase());
        }
        
        // Load allies
        for (String ally : config.getStringList("allies")) {
            team.addAlly(ally);
        }
        
        // Load home
        if (config.contains("home")) {
            team.setHome(config.getLocation("home"));
        }
        
        // Load warps
        if (config.contains("warps")) {
            for (String warpName : config.getConfigurationSection("warps").getKeys(false)) {
                team.setWarp(warpName, config.getLocation("warps." + warpName));
            }
        }
        
        teams.put(name.toLowerCase(), team);
    }
}
