package com.osrschallenge.challenge;

import com.osrschallenge.player.Player;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/** Picks a random eligible template for a player, avoiding their most recent challenges where possible. */
public class ChallengeGenerator {
    private final Random random = new Random();

    public ChallengeTemplate generate(Player player, List<ChallengeTemplate> allTemplates, List<Long> recentTemplateIds) {
        List<ChallengeTemplate> eligible = allTemplates.stream()
                .filter(template -> ChallengeEligibility.isEligible(player, template))
                .filter(template -> !recentTemplateIds.contains(template.id()))
                .collect(Collectors.toList());

        if (eligible.isEmpty()) {
            // Pool too small once repeats are excluded (e.g. very low-level account) -- allow repeats
            // rather than failing to generate a task at all.
            eligible = allTemplates.stream()
                    .filter(template -> ChallengeEligibility.isEligible(player, template))
                    .collect(Collectors.toList());
        }

        if (eligible.isEmpty()) {
            throw new IllegalStateException("No eligible challenge templates for this player yet");
        }

        return eligible.get(random.nextInt(eligible.size()));
    }
}
