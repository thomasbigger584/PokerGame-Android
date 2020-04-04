package com.twb.poker.domain;

import org.junit.Test;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static com.twb.poker.domain.HandType.FLUSH;
import static com.twb.poker.domain.HandType.FOUR_OF_A_KIND;
import static com.twb.poker.domain.HandType.FULL_HOUSE;
import static com.twb.poker.domain.HandType.HIGH_CARD;
import static com.twb.poker.domain.HandType.PAIR;
import static com.twb.poker.domain.HandType.ROYAL_FLUSH;
import static com.twb.poker.domain.HandType.STRAIGHT;
import static com.twb.poker.domain.HandType.STRAIGHT_FLUSH;
import static com.twb.poker.domain.HandType.THREE_OF_A_KIND;
import static com.twb.poker.domain.HandType.TWO_PAIR;
import static org.junit.Assert.assertEquals;

public class HandTest {
    @Test
    public void testRoyalFlush() {
        Hand hand = createRoyalFlush();
        HandType handType = hand.getType();
        assertEquals(ROYAL_FLUSH, handType);
    }

    @Test
    public void testStraightFlushMiddle() {
        Hand hand = createStraightFlushMiddle();
        HandType handType = hand.getType();
        assertEquals(STRAIGHT_FLUSH, handType);
    }

    @Test
    public void testStraightFlushAceLower() {
        Hand hand = createStraightFlushMiddle();
        HandType handType = hand.getType();
        assertEquals(STRAIGHT_FLUSH, handType);
    }

    @Test
    public void testStraightFlushAceHigher() {
        Hand hand = createStraightFlushMiddle();
        HandType handType = hand.getType();
        assertEquals(STRAIGHT_FLUSH, handType);
    }

    @Test
    public void testFourOfAKind() {
        Hand hand = createFourOfAKind();
        HandType handType = hand.getType();
        assertEquals(FOUR_OF_A_KIND, handType);
    }

    @Test
    public void testFullHouse() {
        Hand hand = createFullHouse();
        HandType handType = hand.getType();
        assertEquals(FULL_HOUSE, handType);
    }

    @Test
    public void testFlush() {
        Hand hand = createFlush();
        HandType handType = hand.getType();
        assertEquals(FLUSH, handType);
    }

    @Test
    public void testStraightMiddle() {
        Hand hand = createStraightMiddle();
        HandType handType = hand.getType();
        assertEquals(STRAIGHT, handType);
    }


