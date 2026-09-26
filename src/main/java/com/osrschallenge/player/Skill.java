package com.osrschallenge.player;

public enum Skill {
    ATTACK, STRENGTH, DEFENCE, HITPOINTS, RANGED, PRAYER, MAGIC,
    COOKING, WOODCUTTING, FLETCHING, FISHING, FIREMAKING, CRAFTING,
    SMITHING, MINING, HERBLORE, AGILITY, THIEVING, SLAYER, FARMING,
    RUNECRAFT, HUNTER, CONSTRUCTION;

    public String displayName() {
        String name = name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
