package com.twb.poker;

import android.os.Handler;

import androidx.annotation.MainThread;

import com.twb.poker.domain.BetType;
import com.twb.poker.domain.Card;
import com.twb.poker.domain.CommunityCardType;
import com.twb.poker.domain.PlayerBank;
import com.twb.poker.domain.PlayerUser;
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

public class PokerGameThread extends Thread implements PokerTable.PokerTableCallback {
    private static final Handler UI = new Handler();

    private static final int PLAYER_RESPONSE_TIME_IN_SECONDS = 30;

    private boolean evalWaitingOnUserInput = false;
    private boolean turnButtonPressed = false;

    private PokerTable pokerTable;
    private PokerGameThreadCallback callback;

    PokerGameThread(PokerGameThreadCallback callback) {
        setName(PokerGameThread.class.getSimpleName());
        this.callback = callback;
        pokerTable = new PokerTable(this);
        pokerTable.addPlayer("Thomas", true, 0);
        for (int index = 1; index <= 5; index++) {
            pokerTable.addPlayer(index);
        }
    }

    @Override
    public void run() {
        setThreadPriority(THREAD_PRIORITY_URGENT_DISPLAY);

        while (pokerTable.size() > 1) {
            UI.post(() -> callback.onReset());

            pokerTable.init();

            RoundState roundState = RoundState.INIT_DEAL;
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

    @Override
    public void onDealCommunityCard(Card card, CommunityCardType cardType) {
        UI.post(() -> callback.onDealCommunityCard(card, cardType));
        dealSleep();
    }

    @Override
    public void onUpdatePlayersOnTable(PokerTable pokerTable) {
        UI.post(() -> {
            for (PokerPlayer pokerPlayer : pokerTable) {
                callback.onUpdatePokerPlayer(pokerPlayer);
                if (pokerPlayer.isDealerPlayer()) {
                    callback.onPlayerDealer(pokerPlayer, true);
                }
            }
        });
    }

    @Override
    public void onCurrentPlayerBetTurn(PokerPlayer pokerPlayer) {
        UI.post(() -> {
            callback.onAlert();
            callback.onControlsShow(pokerPlayer);
        });
        turnButtonPressed = false;
        for (double turnSecondsLeft = PLAYER_RESPONSE_TIME_IN_SECONDS; turnSecondsLeft >= 0;
             turnSecondsLeft = turnSecondsLeft - PLAYER_RESPONSE_LOOP_IN_SECONDS) {
            returnPercentageLeft(turnSecondsLeft);
            if (turnButtonPressed) {
                UI.post(() -> callback.onControlsHide());
                break;
            }
            if (turnSecondsLeft - PLAYER_RESPONSE_LOOP_IN_SECONDS <= 0) {
                pokerTable.foldCurrentPlayer();
                UI.post(() -> callback.onControlsHide());
            }
            playerTurnSleep();
        }
    }

    private void returnPercentageLeft(double turnSecondsLeft) {
        int percentage = calculatePercentage(turnSecondsLeft);
        UI.post(() -> callback.onPercentageTimeLeft(percentage));
    }

    @Override
    public void onOtherPlayerBetTurn(PokerPlayer pokerPlayer) {
        UI.post(() -> callback.onControlsHide());
        dealSleep();
    }

    @Override
    public void onDealCardToPlayer(PokerPlayer pokerPlayer, Card card) {
        UI.post(() -> callback.onDealCardToPlayer(pokerPlayer, card));
        dealSleep();
    }

    @Override
    public void onPlayerTurn(PokerPlayer pokerPlayer, boolean turn) {
        UI.post(() -> callback.onPlayerTurn(pokerPlayer, turn));
    }

    @Override
    public void onPlayerDealer(PokerPlayer pokerPlayer, boolean dealer) {
        UI.post(() -> callback.onPlayerDealer(pokerPlayer, dealer));
    }

    @Override
    public void onPlayerFold(PokerPlayer pokerPlayer) {
        UI.post(() -> callback.onPlayerFold(pokerPlayer));
    }

    @Override
    public void onEvent(String event) {
        UI.post(() -> callback.onEvent(event));
    }

    private void eval() {
        List<PokerPlayer> pokerPlayerWinners = pokerTable.evaluateAndGetWinners();
        callback.onWinnerDialogShow(pokerPlayerWinners);
        evalWaitingOnUserInput = true;
        while (evalWaitingOnUserInput) {
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

    void setEvalWaitingOnUserInput() {
        evalWaitingOnUserInput = false;
    }

    void checkCurrentPlayer() {
        turnButtonPressed = true;
        PokerPlayer pokerPlayer = pokerTable.getCurrentPlayer();
        UI.post(() -> {
            callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " checked");
        });
    }

    void foldCurrentPlayer() {
        this.turnButtonPressed = true;
        pokerTable.foldCurrentPlayer();
    }

    private int calculatePercentage(double secondsLeft) {
        return (int) Math.round(secondsLeft * 100 / PLAYER_RESPONSE_TIME_IN_SECONDS);
    }

    PlayerBank getCurrentBank() {
        PokerPlayer pokerPlayer = pokerTable.getCurrentPlayer();
        if (pokerPlayer == null) {
            return null;
        }
        PlayerUser playerUser = pokerPlayer.getPlayerUser();
        return playerUser.getBank();
    }

    void onAmountSelected(BetType type, double amount) {
        this.turnButtonPressed = true;
        PokerPlayer pokerPlayer = pokerTable.getCurrentPlayer();
        if (pokerPlayer == null) {
            return;
        }
        pokerTable.setBetAmount(pokerPlayer, type, amount);
    }

    @MainThread
    public interface PokerGameThreadCallback {
        void onAlert();

        void onControlsShow(PokerPlayer pokerPlayer);

        void onControlsHide();

        void onPercentageTimeLeft(int percentage);

        void onDealCommunityCard(Card card, CommunityCardType cardType);

        void onDealCardToPlayer(PokerPlayer pokerPlayer, Card card);

        void onUpdatePokerPlayer(PokerPlayer pokerPlayer);

        void onWinnerDialogShow(List<PokerPlayer> pokerPlayerWinners);

        void onPlayerTurn(PokerPlayer pokerPlayer, boolean turn);

        void onPlayerFold(PokerPlayer pokerPlayer);

        void onPlayerDealer(PokerPlayer pokerPlayer, boolean dealer);

        void onReset();

        void onEvent(String event);
    }
}
