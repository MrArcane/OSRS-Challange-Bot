package com.osrschallenge.challenge;

import com.osrschallenge.database.repositories.ChallengeRepository;
import com.osrschallenge.database.repositories.ChallengeTemplateRepository;
import com.osrschallenge.player.Player;
import com.osrschallenge.player.PlayerService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** All challenge lifecycle business logic: generation, completion, reroll (with daily limit), abandonment. */
public class ChallengeService {
    private static final int DAILY_REROLL_LIMIT = 3;
    private static final int RECENT_TEMPLATE_HISTORY = 5;

    private final ChallengeRepository challengeRepository;
    private final ChallengeTemplateRepository templateRepository;
    private final PlayerService playerService;
    private final ChallengeGenerator generator;

    public ChallengeService(ChallengeRepository challengeRepository,
                             ChallengeTemplateRepository templateRepository,
                             PlayerService playerService,
                             ChallengeGenerator generator) {
        this.challengeRepository = challengeRepository;
        this.templateRepository = templateRepository;
        this.playerService = playerService;
        this.generator = generator;
    }

    /** Returns the player's currently active challenge, generating a new one if none exists. */
    public Challenge getOrGenerateTask(Player player) {
        return challengeRepository.findActiveForPlayer(player.id())
                .orElseGet(() -> generateNew(player));
    }

    private Challenge generateNew(Player player) {
        var templates = templateRepository.findAllEnabled();
        var recentIds = challengeRepository.recentTemplateIds(player.id(), RECENT_TEMPLATE_HISTORY);
        ChallengeTemplate template = generator.generate(player, templates, recentIds);
        return challengeRepository.insert(Challenge.fromTemplate(player.id(), template));
    }

    public Challenge complete(Player player, long challengeId) {
        Challenge challenge = requireOwnedActiveChallenge(player, challengeId);
        challengeRepository.updateStatus(challengeId, ChallengeStatus.COMPLETED, Instant.now());
        challenge.status(ChallengeStatus.COMPLETED);

        player.addChallengeXp(challenge.rewardXp());
        player.incrementCompleted();
        player.extendStreak();
        playerService.save(player);
        return challenge;
    }

    public Challenge abandon(Player player, long challengeId) {
        Challenge challenge = requireOwnedActiveChallenge(player, challengeId);
        challengeRepository.updateStatus(challengeId, ChallengeStatus.ABANDONED, Instant.now());
        challenge.status(ChallengeStatus.ABANDONED);

        player.incrementFailed();
        player.resetStreak();
        playerService.save(player);
        return challenge;
    }

    public RerollResult reroll(Player player, long challengeId) {
        resetDailyRerollsIfNeeded(player);
        if (player.dailyRerollsUsed() >= DAILY_REROLL_LIMIT) {
            return RerollResult.limitReached(DAILY_REROLL_LIMIT);
        }

        requireOwnedActiveChallenge(player, challengeId);
        challengeRepository.updateStatus(challengeId, ChallengeStatus.REROLLED, Instant.now());

        player.incrementSkipped();
        player.dailyRerollsUsed(player.dailyRerollsUsed() + 1);
        playerService.save(player);

        Challenge newChallenge = generateNew(player);
        int remaining = DAILY_REROLL_LIMIT - player.dailyRerollsUsed();
        return RerollResult.success(newChallenge, remaining);
    }

    private void resetDailyRerollsIfNeeded(Player player) {
        Instant lastReset = player.lastRerollReset();
        if (lastReset == null || ChronoUnit.DAYS.between(lastReset, Instant.now()) >= 1) {
            player.dailyRerollsUsed(0);
            player.lastRerollReset(Instant.now());
        }
    }

    private Challenge requireOwnedActiveChallenge(Player player, long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        if (challenge.playerId() != player.id()) {
            throw new IllegalStateException("This challenge does not belong to you");
        }
        if (challenge.status() != ChallengeStatus.ACTIVE) {
            throw new IllegalStateException("This challenge is no longer active");
        }
        return challenge;
    }

    public static final class RerollResult {
        private final boolean allowed;
        private final Challenge newChallenge;
        private final int rerollsRemaining;
        private final int limit;

        private RerollResult(boolean allowed, Challenge newChallenge, int rerollsRemaining, int limit) {
            this.allowed = allowed;
            this.newChallenge = newChallenge;
            this.rerollsRemaining = rerollsRemaining;
            this.limit = limit;
        }

        static RerollResult success(Challenge newChallenge, int remaining) {
            return new RerollResult(true, newChallenge, remaining, DAILY_REROLL_LIMIT);
        }

        static RerollResult limitReached(int limit) {
            return new RerollResult(false, null, 0, limit);
        }

        public boolean allowed() { return allowed; }
        public Challenge newChallenge() { return newChallenge; }
        public int rerollsRemaining() { return rerollsRemaining; }
        public int limit() { return limit; }
    }
}
