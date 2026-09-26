package com.osrschallenge.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface Command {
    CommandData data();
    void execute(SlashCommandInteractionEvent event);

    default String name() {
        return data().getName();
    }
}
