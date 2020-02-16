package com.twb.poker.domain;

import com.twb.poker.eval.SevenCardHandEvaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hand extends ArrayList<Card> {

    private int rank;

    public Hand(List<Card> playableCards, Card[] hand) {
        this.addAll(new ArrayList<>(playableCards));
        this.addAll(Arrays.asList(hand));
    }

    public int getRank() {
        return rank;
    }

    public void calculateRank() {
        this.rank = SevenCardHandEvaluator.getRank(this);
    }
}
