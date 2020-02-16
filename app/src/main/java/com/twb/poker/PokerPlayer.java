package com.twb.poker;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.Hand;
import com.twb.poker.domain.PlayerUser;
import com.twb.poker.layout.CardPairLayout;

public class PokerPlayer {
    private final PlayerUser playerUser;

    private final CardPairLayout cardPairLayout;

    private final boolean currentPlayer;

    private boolean dealerPlayer;

    private Hand hand = new Hand();

    PokerPlayer(CardPairLayout cardPairLayout, PlayerUser playerUser, boolean currentPlayer) {
        this.cardPairLayout = cardPairLayout;
        this.playerUser = playerUser;
        this.currentPlayer = currentPlayer;
    }

    public void update(final Card card) {
        cardPairLayout.update(card);
        hand.update(card);
    }

    public void displayDisplayName() {
        updateDisplayName(playerUser.getDisplayName());
    }

    public void displayFunds() {
        updateFunds(playerUser.getBank().getFunds());
    }

    public void updateDisplayName(final String displayName) {
        cardPairLayout.update(displayName);
    }

    public void updateFunds(final Double funds) {
        cardPairLayout.update(funds);
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

    public Hand getHand() {
        return hand;
    }

    public PlayerUser getPlayerUser() {
        return playerUser;
    }

    @Override
    public String toString() {
        return "PokerPlayer{" +
                "playerUser=" + playerUser +
                ", currentPlayer=" + currentPlayer +
                ", dealerPlayer=" + dealerPlayer +
                ", hand=" + hand +
                '}';
    }
}
