package com.osrschallenge.discord.buttons;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public final class ChallengeButtons {
    private ChallengeButtons() {}

    public static final String COMPLETE_PREFIX = "challenge:complete:";
    public static final String REROLL_PREFIX = "challenge:reroll:";
    public static final String ABANDON_PREFIX = "challenge:abandon:";

    public static ActionRow forChallenge(long challengeId) {
        return ActionRow.of(
                Button.success(COMPLETE_PREFIX + challengeId, "Complete").withEmoji(Emoji.fromUnicode("\u2705")),
                Button.secondary(REROLL_PREFIX + challengeId, "Reroll").withEmoji(Emoji.fromUnicode("\uD83D\uDD04")),
                Button.danger(ABANDON_PREFIX + challengeId, "Abandon").withEmoji(Emoji.fromUnicode("\u274C"))
        );
    }

    public static ActionRow resolved() {
        return ActionRow.of(
                Button.success(COMPLETE_PREFIX + "done", "Complete").withDisabled(true).withEmoji(Emoji.fromUnicode("\u2705")),
                Button.secondary(REROLL_PREFIX + "done", "Reroll").withDisabled(true).withEmoji(Emoji.fromUnicode("\uD83D\uDD04")),
                Button.danger(ABANDON_PREFIX + "done", "Abandon").withDisabled(true).withEmoji(Emoji.fromUnicode("\u274C"))
        );
    }
}
