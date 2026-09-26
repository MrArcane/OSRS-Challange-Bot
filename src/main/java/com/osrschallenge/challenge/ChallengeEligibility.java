package com.osrschallenge.challenge;

import com.osrschallenge.player.Player;

/**
 * Filters templates down to ones that are realistic for a given player.
 * NOTE: requirements are modeled as simple thresholds (combat/total level, quest points,
 * one required skill, members-only flag) rather than exact OSRS unlock chains (e.g. the
 * real Vorkath requirement is a specific quest, not a quest-point count). This is a
 * deliberate Phase 1 simplification -- see README for how to tighten it later using
 * per-quest unlock data once automatic stat retrieval lands.
 */
public final class ChallengeEligibility {
    private ChallengeEligibility() {}

    public static boolean isEligible(Player player, ChallengeTemplate template) {
        if (!template.enabled()) return false;
        if (template.membersOnly() && !player.accountType().canAccessMembersContent()) return false;
        if (player.combatLevel() < template.minCombatLevel()) return false;
        if (player.combatLevel() > template.maxCombatLevel()) return false;
        if (player.totalLevel() < template.minTotalLevel()) return false;
        if (player.questPoints() < template.minQuestPoints()) return false;
        if (template.requiredSkill() != null
                && player.skillLevel(template.requiredSkill()) < template.requiredSkillLevel()) {
            return false;
        }
        return true;
    }
}
