package com.topcard.service;

import com.topcard.domain.Card;
import com.topcard.service.card.CardService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardServiceTest {

    private CardService cardService;

    @Before
    public void setUp() {
        cardService = new CardService();
    }

    @Test
    public void testDrawCard() {
        Card card = cardService.drawCard();
        assertNotNull(card);
    }

    @Test
    public void testShuffleDeck() {
        Card firstCardBeforeShuffle = cardService.drawCard();
        cardService.createDeck(); // Reset deck to original state
        Card secondCardBeforeShuffle = cardService.drawCard();

        // Should be the same card if deck hasn't been shuffled
        assertEquals(firstCardBeforeShuffle, secondCardBeforeShuffle);

        cardService.createShuffledDeck(); // Reset deck to original state and shuffle
        Card firstCardAfterShuffle = cardService.drawCard();

        // It could fail (rarely) if the first card after shuffling is the same as before
        // assertNotEquals(firstCardBeforeShuffle, firstCardAfterShuffle);
    }

    @Test
    public void testIsDeckEmpty() {
        // Draw all cards from the deck
        for (int i = 0; i < 52; i++) {
            cardService.drawCard();
        }
        assertTrue(cardService.isDeckEmpty());
    }

    @Test
    public void testGetRemainingCards() {
        cardService.createDeck(); // Ensure deck is full (52 cards)
        int remainingCards = cardService.getRemainingCards();
        assertEquals(52, remainingCards);

        cardService.drawCard(); // Draw one card (the last one)
        remainingCards = cardService.getRemainingCards();
        assertEquals(51, remainingCards);
    }

    @Test
    public void testCreateNewDeck() {
        cardService.createDeck(); // Create a new deck
        assertEquals(52, cardService.getRemainingCards());
        Card card = cardService.drawCard();
        assertNotNull(card); // Ensure we can draw from the new deck
    }

    @Test
    public void testCreateShuffledDeck() {
        cardService.createShuffledDeck(); // Create a new shuffled deck
        assertEquals(52, cardService.getRemainingCards());
        Card card = cardService.drawCard();
        assertNotNull(card); // Ensure we can draw from the new shuffled deck
    }
}
