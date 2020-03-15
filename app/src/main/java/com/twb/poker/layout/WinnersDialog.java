package com.twb.poker.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.twb.poker.R;
import com.twb.poker.domain.PlayerUser;
import com.twb.poker.domain.PokerPlayer;

import java.util.List;

public class WinnersDialog extends DialogFragment {
    private static final float DIM_AMOUNT = 0.5f;

    private OnWinnerModalClickListener listener;
    private List<PokerPlayer> pokerPlayerWinnersList;

    public static WinnersDialog newInstance(List<PokerPlayer> pokerPlayerWinnerList, OnWinnerModalClickListener listener) {
        WinnersDialog fragment = new WinnersDialog();
        fragment.listener = listener;
        fragment.pokerPlayerWinnersList = pokerPlayerWinnerList;
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_winner_dialog, container, false);
        inflatedView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        TextView titleTextView = inflatedView.findViewById(R.id.titleTextView);
        titleTextView.setText(toastWinners());


        Button successButton = inflatedView.findViewById(R.id.successButton);
        successButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuccessClick();
            }
            dismissAllowingStateLoss();
        });
        return inflatedView;
    }

    private String toastWinners() {
        if (pokerPlayerWinnersList.size() == 1) {
            PokerPlayer winningPokerPlayer = pokerPlayerWinnersList.get(0);
            PlayerUser playerUser = winningPokerPlayer.getPlayerUser();
            return "Winner is " + playerUser.getDisplayName() +
                    " with : " + winningPokerPlayer.getHand();
        } else {
            StringBuilder winnersString = new StringBuilder();
            for (int index = 0; index < pokerPlayerWinnersList.size(); index++) {
                PokerPlayer winningPokerPlayer = pokerPlayerWinnersList.get(index);
                String displayName = winningPokerPlayer.getPlayerUser().getDisplayName();
                winnersString.append(displayName);
                if (index != pokerPlayerWinnersList.size() - 1) {
                    winnersString.append(", ");
                }
            }
            return "Split pot: " + winnersString.toString();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = DIM_AMOUNT;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(windowParams);
        }
    }

    public void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    public interface OnWinnerModalClickListener {
        void onSuccessClick();
    }
}
