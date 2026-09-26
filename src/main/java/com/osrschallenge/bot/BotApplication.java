package com.osrschallenge.bot;

import com.osrschallenge.bot.commands.CommandRegistry;
import com.osrschallenge.bot.commands.ProfileCommand;
import com.osrschallenge.bot.commands.StatsCommand;
import com.osrschallenge.bot.commands.TaskCommand;
import com.osrschallenge.bot.listeners.ButtonInteractionListener;
import com.osrschallenge.bot.listeners.SlashCommandListener;
import com.osrschallenge.challenge.ChallengeGenerator;
import com.osrschallenge.challenge.ChallengeService;
import com.osrschallenge.challenge.ChallengeTemplateSeeder;
import com.osrschallenge.config.Configuration;
import com.osrschallenge.database.Database;
import com.osrschallenge.database.repositories.ChallengeRepository;
import com.osrschallenge.database.repositories.ChallengeTemplateRepository;
import com.osrschallenge.database.repositories.PlayerRepository;
import com.osrschallenge.player.PlayerService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

/** Composition root: wires repositories -> services -> commands/listeners and starts the bot. */
public final class BotApplication {

    public static void main(String[] args) throws InterruptedException {
        Configuration config = Configuration.fromEnvironment();

        Database database = new Database(config.databasePath());
        PlayerRepository playerRepository = new PlayerRepository(database);
        ChallengeTemplateRepository templateRepository = new ChallengeTemplateRepository(database);
        ChallengeRepository challengeRepository = new ChallengeRepository(database);

        new ChallengeTemplateSeeder(templateRepository).seedIfEmpty();

        PlayerService playerService = new PlayerService(playerRepository);
        ChallengeGenerator generator = new ChallengeGenerator();
        ChallengeService challengeService = new ChallengeService(
                challengeRepository, templateRepository, playerService, generator);

        CommandRegistry registry = new CommandRegistry()
                .register(new ProfileCommand(playerService))
                .register(new StatsCommand(playerService))
                .register(new TaskCommand(playerService, challengeService));

        JDA jda = JDABuilder.createDefault(config.discordToken())
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(
                        new SlashCommandListener(registry),
                        new ButtonInteractionListener(playerService, challengeService)
                )
                .build();

        jda.awaitReady();
        registry.publish(jda, config.devGuildId());

        System.out.println("OSRS Challenge bot is online as " + jda.getSelfUser().getName());
    }
}
