package com.osrschallenge.challenge;

public enum ChallengeCategory {
    SKILLING("\uD83D\uDEE0\uFE0F"),
    PVM("\u2694\uFE0F"),
    QUESTING("\uD83D\uDCDC"),
    MONEY("\uD83D\uDCB0"),
    EXPLORATION("\uD83D\uDDFA\uFE0F"),
    ACHIEVEMENT("\uD83C\uDFC6"),
    CHALLENGE("\uD83D\uDC80");

    private final String emoji;

    ChallengeCategory(String emoji) {
        this.emoji = emoji;
    }

    public String emoji() { return emoji; }
}
