package com.twb.poker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.DeckOfCardsFactory;
import com.twb.poker.domain.PokerGameState;

import java.util.List;

public class PokerGameThread extends Thread {
    private static final int DEAL_TIME_IN_MS = 2 * 1000;

    private static final int NO_CARDS_FOR_PLAYER_DEAL = 2;

    private final Context context;

    private final Handler uiHandler;

    private final List<Card> deckOfCards;

    private final PokerTable pokerTable;

    private int deckCardPointer;

    private PokerGameState gameState;

    PokerGameThread(Context context, PokerTable pokerTable) {
        this.context = context;
        this.uiHandler = new Handler(Looper.getMainLooper());

        this.pokerTable = pokerTable.reorderPokerTableForDealer();
        this.deckOfCards = DeckOfCardsFactory.getCards(true);
        this.deckCardPointer = 0;
        this.gameState = PokerGameState.INIT_DEAL;
    }

    @Override
    public void run() {
        while (gameState != PokerGameState.FINISH) {
            switch (gameState) {
                case INIT_DEAL: {
                    initDeal();
                    break;
                }
                case INIT_DEAL_BET: {
                    initDealBet();
                    break;
                }
                case FLOP_DEAL: {
                    flopDeal();
                    break;
                }
                case FLOP_DEAL_BET: {
                    flopDealBet();
                    break;
                }
                case TURN_DEAL: {
                    turnDeal();
                    break;
                }
                case TURN_DEAL_BET: {
                    turnDealBet();
                    break;
                }
                case RIVER_DEAL: {
                    riverDeal();
                    break;
                }
                case RIVER_DEAL_BET: {
                    riverDealBet();
                    break;
                }
                case EVAL: {
                    eval();
                    break;
                }
            }
            gameState = gameState.nextState();
        }

        toast("Game Finished");
    }

    private void toast(final String message) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDeal() {
        for (int dealRoundIndex = 0; dealRoundIndex < NO_CARDS_FOR_PLAYER_DEAL; dealRoundIndex++) {
            for (PokerPlayer pokerPlayer : pokerTable) {
                final Card cardToDeal = deckOfCards.get(deckCardPointer);
                pokerPlayer.update(cardToDeal);
                deckCardPointer++;
                dealSleep();
            }
        }
    }

    private void initDealBet() {
        dealSleep();
        toast("initDealBet");
        dealSleep();
    }

    private void flopDeal() {
        dealSleep();
        toast("flopDeal");
        dealSleep();
    }

    private void flopDealBet() {
        dealSleep();
        toast("flopDealBet");
        dealSleep();
    }

    private void turnDeal() {
        dealSleep();
        toast("turnDeal");
        dealSleep();
    }

    private void turnDealBet() {
        dealSleep();
        toast("turnDealBet");
        dealSleep();
    }

    private void riverDeal() {
        dealSleep();
        toast("riverDeal");
        dealSleep();
    }

    private void riverDealBet() {
        dealSleep();
        toast("riverDealBet");
        dealSleep();
    }

    private void eval() {
        dealSleep();
        toast("eval");
        dealSleep();
    }

    private void dealSleep() {
        try {
            sleep(DEAL_TIME_IN_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
