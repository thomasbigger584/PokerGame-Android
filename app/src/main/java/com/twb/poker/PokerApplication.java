package com.twb.poker;

import android.app.Application;

import com.twb.poker.domain.Tables;

public class PokerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Tables.loadTables();
    }
}
