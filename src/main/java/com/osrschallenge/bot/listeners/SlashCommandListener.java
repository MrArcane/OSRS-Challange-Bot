package com.osrschallenge.bot.listeners;

import com.osrschallenge.bot.commands.Command;
import com.osrschallenge.bot.commands.CommandRegistry;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {
    private final CommandRegistry registry;

    public SlashCommandListener(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command command = registry.find(event.getName());
        if (command == null) {
            event.reply("Unknown command.").setEphemeral(true).queue();
            return;
        }
        try {
            command.execute(event);
        } catch (Exception e) {
            if (!event.isAcknowledged()) {
                event.reply("Something went wrong running that command.").setEphemeral(true).queue();
            }
            e.printStackTrace();
        }
    }
}
