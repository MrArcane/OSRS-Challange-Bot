package com.osrschallenge.challenge;

import com.osrschallenge.database.repositories.ChallengeTemplateRepository;
import com.osrschallenge.player.Skill;

import java.util.List;

/** Seeds the starter challenge pool on first run. Safe to run every startup: existing keys are skipped. */
public class ChallengeTemplateSeeder {
    private final ChallengeTemplateRepository repository;

    public ChallengeTemplateSeeder(ChallengeTemplateRepository repository) {
        this.repository = repository;
    }

    public void seedIfEmpty() {
        for (ChallengeTemplate template : templates()) {
            if (!repository.existsByKey(template.key())) {
                repository.insert(template);
            }
        }
    }

    private ChallengeTemplate t(String key, String name, ChallengeCategory category, String description,
                                 String bonus, ChallengeDifficulty difficulty, int minCombat, int maxCombat,
                                 int minTotal, int minQp, Skill reqSkill, int reqSkillLevel, boolean members,
                                 String time) {
        return new ChallengeTemplate(0, key, name, category, description, bonus, difficulty,
                minCombat, maxCombat, minTotal, minQp, reqSkill, reqSkillLevel, members, time,
                difficulty.baseRewardXp(), true);
    }

    private List<ChallengeTemplate> templates() {
        return List.of(
            // ---- SKILLING ----
            t("skill_mining_100k", "Rock Solid", ChallengeCategory.SKILLING,
                "Gain 100,000 Mining XP.", "Do it entirely at a single mining site.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, Skill.MINING, 15, false, "3-5 hours"),

            t("skill_mining_500k", "Deep Excavation", ChallengeCategory.SKILLING,
                "Gain 500,000 Mining XP.", "Use only ore that you mine yourself, no buying.",
                ChallengeDifficulty.VERY_HARD, 0, 200, 0, 0, Skill.MINING, 40, false, "1-2 days"),

            t("skill_woodcutting_magics_500", "Timber!", ChallengeCategory.SKILLING,
                "Cut 500 Magic logs.", "Bank them all yourself without using a butler or POH pool.",
                ChallengeDifficulty.HARD, 0, 200, 0, 0, Skill.WOODCUTTING, 75, true, "3-4 hours"),

            t("skill_fishing_1000", "Reel It In", ChallengeCategory.SKILLING,
                "Catch 1,000 fish of any type.", "Cook at least half of what you catch.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, Skill.FISHING, 20, false, "3-4 hours"),

            t("skill_cooking_low_to_mid", "Kitchen Nightmare", ChallengeCategory.SKILLING,
                "Train Cooking from your current level up by 10 levels.", "Do not burn more than 5% of what you cook.",
                ChallengeDifficulty.EASY, 0, 200, 0, 0, Skill.COOKING, 1, false, "1-2 hours"),

            t("skill_firemaking_bonfire", "Light It Up", ChallengeCategory.SKILLING,
                "Gain 50,000 Firemaking XP.", "Use only logs you have gathered yourself.",
                ChallengeDifficulty.EASY, 0, 200, 0, 0, Skill.FIREMAKING, 10, false, "1-2 hours"),

            t("skill_agility_rooftop", "Rooftop Runner", ChallengeCategory.SKILLING,
                "Complete 150 laps of a rooftop agility course appropriate for your level.",
                "Do not fail an obstacle more than 10 times total.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, Skill.AGILITY, 30, false, "2-3 hours"),

            t("skill_thieving_master_farmer", "Sticky Fingers", ChallengeCategory.SKILLING,
                "Gain 75,000 Thieving XP from pickpocketing.", "Stack a Thieving-boosting item or aura if you have one.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, Skill.THIEVING, 25, false, "2-3 hours"),

            t("skill_herblore_potions", "Potion Brewer", ChallengeCategory.SKILLING,
                "Make 250 potions of any type.", "Clean your own herbs instead of buying them cleaned.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, Skill.HERBLORE, 20, true, "1-2 hours"),

            t("skill_runecraft_essence", "Rune Essence Grind", ChallengeCategory.SKILLING,
                "Gain 30,000 Runecraft XP.", "Craft at least one rune type you haven't crafted before this month.",
                ChallengeDifficulty.HARD, 0, 200, 0, 0, Skill.RUNECRAFT, 20, false, "3-5 hours"),

            t("skill_farming_herb_run", "Green Thumb", ChallengeCategory.SKILLING,
                "Complete 5 full herb runs across your unlocked patches.", "Pay for disease protection on every patch.",
                ChallengeDifficulty.EASY, 0, 200, 0, 0, Skill.FARMING, 9, true, "Ongoing, ~1 week"),

            t("skill_construction_poh", "Home Improvement", ChallengeCategory.SKILLING,
                "Gain 40,000 Construction XP building your POH.", "Build at least one new room you don't currently have.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, Skill.CONSTRUCTION, 20, true, "2-3 hours"),

            t("skill_slayer_task_xp", "Task Grinder", ChallengeCategory.SKILLING,
                "Gain 20,000 Slayer XP from Slayer tasks.", "Use a Slayer helm or equivalent if you own one.",
                ChallengeDifficulty.NORMAL, 20, 200, 0, 0, Skill.SLAYER, 10, false, "2-4 hours"),

            // ---- PVM ----
            t("pvm_barrows_10", "Barrows Run", ChallengeCategory.PVM,
                "Complete 10 Barrows chests.", "Obtain a Barrows unique.",
                ChallengeDifficulty.HARD, 70, 200, 0, 0, null, 0, true, "1-2 hours"),

            t("pvm_barrows_no_death", "Flawless Crypt", ChallengeCategory.PVM,
                "Complete 10 Barrows chests without dying.", "Kill all six brothers on every run.",
                ChallengeDifficulty.VERY_HARD, 80, 200, 0, 0, null, 0, true, "2-3 hours"),

            t("pvm_slayer_25", "Slayer Sweep", ChallengeCategory.PVM,
                "Kill 25 monsters assigned by your Slayer master.", "Complete the task without banking mid-way.",
                ChallengeDifficulty.NORMAL, 30, 200, 0, 0, Skill.SLAYER, 15, false, "1-2 hours"),

            t("pvm_gwd_boss_10", "Godwars Duty", ChallengeCategory.PVM,
                "Get 10 boss kills at any God Wars Dungeon faction.", "Bring a friend or use safespots to minimise damage taken.",
                ChallengeDifficulty.HARD, 90, 200, 0, 0, null, 0, true, "2-3 hours"),

            t("pvm_wildy_boss_5", "Into the Wilderness", ChallengeCategory.PVM,
                "Complete 5 Wilderness boss kills.", "Bring only starter gear you're willing to lose.",
                ChallengeDifficulty.VERY_HARD, 85, 200, 0, 0, null, 0, true, "2-4 hours"),

            t("pvm_zulrah_5", "Snake Charmer", ChallengeCategory.PVM,
                "Kill Zulrah 5 times.", "Get a unique Zulrah drop.",
                ChallengeDifficulty.EXTREME, 100, 200, 0, 150, null, 0, true, "2-4 hours"),

            t("pvm_low_level_combat", "First Blood", ChallengeCategory.PVM,
                "Kill 50 low-level monsters of your choice for combat XP.", "Try a combat style you don't usually use.",
                ChallengeDifficulty.EASY, 3, 40, 0, 0, null, 0, false, "30-60 minutes"),

            t("pvm_nightmare_zone", "Nightmare Zone Grind", ChallengeCategory.PVM,
                "Complete 20 laps of the Nightmare Zone.", "Use points earned to buy an upgrade you don't already own.",
                ChallengeDifficulty.NORMAL, 60, 200, 0, 0, null, 0, true, "1-2 hours"),

            // ---- QUESTING ----
            t("quest_specific_low", "Adventure Awaits", ChallengeCategory.QUESTING,
                "Complete a quest you have not finished yet, appropriate for your level.",
                "Read every piece of quest dialogue instead of skipping it.",
                ChallengeDifficulty.EASY, 0, 200, 0, 0, null, 0, false, "30-60 minutes"),

            t("quest_50_points", "Quest Points Push", ChallengeCategory.QUESTING,
                "Earn 50 quest points from any quests.", "Prioritise quests that unlock new skilling or PVM content.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, null, 0, true, "3-5 hours"),

            t("quest_chain_recipe_disaster", "Quest Chain: Cooking Crisis", ChallengeCategory.QUESTING,
                "Make progress toward or complete Recipe for Disaster.", "Free a subquest hostage you haven't freed yet.",
                ChallengeDifficulty.HARD, 0, 200, 0, 150, null, 0, true, "2-4 hours"),

            t("quest_mid_game_push", "Mid-Game Momentum", ChallengeCategory.QUESTING,
                "Complete two quests you haven't finished yet.", "Pick quests from two different quest series.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 30, null, 0, true, "2-3 hours"),

            // ---- MONEY ----
            t("money_1m_activity", "Million GP Hustle", ChallengeCategory.MONEY,
                "Earn 1,000,000 GP from a single activity of your choice.", "Don't touch the Grand Exchange for the source items.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, null, 0, true, "2-3 hours"),

            t("money_500k_skilling", "Skill For Profit", ChallengeCategory.MONEY,
                "Make 500,000 GP from a skilling method.", "Reinvest at least half the profit into your account.",
                ChallengeDifficulty.EASY, 0, 200, 0, 0, null, 0, false, "1-2 hours"),

            t("money_flip_challenge", "Merchant's Gambit", ChallengeCategory.MONEY,
                "Turn a profit by flipping items on the Grand Exchange.", "Only flip items under 100k each.",
                ChallengeDifficulty.HARD, 0, 200, 0, 0, null, 0, true, "Ongoing, ~1 day"),

            t("money_loot_specific", "Bounty Collector", ChallengeCategory.MONEY,
                "Loot 3 valuable items (250k+ each) from monsters or bosses.", "Sell nothing until you've hit the target.",
                ChallengeDifficulty.VERY_HARD, 70, 200, 0, 0, null, 0, true, "3-5 hours"),

            // ---- EXPLORATION ----
            t("exploration_regions", "World Traveler", ChallengeCategory.EXPLORATION,
                "Visit 5 locations you rarely go to.", "Take a screenshot at each for your own records.",
                ChallengeDifficulty.EASY, 0, 200, 0, 0, null, 0, false, "30-60 minutes"),

            t("exploration_transport", "Scenic Route", ChallengeCategory.EXPLORATION,
                "Travel using 3 different transportation methods you don't normally use.",
                "Include at least one you've never used before.",
                ChallengeDifficulty.EASY, 0, 200, 0, 0, null, 0, true, "30-45 minutes"),

            t("exploration_wilderness", "Wilderness Wanderer", ChallengeCategory.EXPLORATION,
                "Explore 3 Wilderness landmarks you haven't visited recently.", "Go in with nothing you're not willing to lose.",
                ChallengeDifficulty.NORMAL, 20, 200, 0, 0, null, 0, true, "1 hour"),

            // ---- ACHIEVEMENT ----
            t("achievement_diary_easy", "Diary Duty", ChallengeCategory.ACHIEVEMENT,
                "Complete an Easy achievement diary task you haven't done yet.",
                "Pick a diary region you haven't focused on before.",
                ChallengeDifficulty.EASY, 0, 200, 0, 0, null, 0, true, "30-60 minutes"),

            t("achievement_collection_log", "Log Hunter", ChallengeCategory.ACHIEVEMENT,
                "Obtain one new collection log item.", "Track your kill count before and after.",
                ChallengeDifficulty.VERY_HARD, 70, 200, 0, 0, null, 0, true, "3+ hours"),

            t("achievement_equipment_unlock", "Gear Up", ChallengeCategory.ACHIEVEMENT,
                "Unlock a new piece of equipment appropriate for your account.", "Wear it immediately once unlocked.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, null, 0, true, "1-2 hours"),

            // ---- CHALLENGE (restrictions / unusual runs) ----
            t("challenge_melee_only_slayer", "Melee Purist", ChallengeCategory.CHALLENGE,
                "Complete a Slayer task using only melee combat.", "No switching to ranged or magic even for safespotting.",
                ChallengeDifficulty.HARD, 40, 200, 0, 0, Skill.SLAYER, 20, false, "1-2 hours"),

            t("challenge_no_ge", "Self Sufficient", ChallengeCategory.CHALLENGE,
                "Complete any activity of your choice without using the Grand Exchange at all.",
                "Craft or gather every item you need yourself.",
                ChallengeDifficulty.VERY_HARD, 0, 200, 0, 0, null, 0, true, "2-4 hours"),

            t("challenge_no_prayer_boss", "Prayerless", ChallengeCategory.CHALLENGE,
                "Defeat a boss of your choice without using Protection Prayers.", "Bring extra food to compensate.",
                ChallengeDifficulty.EXTREME, 80, 200, 0, 0, null, 0, true, "1-3 hours"),

            t("challenge_inventory_restriction", "Tight Squeeze", ChallengeCategory.CHALLENGE,
                "Complete an activity of your choice using only half your normal inventory space.",
                "Decide the restriction before you start and stick to it.",
                ChallengeDifficulty.HARD, 0, 200, 0, 0, null, 0, true, "1-2 hours"),

            t("challenge_time_limit", "Against the Clock", ChallengeCategory.CHALLENGE,
                "Complete an activity of your choice within a 30-minute time limit.",
                "If you don't finish in time, it still counts as an attempt.",
                ChallengeDifficulty.NORMAL, 0, 200, 0, 0, null, 0, false, "30 minutes")
        );
    }
}
