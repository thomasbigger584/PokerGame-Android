package com.twb.poker;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.Hand;
import com.twb.poker.domain.PlayerBank;
import com.twb.poker.domain.PlayerUser;
import com.twb.poker.layout.CardPairLayout;

public class PokerPlayer {
    private final PlayerUser playerUser;

    private final CardPairLayout cardPairLayout;

    private final boolean currentPlayer;

    private boolean dealerPlayer;

    private Hand hand = new Hand();

    private boolean turnPlayer;

    PokerPlayer(CardPairLayout cardPairLayout, PlayerUser playerUser, boolean currentPlayer) {
        this.cardPairLayout = cardPairLayout;
        this.playerUser = playerUser;
        this.currentPlayer = currentPlayer;
    }

    public void update(final Card card) {
        cardPairLayout.updateCardImageView(card);
        hand.update(card);
    }

    public void updatePokerPlayerOnTable() {
        String displayName = playerUser.getDisplayName();
        cardPairLayout.updateDisplayNameTextView(displayName);
        PlayerBank playerBank = playerUser.getBank();
        cardPairLayout.updateFundsTextView(playerBank.getFunds());
        cardPairLayout.updateDealerChip(dealerPlayer);
    }

    public void setTurnPlayer(boolean turnPlayer) {
        this.turnPlayer = turnPlayer;
        cardPairLayout.updateTurnPlayer(turnPlayer);
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

    public boolean isCurrentPlayer() {
        return currentPlayer;
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
