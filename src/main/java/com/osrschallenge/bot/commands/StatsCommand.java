package com.osrschallenge.bot.commands;

import com.osrschallenge.player.AccountType;
import com.osrschallenge.player.Player;
import com.osrschallenge.player.PlayerService;
import com.osrschallenge.player.Skill;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class StatsCommand implements Command {
    private final PlayerService playerService;

    public StatsCommand(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public CommandData data() {
        OptionData skillOption = new OptionData(OptionType.STRING, "skill", "Skill to update", true);
        for (Skill skill : Skill.values()) {
            skillOption.addChoice(skill.displayName(), skill.name());
        }

        OptionData accountTypeOption = new OptionData(OptionType.STRING, "type", "Account type", true);
        for (AccountType type : AccountType.values()) {
            accountTypeOption.addChoice(type.name(), type.name());
        }

        return Commands.slash("stats", "View or manually update your OSRS stats.")
                .addSubcommands(
                        new SubcommandData("view", "View your current stats."),
                        new SubcommandData("set-username", "Set your OSRS username.")
                                .addOption(OptionType.STRING, "username", "Your OSRS username", true),
                        new SubcommandData("set-account-type", "Set your account type.")
                                .addOptions(accountTypeOption),
                        new SubcommandData("set-combat", "Set your combat level.")
                                .addOption(OptionType.INTEGER, "level", "Combat level (3-126)", true),
                        new SubcommandData("set-total", "Set your total level.")
                                .addOption(OptionType.INTEGER, "level", "Total level", true),
                        new SubcommandData("set-quests", "Set your quest points.")
                                .addOption(OptionType.INTEGER, "points", "Quest points", true),
                        new SubcommandData("set-skill", "Set an individual skill level.")
                                .addOptions(skillOption)
                                .addOption(OptionType.INTEGER, "level", "Skill level (1-99)", true)
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Player player = playerService.getOrCreate(event.getUser().getId());
        String sub = event.getSubcommandName();
        if (sub == null) {
            event.reply("Unknown subcommand.").setEphemeral(true).queue();
            return;
        }

        switch (sub) {
            case "view" -> {
                StringBuilder sb = new StringBuilder();
                sb.append("**Combat:** ").append(player.combatLevel())
                        .append(" | **Total:** ").append(player.totalLevel())
                        .append(" | **QP:** ").append(player.questPoints()).append("\n\n");
                for (Skill skill : Skill.values()) {
                    sb.append(skill.displayName()).append(": ").append(player.skillLevel(skill)).append("  ");
                }
                event.reply(sb.toString()).setEphemeral(true).queue();
            }
            case "set-username" -> {
                String username = event.getOption("username").getAsString();
                player.osrsUsername(username);
                playerService.save(player);
                event.reply("OSRS username set to **" + username + "**.").setEphemeral(true).queue();
            }
            case "set-account-type" -> {
                AccountType type = AccountType.valueOf(event.getOption("type").getAsString());
                player.accountType(type);
                playerService.save(player);
                event.reply("Account type set to **" + type.name() + "**.").setEphemeral(true).queue();
            }
            case "set-combat" -> {
                int level = clamp(event.getOption("level").getAsInt(), 3, 126);
                player.combatLevel(level);
                playerService.save(player);
                event.reply("Combat level set to **" + level + "**.").setEphemeral(true).queue();
            }
            case "set-total" -> {
                int level = clamp(event.getOption("level").getAsInt(), 32, 2277);
                player.totalLevel(level);
                playerService.save(player);
                event.reply("Total level set to **" + level + "**.").setEphemeral(true).queue();
            }
            case "set-quests" -> {
                int points = clamp(event.getOption("points").getAsInt(), 0, 400);
                player.questPoints(points);
                playerService.save(player);
                event.reply("Quest points set to **" + points + "**.").setEphemeral(true).queue();
            }
            case "set-skill" -> {
                Skill skill = Skill.valueOf(event.getOption("skill").getAsString());
                int level = clamp(event.getOption("level").getAsInt(), 1, 99);
                player.setSkillLevel(skill, level);
                playerService.save(player);
                event.reply(skill.displayName() + " set to **" + level + "**.").setEphemeral(true).queue();
            }
            default -> event.reply("Unknown subcommand.").setEphemeral(true).queue();
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
