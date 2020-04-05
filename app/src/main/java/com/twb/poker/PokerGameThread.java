package com.twb.poker;

import androidx.annotation.MainThread;

import com.twb.poker.domain.PokerPlayer;
import com.twb.poker.domain.PokerTable;
import com.twb.poker.domain.RoundState;
import com.twb.poker.util.SleepUtil;

import java.util.List;

import static android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY;
import static android.os.Process.setThreadPriority;
import static com.twb.poker.util.SleepUtil.PLAYER_RESPONSE_LOOP_IN_SECONDS;
import static com.twb.poker.util.SleepUtil.dealSleep;
import static com.twb.poker.util.SleepUtil.gameDelaySleep;
import static com.twb.poker.util.SleepUtil.playerTurnSleep;
import static com.twb.poker.util.SleepUtil.roundDelaySleep;

public class PokerGameThread extends Thread implements PokerTable.ThreadCallback {
    private static final String TAG = PokerGameThread.class.getSimpleName();
    private static final int PLAYER_RESPONSE_TIME_IN_SECONDS = 30;

    private boolean evalWaitingOnUserInput = false;
    private boolean turnButtonPressed = false;

    private PokerTable pokerTable;

    private PokerGameThreadCallback callback;

    PokerGameThread(PokerGameThreadCallback gameThreadCallback) {
        setName(PokerGameThread.class.getSimpleName());
        this.callback = gameThreadCallback;
        this.pokerTable = new PokerTable(gameThreadCallback, this);
        this.pokerTable.addPlayer("Thomas", true);
        for (int index = 0; index < 5; index++) {
            this.pokerTable.addPlayer();
        }
    }

    @Override
    public void run() {
        setThreadPriority(THREAD_PRIORITY_URGENT_DISPLAY);

        while (pokerTable.size() > 1) {
            RoundState roundState = RoundState.INIT_DEAL;
            pokerTable.init();

//            todo update poker player on table

            while (roundState != RoundState.FINISH) {
                switch (roundState) {
                    case INIT_DEAL: {
                        pokerTable.initDeal();
                        break;
                    }
                    case INIT_DEAL_BET:
                    case FLOP_DEAL_BET:
                    case RIVER_DEAL_BET:
                    case TURN_DEAL_BET: {
                        pokerTable.performPlayerBetTurn();
                        break;
                    }
                    case FLOP_DEAL: {
                        pokerTable.flopDeal();
                        break;
                    }
                    case RIVER_DEAL: {
                        pokerTable.riverDeal();
                        break;
                    }
                    case TURN_DEAL: {
                        pokerTable.turnDeal();
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

    private void eval() {
        List<PokerPlayer> pokerPlayerWinners =
                pokerTable.evaluateAndGetWinners();
        callback.onWinnerDialogShow(pokerPlayerWinners);
        this.evalWaitingOnUserInput = true;
        while (this.evalWaitingOnUserInput) {
            SleepUtil.sleep(10);
        }
    }

    private void finishRound() {
        this.pokerTable.rotateDealer();
        roundDelaySleep();
    }

    private void finishGame() {
        gameDelaySleep();
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

    @Override
    public void onCurrentPlayerBetTurn(PokerPlayer pokerPlayer) {
        callback.onAlert();
        callback.onControlsShow();

        this.turnButtonPressed = false;
        for (double turnSecondsLeft = PLAYER_RESPONSE_TIME_IN_SECONDS; turnSecondsLeft >= 0;
             turnSecondsLeft = turnSecondsLeft - PLAYER_RESPONSE_LOOP_IN_SECONDS) {
            int percentage = calculatePercentage(turnSecondsLeft);
            callback.onPercentageTimeLeft(percentage);

            if (turnButtonPressed) {
                callback.onControlsHide();
                break;
            }
            if (turnSecondsLeft - PLAYER_RESPONSE_LOOP_IN_SECONDS <= 0) {
                pokerPlayer.setFolded(true);
                callback.onControlsHide();
            }
            playerTurnSleep();
        }
    }

    @Override
    public void onOtherPlayerBetTurn(PokerPlayer pokerPlayer) {
        callback.onControlsHide();
        dealSleep();
    }

    private int calculatePercentage(double secondsLeft) {
        return (int) Math.round(secondsLeft * 100 / PLAYER_RESPONSE_TIME_IN_SECONDS);
    }

    @MainThread
    public interface PokerGameThreadCallback extends PokerTable.ActivityCallback {
        void onWinnerDialogShow(List<PokerPlayer> pokerPlayerWinners);
    }
}
