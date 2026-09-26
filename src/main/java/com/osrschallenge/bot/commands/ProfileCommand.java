package com.osrschallenge.bot.commands;

import com.osrschallenge.discord.embeds.ProfileEmbedBuilder;
import com.osrschallenge.player.Player;
import com.osrschallenge.player.PlayerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ProfileCommand implements Command {
    private final PlayerService playerService;

    public ProfileCommand(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public CommandData data() {
        return Commands.slash("profile", "View your OSRS Challenge profile.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Player player = playerService.getOrCreate(event.getUser().getId());
        event.replyEmbeds(ProfileEmbedBuilder.build(event.getUser(), player)).queue();
    }
}
