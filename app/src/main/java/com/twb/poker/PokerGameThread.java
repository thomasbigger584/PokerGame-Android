package com.twb.poker;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.CommunityCardType;
import com.twb.poker.domain.DeckOfCardsFactory;
import com.twb.poker.domain.PlayerUser;
import com.twb.poker.domain.PokerGameState;

import java.util.List;

public class PokerGameThread extends Thread {
    private static final double DEAL_TIME_IN_MS = 1.25 * 1000;

    private static final int PLAYER_RESPONSE_TIME_IN_SECONDS = 30;

    private static final double PLAYER_RESPONSE_LOOP_UPDATE = 0.1d;

    private static final int NO_CARDS_FOR_PLAYER_DEAL = 2;

    private final Handler uiHandler;

    private List<Card> deckOfCards;

    private PokerTable pokerTable;

    private int deckCardPointer;

    private PokerGameState gameState;

    private PokerGameThreadCallback callback;

    private boolean turnButtonPressed = false;

    PokerGameThread(PokerTable pokerTable, PokerGameThreadCallback callback) {
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.pokerTable = pokerTable;
        this.callback = callback;
    }

    @Override
    public void run() {

        initGame();

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
                case RIVER_DEAL: {
                    riverDeal();
                    break;
                }
                case RIVER_DEAL_BET: {
                    riverDealBet();
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
                case EVAL: {
                    eval();
                    break;
                }
            }
            gameState = gameState.nextState();
        }
    }

    private void initGame() {
        this.pokerTable = this.pokerTable.reorderPokerTableForDealer();
        this.deckOfCards = DeckOfCardsFactory.getCards(true);
        this.deckCardPointer = 0;
        this.gameState = PokerGameState.INIT_DEAL;
        this.pokerTable.initPokerTable();
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
        performPlayerBetTurn();
    }

    private void flopDeal() {
        dealCommunityCard(CommunityCardType.BURN_PRE_FLOP);
        dealCommunityCard(CommunityCardType.FLOP_1);
        dealCommunityCard(CommunityCardType.FLOP_2);
        dealCommunityCard(CommunityCardType.FLOP_3);
    }

    private void flopDealBet() {
        performPlayerBetTurn();
    }

    private void turnDeal() {
        dealCommunityCard(CommunityCardType.BURN_PRE_TURN);
        dealCommunityCard(CommunityCardType.TURN);
    }

    private void turnDealBet() {
        performPlayerBetTurn();
    }

    private void riverDeal() {
        dealCommunityCard(CommunityCardType.BURN_PRE_RIVER);
        dealCommunityCard(CommunityCardType.RIVER);
    }

    private void riverDealBet() {
        performPlayerBetTurn();
    }

    private void eval() {
        List<PokerPlayer> pokerPlayerWinners =
                pokerTable.evaluateAndGetWinners();
        if (pokerPlayerWinners.size() == 1) {
            PokerPlayer winningPokerPlayer = pokerPlayerWinners.get(0);
            PlayerUser playerUser = winningPokerPlayer.getPlayerUser();
            toast("Winner is " + playerUser.getDisplayName() + " with : " + winningPokerPlayer.getHand());
        } else {
            toast("Split pot");
        }
    }

    private void performPlayerBetTurn() {
        for (int index = 0; index < pokerTable.size(); index++) {
            PokerPlayer prevPokerPlayer;
            if (index == 0) {
                prevPokerPlayer = pokerTable.get(pokerTable.size() - 1);
            } else {
                prevPokerPlayer = pokerTable.get(index - 1);
            }
            PokerPlayer thisPokerPlayer = pokerTable.get(index);
            prevPokerPlayer.setTurnPlayer(false);
            thisPokerPlayer.setTurnPlayer(true);

            if (thisPokerPlayer.isCurrentPlayer()) {
                uiHandler.post(() -> {
                    this.callback.onVibrate();
                    this.callback.onControlsShow();
                });

                this.turnButtonPressed = false;

                for (double turnSecondsLeft = PLAYER_RESPONSE_TIME_IN_SECONDS; turnSecondsLeft >= 0;
                     turnSecondsLeft = turnSecondsLeft - PLAYER_RESPONSE_LOOP_UPDATE) {

                    final int secondsleft = (int) turnSecondsLeft;
                    uiHandler.post(() -> {
                        callback.onSecondsLeft(secondsleft);
                    });
                    if (turnButtonPressed) {
                        uiHandler.post(() -> {
                            callback.onControlsHide();
                        });
                        break;
                    }
                    if (turnSecondsLeft == 0) {
                        //force fold here
                    }
                    playerTurnSleep();
                }
            } else {
                uiHandler.post(() -> {
                    callback.onControlsHide();
                });
                dealSleep();
            }
        }
        pokerTable.get(pokerTable.size() - 1).setTurnPlayer(false);
    }

    private void dealCommunityCard(CommunityCardType cardType) {
        final Card card = deckOfCards.get(deckCardPointer);
        pokerTable.dealCommunityCard(card, cardType);
        deckCardPointer++;
        dealSleep();
    }

    private void dealSleep() {
        try {
            sleep(Math.round(DEAL_TIME_IN_MS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playerTurnSleep() {
        try {
            sleep(Math.round(PLAYER_RESPONSE_LOOP_UPDATE * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void toast(final String message) {
        uiHandler.post(() -> callback.toast(message));
    }

    public void setTurnButtonPressed() {
        this.turnButtonPressed = true;
    }

    @MainThread
    public interface PokerGameThreadCallback {
        void toast(String message);

        void onSecondsLeft(int secondsLeft);

        void onControlsShow();

        void onControlsHide();

        void onVibrate();
    }
}
