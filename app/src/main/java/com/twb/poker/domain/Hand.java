package com.twb.poker.domain;

import com.twb.poker.eval.SevenCardHandEvaluator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

// first 2 cards are player cards
@Getter
public class Hand extends ArrayList<Card> implements Comparable<Hand> {

    private Integer rank;

    public Hand() {
        for (int index = 0; index < 2; index++) {
            add(null);
        }
    }

    public void setCommunityCards(List<Card> playableCards) {
        if (size() != 7) {
            addAll(new ArrayList<>(playableCards));
        }
    }

    public void calculateRank() {
        this.rank = SevenCardHandEvaluator.getRank(this);
    }

    public void update(Card card) {
        if (get(0) != null && get(1) != null) {
            set(0, null);
            set(1, null);
        }
        if (get(0) == null) {
            set(0, card);
        } else if (get(1) == null) {
            set(1, card);
        }
    }

    @Override
    public int compareTo(@NotNull Hand otherHand) {
        if (getRank() == null || otherHand.getRank() == null) {
            return 0;
        }
        return getRank().compareTo(otherHand.getRank());
    }
}
