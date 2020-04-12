package com.twb.poker.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import lombok.Data;

@Data
public class Pot {
    private double pot;
    private Bet currentBet;
    private Map<String, List<Bet>> playersBets = new LinkedHashMap<>();

    void addBet(PokerPlayer pokerPlayer, Bet bet) {
        String pokerPlayerId = pokerPlayer.getId();
        if (playersBets.containsKey(pokerPlayerId)) {
            List<Bet> thesePlayerBets = playersBets.get(pokerPlayerId);
            if (thesePlayerBets != null) {
                thesePlayerBets.add(bet);
            }
        } else {
            List<Bet> thesePlayerBets = new ArrayList<>();
            thesePlayerBets.add(bet);
            playersBets.put(pokerPlayerId, thesePlayerBets);
        }

        if (bet.getBetAmount() != null) {
            pot = pot + bet.getBetAmount();
            String potStr = String.format(Locale.getDefault(), "%.2f", pot);
            pot = Double.valueOf(potStr);
        }

        if (bet.getBetType() == BetType.FOLD) {
            playersBets.remove(pokerPlayerId);
        } else {
            currentBet = bet;
        }
    }

    boolean isAllPlayersPaidUp(int tableCount) {
        if (tableCount != playersBets.size()) {
            return false;
        }

        //check if everyone has paid the same
        List<Double> totalBetsList = new ArrayList<>();
        for (Map.Entry<String, List<Bet>> playerBets : playersBets.entrySet()) {
            List<Bet> bets = playerBets.getValue();
            double allBetsCount = countAllBets(bets);
            totalBetsList.add(allBetsCount);
        }

        Double thisTotalBet = null;
        for (Double totalBet : totalBetsList) {
            if (thisTotalBet == null) {
                thisTotalBet = totalBet;
            } else if (!Objects.equals(thisTotalBet, totalBet)) {
                return false;
            }
        }
        return true;
    }

    private double countAllBets(List<Bet> bets) {
        double count = 0d;
        for (Bet bet : bets) {
            if (bet.getBetAmount() != null) {
                count += bet.getBetAmount();
            }
        }
        return count;
    }

    void clearForPlayerRound() {
        currentBet = null;
        playersBets.clear();
    }
}
