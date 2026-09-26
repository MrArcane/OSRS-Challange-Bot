package com.osrschallenge.discord.embeds;

import com.osrschallenge.player.ChallengeLeveling;
import com.osrschallenge.player.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;

public final class ProfileEmbedBuilder {
    private ProfileEmbedBuilder() {}

    public static MessageEmbed build(User user, Player player) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDCCB " + user.getName() + "'s OSRS Profile");
        embed.setColor(new Color(88, 166, 255));

        String username = player.osrsUsername() == null
                ? "_not set - use /stats set-username_" : player.osrsUsername();
        embed.addField("OSRS Username", username, true);
        embed.addField("Account Type", player.accountType().name(), true);
        embed.addField("Combat Level", String.valueOf(player.combatLevel()), true);

        embed.addField("Total Level", String.valueOf(player.totalLevel()), true);
        embed.addField("Quest Points", String.valueOf(player.questPoints()), true);
        embed.addField("\u200b", "\u200b", true);

        int level = player.challengeLevel();
        int intoLevel = ChallengeLeveling.xpIntoCurrentLevel(player.challengeXp());
        int needed = ChallengeLeveling.xpNeededForNextLevel(player.challengeXp());
        embed.addField("Challenge Level", "Challenge Level " + level + "\n" + intoLevel + " / " + needed + " XP", false);

        embed.addField("Completed", String.valueOf(player.completedCount()), true);
        embed.addField("Failed", String.valueOf(player.failedCount()), true);
        embed.addField("Skipped", String.valueOf(player.skippedCount()), true);

        embed.addField("Current Streak", "\uD83D\uDD25 " + player.currentStreak(), true);
        embed.addField("Longest Streak", "\uD83C\uDFC6 " + player.longestStreak(), true);
        embed.addField("\u200b", "\u200b", true);

        embed.setThumbnail(user.getEffectiveAvatarUrl());
        return embed.build();
    }
}
