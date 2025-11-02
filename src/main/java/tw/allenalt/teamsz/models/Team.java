package tw.allenalt.teamsz.models;

import org.bukkit.Location;
import java.util.*;

public class Team {
    
    private final String name;
    private UUID owner;
    private final Map<UUID, TeamRank> members;
    private final Set<UUID> invites;
    private final Set<String> allies;
    
    private Location home;
    private final Map<String, Location> warps;
    
    private boolean pvpEnabled;
    private boolean isOpen;
    private double balance;
    private String color;
    
    private final long creationDate;
    
    public Team(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members = new HashMap<>();
        this.invites = new HashSet<>();
        this.allies = new HashSet<>();
        this.warps = new HashMap<>();
        this.pvpEnabled = false;
        this.isOpen = false;
        this.balance = 0.0;
        this.color = "&b";
        this.creationDate = System.currentTimeMillis();
        
        // Add owner to members
        members.put(owner, TeamRank.OWNER);
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public void setOwner(UUID owner) {
        this.owner = owner;
        members.put(owner, TeamRank.OWNER);
    }
    
    public Map<UUID, TeamRank> getMembers() {
        return members;
    }
    
    public TeamRank getRank(UUID player) {
        return members.getOrDefault(player, TeamRank.MEMBER);
    }
    
    public void setRank(UUID player, TeamRank rank) {
        members.put(player, rank);
    }
    
    public void addMember(UUID player, TeamRank rank) {
        members.put(player, rank);
        invites.remove(player);
    }
    
    public void removeMember(UUID player) {
        members.remove(player);
    }
    
    public boolean isMember(UUID player) {
        return members.containsKey(player);
    }
    
    public int getMemberCount() {
        return members.size();
    }
    
    // Invites
    public void addInvite(UUID player) {
        invites.add(player);
    }
    
    public void removeInvite(UUID player) {
        invites.remove(player);
    }
    
    public boolean hasInvite(UUID player) {
        return invites.contains(player);
    }
    
    // Allies
    public void addAlly(String teamName) {
        allies.add(teamName);
    }
    
    public void removeAlly(String teamName) {
        allies.remove(teamName);
    }
    
    public boolean isAlly(String teamName) {
        return allies.contains(teamName);
    }
    
    public Set<String> getAllies() {
        return allies;
    }
    
    // Locations
    public Location getHome() {
        return home;
    }
    
    public void setHome(Location home) {
        this.home = home;
    }
    
    public boolean hasHome() {
        return home != null;
    }
    
    public Map<String, Location> getWarps() {
        return warps;
    }
    
    public void setWarp(String name, Location location) {
        warps.put(name, location);
    }
    
    public Location getWarp(String name) {
        return warps.get(name);
    }
    
    public void removeWarp(String name) {
        warps.remove(name);
    }
    
    public boolean hasWarp(String name) {
        return warps.containsKey(name);
    }
    
    // Settings
    public boolean isPvpEnabled() {
        return pvpEnabled;
    }
    
    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public void setOpen(boolean open) {
        isOpen = open;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public void addBalance(double amount) {
        this.balance += amount;
    }
    
    public void removeBalance(double amount) {
        this.balance -= amount;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public long getCreationDate() {
        return creationDate;
    }
    
    public String getColoredName() {
        return color + name;
    }
}
