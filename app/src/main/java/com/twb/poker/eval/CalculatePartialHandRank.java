package com.twb.poker.eval;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.DeckOfCardsFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CalculatePartialHandRank {
    public double calculate(List<Card> partialCards, int expectedCardHandSize) {
        List<Card> deckOfCards = DeckOfCardsFactory.getCards(false);
        List<Card> deckOfCardsRemaining = new ArrayList<>(deckOfCards);
        final Iterator<Card> each = deckOfCardsRemaining.iterator();
        while (each.hasNext()) {
            if (partialCards.contains(each.next())) {
                each.remove();
            }
        }

        int cardsToPick = expectedCardHandSize - partialCards.size();

        int averageRanks = 0;
        switch (cardsToPick) {
            case 5: {
                averageRanks = getFiveCardsPermutationRank(deckOfCardsRemaining, partialCards);
                break;
            }
            case 4: {
                averageRanks = getFourCardsPermutationRank(deckOfCardsRemaining, partialCards);
                break;
            }
            case 3: {
                averageRanks = getThreeCardsPermutationRank(deckOfCardsRemaining, partialCards);
                break;
            }
            case 2: {
                averageRanks = getTwoCardsPermutationRank(deckOfCardsRemaining, partialCards);
                break;
            }
            case 1: {
                averageRanks = getOneCardsPermutationRank(deckOfCardsRemaining, partialCards);
                break;
            }
        }
        return averageRanks;
    }

    private int getFiveCardsPermutationRank(List<Card> deckOfCardsRemaining, List<Card> partialCards) {
        Card card1 = partialCards.get(0);
        Card card2 = partialCards.get(1);

        int index = 0;
        int runningAverageRank = 0;
        int sum = 0;
        for (int i = 49; i >= 4; i--) {
            for (int j = i - 1; j >= 3; j--) {
                for (int k = j - 1; k >= 2; k--) {
                    for (int m = k - 1; m >= 1; m--) {
                        for (int n = m - 1; n >= 0; n--) {
                            Card[] cards = new Card[7];
                            cards[0] = deckOfCardsRemaining.get(i);
                            cards[1] = deckOfCardsRemaining.get(j);
                            cards[2] = deckOfCardsRemaining.get(k);
                            cards[3] = deckOfCardsRemaining.get(m);
                            cards[4] = deckOfCardsRemaining.get(n);
                            cards[5] = card1;
                            cards[6] = card2;
                            int rank = SevenCardHandEvaluator.getRank(cards);
                            sum += rank;
                            if (runningAverageRank == 0) {
                                runningAverageRank = rank;
                            } else {
                                runningAverageRank = sum / index;
                            }
                            index++;
                        }
                    }
                }
            }
        }
        return runningAverageRank;
    }


    private int getFourCardsPermutationRank(List<Card> deckOfCardsRemaining, List<Card> partialCards) {
        Card card1 = partialCards.get(0);
        Card card2 = partialCards.get(1);
        Card card3 = partialCards.get(2);

        int index = 0;
        int runningAverageRank = 0;
        int sum = 0;
        for (int i = 48; i >= 3; i--) {
            for (int j = i - 1; j >= 2; j--) {
                for (int k = j - 1; k >= 1; k--) {
                    for (int m = k - 1; m >= 0; m--) {
                        Card[] cards = new Card[7];
                        cards[0] = deckOfCardsRemaining.get(i);
                        cards[1] = deckOfCardsRemaining.get(j);
                        cards[2] = deckOfCardsRemaining.get(k);
                        cards[3] = deckOfCardsRemaining.get(m);
                        cards[4] = card1;
                        cards[5] = card2;
                        cards[6] = card3;
                        int rank = SevenCardHandEvaluator.getRank(cards);
                        sum += rank;
                        if (runningAverageRank == 0) {
                            runningAverageRank = rank;
                        } else {
                            runningAverageRank = sum / index;
                        }
                        index++;
                    }
                }
            }
        }
        return runningAverageRank;
    }

    private int getThreeCardsPermutationRank(List<Card> deckOfCardsRemaining, List<Card> partialCards) {
        Card card1 = partialCards.get(0);
        Card card2 = partialCards.get(1);
        Card card3 = partialCards.get(2);
        Card card4 = partialCards.get(3);

        int index = 0;
        int runningAverageRank = 0;
        int sum = 0;
        for (int i = 47; i >= 2; i--) {
            for (int j = i - 1; j >= 1; j--) {
                for (int k = j - 1; k >= 0; k--) {
                    Card[] cards = new Card[7];
                    cards[0] = deckOfCardsRemaining.get(i);
                    cards[1] = deckOfCardsRemaining.get(j);
                    cards[2] = deckOfCardsRemaining.get(k);
                    cards[3] = card1;
                    cards[4] = card2;
                    cards[5] = card3;
                    cards[6] = card4;
                    int rank = SevenCardHandEvaluator.getRank(cards);
                    sum += rank;
                    if (runningAverageRank == 0) {
                        runningAverageRank = rank;
                    } else {
                        runningAverageRank = sum / index;
                    }
                    index++;
                }
            }
        }
        return runningAverageRank;
    }

    private int getTwoCardsPermutationRank(List<Card> deckOfCardsRemaining, List<Card> partialCards) {
        Card card1 = partialCards.get(0);
        Card card2 = partialCards.get(1);
        Card card3 = partialCards.get(2);
        Card card4 = partialCards.get(3);
        Card card5 = partialCards.get(4);

        int index = 0;
        int runningAverageRank = 0;
        int sum = 0;
        for (int i = 46; i >= 1; i--) {
            for (int j = i - 1; j >= 0; j--) {
                Card[] cards = new Card[7];
                cards[0] = deckOfCardsRemaining.get(i);
                cards[1] = deckOfCardsRemaining.get(j);
                cards[2] = card1;
                cards[3] = card2;
                cards[4] = card3;
                cards[5] = card4;
                cards[6] = card5;
                int rank = SevenCardHandEvaluator.getRank(cards);
                sum += rank;
                if (runningAverageRank == 0) {
                    runningAverageRank = rank;
                } else {
                    runningAverageRank = sum / index;
                }
                index++;
            }
        }
        return runningAverageRank;
    }

    private int getOneCardsPermutationRank(List<Card> deckOfCardsRemaining, List<Card> partialCards) {
        Card card1 = partialCards.get(0);
        Card card2 = partialCards.get(1);
        Card card3 = partialCards.get(2);
        Card card4 = partialCards.get(3);
        Card card5 = partialCards.get(4);
        Card card6 = partialCards.get(5);

        int index = 0;
        int runningAverageRank = 0;
        int sum = 0;
        for (int i = 45; i >= 0; i--) {
            Card[] cards = new Card[7];
            cards[0] = deckOfCardsRemaining.get(i);
            cards[1] = card1;
            cards[2] = card2;
            cards[3] = card3;
            cards[4] = card4;
            cards[5] = card5;
            cards[6] = card6;
            int rank = SevenCardHandEvaluator.getRank(cards);
            sum += rank;
            if (runningAverageRank == 0) {
                runningAverageRank = rank;
            } else {
                runningAverageRank = sum / index;
            }
            index++;
        }
        return runningAverageRank;
    }
}
