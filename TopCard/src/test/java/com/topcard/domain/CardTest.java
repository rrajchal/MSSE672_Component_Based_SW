package com.topcard.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;


public class CardTest {

    @Test
    public void testCardConstructor() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        assertEquals("ACE OF HEARTS", card.toString());
        assertEquals(Card.Suit.HEARTS, card.getSuit());
        assertEquals(Card.Rank.ACE, card.getRank());
    }

    @Test
    public void testSuit() {
        assertEquals("HEARTS", Card.Suit.HEARTS.name());
        assertEquals("DIAMONDS", Card.Suit.DIAMONDS.name());
        assertEquals("CLUBS", Card.Suit.CLUBS.name());
        assertEquals("SPADES", Card.Suit.SPADES.name());
    }

    @Test
    public void testRankValues() {
        assertEquals(1, Card.Rank.ACE.getValue());
        assertEquals(2, Card.Rank.TWO.getValue());
        assertEquals(3, Card.Rank.THREE.getValue());
        assertEquals(4, Card.Rank.FOUR.getValue());
        assertEquals(5, Card.Rank.FIVE.getValue());
        assertEquals(6, Card.Rank.SIX.getValue());
        assertEquals(7, Card.Rank.SEVEN.getValue());
        assertEquals(8, Card.Rank.EIGHT.getValue());
        assertEquals(9, Card.Rank.NINE.getValue());
        assertEquals(10, Card.Rank.TEN.getValue());
        assertEquals(10, Card.Rank.JACK.getValue());
        assertEquals(10, Card.Rank.QUEEN.getValue());
        assertEquals(10, Card.Rank.KING.getValue());
    }

    @Test
    public void testTotalPoints() {
        int total14Points = Card.Rank.ACE.getValue() + Card.Rank.THREE.getValue() + Card.Rank.KING.getValue();
        assertEquals(14, total14Points);

        int total21Points = Card.Rank.SEVEN.getValue() + Card.Rank.SEVEN.getValue() + Card.Rank.SEVEN.getValue();
        assertEquals(21, total21Points);

        int total30Points = Card.Rank.TEN.getValue() + Card.Rank.JACK.getValue() + Card.Rank.QUEEN.getValue();
        assertEquals(30, total30Points);
    }

    @Test
    public void totalValueOfCards() {
        Card card = new Card();
        Card card1 = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        Card card2 = new Card(Card.Suit.HEARTS, Card.Rank.TWO);
        Card card3 = new Card(Card.Suit.HEARTS, Card.Rank.THREE);
        Card[] cards = {card1, card2, card3};

        assertEquals(1, card.getCardValue(card1));
        assertEquals(2, card.getCardValue(card2));
        assertEquals(3, card.getCardValue(card3));
        assertEquals(6, card.getCardsValue(cards));

    }
}
