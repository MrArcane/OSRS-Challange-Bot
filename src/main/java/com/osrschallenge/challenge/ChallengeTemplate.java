package com.osrschallenge.challenge;

import com.osrschallenge.player.Skill;

/** A reusable definition a Challenge instance is generated from. Add new challenges by adding rows/seed entries, not code. */
public class ChallengeTemplate {
    private final long id;
    private final String key;
    private final String name;
    private final ChallengeCategory category;
    private final String description;
    private final String bonusObjective;
    private final ChallengeDifficulty difficulty;
    private final int minCombatLevel;
    private final int maxCombatLevel;
    private final int minTotalLevel;
    private final int minQuestPoints;
    private final Skill requiredSkill;
    private final int requiredSkillLevel;
    private final boolean membersOnly;
    private final String estimatedTime;
    private final int rewardXp;
    private final boolean enabled;

    public ChallengeTemplate(long id, String key, String name, ChallengeCategory category,
                              String description, String bonusObjective, ChallengeDifficulty difficulty,
                              int minCombatLevel, int maxCombatLevel, int minTotalLevel, int minQuestPoints,
                              Skill requiredSkill, int requiredSkillLevel, boolean membersOnly,
                              String estimatedTime, int rewardXp, boolean enabled) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.category = category;
        this.description = description;
        this.bonusObjective = bonusObjective;
        this.difficulty = difficulty;
        this.minCombatLevel = minCombatLevel;
        this.maxCombatLevel = maxCombatLevel;
        this.minTotalLevel = minTotalLevel;
        this.minQuestPoints = minQuestPoints;
        this.requiredSkill = requiredSkill;
        this.requiredSkillLevel = requiredSkillLevel;
        this.membersOnly = membersOnly;
        this.estimatedTime = estimatedTime;
        this.rewardXp = rewardXp;
        this.enabled = enabled;
    }

    public long id() { return id; }
    public String key() { return key; }
    public String name() { return name; }
    public ChallengeCategory category() { return category; }
    public String description() { return description; }
    public String bonusObjective() { return bonusObjective; }
    public ChallengeDifficulty difficulty() { return difficulty; }
    public int minCombatLevel() { return minCombatLevel; }
    public int maxCombatLevel() { return maxCombatLevel; }
    public int minTotalLevel() { return minTotalLevel; }
    public int minQuestPoints() { return minQuestPoints; }
    public Skill requiredSkill() { return requiredSkill; }
    public int requiredSkillLevel() { return requiredSkillLevel; }
    public boolean membersOnly() { return membersOnly; }
    public String estimatedTime() { return estimatedTime; }
    public int rewardXp() { return rewardXp; }
    public boolean enabled() { return enabled; }
}
