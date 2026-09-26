package com.osrschallenge.player;

/** Simple, tunable XP curve for Challenge Levels, independent of any in-game OSRS formula. */
public final class ChallengeLeveling {
    private ChallengeLeveling() {}

    public static int xpForLevel(int level) {
        if (level <= 1) return 0;
        return (int) (50 * Math.pow(level - 1, 1.6));
    }

    public static int levelForXp(int xp) {
        int level = 1;
        while (xpForLevel(level + 1) <= xp) {
            level++;
        }
        return level;
    }

    public static int xpIntoCurrentLevel(int xp) {
        return xp - xpForLevel(levelForXp(xp));
    }

    public static int xpNeededForNextLevel(int xp) {
        int level = levelForXp(xp);
        return xpForLevel(level + 1) - xpForLevel(level);
    }
}
