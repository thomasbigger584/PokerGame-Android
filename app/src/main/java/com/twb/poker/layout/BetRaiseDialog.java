package com.twb.poker.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.twb.poker.R;
import com.twb.poker.util.SeekBarChangeListenerBase;

import java.math.BigDecimal;
import java.util.Locale;

public class BetRaiseDialog extends PokerDialog {
    private static final float PLAYER_BANK_AMOUNT = 2871.23f;
    private DialogType type;
    private TextView titleTextView;
    private SeekBar betRaiseSeekBar;
    private float amountSelected;

    public static BetRaiseDialog newInstance(DialogType type, PokerDialog.OnDialogClickListener listener) {
        BetRaiseDialog fragment = new BetRaiseDialog();
        fragment.listener = listener;
        fragment.type = type;
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_bet_raise_dialog, container, false);
        setFullScreen();

        titleTextView = inflatedView.findViewById(R.id.titleTextView);

        betRaiseSeekBar = inflatedView.findViewById(R.id.betRaiseSeekBar);
        betRaiseSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListenerBase() {
            @Override
            public void onProgressChanged(int progress) {
                setTitleTextView(((float) progress) / 100f);
            }
        });

        setSeekBar(PLAYER_BANK_AMOUNT);

        return inflatedView;
    }

    private void setTitleTextView(float amount) {
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

    private void setSeekBar(float amount) {
        float seekbarAmount = amount * 100;
        betRaiseSeekBar.setMax((int) seekbarAmount);

        //todo: set this to the minimum bet. for now its 10 percent
        float defaultAmount = seekbarAmount / 10;
        betRaiseSeekBar.setProgress((int) defaultAmount);

        setTitleTextView(defaultAmount / 100);
    }

    private float round(float amount) {
        BigDecimal bd = new BigDecimal(Float.toString(amount));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public enum DialogType {
        BET, RAISE
    }
}
