package com.twb.poker.util;

import android.widget.SeekBar;

import org.apache.commons.lang3.NotImplementedException;

public abstract class SeekBarChangeListenerBase implements SeekBar.OnSeekBarChangeListener {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        onProgressChanged(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        throw new NotImplementedException("onStartTrackingTouch not implemented");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        throw new NotImplementedException("onStopTrackingTouch not implemented");
    }

    public abstract void onProgressChanged(int progress);
}
