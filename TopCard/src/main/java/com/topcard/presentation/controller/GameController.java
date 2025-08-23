package com.topcard.presentation.controller;

import com.topcard.business.GameManager;
import com.topcard.domain.Card;
import com.topcard.domain.Player;
import com.topcard.service.game.IGameService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
 * Date: 08/12/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Component
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

    private final GameManager gameManager;
    private ImageView[][] playerCards;

    // Add a new field for the sound effect
    private AudioClip cardDealSound;

    private final int CARD_DISTRIBUTE_DELAY = 100;

    @Autowired
    private ApplicationContext context;

    @Autowired
    public GameController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Initializes the controller. This method is automatically called after the FXML has been loaded.
     */
    @FXML
    public void initialize() {
        playerCards = new ImageView[][]{
                {player1C1, player1C2, player1C3},
                {player2C1, player2C2, player2C3},
                {player3C1, player3C2, player3C3},
                {player4C1, player4C2, player4C3}
        };

        // Initialize the AudioClip for the card dealing sound.
        try {
            cardDealSound = new AudioClip(Objects.requireNonNull(getClass().getResource("/sound/dealCardSound.mp3")).toString());
        } catch (NullPointerException e) {
            logger.error("Could not load sound file: /sound/dealCardSound.mp3", e);
        }
    }

    /**
     * Sets the list of players name and initial balance in the UI.
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
     * Validates the bet amount, starts the game, and initiates the card dealing animation.
     */
    @FXML
    private void startButtonPressed() {
        logger.info("Start Game button pressed");
        // Initialize GameManager with the players
        boolean valid = checkForBetAmount();
        if (valid) {
            // Start the game and deal cards
            IGameService gameService = context.getBean(IGameService.class);
            gameService.setPlayers(players);
            gameManager.startGame();
            displayCardsSequentially();
        }
    }

    /**
     * Creates and plays a Timeline to display cards one by one in a round-robin fashion.
     */
    private void displayCardsSequentially() {
        List<Card[]> hands = gameManager.getHands();
        Timeline timeline = new Timeline();
        Duration delay = Duration.ZERO;

        // Loop through each player and each card to create a sequential animation
        // Outer loop for the cards to be dealt (1st, 2nd, 3rd)
        for (int c = 0; c < 3; c++) {
            // Inner loop for each player
            for (int p = 0; p < hands.size(); p++) {
                final int playerIndex = p;
                final int cardIndex = c;

                // Add a delay for each card
                delay = delay.add(Duration.millis(CARD_DISTRIBUTE_DELAY));

                KeyFrame keyFrame = new KeyFrame(delay, event -> {
                    Card currentCard = hands.get(playerIndex)[cardIndex];
                    ImageView imageView = playerCards[playerIndex][cardIndex];
                    setCardImage(imageView, currentCard);

                    // Play the sound effect for each card dealt
                    if (cardDealSound != null) {
                        cardDealSound.play();
                    }
                });

                timeline.getKeyFrames().add(keyFrame);
            }
        }

        // After the last card is displayed, run the rest of the game logic
        timeline.setOnFinished(event -> {
            // Clone the players list
            List<Player> initialPlayers = clonePlayers(players);

            // Execute betting round
            List<Player> updatedPlayers = gameManager.executeBettingRound(betAmount);

            updatePlayerPointChanges(initialPlayers, updatedPlayers);

            // Determine winners and display them
            List<Player> winners = gameManager.determineWinner();
            displayWinners(winners);
            setPlayersPoints(updatedPlayers);
        });

        // Start the animation
        timeline.play();
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