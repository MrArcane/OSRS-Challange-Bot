package com.osrschallenge.player;

import com.osrschallenge.database.repositories.PlayerRepository;

import java.util.Optional;

/** Business logic for reading/creating/updating player profiles. Commands call this, never the repository directly. */
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player getOrCreate(String discordUserId) {
        return playerRepository.findByDiscordId(discordUserId)
                .orElseGet(() -> playerRepository.insert(Player.newProfile(discordUserId)));
    }

    public Optional<Player> find(String discordUserId) {
        return playerRepository.findByDiscordId(discordUserId);
    }

    public void save(Player player) {
        playerRepository.update(player);
    }
}
