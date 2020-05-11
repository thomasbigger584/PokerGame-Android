package com.twb.poker.eval;

import com.twb.poker.domain.Card;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CalculatePartialHandRankTest {
    private static final int EXPECTED_CARD_HAND_SIZE = 7;

    @Test
    public void calculate() {
        CalculatePartialHandRank calculatePartialHandRank = new CalculatePartialHandRank();

        List<Card> partialCards = createPartialCards();
        double calc = calculatePartialHandRank.calculate(partialCards, EXPECTED_CARD_HAND_SIZE);

        System.out.println("Calc: " + calc);
    }

    private List<Card> createPartialCards() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.getCard(Card.SPADES, Card.ACE));
        cardList.add(Card.getCard(Card.DIAMONDS, Card.ACE));
        cardList.add(Card.getCard(Card.CLUBS, Card.SEVEN));
        return cardList;
    }
}