package com.osrschallenge.bot.commands;

import com.osrschallenge.challenge.Challenge;
import com.osrschallenge.challenge.ChallengeService;
import com.osrschallenge.discord.buttons.ChallengeButtons;
import com.osrschallenge.discord.embeds.ChallengeEmbedBuilder;
import com.osrschallenge.player.Player;
import com.osrschallenge.player.PlayerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class TaskCommand implements Command {
    private final PlayerService playerService;
    private final ChallengeService challengeService;

    public TaskCommand(PlayerService playerService, ChallengeService challengeService) {
        this.playerService = playerService;
        this.challengeService = challengeService;
    }

    @Override
    public CommandData data() {
        return Commands.slash("task", "Get your current OSRS challenge (or generate a new one).");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Player player = playerService.getOrCreate(event.getUser().getId());
        try {
            Challenge challenge = challengeService.getOrGenerateTask(player);
            event.replyEmbeds(ChallengeEmbedBuilder.build(challenge, player))
                    .addComponents(ChallengeButtons.forChallenge(challenge.id()))
                    .queue();
        } catch (IllegalStateException e) {
            event.reply("\u26A0\uFE0F " + e.getMessage() + ". Try updating your stats with `/stats` first.")
                    .setEphemeral(true).queue();
        }
    }
}
