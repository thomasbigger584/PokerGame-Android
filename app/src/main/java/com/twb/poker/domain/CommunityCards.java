package com.twb.poker.domain;

import java.util.ArrayList;
import java.util.List;

public class CommunityCards extends ArrayList<Card> {
    public Card getBurnPreFlop() {
        return get(CommunityCardType.BURN_PRE_FLOP.getPosition());
    }

    public List<Card> getFlop() {
        int flop1 = CommunityCardType.FLOP_1.getPosition();
        int flop3 = CommunityCardType.FLOP_3.getPosition();
        return subList(flop1, flop3);
    }

    public Card getBurnPreRiver() {
        return get(CommunityCardType.BURN_PRE_RIVER.getPosition());
    }

    public Card getRiver() {
        return get(CommunityCardType.RIVER.getPosition());
    }

    public Card getBurnPreTurn() {
        return get(CommunityCardType.BURN_PRE_TURN.getPosition());
    }

    public Card getTurn() {
        return get(CommunityCardType.TURN.getPosition());
    }

    public List<Card> getPlayableCards() {
        List<Card> cards = new ArrayList<>(getFlop());
        cards.add(getRiver());
        cards.add(getTurn());
        return cards;
    }

    public List<Card> getBurnedCards() {
        List<Card> cards = new ArrayList<>();
        cards.add(getBurnPreFlop());
        cards.add(getBurnPreRiver());
        cards.add(getBurnPreTurn());
        return cards;
    }
}
