package com.twb.poker;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.CommunityCardType;
import com.twb.poker.domain.PokerPlayer;
import com.twb.poker.domain.PokerTable;
import com.twb.poker.layout.BetRaiseDialog;
import com.twb.poker.layout.CardPairLayout;
import com.twb.poker.layout.CommunityCardLayout;
import com.twb.poker.layout.PokerDialog;
import com.twb.poker.layout.WinnersDialog;

import java.util.List;

import static com.twb.poker.layout.BetRaiseDialog.DialogType.BET;
import static com.twb.poker.layout.BetRaiseDialog.DialogType.RAISE;

public class PokerGameActivity extends AppCompatActivity implements PokerGameThread.PokerGameThreadCallback {
    private static final int VIBRATE_LENGTH_IN_MS = 500;
    private Handler handler = new Handler();

    private PokerTable pokerTable;
    private PokerGameThread pokerGameThread;

    private LinearLayout pokerGameLinearLayout;
    private GridLayout controlsGridLayout;
    private PokerDialog pokerDialog;

    private ProgressBar secondsLeftProgressBar;
    private CommunityCardLayout communityCardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pokerGameLinearLayout = findViewById(R.id.pokerGameLinearLayout);

        final CardPairLayout playerCardPairLayout =
                pokerGameLinearLayout.findViewById(R.id.playerCardPairLayout);

        final CardPairLayout tablePlayer1CardPairLayout = pokerGameLinearLayout.
                findViewById(R.id.tablePlayer1CardPairLayout);
        final CardPairLayout tablePlayer2CardPairLayout = pokerGameLinearLayout.
                findViewById(R.id.tablePlayer2CardPairLayout);
        final CardPairLayout tablePlayer3CardPairLayout = pokerGameLinearLayout.
                findViewById(R.id.tablePlayer3CardPairLayout);
        final CardPairLayout tablePlayer4CardPairLayout = pokerGameLinearLayout.
                findViewById(R.id.tablePlayer4CardPairLayout);
        final CardPairLayout tablePlayer5CardPairLayout = pokerGameLinearLayout.
                findViewById(R.id.tablePlayer5CardPairLayout);

        communityCardLayout = findViewById(R.id.communityCardLayout);

        controlsGridLayout = findViewById(R.id.controlsGridLayout);
        final Button checkButton = controlsGridLayout.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(v -> {
            pokerGameThread.setTurnButtonPressed();
        });
        final Button foldButton = controlsGridLayout.findViewById(R.id.foldButton);
        foldButton.setOnClickListener(v -> {
            pokerGameThread.foldCurrentPlayer();
        });
        final Button betButton = controlsGridLayout.findViewById(R.id.betButton);
        betButton.setOnClickListener(v -> showBetDialog());

        final Button raiseButton = controlsGridLayout.findViewById(R.id.raiseButton);
        raiseButton.setOnClickListener(v -> {
            showRaiseDialog();
        });

        secondsLeftProgressBar = pokerGameLinearLayout.findViewById(R.id.secondsLeftProgressBar);

        createPokerGameThread();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
        startGameThread();
    }

    private void startGameThread() {
        createPokerGameThread();
        if (!pokerGameThread.isAlive()) {
            pokerGameThread.start();
        }
    }

    private void createPokerGameThread() {
        if (pokerGameThread == null) {
            pokerGameThread = new PokerGameThread(this);
            pokerGameThread.setUncaughtExceptionHandler((t, e) -> toast(e.getMessage()));
        }
    }

    @Override
    public void dealCommunityCard(Card card, CommunityCardType cardType) {
        handler.post(() -> {
            if (cardType.isPlayable()) {
                communityCardLayout.dealCard(card, cardType);
            }
        });
    }

    @Override
    public void onAlert() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_LENGTH_IN_MS, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(VIBRATE_LENGTH_IN_MS);
        }
    }

    @Override
    public void onPercentageTimeLeft(int percentage) {
        handler.post(() -> {
            secondsLeftProgressBar.setProgress(percentage);
        });
    }

    @Override
    public void onControlsShow() {
        handler.post(() -> {
            controlsGridLayout.setVisibility(View.VISIBLE);
            secondsLeftProgressBar.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onControlsHide() {
        dismissPokerDialog();
        handler.post(() -> {
            if (controlsGridLayout.getVisibility() != View.GONE) {
                controlsGridLayout.setVisibility(View.GONE);
            }
            if (secondsLeftProgressBar.getVisibility() != View.INVISIBLE) {
                secondsLeftProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onWinnerDialogShow(List<PokerPlayer> pokerPlayerWinners) {
        dismissPokerDialog();
        pokerDialog = WinnersDialog.newInstance(pokerPlayerWinners, () -> {
            pokerGameThread.setEvalWaitingOnUserInput();
        });
        pokerDialog.show(getSupportFragmentManager());
    }

    private void showBetDialog() {
        dismissPokerDialog();
        pokerDialog = BetRaiseDialog.newInstance(BET, amount -> {
            toast("Bet: " + amount);
        });
        pokerDialog.show(getSupportFragmentManager());
    }

    private void showRaiseDialog() {
        dismissPokerDialog();
        pokerDialog = BetRaiseDialog.newInstance(RAISE, amount -> {
            toast("Raise: " + amount);
        });
        pokerDialog.show(getSupportFragmentManager());
    }

    private void dismissPokerDialog() {
        if (pokerDialog != null) {
            pokerDialog.dismissAllowingStateLoss();
            pokerDialog = null;
        }
    }

    private void setFullScreen() {
        pokerGameLinearLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
