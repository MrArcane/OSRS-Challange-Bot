package com.osrschallenge.osrs;

import java.util.Optional;

/** Placeholder used until a real hiscores/API integration is wired in during a later phase. */
public class NoOpOsrsStatsProvider implements OsrsStatsProvider {
    @Override
    public Optional<OsrsStatsSnapshot> fetchStats(String osrsUsername) {
        return Optional.empty();
    }
}
