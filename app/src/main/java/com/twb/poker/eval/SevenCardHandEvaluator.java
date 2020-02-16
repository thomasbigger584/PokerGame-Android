package com.twb.poker.eval;

import com.twb.poker.domain.Hand;

public class SevenCardHandEvaluator {
    static {
        System.loadLibrary("native-poker-eval-jni");
    }

    private SevenCardHandEvaluator() {
    }

    static native int getRank(int i, int j, int k, int m, int n, int p, int q);

    public static int getRank(Hand hand) {
        if (hand.size() != 7) {
            throw new IllegalArgumentException("Not enough cards in hand: " + hand.size());
        }
        return getRank(hand.get(0).getRankValue(),
                hand.get(1).getRankValue(),
                hand.get(2).getRankValue(),
                hand.get(3).getRankValue(),
                hand.get(4).getRankValue(),
                hand.get(5).getRankValue(),
                hand.get(6).getRankValue());
    }
}
