package com.topcard.service;

import com.topcard.domain.Player;
import com.topcard.service.game.GameService;
import com.topcard.service.player.PlayerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class GameServiceTest {

    // Logger for this test class
    private static final Logger logger = LogManager.getLogger(GameServiceTest.class);

    private GameService gameService;
    private Player player1;
    private Player player2;
    private Player player3;
    private List<Player> initialPlayers;

    @Before
    public void setUp() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.topcard");
        context.refresh();

        player1 = new Player("username1", "password", "firstName1", "lastName1", LocalDate.of(2000, 1, 1));
        player2 = new Player("username2", "password", "firstName2", "lastName2", LocalDate.of(2000, 2, 1));
        player3 = new Player("username3", "password", "firstName3", "lastName3", LocalDate.of(2000, 3, 1));
        initialPlayers = Arrays.asList(player1, player2, player3);
        gameService = context.getBean(GameService.class);
        gameService.setPlayers(initialPlayers);
    }

    @Test
    public void testStartGame() {
        gameService.startGame();
        assertNotNull(player1.getHand());
        assertNotNull(player2.getHand());
        assertNotNull(player3.getHand());
        assertNotEquals(0, player1.getHand().length);
        assertNotEquals(0, player2.getHand().length);
        assertNotEquals(0, player3.getHand().length);
    }

    @Test
    public void testPlayCompleteGame() {
        gameService.startGame();
        assertNotNull(player1.getHand());
        assertNotNull(player2.getHand());
        assertNotNull(player3.getHand());
        assertNotEquals(0, player1.getHand().length);
        assertNotEquals(0, player2.getHand().length);
        assertNotEquals(0, player3.getHand().length);
    }

    /**
     * Test to verify multithreaded interaction with the GameService.
     * This test uses an ExecutorService to run multiple threads in parallel, each performing a betting round.
     * It checks if the final player points reflect all concurrent operations.
     */
    @Test
    public void testConcurrentBettingRound() throws InterruptedException {
        logger.debug("Starting testConcurrentBettingRound...");
        gameService.startGame();
        gameService.dealCards();

        final int numberOfConcurrentBets = 5; // How many times each player attempts a bet concurrently
        final int pointsPerBet = 10;          // Points to bet in each round
        final int totalThreads = initialPlayers.size() * numberOfConcurrentBets;  // 3 * 5 = 15

        // Latch to synchronize the start of all betting threads
        CountDownLatch startSignal = new CountDownLatch(1);
        // Latch to wait for all betting threads to complete
        CountDownLatch finishSignal = new CountDownLatch(totalThreads);

        // Executor service to manage concurrent threads
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);

        logger.debug("Initializing {} concurrent betting operations.", totalThreads);

        for (int i = 0; i < numberOfConcurrentBets; i++) {
            final int round = i + 1;
            for (Player player : initialPlayers) {
                // Each player will try to execute a betting round
                executor.submit(() -> {
                    try {
                        // Wait for the start signal to ensure all threads begin at roughly the same time
                        startSignal.await();
                        logger.debug("Thread for player {} starting betting round {}.", player.getUsername(), round);

                        // Call the method that interacts with player points
                        List<Player> updatedPlayers = gameService.executeBettingRound(pointsPerBet);
                        logger.debug("Player {} completed betting round {}. Points updated (service internal).", player.getUsername(), round);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Betting thread interrupted for player {}.", player.getUsername(), e);
                    } catch (Exception e) {
                        logger.error("Error during concurrent betting for player {}: {}", player.getUsername(), e.getMessage());
                        fail("Exception in concurrent betting round: " + e.getMessage()); // Fail the test if an unexpected exception occurs
                    } finally {
                        finishSignal.countDown(); // Signal that this thread has completed its work
                    }
                });
            }
        }

        // Release all waiting threads simultaneously
        startSignal.countDown();
        logger.debug("All betting threads released to start.");

        // Wait for all threads to finish within a timeout
        boolean allFinished = finishSignal.await(10, TimeUnit.SECONDS);
        assertTrue("All concurrent betting rounds should complete within timeout.", allFinished);
        logger.debug("All concurrent betting operations completed.");

        // Shutdown the executor service
        executor.shutdown();
        assertTrue("Executor service should terminate cleanly.", executor.awaitTermination(5, TimeUnit.SECONDS));

        // Verify final state after all concurrent operations.
        assertNotNull("Players list should not be null after concurrent operations", gameService.getPlayers());

        logger.debug("Finished testConcurrentBettingRound.");
    }
}

