package com.osrschallenge.discord.embeds;

import com.osrschallenge.challenge.Challenge;
import com.osrschallenge.challenge.ChallengeDifficulty;
import com.osrschallenge.player.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;

public final class ChallengeEmbedBuilder {
    private ChallengeEmbedBuilder() {}

    public static MessageEmbed build(Challenge challenge, Player player) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83C\uDFAF YOUR OSRS TASK");
        embed.setColor(colorFor(challenge.difficulty()));

        embed.addField(challenge.category().emoji() + " " + challenge.name(), challenge.description(), false);

        if (challenge.bonusObjective() != null && !challenge.bonusObjective().isBlank()) {
            embed.addField("Bonus", challenge.bonusObjective(), false);
        }

        embed.addField("Difficulty", challenge.difficulty().stars(), true);
        embed.addField("Estimated time", challenge.estimatedTime(), true);
        embed.addField("Category", challenge.category().name(), true);

        embed.addField("Rewards", "+" + challenge.rewardXp() + " Challenge XP\n+1 Streak on completion", true);
        embed.addField("Current streak", "\uD83D\uDD25 " + player.currentStreak()
                + " day" + (player.currentStreak() == 1 ? "" : "s"), true);
        embed.addField("\u200b", "\u200b", true);

        embed.setFooter("Challenge #" + challenge.id());
        return embed.build();
    }

    private static Color colorFor(ChallengeDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> new Color(87, 242, 135);
            case NORMAL -> new Color(88, 166, 255);
            case HARD -> new Color(255, 173, 51);
            case VERY_HARD -> new Color(255, 99, 71);
            case EXTREME -> new Color(186, 40, 40);
        };
    }
}
