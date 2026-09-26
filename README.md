# OSRS Challenge — Discord Bot (Phase 1)

A Discord bot that generates personalized Old School RuneScape challenges based on a
player's self-reported progression, with a complete/reroll/abandon loop and basic
streak + Challenge XP tracking.

This is the **Phase 1 MVP** as scoped in the project brief: bot skeleton, SQLite
persistence, player profiles with manually-entered stats, a data-driven challenge
template system, the generator, `/task`, and the three action buttons. History,
leaderboards, daily/group challenges, modifiers and automatic stat retrieval are
intentionally not built yet — see "Roadmap" below for where they plug in.

## Architecture

```
bot/
  BotApplication      composition root: wires repositories -> services -> commands
  commands/            thin Discord-facing classes; parse options, call services
  listeners/            SlashCommandListener, ButtonInteractionListener

challenge/
  Challenge             a specific instance assigned to a player (has a lifecycle)
  ChallengeTemplate      reusable definition a Challenge is generated from
  ChallengeCategory / ChallengeDifficulty / ChallengeStatus
  ChallengeEligibility   pure filter: is this template realistic for this player?
  ChallengeGenerator     picks a random eligible template, avoiding recent repeats
  ChallengeService       all business logic: generate/complete/reroll/abandon
  ChallengeTemplateSeeder  the starter content library (~38 templates across all 7 categories)

player/
  Player, Skill, AccountType, ChallengeLeveling
  PlayerService          business logic; commands never touch the repository directly

database/
  Database                connection + schema (SQLite)
  repositories/            all SQL lives here (PlayerRepository, ChallengeRepository, ChallengeTemplateRepository)

osrs/
  OsrsStatsProvider (interface) + OsrsStatsSnapshot + NoOpOsrsStatsProvider
  Swap in a real hiscores/RuneLite/WOM client later without touching PlayerService.

discord/
  embeds/   ChallengeEmbedBuilder, ProfileEmbedBuilder
  buttons/  ChallengeButtons (Complete / Reroll / Abandon)

config/
  Configuration   reads DISCORD_TOKEN, DATABASE_PATH, DEV_GUILD_ID from env vars
```

**Key design decisions:**

- **Template vs. instance separation.** `ChallengeTemplate` rows are the content library;
  `Challenge` rows are per-player assignments with a status (`ACTIVE`, `COMPLETED`,
  `FAILED`, `ABANDONED`, `REROLLED`). Adding a new challenge is a new seed entry, not a
  new `if` branch in a command handler.
- **Eligibility as data.** Each template carries thresholds (min/max combat, min total
  level, min quest points, one required skill+level, members-only flag).
  `ChallengeEligibility.isEligible(player, template)` is the single gate everything
  passes through, which is what prevents e.g. a level-20 account getting a Barrows
  template (`min_combat_level = 70`) or an F2P account getting a members-only one.
- **Simplification, called out on purpose:** real OSRS boss/content unlocks are gated by
  *specific quests*, not quest-point totals or levels. Phase 1 approximates this with
  quest-point/level thresholds because per-quest unlock tracking isn't in the Phase 1
  data model. Once automatic stat retrieval (Phase 3) can also report completed quests,
  tighten `ChallengeEligibility` to check exact quest completion instead.
- **Services own logic, commands are dumb.** `ProfileCommand`, `StatsCommand`,
  `TaskCommand` only parse Discord options and call `PlayerService`/`ChallengeService`.
  This is what lets Phase 3/4 add a `/leaderboard` or a scheduled daily-challenge job
  that reuses the same services without duplicating streak/XP math.
- **Repository layer isolates SQL.** Nothing outside `database/repositories` writes SQL,
  so swapping SQLite for Postgres later is confined to that package.
- **Reroll limiting lives in `ChallengeService`**, not in the button listener, so a
  future `/admin rerolls` command can change the limit without touching Discord code.

## Running it

1. Create a Discord application + bot at https://discord.com/developers/applications,
   enable it, and invite it to a server with the `applications.commands` and `bot`
   scopes (permissions: Send Messages, Embed Links, Use Slash Commands).
2. Copy `.env.example` to `.env` (or just export the variables) and fill in
   `DISCORD_TOKEN`. Set `DEV_GUILD_ID` to your test server's ID for instant command
   registration while developing — global commands can take up to an hour to appear.
3. This project has no Gradle wrapper checked in (no network access to generate one in
   this environment). If you have Gradle installed locally, run:
   ```
   gradle wrapper --gradle-version 8.7
   ./gradlew run
   ```
   or just `gradle run` directly if you don't want a wrapper.
4. On first run, the bot creates `osrs-challenge.db` (SQLite) next to the jar and seeds
   the challenge template table automatically.

## Using it

- `/profile` — view your Challenge Level, XP, streaks, and completion counts.
- `/stats view` — see your current combat/total/quest points and all 23 skill levels.
- `/stats set-username`, `set-account-type`, `set-combat`, `set-total`, `set-quests`,
  `set-skill` — manually enter your OSRS progression (Phase 1 has no live hiscores
  lookup; see `osrs/OsrsStatsProvider`).
- `/task` — get your active challenge, or generate a new one if you don't have one.
  Shows an embed with category, difficulty, bonus objective, reward, and your current
  streak, plus **Complete / Reroll / Abandon** buttons.
  - **Complete** grants Challenge XP, extends your streak, and locks the buttons.
  - **Reroll** marks the old challenge as skipped and generates a new one (max 3/day,
    resets 24h after your last reset).
  - **Abandon** marks it failed and resets your streak.

## Roadmap (from the original brief, not built here)

- **Phase 2:** `/history`, richer streak displays, more challenge categories/content.
  `ChallengeRepository.recentForPlayer()` already exists for this.
- **Phase 3:** real `OsrsStatsProvider` implementation (official hiscores or similar),
  `/leaderboard`, `/daily`, `/admin` server settings (a `ServerSettings` table/repository
  following the same pattern as `PlayerRepository`).
- **Phase 4:** `/party` group challenges, challenge modifiers (melee-only, hardcore,
  no-GE, etc. — these map naturally onto extra boolean/enum columns on `Challenge`),
  automatic verification, more advanced weighted generation (e.g. avoid repeating a
  *category* too often, not just a template).

Do not attempt to build all of that at once — the brief is explicit that a small,
fully-working slice beats a large, half-working system.
