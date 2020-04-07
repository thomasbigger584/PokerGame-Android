package com.twb.poker.domain;

import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@RequiredArgsConstructor
public class PokerPlayer {
    private final String id = UUID.randomUUID().toString();

    private final PlayerUser playerUser;

    private final boolean currentPlayer;

    private final int tableIndex;

    private boolean dealerPlayer;

    private Hand hand = new Hand();

    private boolean turnPlayer;

    private boolean folded;

    private int betCount = 0;

    void update(final Card card) {
        hand.update(card);
    }

    void reset() {
        this.folded = false;
        this.hand = new Hand();
    }
}
