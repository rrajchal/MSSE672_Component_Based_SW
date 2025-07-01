package com.topcard.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * The Card class represents a playing card with a specific suit and rank.
 * It is immutable, meaning once it is created, its suit and rank cannot be changed.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 */
public class Card implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Suit suit;
    private Rank rank;

    public Card() {

    }

    /**
     * Constructs a new Card with the specified suit and rank.
     *
     * @param suit the suit of the card
     * @param rank the rank of the card
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * The Suit enum represents the four possible suits of a playing card.
     */
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    /**
     * The Rank enum represents the possible ranks of a playing card.
     * Each rank has an associated value and precedence.
     */
    public enum Rank {
        ACE(1, 13),
        TWO(2, 12),
        THREE(3, 11),
        FOUR(4, 10),
        FIVE(5, 9),
        SIX(6, 8),
        SEVEN(7, 7),
        EIGHT(8, 6),
        NINE(9, 5),
        TEN(10, 4),
        JACK(10, 3),
        QUEEN(10, 2),
        KING(10, 1);

        private final int value;
        private final int precedence;

        /**
         * Constructs a new Rank with the specified value and precedence.
         *
         * @param value the value of the rank
         * @param precedence the precedence of the rank
         */
        Rank(int value, int precedence) {
            this.value = value;
            this.precedence = precedence;
        }

        /**
         * Returns the value of the rank.
         *
         * @return the value of the rank
         */
        public int getValue() {
            return value;
        }

        /**
         * Returns the precedence of the rank.
         *
         * @return the precedence of the rank
         */
        public int getPrecedence() {
            return precedence;
        }
    }

    /**
     * Calculates the value of the given card
     *
     * @param card A Card
     * @return the value of the card
     */
    public int getCardValue(Card card) {
        int totalValue = 0;
        if (card != null) {
            totalValue += card.getRank().getValue();
        }
        return totalValue;
    }

    /**
     * Calculates and returns the total value of the given array of cards.
     *
     * @param cards the array of cards
     * @return the total value of the cards
     */
    public int getCardsValue(Card[] cards) {
        int totalValue = 0;
        for (Card card : cards) {
            totalValue += getCardValue(card);
        }
        return totalValue;
    }

    /**
     * Returns the suit of the card.
     *
     * @return the suit of the card
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Returns the rank of the card.
     *
     * @return the rank of the card
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Returns a string representation of the card in the format "RANK OF SUIT", like ACE OF HEART, TWO OF HEARTS.
     *
     * @return a string representation of the card
     */
    @Override
    public String toString() {
        return rank + " OF " + suit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}
