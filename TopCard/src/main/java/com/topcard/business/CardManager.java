package com.topcard.business;

import com.topcard.domain.Card;
import com.topcard.domain.Deck;
import com.topcard.service.card.CardService;
import com.topcard.service.card.ICardService;
import com.topcard.service.factory.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CardManager is responsible for managing card-related operations.
 * It interacts with the ICardService to shuffle the deck, create decks, draw cards,
 * and retrieve card-related information.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 08/22/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Component
public class CardManager {
    private final ICardService cardService;

    /**
     * Constructs a new CardManager and initializes the card service.
     */
    @Autowired
    public CardManager(ICardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Shuffles the current deck of cards.
     */
    public void shuffleDeck() {
        cardService.shuffleDeck();
    }

    /**
     * Creates a new deck of cards.
     *
     * @return the newly created deck
     */
    public Deck createDeck() {
        return cardService.createDeck();
    }

    /**
     * Creates a new shuffled deck of cards.
     *
     * @return the newly created shuffled deck
     */
    public Deck createShuffledDeck() {
        return cardService.createShuffledDeck();
    }

    /**
     * Draws a card from the deck.
     *
     * @return the drawn card
     */
    public Card drawCard() {
        return cardService.drawCard();
    }

    /**
     * Retrieves the number of remaining cards in the deck.
     *
     * @return the number of remaining cards
     */
    public int getRemainingCards() {
        return cardService.getRemainingCards();
    }

    /**
     * Calculates the total value of the given cards.
     *
     * @param cards the array of cards to calculate the value for
     * @return the total value of the cards
     */
    public int getCardsValue(Card[] cards) {
        return cardService.getCardsValue(cards);
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isDeckEmpty() {
        return cardService.isDeckEmpty();
    }
}
