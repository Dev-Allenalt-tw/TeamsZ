package tw.allenalt.teamsz.models;

public enum TeamRank {
    MEMBER(1, "&7Member"),
    RECRUITER(2, "&aRecruiter"),
    MOD(3, "&bMod"),
    ADMIN(4, "&6Admin"),
    OWNER(5, "&cOwner");
    
    private final int level;
    private final String displayName;
    
    TeamRank(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isAtLeast(TeamRank other) {
        return this.level >= other.level;
    }
    
    public boolean isHigherThan(TeamRank other) {
        return this.level > other.level;
    }
    
    public static TeamRank fromLevel(int level) {
        for (TeamRank rank : values()) {
            if (rank.level == level) {
                return rank;
            }
        }
        return MEMBER;
    }
    
    public TeamRank getNext() {
        if (this == OWNER) return this;
        return fromLevel(this.level + 1);
    }
    
    public TeamRank getPrevious() {
        if (this == MEMBER) return this;
        return fromLevel(this.level - 1);
    }
}
