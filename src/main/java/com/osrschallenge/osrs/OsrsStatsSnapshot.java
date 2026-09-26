package com.osrschallenge.osrs;

import com.osrschallenge.player.Skill;

import java.util.Map;

public class OsrsStatsSnapshot {
    private final String username;
    private final int combatLevel;
    private final int totalLevel;
    private final int questPoints;
    private final Map<Skill, Integer> skillLevels;

    public OsrsStatsSnapshot(String username, int combatLevel, int totalLevel, int questPoints,
                              Map<Skill, Integer> skillLevels) {
        this.username = username;
        this.combatLevel = combatLevel;
        this.totalLevel = totalLevel;
        this.questPoints = questPoints;
        this.skillLevels = skillLevels;
    }

    public String username() { return username; }
    public int combatLevel() { return combatLevel; }
    public int totalLevel() { return totalLevel; }
    public int questPoints() { return questPoints; }
    public Map<Skill, Integer> skillLevels() { return skillLevels; }
}
