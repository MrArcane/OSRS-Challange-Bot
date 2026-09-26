package com.osrschallenge.challenge;

public enum ChallengeDifficulty {
    EASY("\u2B50", 25),
    NORMAL("\u2B50\u2B50", 50),
    HARD("\u2B50\u2B50\u2B50", 100),
    VERY_HARD("\u2B50\u2B50\u2B50\u2B50", 200),
    EXTREME("\u2B50\u2B50\u2B50\u2B50\u2B50", 500);

    private final String stars;
    private final int baseRewardXp;

    ChallengeDifficulty(String stars, int baseRewardXp) {
        this.stars = stars;
        this.baseRewardXp = baseRewardXp;
    }

    public String stars() { return stars; }
    public int baseRewardXp() { return baseRewardXp; }
}
