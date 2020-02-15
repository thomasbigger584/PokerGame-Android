package com.twb.poker;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.PlayerUser;
import com.twb.poker.layout.CardPairLayout;

public class PokerPlayer {

    private final PlayerUser playerUser;
    private final CardPairLayout cardPairLayout;
    private final boolean currentPlayer;
    private boolean dealerPlayer;
    private Card[] hand = new Card[2];

    PokerPlayer(CardPairLayout cardPairLayout, PlayerUser playerUser, boolean currentPlayer) {
        this.cardPairLayout = cardPairLayout;
        this.playerUser = playerUser;
        this.currentPlayer = currentPlayer;
    }

    public void update(final Card card) {
        cardPairLayout.update(card);
        updateHand(card);
    }

    private void updateHand(final Card card) {
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

    public void setDealerPlayer(boolean dealerPlayer) {
        this.dealerPlayer = dealerPlayer;
    }
}