    @Test
    //found through testing
    public void testStraight() {
        Hand hand = createStraight();
        HandType handType = hand.getType();
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testStraightWithDuplicates() {
        Hand hand = createStraightDuplicate();
        HandType handType = hand.getType();
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testStraightAceLower() {
        Hand hand = createStraightAceLower();
        HandType handType = hand.getType();
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testStraightAceHigher() {
        Hand hand = createStraightAceHigher();
        HandType handType = hand.getType();
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testThreeOfAKind() {
        Hand hand = createThreeOfAKind();
        HandType handType = hand.getType();
        assertEquals(THREE_OF_A_KIND, handType);
    }

    @Test
    public void testTwoPair() {
        Hand hand = createTwoPair();
        HandType handType = hand.getType();
        assertEquals(TWO_PAIR, handType);
    }

    @Test
    public void testPair() {
        Hand hand = createPair();
        HandType handType = hand.getType();
        assertEquals(PAIR, handType);
    }

    @Test
    public void testHighCard() {
        Hand hand = createHighCard();
        HandType handType = hand.getType();
        assertEquals(HIGH_CARD, handType);
    }

    private Hand createRoyalFlush() {
        List<Card> cardList = new ArrayList<>();
        int suit = Card.HEARTS;
        cardList.add(new Card(Card.TEN, suit, 0));
        cardList.add(new Card(Card.JACK, suit, 0));
        cardList.add(new Card(Card.QUEEN, suit, 0));
        cardList.add(new Card(Card.KING, suit, 0));
        cardList.add(new Card(Card.ACE, suit, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.TEN, Card.DIAMONDS, 0));
        return createHand(cardList);
    }


    private Hand createStraightFlushMiddle() {
        List<Card> cardList = new ArrayList<>();
        int suit = Card.CLUBS;
        cardList.add(new Card(Card.SEVEN, suit, 0));
        cardList.add(new Card(Card.EIGHT, suit, 0));
        cardList.add(new Card(Card.NINE, suit, 0));
        cardList.add(new Card(Card.TEN, suit, 0));
        cardList.add(new Card(Card.JACK, suit, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.TEN, Card.DIAMONDS, 0));
        return createHand(cardList);
    }


    private Hand createFourOfAKind() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.ACE, Card.CLUBS, 0));
        cardList.add(new Card(Card.FOUR, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.QUEEN, Card.HEARTS, 0));
        cardList.add(new Card(Card.ACE, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.HEARTS, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createFullHouse() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.TEN, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.QUEEN, Card.HEARTS, 0));
        cardList.add(new Card(Card.ACE, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.HEARTS, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createFlush() {
        List<Card> cardList = new ArrayList<>();
        int suit = Card.CLUBS;
        cardList.add(new Card(Card.TEN, suit, 0));
        cardList.add(new Card(Card.FOUR, suit, 0));
        cardList.add(new Card(Card.QUEEN, suit, 0));
        cardList.add(new Card(Card.SEVEN, suit, 0));
        cardList.add(new Card(Card.ACE, suit, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.TEN, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createStraightMiddle() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.FIVE, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.SIX, Card.HEARTS, 0));
        cardList.add(new Card(Card.SEVEN, Card.SPADES, 0));
        cardList.add(new Card(Card.EIGHT, Card.HEARTS, 0));
        cardList.add(new Card(Card.NINE, Card.SPADES, 0));
        cardList.add(new Card(Card.JACK, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createStraight() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TREY, Card.HEARTS, 0));
        cardList.add(new Card(Card.FIVE, Card.CLUBS, 0));
        cardList.add(new Card(Card.SIX, Card.HEARTS, 0));
        cardList.add(new Card(Card.SEVEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.SEVEN, Card.SPADES, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.NINE, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createStraightDuplicate() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.FIVE, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.SIX, Card.HEARTS, 0));
        cardList.add(new Card(Card.SEVEN, Card.HEARTS, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.SIX, Card.SPADES, 0));
        cardList.add(new Card(Card.NINE, Card.DIAMONDS, 0));
        return createHand(cardList);
    }


    private Hand createStraightAceLower() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.ACE, Card.CLUBS, 0));
        cardList.add(new Card(Card.DEUCE, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.TREY, Card.HEARTS, 0));
        cardList.add(new Card(Card.FOUR, Card.SPADES, 0));
        cardList.add(new Card(Card.FIVE, Card.HEARTS, 0));
        cardList.add(new Card(Card.NINE, Card.SPADES, 0));
        cardList.add(new Card(Card.JACK, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createStraightAceHigher() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.JACK, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.QUEEN, Card.HEARTS, 0));
        cardList.add(new Card(Card.KING, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.HEARTS, 0));
        cardList.add(new Card(Card.SEVEN, Card.SPADES, 0));
        cardList.add(new Card(Card.FIVE, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createThreeOfAKind() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.FOUR, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.QUEEN, Card.HEARTS, 0));
        cardList.add(new Card(Card.ACE, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.HEARTS, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createTwoPair() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.FOUR, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.QUEEN, Card.HEARTS, 0));
        cardList.add(new Card(Card.FOUR, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.HEARTS, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createPair() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.FOUR, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.QUEEN, Card.HEARTS, 0));
        cardList.add(new Card(Card.SEVEN, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.HEARTS, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createHighCard() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(Card.TEN, Card.CLUBS, 0));
        cardList.add(new Card(Card.FOUR, Card.DIAMONDS, 0));
        cardList.add(new Card(Card.QUEEN, Card.HEARTS, 0));
        cardList.add(new Card(Card.SEVEN, Card.SPADES, 0));
        cardList.add(new Card(Card.ACE, Card.HEARTS, 0));
        cardList.add(new Card(Card.EIGHT, Card.SPADES, 0));
        cardList.add(new Card(Card.JACK, Card.DIAMONDS, 0));
        return createHand(cardList);
    }

    private Hand createHand(List<Card> cardHandList) {
        if (cardHandList.size() != 7) {
            throw new InvalidParameterException();
        }
        Hand hand = new Hand();
        for (int index = 0; index < 2; index++) {
            hand.update(cardHandList.get(index));
        }
        List<Card> playableCards = cardHandList.subList(2, cardHandList.size());
        hand.setCommunityCards(playableCards);
        return hand;
    }
}