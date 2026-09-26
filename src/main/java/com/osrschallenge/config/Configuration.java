package com.osrschallenge.config;

/** Reads runtime configuration from environment variables. See .env.example for the full list. */
public final class Configuration {
    private final String discordToken;
    private final String databasePath;
    private final Long devGuildId;

    private Configuration(String discordToken, String databasePath, Long devGuildId) {
        this.discordToken = discordToken;
        this.databasePath = databasePath;
        this.devGuildId = devGuildId;
    }

    public static Configuration fromEnvironment() {
        String token = require("DISCORD_TOKEN");
        String dbPath = System.getenv().getOrDefault("DATABASE_PATH", "osrs-challenge.db");
        String guildIdRaw = System.getenv("DEV_GUILD_ID");
        Long guildId = (guildIdRaw == null || guildIdRaw.isBlank()) ? null : Long.parseLong(guildIdRaw.trim());
        return new Configuration(token, dbPath, guildId);
    }

    private static String require(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }

    public String discordToken() { return discordToken; }
    public String databasePath() { return databasePath; }
    public Long devGuildId() { return devGuildId; }
}
