package tw.allenalt.teamsz.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tw.allenalt.teamsz.TeamsZ;
import tw.allenalt.teamsz.managers.ConfigManager;
import tw.allenalt.teamsz.managers.TeamManager;
import tw.allenalt.teamsz.models.Team;
import tw.allenalt.teamsz.models.TeamRank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {
    
    private final TeamsZ plugin;
    private final TeamManager teamManager;
    private final ConfigManager configManager;
    
    public TeamCommand(TeamsZ plugin) {
        this.plugin = plugin;
        this.teamManager = plugin.getTeamManager();
        this.configManager = plugin.getConfigManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(configManager.getMessage("general.not-a-player"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "disband":
                handleDisband(player);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "list":
                handleList(player);
                break;
            case "invite":
                handleInvite(player, args);
                break;
            case "join":
                handleJoin(player, args);
                break;
            case "accept":
                handleAccept(player, args);
                break;
            case "kick":
                handleKick(player, args);
                break;
            case "home":
                handleHome(player);
                break;
            case "sethome":
                handleSetHome(player);
                break;
            case "pvp":
                handlePvp(player);
                break;
            case "promote":
                handlePromote(player, args);
                break;
            case "demote":
                handleDemote(player, args);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(configManager.getMessage("command-messages.create.invalid-arguments"));
            return;
        }
        
        if (teamManager.hasTeam(player.getUniqueId())) {
            player.sendMessage(configManager.getMessage("command-messages.create.already-in-team"));
            return;
        }
        
        String teamName = args[1];
        
        if (teamName.length() < configManager.getMinTeamNameLength()) {
            player.sendMessage(configManager.getMessage("command-messages.create.name-too-short",
                    "{min_length}", String.valueOf(configManager.getMinTeamNameLength())));
            return;
        }
        
        if (teamName.length() > configManager.getMaxTeamNameLength()) {
            player.sendMessage(configManager.getMessage("command-messages.create.name-too-long",
                    "{max_length}", String.valueOf(configManager.getMaxTeamNameLength())));
            return;
        }
        
        if (teamManager.teamExists(teamName)) {
            player.sendMessage(configManager.getMessage("command-messages.create.name-already-exists",
                    "{team_name}", teamName));
            return;
        }
        
        // Open confirmation GUI
        openCreationConfirmationGUI(player, teamName);
    }
    
    private void openCreationConfirmationGUI(Player player, String teamName) {
        String title = TeamsZ.colorize("&8» &6Confirm &b" + teamName + " &6Creation");
        Inventory gui = Bukkit.createInventory(null, 27, title);
        
        // Filler
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, filler);
        }
        
        // Confirm button
        ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(TeamsZ.colorize("&a&lCONFIRM TEAM CREATION"));
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(TeamsZ.colorize("&7Clicking this button will finalize"));
        confirmLore.add(TeamsZ.colorize("&7your team &b" + teamName + "&7."));
        confirmLore.add("");
        confirmLore.add(TeamsZ.colorize("&a&lCREATION IS FREE!"));
        confirmLore.add(TeamsZ.colorize("&a&lCLICK TO CONFIRM"));
        confirmMeta.setLore(confirmLore);
        confirm.setItemMeta(confirmMeta);
        gui.setItem(11, confirm);
        
        // Cancel button
        ItemStack cancel = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(TeamsZ.colorize("&c&lCANCEL"));
        List<String> cancelLore = new ArrayList<>();
        cancelLore.add(TeamsZ.colorize("&7Click to close the menu and"));
        cancelLore.add(TeamsZ.colorize("&7cancel team creation."));
        cancelMeta.setLore(cancelLore);
        cancel.setItemMeta(cancelMeta);
        gui.setItem(15, cancel);
        
        // Info item
        ItemStack info = new ItemStack(Material.NAME_TAG);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(TeamsZ.colorize("&6&lTEAM DETAILS"));
        List<String> infoLore = new ArrayList<>();
        infoLore.add(TeamsZ.colorize("&7Team Name: &b" + teamName));
        infoLore.add(TeamsZ.colorize("&7Max Members: &e" + configManager.getMaxMembersPerTeam()));
        infoLore.add(TeamsZ.colorize("&7Cost: &aFREE"));
        infoLore.add(TeamsZ.colorize("&7Starting Balance: &e$" + configManager.getStartingBalance()));
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        gui.setItem(13, info);
        
        // Store team name in player metadata for confirmation
        player.setMetadata("TEAMSZ_PENDING_CREATE", new org.bukkit.metadata.FixedMetadataValue(plugin, teamName));
        
        player.openInventory(gui);
    }
    
    public void finalizeTeamCreation(Player player) {
        if (!player.hasMetadata("TEAMSZ_PENDING_CREATE")) return;
        
        String teamName = player.getMetadata("TEAMSZ_PENDING_CREATE").get(0).asString();
        player.removeMetadata("TEAMSZ_PENDING_CREATE", plugin);
        
        Team team = teamManager.createTeam(teamName, player.getUniqueId());
        if (team != null) {
            player.sendMessage(configManager.getMessage("command-messages.create.success",
                    "{team_name}", teamName));
        }
    }
    
    private void handleDisband(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(configManager.getMessage("command-messages.disband.not-owner"));
            return;
        }
        
        String teamName = team.getName();
        teamManager.disbandTeam(teamName);
        player.sendMessage(configManager.getMessage("command-messages.disband.success-player",
                "{team_name}", teamName));
    }
    
    private void handleLeave(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        if (team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(configManager.getMessage("command-messages.leave.owner-must-disband"));
            return;
        }
        
        String teamName = team.getName();
        teamManager.removePlayerFromTeam(player.getUniqueId());
        player.sendMessage(configManager.getMessage("command-messages.leave.success",
                "{team_name}", teamName));
    }
    
    private void handleInfo(Player player, String[] args) {
        Team team;
        if (args.length > 1) {
            team = teamManager.getTeam(args[1]);
        } else {
            team = teamManager.getPlayerTeam(player.getUniqueId());
        }
        
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        player.sendMessage(TeamsZ.colorize("&8&m----------------------"));
        player.sendMessage(TeamsZ.colorize("&6Team: " + team.getColoredName()));
        player.sendMessage(TeamsZ.colorize("&7Owner: &e" + Bukkit.getOfflinePlayer(team.getOwner()).getName()));
        player.sendMessage(TeamsZ.colorize("&7Members: &e" + team.getMemberCount() + "/" + configManager.getMaxMembersPerTeam()));
        player.sendMessage(TeamsZ.colorize("&7Balance: &a$" + team.getBalance()));
        player.sendMessage(TeamsZ.colorize("&7PvP: " + (team.isPvpEnabled() ? "&aEnabled" : "&cDisabled")));
        player.sendMessage(TeamsZ.colorize("&7Status: " + (team.isOpen() ? "&aOpen" : "&cInvite Only")));
        player.sendMessage(TeamsZ.colorize("&8&m----------------------"));
    }
    
    private void handleList(Player player) {
        if (teamManager.getAllTeams().isEmpty()) {
            player.sendMessage(TeamsZ.colorize("&cNo teams exist yet!"));
            return;
        }
        
        player.sendMessage(TeamsZ.colorize("&8&m----------------------"));
        player.sendMessage(TeamsZ.colorize("&6&lALL TEAMS"));
        for (Team team : teamManager.getAllTeams()) {
            player.sendMessage(TeamsZ.colorize("&7- " + team.getColoredName() + " &7(&e" + team.getMemberCount() + "&7)"));
        }
        player.sendMessage(TeamsZ.colorize("&8&m----------------------"));
    }
    
    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(TeamsZ.colorize("&cUsage: /team invite <player>"));
            return;
        }
        
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(configManager.getMessage("general.player-offline", "{player}", args[1]));
            return;
        }
        
        if (teamManager.hasTeam(target.getUniqueId())) {
            player.sendMessage(configManager.getMessage("command-messages.invite.already-in-team",
                    "{target_player}", target.getName()));
            return;
        }
        
        team.addInvite(target.getUniqueId());
        player.sendMessage(configManager.getMessage("command-messages.invite.sent",
                "{target_player}", target.getName()));
        target.sendMessage(configManager.getMessage("command-messages.invite.received",
                "{team_name}", team.getName(), "{inviter}", player.getName()));
    }
    
    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(TeamsZ.colorize("&cUsage: /team join <team>"));
            return;
        }
        
        Team team = teamManager.getTeam(args[1]);
        if (team == null) {
            player.sendMessage(TeamsZ.colorize("&cTeam not found!"));
            return;
        }
        
        if (!team.isOpen() && !team.hasInvite(player.getUniqueId())) {
            player.sendMessage(configManager.getMessage("command-messages.join.no-invite",
                    "{team_name}", team.getName()));
            return;
        }
        
        team.addMember(player.getUniqueId(), TeamRank.MEMBER);
        teamManager.addPlayerToTeam(player.getUniqueId(), team);
        player.sendMessage(configManager.getMessage("command-messages.join.success",
                "{team_name}", team.getName()));
    }
    
    private void handleAccept(Player player, String[] args) {
        handleJoin(player, args);
    }
    
    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(TeamsZ.colorize("&cUsage: /team kick <player>"));
            return;
        }
        
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(configManager.getMessage("general.player-offline", "{player}", args[1]));
            return;
        }
        
        if (!team.isMember(target.getUniqueId())) {
            player.sendMessage(configManager.getMessage("command-messages.kick.target-not-in-team"));
            return;
        }
        
        teamManager.removePlayerFromTeam(target.getUniqueId());
        player.sendMessage(configManager.getMessage("command-messages.kick.kicked-by-player",
                "{kicked_player}", target.getName()));
        target.sendMessage(configManager.getMessage("command-messages.kick.kicked-notification",
                "{team_name}", team.getName(), "{kicker}", player.getName()));
    }
    
    private void handleHome(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        if (!team.hasHome()) {
            player.sendMessage(configManager.getMessage("command-messages.home.not-set"));
            return;
        }
        
        player.teleport(team.getHome());
        player.sendMessage(configManager.getMessage("command-messages.home.teleport-success"));
    }
    
    private void handleSetHome(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        team.setHome(player.getLocation());
        teamManager.saveTeam(team);
        player.sendMessage(configManager.getMessage("command-messages.sethome.success"));
    }
    
    private void handlePvp(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(TeamsZ.colorize("&cOnly the owner can toggle PvP!"));
            return;
        }
        
        team.setPvpEnabled(!team.isPvpEnabled());
        teamManager.saveTeam(team);
        
        if (team.isPvpEnabled()) {
            player.sendMessage(configManager.getMessage("command-messages.pvp.enabled",
                    "{team_name}", team.getName()));
        } else {
            player.sendMessage(configManager.getMessage("command-messages.pvp.disabled",
                    "{team_name}", team.getName()));
        }
    }
    
    private void handlePromote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(TeamsZ.colorize("&cUsage: /team promote <player>"));
            return;
        }
        
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(configManager.getMessage("general.player-offline", "{player}", args[1]));
            return;
        }
        
        if (!team.isMember(target.getUniqueId())) {
            player.sendMessage(TeamsZ.colorize("&cThat player is not in your team!"));
            return;
        }
        
        TeamRank currentRank = team.getRank(target.getUniqueId());
        TeamRank newRank = currentRank.getNext();
        
        if (newRank == TeamRank.OWNER) {
            player.sendMessage(TeamsZ.colorize("&cUse /team transfer to transfer ownership!"));
            return;
        }
        
        team.setRank(target.getUniqueId(), newRank);
        teamManager.saveTeam(team);
        player.sendMessage(TeamsZ.colorize("&aPromoted " + target.getName() + " to " + newRank.getDisplayName()));
    }
    
    private void handleDemote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(TeamsZ.colorize("&cUsage: /team demote <player>"));
            return;
        }
        
        Team team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(configManager.getMessage("command-messages.leave.not-in-team"));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(configManager.getMessage("general.player-offline", "{player}", args[1]));
            return;
        }
        
        if (!team.isMember(target.getUniqueId())) {
            player.sendMessage(TeamsZ.colorize("&cThat player is not in your team!"));
            return;
        }
        
        TeamRank currentRank = team.getRank(target.getUniqueId());
        TeamRank newRank = currentRank.getPrevious();
        
        team.setRank(target.getUniqueId(), newRank);
        teamManager.saveTeam(team);
        player.sendMessage(TeamsZ.colorize("&aDemoted " + target.getName() + " to " + newRank.getDisplayName()));
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(TeamsZ.colorize("&8&m----------------------"));
        player.sendMessage(TeamsZ.colorize("&b&lTEAMSZ COMMANDS"));
        player.sendMessage(TeamsZ.colorize("&e/team create <n> &7- Create team"));
        player.sendMessage(TeamsZ.colorize("&e/team disband &7- Disband team"));
        player.sendMessage(TeamsZ.colorize("&e/team info &7- Team info"));
        player.sendMessage(TeamsZ.colorize("&e/team list &7- List all teams"));
        player.sendMessage(TeamsZ.colorize("&e/team invite <player> &7- Invite player"));
        player.sendMessage(TeamsZ.colorize("&e/team home &7- Teleport home"));
        player.sendMessage(TeamsZ.colorize("&e/tc <msg> &7- Team chat"));
        player.sendMessage(TeamsZ.colorize("&8&m----------------------"));
    }
}
