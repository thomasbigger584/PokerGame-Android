package com.twb.poker.layout;

import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class PokerDialog extends DialogFragment {
    private static final float DIM_AMOUNT = 0.5f;

    PokerDialog.OnDialogClickListener listener;

    View inflatedView;

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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onSuccessClick();
        }
    }

    void successClick() {
        if (listener != null) {
            listener.onSuccessClick();
        }
        dismissAllowingStateLoss();
    }

    void setFullScreen() {
        inflatedView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    public interface OnDialogClickListener {
        void onSuccessClick();
    }
}
