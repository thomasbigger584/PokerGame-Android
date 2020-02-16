package com.twb.poker;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.javafaker.Faker;
import com.twb.poker.layout.CardPairLayout;
import com.twb.poker.layout.CommunityCardLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class PokerGameActivity extends AppCompatActivity {
    private PokerGameThread pokerGameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        LinearLayout pokerGameRelativeLayout = findViewById(R.id.pokerGameLinearLayout);
        pokerGameRelativeLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        final CardPairLayout playerCardPairLayout =
                pokerGameRelativeLayout.findViewById(R.id.playerCardPairLayout);

        final CardPairLayout tablePlayer1CardPairLayout = pokerGameRelativeLayout.
                findViewById(R.id.tablePlayer1CardPairLayout);
        final CardPairLayout tablePlayer2CardPairLayout = pokerGameRelativeLayout.
                findViewById(R.id.tablePlayer2CardPairLayout);
        final CardPairLayout tablePlayer3CardPairLayout = pokerGameRelativeLayout.
                findViewById(R.id.tablePlayer3CardPairLayout);
        final CardPairLayout tablePlayer4CardPairLayout = pokerGameRelativeLayout.
                findViewById(R.id.tablePlayer4CardPairLayout);
        final CardPairLayout tablePlayer5CardPairLayout = pokerGameRelativeLayout.
                findViewById(R.id.tablePlayer5CardPairLayout);

        final CommunityCardLayout communityCardLayout = findViewById(R.id.communityCardLayout);

        PokerTable pokerTable = new PokerTable(communityCardLayout);
        pokerTable.addPlayer(playerCardPairLayout, "Thomas", generateRandomFunds(100, 300), true);

        pokerTable.addPlayer(tablePlayer1CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer2CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer3CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer4CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);
        pokerTable.addPlayer(tablePlayer5CardPairLayout, generateRandomName(), generateRandomFunds(75, 200), false);

        pokerGameThread = new PokerGameThread(this, pokerTable);
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
}
