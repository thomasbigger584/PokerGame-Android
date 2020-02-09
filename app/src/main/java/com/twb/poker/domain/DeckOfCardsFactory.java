package com.twb.poker.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckOfCardsFactory {

    private static final int[] RANKS = new int[]{Card.DEUCE, Card.TREY, Card.FOUR, Card.FIVE, Card.SIX, Card.SEVEN, Card.EIGHT, Card.NINE, Card.TEN, Card.JACK, Card.QUEEN, Card.KING, Card.ACE};
    private static final int[] SUITS = new int[]{Card.SPADES, Card.CLUBS, Card.HEARTS, Card.DIAMONDS};

    private static final List<Card> CARDS = new ArrayList<>();

    static {
        for (int rank : RANKS) {
            for (int suit : SUITS) {
                CARDS.add(new Card(rank, suit));
            }
        }
    }

    //this will return a new instance of cards
    public static List<Card> getCards(boolean shuffle) {
        List<Card> deckOfCardsCopy = new ArrayList<>(CARDS);
        if (shuffle) {
            Collections.shuffle(deckOfCardsCopy);
        }
        return deckOfCardsCopy;
    }
}
