package com.topcard.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

/**
 * The Deck class represents a deck of playing cards.
 * It provides methods to shuffle the deck and deal a card from it.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 */
public class Deck implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private static final Logger logger = LogManager.getLogger(Deck.class);

    private final Card[] cards; // There will be exactly 52 cards
    /**
     * The index of the next card to be dealt from the deck. This variable tacks the position of the next card to be drawn.
     * For example, the first card after the creation of a deck will be at index 0.
     * And, after dealing 51 cards, there is only one card left on the deck; the card index is 51.
     */
    private int currentIndex;

    /**
     * Total number of cards in a deck
     */
    final static int NUM_OF_CARDS_IN_DECK = 52;

    /**
     * Constructs a new Deck with 52 cards, including all suits and ranks.
     */
    public Deck() {
        logger.info("Deck created.");
        cards = new Card[NUM_OF_CARDS_IN_DECK];
        currentIndex = 0;
        // Create the deck
        int index = 0;
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards[index++] = new Card(suit, rank);
            }
        }
    }

    /**
     * Shuffles the deck, randomizing the order of the cards.
     * Gets ready to deal card. Card will be dealt from the top (currentIndex 0)
     */
    public void shuffle() {
        Collections.shuffle(Arrays.asList(cards));
        currentIndex = 0;
    }

    /**
     * Deals a card from the deck. If no more cards are available, returns null.
     *
     * @return the dealt card, or null if no more cards are available
     */
    public Card deal() {
        if (currentIndex >= cards.length) {
            return null; // No cards to deal
        }
        return cards[currentIndex++];
    }

    /**
     * Returns the number of remaining cards in the deck.
     *
     * @return the number of remaining cards
     */
    public int getRemainingCards() {
        return cards.length - currentIndex;
    }

    /**
     * Returns a string representation of the deck.
     * Each card is listed with its rank and suit.
     *
     * @return a string representation of the deck
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Deck contains:\n");
        for (int i = currentIndex; i < cards.length; i++) {
            sb.append(cards[i].toString()).append("\n");
        }
        return sb.toString();
    }

}