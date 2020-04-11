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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.poker.chatbox.ChatBoxRecyclerAdapter;
import com.twb.poker.domain.BetType;
import com.twb.poker.domain.Card;
import com.twb.poker.domain.CommunityCardType;
import com.twb.poker.domain.PlayerBank;
import com.twb.poker.domain.PokerPlayer;
import com.twb.poker.layout.BetRaiseDialog;
import com.twb.poker.layout.CardPairLayout;
import com.twb.poker.layout.CommunityCardLayout;
import com.twb.poker.layout.PokerDialog;
import com.twb.poker.layout.WinnersDialog;

import java.util.List;

public class PokerGameActivity extends AppCompatActivity
        implements PokerGameThread.PokerGameThreadCallback, BetRaiseDialog.BetRaiseClickListener {
    private static final int VIBRATE_LENGTH_IN_MS = 500;

    private PokerGameThread pokerGameThread;

    private LinearLayout pokerGameLinearLayout;
    private GridLayout controlsGridLayout;
    private RecyclerView chatBoxRecyclerView;
    private CommunityCardLayout communityCardLayout;
    private PokerDialog pokerDialog;
    private ProgressBar secondsLeftProgressBar;

    private CardPairLayout[] cardPairLayouts = new CardPairLayout[6];
    private ChatBoxRecyclerAdapter chatBoxAdapter;
    private Button checkButton;
    private Button foldButton;
    private Button betButton;
    private Button raiseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pokerGameLinearLayout = findViewById(R.id.pokerGameLinearLayout);
        cardPairLayouts[0] = pokerGameLinearLayout.findViewById(R.id.playerCardPairLayout);
        cardPairLayouts[1] = pokerGameLinearLayout.findViewById(R.id.tablePlayer1CardPairLayout);
        cardPairLayouts[2] = pokerGameLinearLayout.findViewById(R.id.tablePlayer2CardPairLayout);
        cardPairLayouts[3] = pokerGameLinearLayout.findViewById(R.id.tablePlayer3CardPairLayout);
        cardPairLayouts[4] = pokerGameLinearLayout.findViewById(R.id.tablePlayer4CardPairLayout);
        cardPairLayouts[5] = pokerGameLinearLayout.findViewById(R.id.tablePlayer5CardPairLayout);
        communityCardLayout = findViewById(R.id.communityCardLayout);
        controlsGridLayout = findViewById(R.id.controlsGridLayout);
        chatBoxRecyclerView = pokerGameLinearLayout.findViewById(R.id.chatBoxRecyclerView);
        setupChatBoxRecyclerView();

        checkButton = controlsGridLayout.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(v -> {
            pokerGameThread.checkCurrentPlayer();
        });
        foldButton = controlsGridLayout.findViewById(R.id.foldButton);
        foldButton.setOnClickListener(v -> {
            pokerGameThread.foldCurrentPlayer();
        });
        betButton = controlsGridLayout.findViewById(R.id.betButton);
        betButton.setOnClickListener(v -> showBetDialog());

        raiseButton = controlsGridLayout.findViewById(R.id.raiseButton);
        raiseButton.setOnClickListener(v -> {
            showRaiseDialog();
        });
        secondsLeftProgressBar = pokerGameLinearLayout.findViewById(R.id.secondsLeftProgressBar);
    }

    private void setupChatBoxRecyclerView() {
        chatBoxRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatBoxRecyclerView.setLayoutManager(layoutManager);

        chatBoxAdapter = new ChatBoxRecyclerAdapter();
        chatBoxRecyclerView.setAdapter(chatBoxAdapter);
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
    public void onUpdatePokerPlayer(PokerPlayer pokerPlayer) {
        int tableIndex = pokerPlayer.getTableIndex();
        CardPairLayout cardPairLayout = cardPairLayouts[tableIndex];
        cardPairLayout.updateDetails(pokerPlayer);
    }

    @Override
    public void onAlert() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.
                    createOneShot(VIBRATE_LENGTH_IN_MS, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(VIBRATE_LENGTH_IN_MS);
        }
    }

    @Override
    public void onPercentageTimeLeft(int percentage) {
        secondsLeftProgressBar.setProgress(percentage);
    }

    @Override
    public void onDealCommunityCard(Card card, CommunityCardType cardType) {
        communityCardLayout.dealCard(card, cardType);
    }

    @Override
    public void onDealCardToPlayer(PokerPlayer pokerPlayer, Card card) {
        int tableIndex = pokerPlayer.getTableIndex();
        CardPairLayout cardPairLayout = cardPairLayouts[tableIndex];
        cardPairLayout.updateCardImageView(card);
    }

    @Override
    public void onPlayerTurn(PokerPlayer pokerPlayer, boolean turn) {
        int tableIndex = pokerPlayer.getTableIndex();
        CardPairLayout cardPairLayout = cardPairLayouts[tableIndex];
        cardPairLayout.updateTurnPlayer(turn);
    }

    @Override
    public void onPlayerFold(PokerPlayer pokerPlayer) {
        int tableIndex = pokerPlayer.getTableIndex();
        CardPairLayout cardPairLayout = cardPairLayouts[tableIndex];
        cardPairLayout.fold();
    }

    @Override
    public void onPlayerDealer(PokerPlayer pokerPlayer, boolean dealer) {
        int tableIndex = pokerPlayer.getTableIndex();
        CardPairLayout cardPairLayout = cardPairLayouts[tableIndex];
        cardPairLayout.updateDealerChip(dealer);
    }

    @Override
    public void onControlsShow(List<BetType> betTypes) {
        checkButton.setVisibility(View.GONE);
        foldButton.setVisibility(View.GONE);
        betButton.setVisibility(View.GONE);
        raiseButton.setVisibility(View.GONE);
        for (BetType betType : betTypes) {
            switch (betType) {
                case CHECK: {
                    checkButton.setVisibility(View.VISIBLE);
                    break;
                }
                case FOLD: {
                    foldButton.setVisibility(View.VISIBLE);
                    break;
                }
                case BET: {
                    betButton.setVisibility(View.VISIBLE);
                    break;
                }
                case RAISE: {
                    raiseButton.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
        controlsGridLayout.setVisibility(View.VISIBLE);
        secondsLeftProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onControlsHide() {
        dismissPokerDialog();
        if (controlsGridLayout.getVisibility() != View.GONE) {
            controlsGridLayout.setVisibility(View.GONE);
        }
        if (secondsLeftProgressBar.getVisibility() != View.INVISIBLE) {
            secondsLeftProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onWinnerDialogShow(List<PokerPlayer> pokerPlayerWinners) {
        dismissPokerDialog();
        pokerDialog = WinnersDialog.newInstance(pokerPlayerWinners, () -> {
            pokerGameThread.setEvalWaitingOnUserInput();
        });
        pokerDialog.show(getSupportFragmentManager());
    }

    @Override
    public void onReset() {
        for (CardPairLayout cardPairLayout : cardPairLayouts) {
            cardPairLayout.reset();
        }
        communityCardLayout.reset();
    }

    @Override
    public void onEvent(String event) {
        chatBoxAdapter.add(event);
        int itemCount = chatBoxAdapter.getItemCount();
        chatBoxRecyclerView.scrollToPosition(itemCount - 1);
    }

    private void showBetDialog() {
        dismissPokerDialog();
        PlayerBank playerBank = pokerGameThread.getCurrentBank();
        if (playerBank != null) {
            double funds = playerBank.getFunds();
            pokerDialog = BetRaiseDialog.newInstance(BetType.BET, funds, this);
            pokerDialog.show(getSupportFragmentManager());
        }
    }

    private void showRaiseDialog() {
        dismissPokerDialog();
        PlayerBank playerBank = pokerGameThread.getCurrentBank();
        if (playerBank != null) {
            double funds = playerBank.getFunds();
            pokerDialog = BetRaiseDialog.newInstance(BetType.RAISE, funds, this);
            pokerDialog.show(getSupportFragmentManager());
        }
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

    @Override
    public void onAmountSelected(BetType type, double amount) {
        pokerGameThread.onAmountSelected(type, amount);
    }
}
