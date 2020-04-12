package com.twb.poker.domain;

import org.jetbrains.annotations.NotNull;

import lombok.Data;

@Data
public class Bet {
    private Double betAmount;
    private BetType betType;

    @NotNull
    @Override
    public String toString() {
        if (betAmount == null) {
            return "Bet{betType=" + betType + "}";
        }
        return "Bet{" +
                "betAmount=" + betAmount +
                ", betType=" + betType +
                '}';
    }
}
