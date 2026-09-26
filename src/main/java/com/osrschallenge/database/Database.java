package com.osrschallenge.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Owns the single SQLite connection and creates the schema on startup if it doesn't exist. */
public final class Database {
    private final Connection connection;

    public Database(String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }
            initializeSchema();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public Connection connection() {
        return connection;
    }

    private void initializeSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(SCHEMA_PLAYERS);
            statement.execute(SCHEMA_PLAYER_SKILLS);
            statement.execute(SCHEMA_CHALLENGE_TEMPLATES);
            statement.execute(SCHEMA_CHALLENGES);
        }
    }

    private static final String SCHEMA_PLAYERS = """
        CREATE TABLE IF NOT EXISTS players (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            discord_user_id TEXT NOT NULL UNIQUE,
            osrs_username TEXT,
            account_type TEXT NOT NULL DEFAULT 'REGULAR',
            combat_level INTEGER NOT NULL DEFAULT 3,
            total_level INTEGER NOT NULL DEFAULT 32,
            quest_points INTEGER NOT NULL DEFAULT 0,
            challenge_xp INTEGER NOT NULL DEFAULT 0,
            completed_count INTEGER NOT NULL DEFAULT 0,
            failed_count INTEGER NOT NULL DEFAULT 0,
            skipped_count INTEGER NOT NULL DEFAULT 0,
            current_streak INTEGER NOT NULL DEFAULT 0,
            longest_streak INTEGER NOT NULL DEFAULT 0,
            daily_rerolls_used INTEGER NOT NULL DEFAULT 0,
            last_reroll_reset TEXT,
            created_at TEXT NOT NULL
        )
        """;

    private static final String SCHEMA_PLAYER_SKILLS = """
        CREATE TABLE IF NOT EXISTS player_skills (
            player_id INTEGER NOT NULL REFERENCES players(id) ON DELETE CASCADE,
            skill_name TEXT NOT NULL,
            level INTEGER NOT NULL DEFAULT 1,
            PRIMARY KEY (player_id, skill_name)
        )
        """;

    private static final String SCHEMA_CHALLENGE_TEMPLATES = """
        CREATE TABLE IF NOT EXISTS challenge_templates (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            template_key TEXT NOT NULL UNIQUE,
            name TEXT NOT NULL,
            category TEXT NOT NULL,
            description TEXT NOT NULL,
            bonus_objective TEXT,
            difficulty TEXT NOT NULL,
            min_combat_level INTEGER NOT NULL DEFAULT 0,
            max_combat_level INTEGER NOT NULL DEFAULT 200,
            min_total_level INTEGER NOT NULL DEFAULT 0,
            min_quest_points INTEGER NOT NULL DEFAULT 0,
            required_skill TEXT,
            required_skill_level INTEGER NOT NULL DEFAULT 0,
            members_only INTEGER NOT NULL DEFAULT 0,
            estimated_time TEXT NOT NULL,
            reward_xp INTEGER NOT NULL,
            enabled INTEGER NOT NULL DEFAULT 1
        )
        """;

    private static final String SCHEMA_CHALLENGES = """
        CREATE TABLE IF NOT EXISTS challenges (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            player_id INTEGER NOT NULL REFERENCES players(id) ON DELETE CASCADE,
            template_id INTEGER REFERENCES challenge_templates(id),
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            bonus_objective TEXT,
            category TEXT NOT NULL,
            difficulty TEXT NOT NULL,
            status TEXT NOT NULL,
            estimated_time TEXT NOT NULL,
            reward_xp INTEGER NOT NULL,
            created_at TEXT NOT NULL,
            resolved_at TEXT
        )
        """;
}
