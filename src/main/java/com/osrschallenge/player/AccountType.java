package com.osrschallenge.player;

public enum AccountType {
    REGULAR(true),
    IRONMAN(true),
    HARDCORE_IRONMAN(true),
    ULTIMATE_IRONMAN(true),
    F2P(false);

    private final boolean membersContentAllowed;

    AccountType(boolean membersContentAllowed) {
        this.membersContentAllowed = membersContentAllowed;
    }

    public boolean canAccessMembersContent() {
        return membersContentAllowed;
    }
}
