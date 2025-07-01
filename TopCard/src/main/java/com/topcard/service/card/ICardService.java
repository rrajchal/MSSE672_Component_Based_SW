package com.topcard.service.card;

import com.topcard.domain.Card;
import com.topcard.domain.Deck;

/**
 * ICardService defines the operations related to card management in the TopCard game.
 * This interface includes methods for drawing cards, shuffling the deck, creating new decks, and checking remaining cards.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public interface ICardService {

    /**
     * Draws a card from the deck.
     *
     * @return the drawn Card, or null if no more cards are available
     */
    Card drawCard();

    /**
     * Shuffles the deck, randomizing the order of the cards.
     */
    void shuffleDeck();

    /**
     * Creates and returns a new deck (not shuffled) of cards.
     *
     * @return the new Deck of cards
     */
    Deck createDeck();

    /**
     * Creates and returns a new shuffled deck of cards.
     *
     * @return the new shuffled Deck of cards
     */
    Deck createShuffledDeck();

    /**
     * Returns the number of remaining cards in the deck.
     *
     * @return the number of remaining cards
     */
    int getRemainingCards();

    /**
     * Calculates the value of the given card
     *
     * @param cards the cards in hand
     * @return the value of the card
     */
    int getCardsValue(Card[] cards);

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    boolean isDeckEmpty();
}
