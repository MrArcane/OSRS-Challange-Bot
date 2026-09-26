package com.osrschallenge.challenge;

import java.time.Instant;

/** A specific challenge instance assigned to a player, generated from a ChallengeTemplate. */
public class Challenge {
    private final long id;
    private final long playerId;
    private final Long templateId;
    private final String name;
    private final String description;
    private final String bonusObjective;
    private final ChallengeCategory category;
    private final ChallengeDifficulty difficulty;
    private ChallengeStatus status;
    private final String estimatedTime;
    private final int rewardXp;
    private final Instant createdAt;
    private Instant resolvedAt;

    public Challenge(long id, long playerId, Long templateId, String name, String description,
                      String bonusObjective, ChallengeCategory category, ChallengeDifficulty difficulty,
                      ChallengeStatus status, String estimatedTime, int rewardXp,
                      Instant createdAt, Instant resolvedAt) {
        this.id = id;
        this.playerId = playerId;
        this.templateId = templateId;
        this.name = name;
        this.description = description;
        this.bonusObjective = bonusObjective;
        this.category = category;
        this.difficulty = difficulty;
        this.status = status;
        this.estimatedTime = estimatedTime;
        this.rewardXp = rewardXp;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public static Challenge fromTemplate(long playerId, ChallengeTemplate template) {
        return new Challenge(0, playerId, template.id(), template.name(), template.description(),
                template.bonusObjective(), template.category(), template.difficulty(),
                ChallengeStatus.ACTIVE, template.estimatedTime(), template.rewardXp(),
                Instant.now(), null);
    }

    public long id() { return id; }
    public long playerId() { return playerId; }
    public Long templateId() { return templateId; }
    public String name() { return name; }
    public String description() { return description; }
    public String bonusObjective() { return bonusObjective; }
    public ChallengeCategory category() { return category; }
    public ChallengeDifficulty difficulty() { return difficulty; }
    public ChallengeStatus status() { return status; }
    public void status(ChallengeStatus status) { this.status = status; }
    public String estimatedTime() { return estimatedTime; }
    public int rewardXp() { return rewardXp; }
    public Instant createdAt() { return createdAt; }
    public Instant resolvedAt() { return resolvedAt; }
    public void resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}
