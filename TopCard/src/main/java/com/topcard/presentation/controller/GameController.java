package com.topcard.presentation.controller;

import com.topcard.business.GameManager;
import com.topcard.domain.Card;
import com.topcard.domain.Player;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class represents the GameController.
 * It manages the user interactions for the game view.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class GameController {

    private static final Logger logger = LogManager.getLogger(GameController.class);

    @FXML
    private ImageView player1C1, player1C2, player1C3;
    @FXML
    private ImageView player2C1, player2C2, player2C3;
    @FXML
    private ImageView player3C1, player3C2, player3C3;
    @FXML
    private ImageView player4C1, player4C2, player4C3;
    @FXML
    private TextField winnerTextField;
    @FXML
    private TextField betAmountTextField;
    @FXML
    private TextField player1FirstName, player1Balance, player1Change;
    @FXML
    private TextField player2FirstName, player2Balance, player2Change;
    @FXML
    private TextField player3FirstName, player3Balance, player3Change;
    @FXML
    private TextField player4FirstName, player4Balance, player4Change;

    private int betAmount = 0;
    private List<Player> players;
    private GameManager gameManager;

    /**
     * Sets the list of players name in the UI.
     *
     * @param players the list of players
     */
    public void setPlayersName(List<Player> players) {
        this.players = players;
        player1FirstName.setText(players.get(0).getFirstName());
        player2FirstName.setText(players.get(1).getFirstName());
        player3FirstName.setText(players.get(2).getFirstName());
        player4FirstName.setText(players.get(3).getFirstName());

        player1Balance.setText(String.valueOf(players.get(0).getPoints()));
        player2Balance.setText(String.valueOf(players.get(1).getPoints()));
        player3Balance.setText(String.valueOf(players.get(2).getPoints()));
        player4Balance.setText(String.valueOf(players.get(3).getPoints()));
    }

    /**
     * Sets the balance of players name in the UI.
     *
     * @param players the list of players
     */
    public void setPlayersPoints(List<Player> players) {
        this.players = players;
        player1Balance.setText(String.valueOf(players.get(0).getPoints()));
        player2Balance.setText(String.valueOf(players.get(1).getPoints()));
        player3Balance.setText(String.valueOf(players.get(2).getPoints()));
        player4Balance.setText(String.valueOf(players.get(3).getPoints()));
    }

    /**
     * Handles the event when the start button is pressed.
     * Validates the bet amount, starts the game, deals cards, executes the betting round,
     * updates player point changes, and displays the winners.
     */
    @FXML
    private void startButtonPressed() {
        logger.info("Start Game button pressed");
        // Initialize GameManager with the players
        gameManager = new GameManager(players);
        boolean valid = checkForBetAmount();
        if (valid) {
            // Start the game and deal cards
            gameManager.startGame();

            // Clone the players list
            List<Player> initialPlayers = clonePlayers(players);

            // Execute betting round
            List<Player> updatedPlayers = gameManager.executeBettingRound(betAmount);

            displayCards();

            updatePlayerPointChanges(initialPlayers, updatedPlayers);

            // Determine winners and display them
            List<Player> winners = gameManager.determineWinner();
            displayWinners(winners);
            setPlayersPoints(updatedPlayers);
        }
    }

    /**
     * Validates the bet amount entered by the user.
     *
     * @return true if the bet amount is valid, false otherwise
     */
    private boolean checkForBetAmount() {
        Pattern pattern = Pattern.compile("\\d+");
        String betAmountEntered = betAmountTextField.getText().trim();
        if (betAmountEntered.isEmpty()) {
            winnerTextField.setStyle("-fx-text-fill: red;");
            winnerTextField.setText("Put a bet amount in digits in the Bet Amount TextField");
        }

        if (pattern.matcher(betAmountEntered).matches()) {
            winnerTextField.setText("");
            betAmount = Integer.parseInt(betAmountEntered);
            logger.info("Bet amount: " + betAmountEntered);
            return true;
        } else {
            winnerTextField.setStyle("-fx-text-fill: red;");
            winnerTextField.setText("Invalid amount in the Bet TextField");
        }
        return false;
    }

    /**
     * Display cards in the ImageView elements.
     */
    private void displayCards() {
        List<Card[]> hands = gameManager.getHands();

        // Player 1
        setCardImage(player1C1, hands.get(0)[0]);
        setCardImage(player1C2, hands.get(0)[1]);
        setCardImage(player1C3, hands.get(0)[2]);

        // Player 2
        setCardImage(player2C1, hands.get(1)[0]);
        setCardImage(player2C2, hands.get(1)[1]);
        setCardImage(player2C3, hands.get(1)[2]);

        // Player 3
        setCardImage(player3C1, hands.get(2)[0]);
        setCardImage(player3C2, hands.get(2)[1]);
        setCardImage(player3C3, hands.get(2)[2]);

        // Player 4
        setCardImage(player4C1, hands.get(3)[0]);
        setCardImage(player4C2, hands.get(3)[1]);
        setCardImage(player4C3, hands.get(3)[2]);
    }

    /**
     * Sets the card image in the specified ImageView.
     *
     * @param imageView the ImageView to set the card image
     * @param card the card to be displayed
     */
    private void setCardImage(ImageView imageView, Card card) {
        String imagePath = "/images/" + card.toString() + ".JPG";
        logger.info("Attempting to load image from path: " + imagePath);
        try {
            Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            logger.info("Successfully loaded image: " + imagePath);
            imageView.setImage(cardImage);
        } catch (Exception e) {
            logger.error("Failed to load image: " + imagePath);
            imageView.setImage(null); // Optionally set a placeholder or default image
        }
    }

    /**
     * Updates the changes in player points and displays them in the UI.
     *
     * @param initialPlayers the list of players before the betting round
     * @param updatedPlayers the list of players with updated points
     */
    private void updatePlayerPointChanges(List<Player> initialPlayers, List<Player> updatedPlayers) {
        // Create a map to store initial points for players
        Map<String, Integer> initialPoints = initialPlayers.stream()
                .collect(Collectors.toMap(Player::getUsername, Player::getPoints));

        // Sort the updatedPlayers to match the order of initialPlayers by username
        updatedPlayers.sort(Comparator.comparingInt(updatedPlayer ->
                initialPlayers.indexOf(initialPlayers.stream()
                        .filter(player -> player.getUsername().equals(updatedPlayer.getUsername()))
                        .findFirst()
                        .orElse(null))
        ));

        // Calculate and set the changes in player points
        for (int i = 0; i < initialPlayers.size(); i++) {
            int initialPoint = initialPoints.get(initialPlayers.get(i).getUsername());
            int updatedPoint = updatedPlayers.get(i).getPoints();
            int change = updatedPoint - initialPoint;

            switch (i) {
                case 0:
                    player1Change.setText(String.valueOf(change));
                    break;
                case 1:
                    player2Change.setText(String.valueOf(change));
                    break;
                case 2:
                    player3Change.setText(String.valueOf(change));
                    break;
                case 3:
                    player4Change.setText(String.valueOf(change));
                    break;
            }
        }
    }

    /**
     * Displays the winners in the winnerTextField.
     *
     * @param winners the list of winning players
     */
    private void displayWinners(List<Player> winners) {
        StringBuilder winnerNames = new StringBuilder("Winner(s): ");
        for (Player winner : winners) {
            winnerNames.append(winner.getFirstName()).append(" ");
        }
        winnerTextField.setText(winnerNames.toString().trim());
    }

    private List<Player> clonePlayers(List<Player> players) {
        return players.stream().map(this::clonePlayer).collect(Collectors.toList());
    }

    /**
     * Creates a deep copy of the given Player object.
     * The cloned player retains the same ID, password, and other attributes as the original player.
     *
     * @param player the Player object to be cloned
     * @return a new Player object that is a deep copy of the provided player
     */
    private Player clonePlayer(Player player) {
        Player clonedPlayer = new Player(
                player.getUsername(),
                player.getPassword(),
                player.getFirstName(),
                player.getLastName(),
                player.getDateOfBirth()
        );
        clonedPlayer.setPlayerId(player.getPlayerId());
        clonedPlayer.setPoints(player.getPoints());
        clonedPlayer.setAdmin(player.isAdmin());
        clonedPlayer.setLoggedIn(player.isLoggedIn());
        return clonedPlayer;
    }
}
