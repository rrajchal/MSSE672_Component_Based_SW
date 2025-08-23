package com.topcard.business;

import com.topcard.config.SpringAppConfigForTest;
import com.topcard.domain.Card;
import com.topcard.domain.Deck;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringAppConfigForTest.class)
public class CardManagerTest {

    @Autowired
    private CardManager cardManager;

    @Before
    public void setUp() {
        // Reset the deck before each test
        cardManager.createDeck();
    }

    @Test
    public void testCreateDeck() {
        Deck deck = cardManager.createDeck();
        assertNotNull(deck);
        assertEquals(52, cardManager.getRemainingCards());
    }

    @Test
    public void testCreateShuffledDeck() {
        Deck deck = cardManager.createShuffledDeck();
        assertNotNull(deck);
        assertEquals(52, cardManager.getRemainingCards());
    }

    @Test
    public void testShuffleDeck() {
        cardManager.shuffleDeck();
        // Deck should still have 52 cards after shuffling
        assertEquals(52, cardManager.getRemainingCards());
    }

    @Test
    public void testDrawCard() {
        Card card = cardManager.drawCard();
        assertNotNull(card);
        assertEquals(51, cardManager.getRemainingCards());
    }

    @Test
    public void testGetRemainingCards() {
        assertEquals(52, cardManager.getRemainingCards());
        cardManager.drawCard();
        assertEquals(51, cardManager.getRemainingCards());
    }

    @Test
    public void testGetCardsValue() {
        Card[] cards = {
                new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                new Card(Card.Suit.SPADES, Card.Rank.KING)
        };
        int totalValue = cardManager.getCardsValue(cards);
        assertEquals(11, totalValue);
    }

    @Test
    public void testIsDeckEmpty() {
        assertFalse(cardManager.isDeckEmpty());
        for (int i = 0; i < 52; i++) {
            cardManager.drawCard();
        }
        assertTrue(cardManager.isDeckEmpty());
    }
}
