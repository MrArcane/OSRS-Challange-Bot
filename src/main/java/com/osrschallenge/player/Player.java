package com.osrschallenge.player;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

/** In-memory representation of a player's profile: OSRS progression + challenge statistics. */
public class Player {
    private final long id;
    private final String discordUserId;
    private String osrsUsername;
    private AccountType accountType;
    private int combatLevel;
    private int totalLevel;
    private int questPoints;
    private final Map<Skill, Integer> skills;

    private int challengeXp;
    private int completedCount;
    private int failedCount;
    private int skippedCount;
    private int currentStreak;
    private int longestStreak;
    private int dailyRerollsUsed;
    private Instant lastRerollReset;
    private final Instant createdAt;

    public Player(long id, String discordUserId, String osrsUsername, AccountType accountType,
                  int combatLevel, int totalLevel, int questPoints, Map<Skill, Integer> skills,
                  int challengeXp, int completedCount, int failedCount, int skippedCount,
                  int currentStreak, int longestStreak, int dailyRerollsUsed,
                  Instant lastRerollReset, Instant createdAt) {
        this.id = id;
        this.discordUserId = discordUserId;
        this.osrsUsername = osrsUsername;
        this.accountType = accountType;
        this.combatLevel = combatLevel;
        this.totalLevel = totalLevel;
        this.questPoints = questPoints;
        this.skills = new EnumMap<>(skills);
        this.challengeXp = challengeXp;
        this.completedCount = completedCount;
        this.failedCount = failedCount;
        this.skippedCount = skippedCount;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.dailyRerollsUsed = dailyRerollsUsed;
        this.lastRerollReset = lastRerollReset;
        this.createdAt = createdAt;
    }

    public static Player newProfile(String discordUserId) {
        Map<Skill, Integer> baseSkills = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) {
            baseSkills.put(skill, skill == Skill.HITPOINTS ? 10 : 1);
        }
        return new Player(0, discordUserId, null, AccountType.REGULAR, 3, 32, 0, baseSkills,
                0, 0, 0, 0, 0, 0, 0, null, Instant.now());
    }

    public int skillLevel(Skill skill) {
        return skills.getOrDefault(skill, 1);
    }

    public void setSkillLevel(Skill skill, int level) {
        skills.put(skill, level);
    }

    public Map<Skill, Integer> skills() { return skills; }

    public long id() { return id; }
    public String discordUserId() { return discordUserId; }
    public String osrsUsername() { return osrsUsername; }
    public void osrsUsername(String v) { this.osrsUsername = v; }
    public AccountType accountType() { return accountType; }
    public void accountType(AccountType v) { this.accountType = v; }
    public int combatLevel() { return combatLevel; }
    public void combatLevel(int v) { this.combatLevel = v; }
    public int totalLevel() { return totalLevel; }
    public void totalLevel(int v) { this.totalLevel = v; }
    public int questPoints() { return questPoints; }
    public void questPoints(int v) { this.questPoints = v; }
    public int challengeXp() { return challengeXp; }
    public void addChallengeXp(int amount) { this.challengeXp += amount; }
    public int completedCount() { return completedCount; }
    public void incrementCompleted() { this.completedCount++; }
    public int failedCount() { return failedCount; }
    public void incrementFailed() { this.failedCount++; }
    public int skippedCount() { return skippedCount; }
    public void incrementSkipped() { this.skippedCount++; }
    public int currentStreak() { return currentStreak; }
    public int longestStreak() { return longestStreak; }

    public void extendStreak() {
        currentStreak++;
        if (currentStreak > longestStreak) longestStreak = currentStreak;
    }

    public void resetStreak() { currentStreak = 0; }
    public int dailyRerollsUsed() { return dailyRerollsUsed; }
    public void dailyRerollsUsed(int v) { this.dailyRerollsUsed = v; }
    public Instant lastRerollReset() { return lastRerollReset; }
    public void lastRerollReset(Instant v) { this.lastRerollReset = v; }
    public Instant createdAt() { return createdAt; }

    public int challengeLevel() {
        return ChallengeLeveling.levelForXp(challengeXp);
    }
}
