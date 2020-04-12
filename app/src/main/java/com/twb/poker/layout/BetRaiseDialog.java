package com.twb.poker.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.twb.poker.R;
import com.twb.poker.domain.Bet;
import com.twb.poker.domain.BetType;
import com.twb.poker.util.SeekBarChangeListenerBase;

import java.math.BigDecimal;
import java.util.Locale;

public class BetRaiseDialog extends PokerDialog {
    private BetRaiseClickListener betRaiseListener;
    private BetType type;
    private TextView titleTextView;
    private SeekBar betRaiseSeekBar;
    private double playerCurrentFunds;
    private double amountSelected;

    public static BetRaiseDialog newInstance(BetType type, double playerCurrentFunds, BetRaiseClickListener listener) {
        BetRaiseDialog fragment = new BetRaiseDialog();
        fragment.betRaiseListener = listener;
        fragment.type = type;
        fragment.playerCurrentFunds = playerCurrentFunds;
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_bet_raise_dialog, container, false);

        titleTextView = inflatedView.findViewById(R.id.titleTextView);
        betRaiseSeekBar = inflatedView.findViewById(R.id.betRaiseSeekBar);
        betRaiseSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(int progress) {
                setTitleTextView(((float) progress) / 100f);
            }
        });

        Button successButton = inflatedView.findViewById(R.id.successButton);
        successButton.setOnClickListener(v -> {
            if (betRaiseListener != null) {
                Bet bet = new Bet();
                bet.setBetType(type);
                bet.setBetAmount(amountSelected);
                betRaiseListener.onBetSelected(bet);
            }
        });

        setSeekBar(playerCurrentFunds);
        return inflatedView;
    }

    private void setTitleTextView(double amount) {
        amountSelected = round(amount);
        switch (type) {
            case BET: {
                titleTextView.setText(String.format(Locale.getDefault(),
                        "Bet: %.2f", amountSelected));
                break;
            }
            case RAISE: {
                titleTextView.setText(String.format(Locale.getDefault(),
                        "Raise: %.2f", amountSelected));
                break;
            }
        }
    }

    private void setSeekBar(double amount) {
        double seekbarAmount = amount * 100;
        betRaiseSeekBar.setMax((int) seekbarAmount);

        double defaultAmount = seekbarAmount / 10;
        betRaiseSeekBar.setProgress((int) defaultAmount);

        setTitleTextView(defaultAmount / 100);
    }

    private double round(double amount) {
        BigDecimal bd = new BigDecimal(Double.toString(amount));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public interface BetRaiseClickListener {
        void onBetSelected(Bet bet);
    }
}
