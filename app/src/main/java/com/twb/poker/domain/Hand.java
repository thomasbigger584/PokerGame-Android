package com.twb.poker.domain;

import android.annotation.SuppressLint;

import com.twb.poker.eval.SevenCardHandEvaluator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

// first 2 cards are player cards
@Getter
@SuppressLint("UseSparseArrays")
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

    public HandType getType() {
        if (isRoyalFlush()) {
            return HandType.ROYAL_FLUSH;
        } else if (isStraightFlush()) {
            return HandType.STRAIGHT_FLUSH;
        } else if (isFourOfAKind()) {
            return HandType.FOUR_OF_A_KIND;
        } else if (isFullHouse()) {
            return HandType.FULL_HOUSE;
        } else if (isFlush()) {
            return HandType.FLUSH;
        } else if (isStraight()) {
            return HandType.STRAIGHT;
        } else if (isThreeOfAKind()) {
            return HandType.THREE_OF_A_KIND;
        } else if (isTwoPair()) {
            return HandType.TWO_PAIR;
        } else if (isPair()) {
            return HandType.PAIR;
        }
        return HandType.HIGH_CARD;
    }

    private boolean isRoyalFlush() {
        if (checkCardNullability()) return false;
        if (checkHandSize(5)) return false;


        return false;
    }

    //todo ace is -1 (before 0) or 12
    private boolean isStraightFlush() {
        if (checkCardNullability()) return false;
        if (checkHandSize(5)) return false;

        return false;
    }

    private boolean isFourOfAKind() {
        return isOfAKind(4);
    }

    private boolean isFullHouse() {
        if (checkCardNullability()) return false;
        if (checkHandSize(5)) return false;

        Map<Integer, Integer> rankToCountMap = getRankCount();

        int twosCount = 0;
        int threesCount = 0;
        for (Map.Entry<Integer, Integer> entry : rankToCountMap.entrySet()) {
            Integer suitCount = entry.getValue();
            if (suitCount == 2) {
                twosCount++;
            } else if (suitCount == 3) {
                threesCount++;
            }
        }
        return (twosCount >= 1 && threesCount >= 1);
    }

    private boolean isFlush() {
        if (checkCardNullability()) return false;
        if (checkHandSize(5)) return false;

        List<Card> copyHand = new ArrayList<>(this);
        Collections.sort(copyHand, (o1, o2) ->
                Integer.compare(o2.getSuit(), o1.getSuit()));
        int count = 0;
        Integer current = null;
        for (Card card : copyHand) {
            int suit = card.getSuit();
            if (current != null && current == suit) {
                current = suit;
                count++;
                if (count == 5) {
                    return true;
                }
            } else {
                current = suit;
                count = 1;
            }
        }
        return false;
    }

    //todo ace is -1 (before 0) or 12
    private boolean isStraight() {
        if (checkCardNullability()) return false;
        if (checkHandSize(5)) return false;

        List<Card> copyHand = new ArrayList<>(this);
        Collections.sort(copyHand, (o1, o2) ->
                Integer.compare(o1.getRank(), o2.getRank()));
        int count = 0;
        Integer current = null;
        for (Card card : copyHand) {
            int rank = card.getRank();
            if (current != null && current + 1 == rank) {
                current = rank;
                count++;
                if (count == 5) {
                    return true;
                }
            } else {
                current = rank;
                count = 1;
            }
        }
        return false;
    }

    private boolean isThreeOfAKind() {
        return isOfAKind(3);
    }

    private boolean isTwoPair() {
        if (checkCardNullability()) return false;
        if (checkHandSize(4)) return false;

        Map<Integer, Integer> rankToCountMap = getRankCount();

        int count = 0;
        for (Map.Entry<Integer, Integer> entry : rankToCountMap.entrySet()) {
            Integer suitCount = entry.getValue();
            if (suitCount == 2) {
                count++;
                if (count == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    private Map<Integer, Integer> getRankCount() {
        Map<Integer, Integer> rankToCountMap = new HashMap<>();
        for (Card card : Hand.this) {
            int rank = card.getRank();
            Integer count = rankToCountMap.get(rank);
            if (count == null) {
                rankToCountMap.put(rank, 1);
            } else {
                rankToCountMap.put(rank, ++count);
            }
        }
        return rankToCountMap;
    }

    private boolean isPair() {
        return isOfAKind(2);
    }

    private boolean isOfAKind(int kindness) {
        if (checkCardNullability()) return false;
        if (checkHandSize(kindness)) return false;

        Map<Integer, Integer> rankToCountMap = new HashMap<>();
        for (Card card : this) {
            int rank = card.getRank();
            Integer count = rankToCountMap.get(rank);
            if (count == null) {
                rankToCountMap.put(rank, 1);
            } else {
                count++;
                if (count == kindness) {
                    return true;
                }
                rankToCountMap.put(rank, count);
            }
        }
        return false;
    }

    private boolean checkHandSize(int expectedSize) {
        return size() < expectedSize;
    }

    private boolean checkCardNullability() {
        for (Card card : this) {
            if (card == null) return true;
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull Hand otherHand) {
        if (getRank() == null || otherHand.getRank() == null) {
            return 0;
        }
        return getRank().compareTo(otherHand.getRank());
    }
}
