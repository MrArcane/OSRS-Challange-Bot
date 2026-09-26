package com.osrschallenge.osrs;

import java.util.Optional;

/**
 * Abstraction over any external source of OSRS account data (official hiscores,
 * RuneLite profile, Wise Old Man, etc). Phase 1 ships only a no-op implementation --
 * players enter their stats manually via /stats. A real implementation can be swapped
 * in later (Phase 3) without touching PlayerService or the challenge generator.
 */
public interface OsrsStatsProvider {
    Optional<OsrsStatsSnapshot> fetchStats(String osrsUsername);
}
