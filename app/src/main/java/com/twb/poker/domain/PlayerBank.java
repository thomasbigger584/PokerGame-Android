package com.twb.poker.domain;

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
}
