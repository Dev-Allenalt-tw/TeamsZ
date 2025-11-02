# TeamsZ Plugin - Complete Project Structure

## 📁 Directory Structure

```
TeamsZ/
├── .github/
│   └── workflows/
│       └── build.yml                    # GitHub Actions workflow
├── src/
│   └── main/
│       ├── java/
│       │   └── tw/
│       │       └── allenalt/
│       │           └── teamsz/
│       │               ├── TeamsZ.java                    # Main plugin class
│       │               ├── commands/
│       │               │   ├── TeamCommand.java           # Main /team command
│       │               │   └── TeamChatCommand.java       # /tc command
│       │               ├── managers/
│       │               │   ├── ConfigManager.java         # Config handler
│       │               │   └── TeamManager.java           # Team data manager
│       │               ├── models/
│       │               │   ├── Team.java                  # Team data model
│       │               │   └── TeamRank.java              # Rank enum
│       │               ├── listeners/
│       │               │   └── PlayerListener.java        # Event handlers
│       │               └── placeholders/
│       │                   └── TeamPlaceholders.java      # PlaceholderAPI
│       └── resources/
│           ├── plugin.yml                # Plugin metadata
│           ├── config.yml                # Configuration file
│           └── messages.yml              # Messages file
├── pom.xml                               # Maven build file
└── README.md                             # Documentation
```

## 🚀 Setup Instructions

### 1. Create GitHub Repository

```bash
# Initialize git repository
git init
git add .
git commit -m "Initial commit - TeamsZ Plugin v1.0.0"

# Add remote and push
git remote add origin https://github.com/YOUR_USERNAME/TeamsZ.git
git branch -M main
git push -u origin main
```

### 2. Enable GitHub Actions

1. Go to your repository on GitHub
2. Click on "Actions" tab
3. GitHub Actions will automatically detect the workflow file
4. Workflows will run on every push to main/develop branches

### 3. Build Locally

```bash
# Build the plugin
mvn clean package

# The compiled JAR will be in: target/TeamsZ-1.0.0.jar
```

## 📦 Required Files to Create

Copy the provided configuration files to `src/main/resources/`:

1. **config.yml** - Main configuration (provided in documents)
2. **messages.yml** - All plugin messages (provided in documents)

## 🔧 Features Implemented

### Core Features
- ✅ Team creation with confirmation GUI
- ✅ Team management (disband, leave, invite, kick)
- ✅ Rank system (Member, Recruiter, Mod, Admin, Owner)
- ✅ Team home and warps
- ✅ Team chat (/tc command)
- ✅ PvP toggle (friendly fire)
- ✅ Team balance system
- ✅ Free team creation (no cost)

### PlaceholderAPI Placeholders
- `%teamsz_name%` - Team name or "N/A"
- `%teamsz_colored_name%` - Colored team name
- `%teamsz_rank%` - Player's rank in team
- `%teamsz_members%` - Member count
- `%teamsz_balance%` - Team balance
- `%teamsz_owner%` - Team owner name
- `%teamsz_pvp%` - PvP status
- `%teamsz_open%` - Open/Invite status

## 🎮 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/team create <name>` | Create a team | `teamsz.create` |
| `/team disband` | Disband team (owner only) | - |
| `/team leave` | Leave team | `teamsz.leave` |
| `/team info [team]` | View team info | - |
| `/team list` | List all teams | - |
| `/team invite <player>` | Invite player | - |
| `/team join <team>` | Join team | `teamsz.join` |
| `/team accept <team>` | Accept invite | - |
| `/team kick <player>` | Kick member | - |
| `/team promote <player>` | Promote member | - |
| `/team demote <player>` | Demote member | - |
| `/team home` | Teleport to team home | `teamsz.home` |
| `/team sethome` | Set team home | - |
| `/team pvp` | Toggle friendly fire | - |
| `/tc <message>` | Team chat | `teamsz.chat` |

## 🔨 Building with GitHub Actions

### Automatic Builds
- Triggered on push to `main` or `develop` branches
- Triggered on pull requests to `main`
- Manual trigger available via "workflow_dispatch"

### Release Creation
To create a release with automatic artifact upload:

```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0
```

The workflow will automatically:
1. Build the plugin
2. Create a GitHub release
3. Upload the JAR file to the release

## 📋 Dependencies

- **Paper/Spigot API** 1.21.4 (provided)
- **PlaceholderAPI** 2.11.6 (optional)
- **Vault API** 1.7 (optional)

## 🎯 Next Steps

1. **Copy all Java files** to their respective directories
2. **Copy resource files** (plugin.yml, config.yml, messages.yml)
3. **Test locally** using `mvn clean package`
4. **Push to GitHub** to trigger automatic builds
5. **Install on server** and test all features

## 🐛 Testing Checklist

- [ ] Team creation with GUI confirmation
- [ ] Team disbanding
- [ ] Inviting and joining teams
- [ ] Rank promotion/demotion
- [ ] Team home teleportation
- [ ] Team chat functionality
- [ ] PvP toggle (friendly fire)
- [ ] PlaceholderAPI integration
- [ ] All commands working
- [ ] Configuration reloading

## 📝 Notes

- Plugin uses **Java 21** (Paper 1.21+ requirement)
- All team data is saved to `plugins/TeamsZ/teams/*.yml`
- Supports both **Paper** and **Spigot** servers
- Free team creation (cost set to 0.0 in config)
- Colorized startup message on server load

## 🆘 Support

For issues or questions:
1. Check the configuration files
2. Review server console for errors
3. Verify all dependencies are installed
4. Check GitHub Actions build logs
