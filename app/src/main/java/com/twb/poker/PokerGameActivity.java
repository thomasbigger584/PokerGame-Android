package com.twb.poker;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PokerGameActivity extends AppCompatActivity {
    private PokerGameThread pokerGameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        RelativeLayout pokerGameRelativeLayout = findViewById(R.id.pokerGameRelativeLayout);
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

        PokerTable pokerTable = new PokerTable();
        pokerTable.add(new PokerPlayer(playerCardPairLayout, true));
        pokerTable.add(new PokerPlayer(tablePlayer1CardPairLayout, false));
        pokerTable.add(new PokerPlayer(tablePlayer2CardPairLayout, false));
        pokerTable.add(new PokerPlayer(tablePlayer3CardPairLayout, false));
        pokerTable.add(new PokerPlayer(tablePlayer4CardPairLayout, false));
        pokerTable.add(new PokerPlayer(tablePlayer5CardPairLayout, false));

        pokerGameThread = new PokerGameThread(this, pokerTable);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        pokerGameThread.start();
    }
}
