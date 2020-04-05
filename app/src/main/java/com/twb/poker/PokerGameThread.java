package com.twb.poker;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.CommunityCardType;
import com.twb.poker.domain.DeckOfCardsFactory;
import com.twb.poker.domain.PokerPlayer;
import com.twb.poker.domain.PokerTable;
import com.twb.poker.domain.RoundState;
import com.twb.poker.util.SleepUtil;

import java.util.List;

import static android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY;
import static android.os.Process.setThreadPriority;
import static com.twb.poker.util.SleepUtil.dealSleep;
import static com.twb.poker.util.SleepUtil.gameDelaySleep;
import static com.twb.poker.util.SleepUtil.playerTurnSleep;
import static com.twb.poker.util.SleepUtil.roundDelaySleep;

public class PokerGameThread extends Thread {
    private static final String TAG = PokerGameThread.class.getSimpleName();
    private static final int PLAYER_RESPONSE_TIME_IN_SECONDS = 30;
    private static final int NO_CARDS_FOR_PLAYER_DEAL = 2;

    private final Handler uiHandler;
    private List<Card> deckOfCards;
    private PokerTable pokerTable;
    private int deckCardPointer;
    private RoundState roundState;
    private PokerGameThreadCallback callback;
    private boolean turnButtonPressed = false;
    private boolean evalWaitingOnUserInput = false;


    PokerGameThread(PokerTable pokerTable, PokerGameThreadCallback callback) {
        setName(PokerGameThread.class.getSimpleName());
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.pokerTable = pokerTable;
        this.callback = callback;
    }

    @Override
    public void run() {
        setThreadPriority(THREAD_PRIORITY_URGENT_DISPLAY);

        while (pokerTable.size() > 1) {
            initGame();

            while (roundState != RoundState.FINISH) {
                switch (roundState) {
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
                roundState = roundState.nextState();
            }
            finishRound();
        }
        finishGame();
    }

    private void initGame() {
        this.pokerTable.reset();

        this.pokerTable = this.pokerTable.reassignPokerTableForDealer();

        this.deckCardPointer = 0;
        this.roundState = RoundState.INIT_DEAL;
        this.deckOfCards = DeckOfCardsFactory.getCards(true);

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
        callback.onWinnerDialogShow(pokerPlayerWinners);
        this.evalWaitingOnUserInput = true;
        while (this.evalWaitingOnUserInput) {
            SleepUtil.sleep(10);
        }
    }

    private void performPlayerBetTurn() {
        for (int index = 0; index < pokerTable.size(); index++) {
            PokerPlayer prevPokerPlayer = pokerTable.getPrevious(index);
            PokerPlayer thisPokerPlayer = pokerTable.get(index);
            prevPokerPlayer.setTurnPlayer(false);
            if (thisPokerPlayer.isFolded()) {
                continue;
            }
            thisPokerPlayer.setTurnPlayer(true);

            if (thisPokerPlayer.isCurrentPlayer()) {
                uiHandler.post(() -> {
                    this.callback.onVibrate();
                    this.callback.onControlsShow();
                });

                this.turnButtonPressed = false;

                for (double turnSecondsLeft = PLAYER_RESPONSE_TIME_IN_SECONDS; turnSecondsLeft >= 0;
                     turnSecondsLeft = turnSecondsLeft - SleepUtil.PLAYER_RESPONSE_LOOP_IN_SECONDS) {

                    int percentage = calculatePercentage(turnSecondsLeft);
                    uiHandler.post(() -> {
                        callback.onPercentageTimeLeft(percentage);
                    });
                    if (turnButtonPressed) {
                        uiHandler.post(() -> {
                            callback.onControlsHide();
                        });
                        break;
                    }

                    if (turnSecondsLeft - SleepUtil.PLAYER_RESPONSE_LOOP_IN_SECONDS <= 0) {
                        thisPokerPlayer.setFolded(true);
                        uiHandler.post(() -> {
                            callback.onControlsHide();
                        });
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

    private int calculatePercentage(double secondsLeft) {
        return (int) Math.round(secondsLeft * 100 / PLAYER_RESPONSE_TIME_IN_SECONDS);
    }

    private void dealCommunityCard(CommunityCardType cardType) {
        final Card card = deckOfCards.get(deckCardPointer);
        pokerTable.dealCommunityCard(card, cardType);
        deckCardPointer++;
        dealSleep();
    }

    private void finishRound() {
        this.pokerTable.rotateDealer();
        roundDelaySleep();
    }

    private void finishGame() {
        gameDelaySleep();
    }

    private void toast(final String message) {
        uiHandler.post(() -> callback.toast(message));
    }

    void setTurnButtonPressed() {
        this.turnButtonPressed = true;
    }

    void setEvalWaitingOnUserInput() {
        this.evalWaitingOnUserInput = false;
    }

    void foldCurrentPlayer() {
        this.pokerTable.foldCurrentPlayer();
        setTurnButtonPressed();
    }

    @MainThread
    public interface PokerGameThreadCallback {
        void toast(String message);

        void onPercentageTimeLeft(int percentage);

        void onControlsShow();

        void onControlsHide();

        void onVibrate();

        void onWinnerDialogShow(List<PokerPlayer> pokerPlayerWinners);
    }
}
