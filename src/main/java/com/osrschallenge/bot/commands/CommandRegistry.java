package com.osrschallenge.bot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry register(Command command) {
        commands.put(command.name(), command);
        return this;
    }

    public Command find(String name) {
        return commands.get(name);
    }

    public void publish(JDA jda, Long devGuildId) {
        List<CommandData> data = commands.values().stream().map(Command::data).toList();
        if (devGuildId != null) {
            Guild guild = jda.getGuildById(devGuildId);
            if (guild != null) {
                guild.updateCommands().addCommands(data).queue();
                return;
            }
        }
        jda.updateCommands().addCommands(data).queue();
    }
}
