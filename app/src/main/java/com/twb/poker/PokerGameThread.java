package com.twb.poker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.DeckOfCardsFactory;

import java.util.List;

public class PokerGameThread extends Thread {
    private static final int DEAL_TIME_IN_MS = 1000;

    private static final int NO_CARDS_FOR_PLAYER_DEAL = 2;

    private final Context context;

    private final Handler uiHandler;

    private final List<Card> deckOfCards;

    private final PokerTable pokerTable;

    private int deckCardPointer;

    PokerGameThread(Context context, PokerTable pokerTable) {
        this.context = context;
        this.uiHandler = new Handler(Looper.getMainLooper());

        this.pokerTable = pokerTable.reorderPokerTableForDealer();
        this.deckOfCards = DeckOfCardsFactory.getCards(true);
        this.deckCardPointer = 0;
    }

    @Override
    public void run() {
        dealCards();

    }

    private void dealCards() {
        for (int dealRoundIndex = 0; dealRoundIndex < NO_CARDS_FOR_PLAYER_DEAL; dealRoundIndex++) {
            for (PokerPlayer pokerPlayer : pokerTable) {
                final Card cardToDeal = deckOfCards.get(deckCardPointer);
                pokerPlayer.update(cardToDeal);
                deckCardPointer++;
                dealSleep();
            }
        }
    }

    private void dealSleep() {
        try {
            Thread.sleep(DEAL_TIME_IN_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
