package com.twb.poker.domain;

import lombok.Data;

@Data
public class Pot {
    private double pot;

    private double currentBet;
    private BetType currentBetType;
}
