package com.osrschallenge.bot.listeners;

import com.osrschallenge.challenge.Challenge;
import com.osrschallenge.challenge.ChallengeService;
import com.osrschallenge.discord.buttons.ChallengeButtons;
import com.osrschallenge.discord.embeds.ChallengeEmbedBuilder;
import com.osrschallenge.player.Player;
import com.osrschallenge.player.PlayerService;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonInteractionListener extends ListenerAdapter {
    private final PlayerService playerService;
    private final ChallengeService challengeService;

    public ButtonInteractionListener(PlayerService playerService, ChallengeService challengeService) {
        this.playerService = playerService;
        this.challengeService = challengeService;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId();
        Player player = playerService.getOrCreate(event.getUser().getId());

        try {
            if (id.startsWith(ChallengeButtons.COMPLETE_PREFIX)) {
                long challengeId = parseId(id, ChallengeButtons.COMPLETE_PREFIX);
                Challenge challenge = challengeService.complete(player, challengeId);
                event.editComponents(ChallengeButtons.resolved()).queue();
                event.getHook().sendMessage("\u2705 **" + challenge.name() + "** marked as complete! +"
                        + challenge.rewardXp() + " Challenge XP. Streak: \uD83D\uDD25 " + player.currentStreak()).queue();
            } else if (id.startsWith(ChallengeButtons.REROLL_PREFIX)) {
                long challengeId = parseId(id, ChallengeButtons.REROLL_PREFIX);
                ChallengeService.RerollResult result = challengeService.reroll(player, challengeId);
                if (!result.allowed()) {
                    event.reply("\uD83D\uDEAB You've used all " + result.limit()
                            + " of your daily rerolls. Try again tomorrow!").setEphemeral(true).queue();
                    return;
                }
                event.editMessageEmbeds(ChallengeEmbedBuilder.build(result.newChallenge(), player))
                        .setComponents(ChallengeButtons.forChallenge(result.newChallenge().id()))
                        .queue();
            } else if (id.startsWith(ChallengeButtons.ABANDON_PREFIX)) {
                long challengeId = parseId(id, ChallengeButtons.ABANDON_PREFIX);
                Challenge challenge = challengeService.abandon(player, challengeId);
                event.editComponents(ChallengeButtons.resolved()).queue();
                event.getHook().sendMessage("\u274C **" + challenge.name()
                        + "** abandoned. Your streak has been reset. Use `/task` for a new challenge.").queue();
            } else {
                event.deferEdit().queue();
            }
        } catch (NumberFormatException e) {
            event.reply("This challenge button is no longer valid.").setEphemeral(true).queue();
        } catch (IllegalStateException | IllegalArgumentException e) {
            event.reply("\u26A0\uFE0F " + e.getMessage()).setEphemeral(true).queue();
        }
    }

    private long parseId(String componentId, String prefix) {
        return Long.parseLong(componentId.substring(prefix.length()));
    }
}
