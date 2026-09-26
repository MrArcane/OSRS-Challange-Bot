package com.osrschallenge.database.repositories;

import com.osrschallenge.challenge.*;
import com.osrschallenge.database.Database;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChallengeRepository {
    private final Database database;

    public ChallengeRepository(Database database) {
        this.database = database;
    }

    public Challenge insert(Challenge challenge) {
        String sql = """
            INSERT INTO challenges (player_id, template_id, name, description, bonus_objective,
                category, difficulty, status, estimated_time, reward_xp, created_at, resolved_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = database.connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, challenge.playerId());
            if (challenge.templateId() != null) {
                statement.setLong(2, challenge.templateId());
            } else {
                statement.setNull(2, Types.INTEGER);
            }
            statement.setString(3, challenge.name());
            statement.setString(4, challenge.description());
            statement.setString(5, challenge.bonusObjective());
            statement.setString(6, challenge.category().name());
            statement.setString(7, challenge.difficulty().name());
            statement.setString(8, challenge.status().name());
            statement.setString(9, challenge.estimatedTime());
            statement.setInt(10, challenge.rewardXp());
            statement.setString(11, challenge.createdAt().toString());
            statement.setString(12, challenge.resolvedAt() == null ? null : challenge.resolvedAt().toString());
            statement.executeUpdate();
            long id;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                id = keys.getLong(1);
            }
            return findById(id).orElseThrow();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert challenge", e);
        }
    }

    public void updateStatus(long challengeId, ChallengeStatus status, Instant resolvedAt) {
        String sql = "UPDATE challenges SET status = ?, resolved_at = ? WHERE id = ?";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setString(2, resolvedAt == null ? null : resolvedAt.toString());
            statement.setLong(3, challengeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update challenge status", e);
        }
    }

    public Optional<Challenge> findById(long id) {
        String sql = "SELECT * FROM challenges WHERE id = ?";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load challenge", e);
        }
    }

    public Optional<Challenge> findActiveForPlayer(long playerId) {
        String sql = "SELECT * FROM challenges WHERE player_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setLong(1, playerId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load active challenge", e);
        }
    }

    public List<Long> recentTemplateIds(long playerId, int limit) {
        String sql = "SELECT template_id FROM challenges WHERE player_id = ? AND template_id IS NOT NULL " +
                "ORDER BY created_at DESC LIMIT ?";
        List<Long> ids = new ArrayList<>();
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setLong(1, playerId);
            statement.setInt(2, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong("template_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load recent templates", e);
        }
        return ids;
    }

    public List<Challenge> recentForPlayer(long playerId, int limit) {
        String sql = "SELECT * FROM challenges WHERE player_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Challenge> challenges = new ArrayList<>();
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setLong(1, playerId);
            statement.setInt(2, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    challenges.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load challenge history", e);
        }
        return challenges;
    }

    private Challenge map(ResultSet rs) throws SQLException {
        String resolvedRaw = rs.getString("resolved_at");
        long templateIdRaw = rs.getLong("template_id");
        Long templateId = rs.wasNull() ? null : templateIdRaw;
        return new Challenge(
                rs.getLong("id"),
                rs.getLong("player_id"),
                templateId,
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("bonus_objective"),
                ChallengeCategory.valueOf(rs.getString("category")),
                ChallengeDifficulty.valueOf(rs.getString("difficulty")),
                ChallengeStatus.valueOf(rs.getString("status")),
                rs.getString("estimated_time"),
                rs.getInt("reward_xp"),
                Instant.parse(rs.getString("created_at")),
                resolvedRaw == null ? null : Instant.parse(resolvedRaw)
        );
    }
}
