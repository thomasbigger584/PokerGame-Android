package com.twb.poker;

import com.twb.poker.domain.Card;

public class PokerPlayer {

    private final CardPairLayout cardPairLayout;
    private final boolean currentPlayer;
    private final boolean dealerPlayer;
    private Card[] hand = new Card[2];

    PokerPlayer(CardPairLayout cardPairLayout, boolean currentPlayer, boolean dealerPlayer) {
        this.cardPairLayout = cardPairLayout;
        this.currentPlayer = currentPlayer;
        this.dealerPlayer = dealerPlayer;
    }

    public void updateHand(final Card card) {
        if (hand[0] != null && hand[1] != null) {
            hand = new Card[2];
        }
        if (hand[0] == null) {
            hand[0] = card;
        } else if (hand[1] == null) {
            hand[1] = card;
        }
    }

    public CardPairLayout getCardPairLayout() {
        return this.cardPairLayout;
    }

    public boolean isDealerPlayer() {
        return this.dealerPlayer;
    }
}
