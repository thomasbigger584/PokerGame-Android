package com.twb.poker.domain;

import org.jetbrains.annotations.NotNull;

public class PlayerBank {

    private double funds = 100d;

    public PlayerBank(double funds) {
        this.funds = funds;
    }

    public double getFunds() {
        return funds;
    }

    public void setFunds(double funds) {
        this.funds = funds;
    }

    @NotNull
    @Override
    public String toString() {
        return "{" +
                "funds=" + funds +
                '}';
    }
}
