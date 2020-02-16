package com.twb.poker.eval;

import com.twb.poker.domain.Card;

public class SevenCardHandEvaluator {
    static {
        System.loadLibrary("native-poker-eval-jni");
    }

    private SevenCardHandEvaluator() {
    }

    public static native int getRank(int i, int j, int k, int m, int n, int p, int q);

    public static int getRank(Card[] hand) {

        return 0;
    }
}
