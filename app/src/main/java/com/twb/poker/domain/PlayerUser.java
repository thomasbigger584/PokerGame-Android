package com.twb.poker.domain;

public class PlayerUser {

    private String displayName;
    private PlayerBank bank;

    public PlayerUser(String displayName, PlayerBank bank) {
        this.displayName = displayName;
        this.bank = bank;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public PlayerBank getBank() {
        return bank;
    }

    public void setBank(PlayerBank bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return "{" +
                "displayName='" + displayName + '\'' +
                ", bank=" + bank +
                '}';
    }
}
