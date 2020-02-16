package com.twb.poker;

import com.twb.poker.domain.Card;

public class PokerHandEvaluator {
    static {
        System.loadLibrary("native-poker-eval-jni");
    }

    private PokerHandEvaluator() {
    }

    public static native int getRank(int i, int j, int k, int m, int n, int p, int q);

    public static int getRank(Card[] hand) {

        return 0;
    }
}
