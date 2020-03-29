package com.twb.poker;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.javafaker.Faker;
import com.twb.poker.domain.PokerPlayer;
import com.twb.poker.domain.PokerTable;
import com.twb.poker.layout.BetRaiseDialog;
import com.twb.poker.layout.CardPairLayout;
import com.twb.poker.layout.CommunityCardLayout;
import com.twb.poker.layout.PokerDialog;
import com.twb.poker.layout.WinnersDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import static com.twb.poker.layout.BetRaiseDialog.DialogType.BET;
import static com.twb.poker.layout.BetRaiseDialog.DialogType.RAISE;

public class PokerGameActivity extends AppCompatActivity
        implements PokerGameThread.PokerGameThreadCallback, Thread.UncaughtExceptionHandler {
    private static final int VIBRATE_LENGTH_IN_MS = 500;

    private PokerGameThread pokerGameThread;
    private LinearLayout pokerGameLinearLayout;
    private GridLayout controlsGridLayout;
    private PokerDialog pokerDialog;
    private PokerTable pokerTable;

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

        final CommunityCardLayout communityCardLayout = findViewById(R.id.communityCardLayout);

        controlsGridLayout = findViewById(R.id.controlsGridLayout);
        Button checkButton = controlsGridLayout.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(v -> {
            pokerGameThread.setTurnButtonPressed();
        });
        Button foldButton = controlsGridLayout.findViewById(R.id.foldButton);
        foldButton.setOnClickListener(v -> {
            pokerGameThread.setTurnButtonPressed();
        });
        Button betButton = controlsGridLayout.findViewById(R.id.betButton);
        betButton.setOnClickListener(v -> showBetDialog());

        Button raiseButton = controlsGridLayout.findViewById(R.id.raiseButton);
        raiseButton.setOnClickListener(v -> {
            showRaiseDialog();
        });

        pokerTable = new PokerTable(communityCardLayout);
        pokerTable.addPlayer(playerCardPairLayout, "Thomas", generateRandomFunds(100, 300), true);
        pokerTable.addPlayer(tablePlayer1CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer2CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer3CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer4CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer5CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
    }

    private String generateRandomName() {
        return new Faker().name().firstName();
    }

    private double generateRandomFunds(int rangeMin, int rangeMax) {
        Random r = new Random();
        double funds = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        BigDecimal bd = new BigDecimal(funds).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
        checkThreadLife();
    }

    private void checkThreadLife() {
        if (pokerGameThread != null) {
            if (!pokerGameThread.isAlive()) {
                pokerGameThread.start();
            }
        } else {
            createPokerGameThread(pokerTable);
            pokerGameThread.start();
        }
    }

    private void createPokerGameThread(PokerTable pokerTable) {
        pokerGameThread = new PokerGameThread(pokerTable, this);
        pokerGameThread.setUncaughtExceptionHandler(this);
    }

    @Override
    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSecondsLeft(int secondsLeft) {
//        Log.e(PokerGameActivity.class.getName(), "onSecondsLeft: " + secondsLeft);
    }

    @Override
    public void onControlsShow() {
        controlsGridLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onControlsHide() {
        dismissPokerDialog();
        if (controlsGridLayout.getVisibility() != View.GONE) {
            controlsGridLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onVibrate() {
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
    public void onWinnerDialogShow(List<PokerPlayer> pokerPlayerWinners, UserInputCallback userInputCallback) {
        dismissPokerDialog();
        pokerDialog = WinnersDialog.newInstance(pokerPlayerWinners, userInputCallback::onUserInput);
        pokerDialog.show(getSupportFragmentManager());
    }

    private void showBetDialog() {
        dismissPokerDialog();
        pokerDialog = BetRaiseDialog.newInstance(BET, amount -> {

        });
        pokerDialog.show(getSupportFragmentManager());
    }

    private void showRaiseDialog() {
        dismissPokerDialog();
        pokerDialog = BetRaiseDialog.newInstance(RAISE, amount -> {

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
        pokerGameLinearLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        toast(e.getMessage());
    }
}
