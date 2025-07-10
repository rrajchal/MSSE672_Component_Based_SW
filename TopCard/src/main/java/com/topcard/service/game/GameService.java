package com.topcard.service.game;

import com.topcard.domain.Card;
import com.topcard.domain.Game;
import com.topcard.domain.Player;
import com.topcard.service.player.PlayerService;

import java.util.List;

public class GameService implements IGameService {

    private final Game game;
    private final PlayerService playerService;
    private final List<Player> players;

    public GameService(List<Player> players) {
        this.playerService = new PlayerService();
        this.players = players;
        List<Player> updatedPlayers = updateExistingPlayers(players);
        this.game = new Game(updatedPlayers);
    }

    @Override
    public void startGame() {
        game.startGame();
    }

    @Override
    public List<Card[]> getHands() {
        return game.getHands();
    }

    @Override
    public void showHands() {
        game.showHand();
    }

    @Override
    public void dealCards() {
        game.dealCards();
    }

    @Override
    public List<Player> executeBettingRound(int points) {
        List<Player> updatedPlayers = game.betAndUpdatePlayerPoints(points, game.getPlayers());
        updatePoints(updatedPlayers);
        return updatedPlayers;
    }

    @Override
    public List<Player> determineWinner() {
        return game.determineWinner();
    }

    @Override
    public void updateProfile(Player player) {
        playerService.updateProfile(player);
    }

    @Override
    public void updateProfiles(List<Player> players) {
        for (Player player : players) {
            updateProfile(player);
        }
    }

    @Override
    public void updatePoints(Player player) {
        playerService.changePoints(player.getPlayerId(), player.getPoints());
    }

    @Override
    public void updatePoints(List<Player> players) {
        for (Player player : players) {
            updatePoints(player);
        }
    }

    @Override
    public void displayWinners(List<Player> winners) {
        game.displayWinners(winners);
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Add players if they are already not in data
     * @param players players to be added to the data if they don't exist
     */
    private void addPlayers(List<Player> players) {
        playerService.addPlayers(players);
    }

    /**
     * Updates the points of existing players based on the data.
     * If a player with the same username is found in the data, their points are updated.
     *
     * @param players the list of players to be checked and updated
     * @return the list of players with updated points
     */
    private List<Player> updateExistingPlayers(List<Player> players) {
        for (Player player : players) {
            Player existingPlayer = playerService.getPlayerByUsername(player.getUsername());
            if (existingPlayer != null) {
                player.setPlayerId(existingPlayer.getPlayerId()); // preserve id
                player.setPoints(existingPlayer.getPoints());     // preserve point
            }
        }
        return players;
    }
}
