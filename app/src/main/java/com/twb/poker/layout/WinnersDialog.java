package com.twb.poker.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.twb.poker.R;
import com.twb.poker.domain.Card;
import com.twb.poker.domain.Hand;
import com.twb.poker.domain.PlayerUser;
import com.twb.poker.domain.PokerPlayer;

import java.util.List;

public class WinnersDialog extends PokerDialog {
    private WinnersClickListener listener;
    private List<PokerPlayer> pokerPlayerWinnersList;

    public static WinnersDialog newInstance(List<PokerPlayer> pokerPlayerWinnerList, WinnersClickListener listener) {
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
        inflatedView = inflater.inflate(R.layout.fragment_winner_dialog, container, false);

        setFullScreen();

        TextView titleTextView = inflatedView.findViewById(R.id.titleTextView);
        titleTextView.setText(toastWinners());

        CardLayout[] cardLayouts = new CardLayout[7];
        cardLayouts[0] = inflatedView.findViewById(R.id.card1CardLayout);
        cardLayouts[1] = inflatedView.findViewById(R.id.card2CardLayout);
        cardLayouts[2] = inflatedView.findViewById(R.id.card3CardLayout);
        cardLayouts[3] = inflatedView.findViewById(R.id.card4CardLayout);
        cardLayouts[4] = inflatedView.findViewById(R.id.card5CardLayout);
        cardLayouts[5] = inflatedView.findViewById(R.id.card6CardLayout);
        cardLayouts[6] = inflatedView.findViewById(R.id.card7CardLayout);

        PokerPlayer winningPokerPlayer = pokerPlayerWinnersList.get(0);
        Hand winningHand = winningPokerPlayer.getHand();
        for (int index = 0; index < cardLayouts.length; index++) {
            CardLayout cardLayout = cardLayouts[index];
            Card card = winningHand.get(index);
            cardLayout.update(card);
        }
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

    public interface WinnersClickListener {
        void onSuccessClick();
    }
}
