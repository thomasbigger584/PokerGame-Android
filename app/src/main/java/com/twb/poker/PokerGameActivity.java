package com.twb.poker;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.javafaker.Faker;
import com.twb.poker.layout.CardPairLayout;
import com.twb.poker.layout.CommunityCardLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class PokerGameActivity extends AppCompatActivity implements PokerGameThread.PokerGameThreadCallback {
    private PokerGameThread pokerGameThread;
    private LinearLayout pokerGameLinearLayout;
    private GridLayout controlsGridLayout;

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
        betButton.setOnClickListener(v -> {
            pokerGameThread.setTurnButtonPressed();
        });
        Button raiseButton = controlsGridLayout.findViewById(R.id.raiseButton);
        raiseButton.setOnClickListener(v -> {
            pokerGameThread.setTurnButtonPressed();
        });

        PokerTable pokerTable = new PokerTable(communityCardLayout);
        pokerTable.addPlayer(playerCardPairLayout, "Thomas", generateRandomFunds(100, 300), true);

        pokerTable.addPlayer(tablePlayer1CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer2CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer3CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer4CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer5CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);

        PokerGameThread.PokerGameThreadCallback thisCallback = this;
        pokerGameThread = new PokerGameThread(pokerTable, thisCallback);
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
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        pokerGameThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pokerGameLinearLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSecondsLeft(int secondsLeft) {
        Log.e(PokerGameActivity.class.getName(), "onSecondsLeft: " + secondsLeft);
    }

    @Override
    public void onControlsShow() {
        controlsGridLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onControlsHide() {
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
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
    }
}
