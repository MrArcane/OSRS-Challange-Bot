package com.osrschallenge.database.repositories;

import com.osrschallenge.database.Database;
import com.osrschallenge.player.AccountType;
import com.osrschallenge.player.Player;
import com.osrschallenge.player.Skill;

import java.sql.*;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class PlayerRepository {
    private final Database database;

    public PlayerRepository(Database database) {
        this.database = database;
    }

    public Optional<Player> findByDiscordId(String discordUserId) {
        String sql = "SELECT * FROM players WHERE discord_user_id = ?";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setString(1, discordUserId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapPlayer(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player", e);
        }
    }

    public Optional<Player> findById(long id) {
        String sql = "SELECT * FROM players WHERE id = ?";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapPlayer(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player", e);
        }
    }

    public Player insert(Player player) {
        String sql = """
            INSERT INTO players (discord_user_id, osrs_username, account_type, combat_level,
                total_level, quest_points, challenge_xp, completed_count, failed_count,
                skipped_count, current_streak, longest_streak, daily_rerolls_used,
                last_reroll_reset, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = database.connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindPlayer(statement, player);
            statement.executeUpdate();
            long id;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                id = keys.getLong(1);
            }
            insertDefaultSkills(id, player);
            return findById(id).orElseThrow();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert player", e);
        }
    }

    private void insertDefaultSkills(long playerId, Player player) throws SQLException {
        String sql = "INSERT INTO player_skills (player_id, skill_name, level) VALUES (?, ?, ?)";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            for (Map.Entry<Skill, Integer> entry : player.skills().entrySet()) {
                statement.setLong(1, playerId);
                statement.setString(2, entry.getKey().name());
                statement.setInt(3, entry.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public void update(Player player) {
        String sql = """
            UPDATE players SET osrs_username = ?, account_type = ?, combat_level = ?,
                total_level = ?, quest_points = ?, challenge_xp = ?, completed_count = ?,
                failed_count = ?, skipped_count = ?, current_streak = ?, longest_streak = ?,
                daily_rerolls_used = ?, last_reroll_reset = ?
            WHERE id = ?
            """;
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setString(1, player.osrsUsername());
            statement.setString(2, player.accountType().name());
            statement.setInt(3, player.combatLevel());
            statement.setInt(4, player.totalLevel());
            statement.setInt(5, player.questPoints());
            statement.setInt(6, player.challengeXp());
            statement.setInt(7, player.completedCount());
            statement.setInt(8, player.failedCount());
            statement.setInt(9, player.skippedCount());
            statement.setInt(10, player.currentStreak());
            statement.setInt(11, player.longestStreak());
            statement.setInt(12, player.dailyRerollsUsed());
            statement.setString(13, player.lastRerollReset() == null ? null : player.lastRerollReset().toString());
            statement.setLong(14, player.id());
            statement.executeUpdate();
            updateSkills(player);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update player", e);
        }
    }

    private void updateSkills(Player player) throws SQLException {
        String sql = """
            INSERT INTO player_skills (player_id, skill_name, level) VALUES (?, ?, ?)
            ON CONFLICT(player_id, skill_name) DO UPDATE SET level = excluded.level
            """;
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            for (Map.Entry<Skill, Integer> entry : player.skills().entrySet()) {
                statement.setLong(1, player.id());
                statement.setString(2, entry.getKey().name());
                statement.setInt(3, entry.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void bindPlayer(PreparedStatement statement, Player player) throws SQLException {
        statement.setString(1, player.discordUserId());
        statement.setString(2, player.osrsUsername());
        statement.setString(3, player.accountType().name());
        statement.setInt(4, player.combatLevel());
        statement.setInt(5, player.totalLevel());
        statement.setInt(6, player.questPoints());
        statement.setInt(7, player.challengeXp());
        statement.setInt(8, player.completedCount());
        statement.setInt(9, player.failedCount());
        statement.setInt(10, player.skippedCount());
        statement.setInt(11, player.currentStreak());
        statement.setInt(12, player.longestStreak());
        statement.setInt(13, player.dailyRerollsUsed());
        statement.setString(14, player.lastRerollReset() == null ? null : player.lastRerollReset().toString());
        statement.setString(15, player.createdAt().toString());
    }

    private Player mapPlayer(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        Map<Skill, Integer> skills = loadSkills(id);
        String lastResetRaw = rs.getString("last_reroll_reset");
        return new Player(
                id,
                rs.getString("discord_user_id"),
                rs.getString("osrs_username"),
                AccountType.valueOf(rs.getString("account_type")),
                rs.getInt("combat_level"),
                rs.getInt("total_level"),
                rs.getInt("quest_points"),
                skills,
                rs.getInt("challenge_xp"),
                rs.getInt("completed_count"),
                rs.getInt("failed_count"),
                rs.getInt("skipped_count"),
                rs.getInt("current_streak"),
                rs.getInt("longest_streak"),
                rs.getInt("daily_rerolls_used"),
                lastResetRaw == null ? null : Instant.parse(lastResetRaw),
                Instant.parse(rs.getString("created_at"))
        );
    }

    private Map<Skill, Integer> loadSkills(long playerId) throws SQLException {
        Map<Skill, Integer> skills = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) {
            skills.put(skill, skill == Skill.HITPOINTS ? 10 : 1);
        }
        String sql = "SELECT skill_name, level FROM player_skills WHERE player_id = ?";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setLong(1, playerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    skills.put(Skill.valueOf(rs.getString("skill_name")), rs.getInt("level"));
                }
            }
        }
        return skills;
    }
}
