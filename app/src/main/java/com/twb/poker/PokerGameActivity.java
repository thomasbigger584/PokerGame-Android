package com.twb.poker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.DeckOfCardsFactory;

import java.util.List;

public class PokerGameActivity extends AppCompatActivity {

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

        final CardPairLayout playerCardPlayerLayout =
                pokerGameRelativeLayout.findViewById(R.id.playerCardPairLayout);


        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Card> deckOfCards = DeckOfCardsFactory.getCards(true);

                for (int index = 0; index < deckOfCards.size(); index++) {
                    final Card leftCard = deckOfCards.get(index);
                    index++;
                    final Card rightCard = deckOfCards.get(index);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PokerGameActivity.this, leftCard.getDrawable() + " - " + rightCard.getDrawable(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    playerCardPlayerLayout.update(leftCard);
                    playerCardPlayerLayout.update(rightCard);

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();


    }

}
