package com.twb.poker.util;

import android.widget.SeekBar;

public abstract class SeekBarChangeListenerBase implements SeekBar.OnSeekBarChangeListener {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        onProgressChanged(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //left blank intentionally
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //left blank intentionally
    }

    public abstract void onProgressChanged(int progress);
}
