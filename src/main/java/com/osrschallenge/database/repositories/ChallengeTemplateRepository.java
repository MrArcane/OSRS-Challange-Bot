package com.osrschallenge.database.repositories;

import com.osrschallenge.challenge.ChallengeCategory;
import com.osrschallenge.challenge.ChallengeDifficulty;
import com.osrschallenge.challenge.ChallengeTemplate;
import com.osrschallenge.database.Database;
import com.osrschallenge.player.Skill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengeTemplateRepository {
    private final Database database;

    public ChallengeTemplateRepository(Database database) {
        this.database = database;
    }

    public List<ChallengeTemplate> findAllEnabled() {
        String sql = "SELECT * FROM challenge_templates WHERE enabled = 1";
        List<ChallengeTemplate> templates = new ArrayList<>();
        try (Statement statement = database.connection().createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                templates.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load challenge templates", e);
        }
        return templates;
    }

    public boolean existsByKey(String key) {
        String sql = "SELECT 1 FROM challenge_templates WHERE template_key = ?";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setString(1, key);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check template existence", e);
        }
    }

    public void insert(ChallengeTemplate template) {
        String sql = """
            INSERT INTO challenge_templates (template_key, name, category, description, bonus_objective,
                difficulty, min_combat_level, max_combat_level, min_total_level, min_quest_points,
                required_skill, required_skill_level, members_only, estimated_time, reward_xp, enabled)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setString(1, template.key());
            statement.setString(2, template.name());
            statement.setString(3, template.category().name());
            statement.setString(4, template.description());
            statement.setString(5, template.bonusObjective());
            statement.setString(6, template.difficulty().name());
            statement.setInt(7, template.minCombatLevel());
            statement.setInt(8, template.maxCombatLevel());
            statement.setInt(9, template.minTotalLevel());
            statement.setInt(10, template.minQuestPoints());
            statement.setString(11, template.requiredSkill() == null ? null : template.requiredSkill().name());
            statement.setInt(12, template.requiredSkillLevel());
            statement.setBoolean(13, template.membersOnly());
            statement.setString(14, template.estimatedTime());
            statement.setInt(15, template.rewardXp());
            statement.setBoolean(16, template.enabled());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert challenge template", e);
        }
    }

    private ChallengeTemplate map(ResultSet rs) throws SQLException {
        String requiredSkillRaw = rs.getString("required_skill");
        return new ChallengeTemplate(
                rs.getLong("id"),
                rs.getString("template_key"),
                rs.getString("name"),
                ChallengeCategory.valueOf(rs.getString("category")),
                rs.getString("description"),
                rs.getString("bonus_objective"),
                ChallengeDifficulty.valueOf(rs.getString("difficulty")),
                rs.getInt("min_combat_level"),
                rs.getInt("max_combat_level"),
                rs.getInt("min_total_level"),
                rs.getInt("min_quest_points"),
                requiredSkillRaw == null ? null : Skill.valueOf(requiredSkillRaw),
                rs.getInt("required_skill_level"),
                rs.getBoolean("members_only"),
                rs.getString("estimated_time"),
                rs.getInt("reward_xp"),
                rs.getBoolean("enabled")
        );
    }
}
