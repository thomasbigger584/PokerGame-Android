package com.twb.poker.domain;

import java.util.List;

/**
 * An immutable class representing a card from a normal 52-card deck.
 */
public class Card {
    // Ranks
    public static final int DEUCE = 0;
    public static final int TREY = 1;
    public static final int FOUR = 2;
    public static final int FIVE = 3;
    public static final int SIX = 4;
    public static final int SEVEN = 5;
    public static final int EIGHT = 6;
    public static final int NINE = 7;
    public static final int TEN = 8;
    public static final int JACK = 9;
    public static final int QUEEN = 10;
    public static final int KING = 11;
    public static final int ACE = 12;
    // Suits
    public static final int CLUBS = 0x8000;
    public static final int DIAMONDS = 0x4000;
    public static final int HEARTS = 0x2000;
    public static final int SPADES = 0x1000;
    // Rank symbols
    private static final String RANKS = "23456789TJQKA";
    private static final String SUITS = "shdc";
    private final int value;  // Format: xxxAKQJT 98765432 CDHSrrrr xxPPPPPP
    private final int rankValue;

    /**
     * Creates a new card with the given rank and suit.
     *
     * @param rank the rank of the card, e.g. {@link Card#SIX}
     * @param suit the suit of the card, e.g. {@link Card#CLUBS}
     */
    public Card(int rank, int suit, int rankValue) {
        if (!isValidRank(rank)) {
            throw new IllegalArgumentException("Invalid rank.");
        }

        if (!isValidSuit(suit)) {
            throw new IllegalArgumentException("Invalid suit.");
        }

        this.value = (1 << (rank + 16)) | suit | (rank << 8) | Tables.PRIMES[rank];

        if (rankValue < 0 || rankValue > 51) {
            throw new IllegalArgumentException("Invalid Rank Value");
        }
        this.rankValue = rankValue;
    }

    /**
     * Returns whether the given rank is valid or not.
     *
     * @param rank rank to check.
     * @return true if the rank is valid, false otherwise.
     */
    private static boolean isValidRank(int rank) {
        return rank >= DEUCE && rank <= ACE;
    }

    /**
     * Returns whether the given suit is valid or not.
     *
     * @param suit suit to check.
     * @return true if the suit is valid, false otherwise.
     */
    private static boolean isValidSuit(int suit) {
        return suit == CLUBS || suit == DIAMONDS || suit == HEARTS || suit == SPADES;
    }

    public static Card getCard(int suit, int rank) {
        List<Card> deckOfCards = DeckOfCardsFactory.getCards(false);
        for (Card card : deckOfCards) {
            if (card.getSuit() == suit && card.getRank() == rank) {
                return card;
            }
        }
        return null;
    }

    public String getDrawable() {
        char rank = RANKS.charAt(getRank());
        char suit = SUITS.charAt((int) (Math.log(getSuit()) / Math.log(2)) - 12);
        String drawableName = String.valueOf(suit) + rank;
        return drawableName.toLowerCase();
    }

    /**
     * Returns the rank of the card.
     *
     * @return rank of the card as an integer.
     * @see Card#ACE
     * @see Card#DEUCE
     * @see Card#TREY
     * @see Card#FOUR
     * @see Card#FIVE
     * @see Card#SIX
     * @see Card#SEVEN
     * @see Card#EIGHT
     * @see Card#NINE
     * @see Card#TEN
     * @see Card#JACK
     * @see Card#QUEEN
     * @see Card#KING
     */
    public int getRank() {
        return (value >> 8) & 0xF;
    }

    /**
     * Returns the suit of the card.
     *
     * @return Suit of the card as an integer.
     * @see Card#SPADES
     * @see Card#HEARTS
     * @see Card#DIAMONDS
     * @see Card#CLUBS
     */
    public int getSuit() {
        return value & 0xF000;
    }

    /**
     * Returns a string representation of the card.
     * For example, the king of spades is "Ks", and the jack of hearts is "Jh".
     *
     * @return a string representation of the card.
     */
    @Override
    public String toString() {
        char rank = RANKS.charAt(getRank());
        char suit = SUITS.charAt((int) (Math.log(getSuit()) / Math.log(2)) - 12);
        return "" + rank + suit;
    }

    /**
     * Returns the value of the card as an integer.
     * The value is represented as the bits <code>xxxAKQJT 98765432 CDHSrrrr xxPPPPPP</code>,
     * where <code>x</code> means unused, <code>AKQJT 98765432</code> are bits turned on/off
     * depending on the rank of the card, <code>CDHS</code> are the bits corresponding to the
     * suit, and <code>PPPPPP</code> is the prime number of the card.
     *
     * @return the value of the card.
     */
    public int getValue() {
        return value;
    }

    public int getRankValue() {
        return this.rankValue;
    }
}