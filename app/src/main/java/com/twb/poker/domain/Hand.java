package com.twb.poker.domain;

import android.annotation.SuppressLint;

import com.twb.poker.eval.SevenCardHandEvaluator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

// first 2 cards are player cards
@Getter
@SuppressLint("UseSparseArrays")
public class Hand extends ArrayList<Card> implements Comparable<Hand> {
    private static final List<Integer> PARTIAL_LOWER_STRAIGHT =
            Arrays.asList(Card.DEUCE, Card.TREY, Card.FOUR, Card.FIVE);
    private static final int SEVEN_CARDS_NEEDED = 7;
    private static final int FIVE_CARDS_NEEDED = 5;
    private static final int FOUR_CARDS_NEEDED = 4;
    private static final int THREE_CARDS_NEEDED = 3;
    private static final int TWO_CARDS_NEEDED = 2;
    private Integer rank;

    public Hand() {
        for (int index = 0; index < TWO_CARDS_NEEDED; index++) {
            add(null);
        }
    }

    public void setCommunityCards(List<Card> playableCards) {
        if (size() != SEVEN_CARDS_NEEDED) {
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
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;


        return false;
    }

    private boolean isStraightFlush() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;

//        List<Card> copyHand = new ArrayList<>(this);
//        Collections.sort(copyHand, (o1, o2) ->
//                Integer.compare(o1.getRank(), o2.getRank()));
//
//        //pair rank to suit
//        List<Card> currentStraights = null;
//        for (Card card : copyHand) {
//            final int rank = card.getRank();
//            final int suit = card.getSuit();
//
//            int straightSize = (currentStraights != null) ? currentStraights.size() : 0;
//            if (currentStraights != null && currentStraights.get(straightSize - 1).getRank() + 1 == rank) {
//                currentStraights.add(card);
//                straightSize = currentStraights.size();
//                if (straightSize == 5) {
//                    return true;
//                }
//            } else {
//                currentStraights = new ArrayList<>();
//                currentStraights.add(card);
//            }
//        }

        return false;
    }

    private boolean isFourOfAKind() {
        return isOfAKind(FOUR_CARDS_NEEDED);
    }

    private boolean isFullHouse() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;

        Map<Integer, Integer> rankToCountMap = getRankCount();

        int twosCount = 0;
        int threesCount = 0;
        for (Map.Entry<Integer, Integer> entry : rankToCountMap.entrySet()) {
            Integer suitCount = entry.getValue();
            if (suitCount == TWO_CARDS_NEEDED) {
                twosCount++;
            } else if (suitCount == THREE_CARDS_NEEDED) {
                threesCount++;
            }
        }
        return (twosCount >= 1 && threesCount >= 1);
    }

    private boolean isFlush() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;

        Map<Integer, Integer> suitToCountMap = getSuitCount();
        for (Map.Entry<Integer, Integer> suitToCount : suitToCountMap.entrySet()) {
            if (suitToCount.getValue() == FIVE_CARDS_NEEDED) {
                return true;
            }
        }
        return false;
    }

    private boolean isStraight() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FIVE_CARDS_NEEDED)) return false;

        List<Card> copyHand = new ArrayList<>(this);
        Collections.sort(copyHand, (o1, o2) ->
                Integer.compare(o1.getRank(), o2.getRank()));

        boolean reachedPartLowStraight = false;
        List<Integer> currentStraights = null;
        for (Card card : copyHand) {
            final int rank = card.getRank();
            int straightSize = (currentStraights != null) ? currentStraights.size() : 0;
            if (!reachedPartLowStraight) {
                reachedPartLowStraight = (currentStraights != null) &&
                        currentStraights.containsAll(PARTIAL_LOWER_STRAIGHT);
            } else if (rank == Card.ACE) {
                return true;
            }
            if (currentStraights != null && currentStraights.get(straightSize - 1) + 1 == rank) {
                currentStraights.add(rank);
                straightSize = currentStraights.size();
                if (straightSize == 5) {
                    return true;
                }
            } else {
                currentStraights = new ArrayList<>();
                currentStraights.add(rank);
            }
        }
        return false;
    }

    private boolean isThreeOfAKind() {
        return isOfAKind(THREE_CARDS_NEEDED);
    }

    private boolean isTwoPair() {
        if (checkCardNullability()) return false;
        if (checkHandSize(FOUR_CARDS_NEEDED)) return false;

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
    private Map<Integer, Integer> getSuitCount() {
        Map<Integer, Integer> suitToCountMap = new HashMap<>();
        for (Card card : this) {
            int suit = card.getSuit();
            Integer count = suitToCountMap.get(suit);
            if (count == null) {
                suitToCountMap.put(suit, 1);
            } else {
                suitToCountMap.put(suit, ++count);
            }
        }
        return suitToCountMap;
    }

    @NotNull
    private Map<Integer, Integer> getRankCount() {
        Map<Integer, Integer> rankToCountMap = new HashMap<>();
        for (Card card : this) {
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
        return isOfAKind(TWO_CARDS_NEEDED);
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
