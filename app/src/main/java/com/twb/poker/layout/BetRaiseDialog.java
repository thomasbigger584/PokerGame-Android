package com.twb.poker.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.twb.poker.R;

public class BetRaiseDialog extends PokerDialog {

    public static BetRaiseDialog newInstance(PokerDialog.OnDialogClickListener listener) {
        BetRaiseDialog fragment = new BetRaiseDialog();
        fragment.listener = listener;
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_bet_raise_dialog, container, false);

        setFullScreen();

        return inflatedView;
    }

}
