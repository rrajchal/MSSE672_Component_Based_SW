package com.topcard.service.card;

import com.topcard.domain.Card;
import com.topcard.domain.Deck;
import org.springframework.stereotype.Service;

/**
 * CardService is a service class that implements the ICardService interface.
 * It provides the business logic for card-related operations in the TopCard game.
 * This includes drawing cards and shuffling the deck.
 *
 */
@Service
public class CardService implements ICardService {
    private Deck deck = new Deck();

    @Override
    public Card drawCard() {
        return deck.deal();
    }

    @Override
    public void shuffleDeck() {
        deck.shuffle();
    }

    @Override
    public Deck createDeck() {
        this.deck = new Deck();
        return deck;
    }

    @Override
    public Deck createShuffledDeck() {
        this.deck = new Deck();
        deck.shuffle();
        return this.deck;
    }

    @Override
    public int getRemainingCards() {
        return deck.getRemainingCards();
    }

    /**
     * Calculates the value of the given card
     *
     * @param cards the cards in hand
     * @return the value of the card
     */
    @Override
    public int getCardsValue(Card[] cards) {
        // Card object has the function.
        return cards[0].getCardsValue(cards);
    }

    @Override
    public boolean isDeckEmpty() {
        return deck.getRemainingCards() == 0;
    }
}
