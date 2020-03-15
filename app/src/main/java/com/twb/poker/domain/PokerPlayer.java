package com.twb.poker.domain;

import com.twb.poker.layout.CardPairLayout;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@RequiredArgsConstructor
public class PokerPlayer {
    private final PlayerUser playerUser;

    private final CardPairLayout cardPairLayout;

    private final boolean currentPlayer;

    private boolean dealerPlayer;

    private Hand hand = new Hand();

    private boolean turnPlayer;

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

    public void reset() {
        this.hand = new Hand();
        cardPairLayout.reset();
    }
}
