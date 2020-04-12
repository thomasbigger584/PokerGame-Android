package com.twb.poker.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Pot {
    private double pot;

    private Bet currentBet;

    private Map<String, List<Bet>> playerBets = new LinkedHashMap<>();

    void addBet(PokerPlayer pokerPlayer, Bet bet) {
        String pokerPlayerId = pokerPlayer.getId();
        if (playerBets.containsKey(pokerPlayerId)) {
            List<Bet> thesePlayerBets = playerBets.get(pokerPlayerId);
            if (thesePlayerBets != null) {
                thesePlayerBets.add(bet);
            }
        } else {
            List<Bet> thesePlayerBets = new ArrayList<>();
            thesePlayerBets.add(bet);
            playerBets.put(pokerPlayerId, thesePlayerBets);
        }

        if (bet.getBetAmount() != null) {
            pot = pot + bet.getBetAmount();
        }

        if (bet.getBetType() != BetType.FOLD) {
            currentBet = bet;
        }
    }

}
