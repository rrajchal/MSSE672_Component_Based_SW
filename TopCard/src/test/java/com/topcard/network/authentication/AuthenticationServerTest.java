package com.topcard.network.authentication;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.network.game.GameMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for the AuthenticationServer to verify its functionality, especially its multi-threading capabilities
 * and proper handling of concurrent login.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // Required for @BeforeAll and @AfterAll on non-static methods
public class AuthenticationServerTest {

    private static final Logger logger = LogManager.getLogger(AuthenticationServerTest.class);

    private AuthenticationServer authenticationServer;
    private Thread serverThread;
    private static final int TEST_AUTH_PORT = 9000;
    private static final int NUM_CLIENTS = 4;

    private PlayerManager mockPlayerManager;
    private List<Player> testPlayers;

    @BeforeAll
    void setUpAll() throws Exception {
        AnnotationConfigApplicationContext context =
                new org.springframework.context.annotation.AnnotationConfigApplicationContext();
        context.scan("com.topcard");
        context.refresh();

        authenticationServer = new AuthenticationServer(mockPlayerManager);

        // copied code from other test class.
        testPlayers = List.of(
                new Player("mickey", "password", "Mickey", "Mouse", LocalDate.of(1990, 1, 1)),
                new Player("donald", "password", "Donald", "Duck", LocalDate.of(1995, 6, 15)),
                new Player("michael", "password", "Michael", "Smith", LocalDate.of(1985, 11, 25)),
                new Player("rajesh", "password", "Rajesh", "Rajchal", LocalDate.of(1980, 4, 10))
        );

        mockPlayerManager = Mockito.mock(PlayerManager.class);

        authenticationServer.setAuthPort(TEST_AUTH_PORT);

        configureMockPlayerManager();

        // Inject the mock into the AuthenticationServer instance
        Field playerManagerField = AuthenticationServer.class.getDeclaredField("playerManager");
        playerManagerField.setAccessible(true);
        playerManagerField.set(authenticationServer, mockPlayerManager);

        // Start the AuthenticationServer in a separate thread
        serverThread = new Thread(() -> {
            try {
                authenticationServer.start();
            } catch (IOException e) {
                // We expect this exception when we close the socket in tearDownAll
                logger.error("AuthenticationServer test startup error: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Give the server a moment to start up
        TimeUnit.MILLISECONDS.sleep(500);
    }

    /**
     * Configures the behavior of the mockPlayerManager.
     */
    private void configureMockPlayerManager() {
        when(mockPlayerManager.getPlayerByUsername(Mockito.anyString())).thenAnswer(
                (Answer<Player>) invocation -> {
                    String username = invocation.getArgument(0);
                    return testPlayers.stream()
                            .filter(p -> p.getUsername().equals(username))
                            .findFirst()
                            .orElse(null);
                });

        when(mockPlayerManager.verifyPassword(Mockito.anyString(), Mockito.anyString())).thenAnswer(
                (Answer<Boolean>) invocation -> {
                    String password = invocation.getArgument(0);
                    String storedPassword = invocation.getArgument(1);
                    return password.equals(storedPassword);
                });

        when(mockPlayerManager.addPlayer(Mockito.any(Player.class))).thenAnswer(
                (Answer<Void>) invocation -> null);
    }

    @AfterAll
    void tearDownAll() throws IOException, InterruptedException {
        if (authenticationServer != null) {
            authenticationServer.stop();
            serverThread.join(1000);
        }
    }

    // Multiple client threads simultaneously connect to the server and attempt to log in
    @Test
    void testConcurrentValidLogins() throws InterruptedException {
        CountDownLatch allClientsFinished = new CountDownLatch(testPlayers.size());
        ExecutorService clientExecutor = Executors.newFixedThreadPool(testPlayers.size());

        List<Future<GameMessage>> futures = new ArrayList<>();

        for (Player player : testPlayers) {
            futures.add(clientExecutor.submit(() -> {
                GameMessage response = null;
                try (Socket clientSocket = new Socket("localhost", TEST_AUTH_PORT);
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                    out.writeObject(new GameMessage("LOGIN", player));
                    out.flush();
                    response = (GameMessage) in.readObject();

                } catch (IOException | ClassNotFoundException e) {
                    fail("Client failed to connect or read response for player " + player.getUsername() + ": " + e.getMessage());
                } finally {
                    allClientsFinished.countDown();
                }
                return response;
            }));
        }

        assertTrue(allClientsFinished.await(5, TimeUnit.SECONDS), "Not all clients finished in time");

        for (Future<GameMessage> future : futures) {
            try {
                GameMessage response = future.get();
                assertNotNull(response);
                assertEquals("AUTH_SUCCESS", response.getType(), "Login should be successful for a valid player");
            } catch (Exception e) {
                fail("Exception during client request processing: " + e.getMessage());
            }
        }

        clientExecutor.shutdown();
        assertTrue(clientExecutor.awaitTermination(1, TimeUnit.SECONDS), "Client executor did not terminate");
    }

    @Test
    void testConcurrentInvalidLogins() throws InterruptedException {
        CountDownLatch allClientsFinished = new CountDownLatch(testPlayers.size());
        ExecutorService clientExecutor = Executors.newFixedThreadPool(testPlayers.size());

        List<Future<GameMessage>> futures = new ArrayList<>();

        for (Player player : testPlayers) {
            Player invalidPlayer = new Player(player.getUsername(), "wrong_password", player.getFirstName(), player.getLastName(), player.getDateOfBirth());
            futures.add(clientExecutor.submit(() -> {
                GameMessage response = null;
                try (Socket clientSocket = new Socket("localhost", TEST_AUTH_PORT);
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                    out.writeObject(new GameMessage("LOGIN", invalidPlayer));
                    out.flush();
                    response = (GameMessage) in.readObject();

                } catch (IOException | ClassNotFoundException e) {
                    fail("Client failed to connect or read response for player " + player.getUsername() + ": " + e.getMessage());
                } finally {
                    allClientsFinished.countDown();
                }
                return response;
            }));
        }

        assertTrue(allClientsFinished.await(5, TimeUnit.SECONDS), "Not all clients finished in time");

        for (Future<GameMessage> future : futures) {
            try {
                GameMessage response = future.get();
                assertNotNull(response);
                assertEquals("AUTH_FAILURE", response.getType(), "Login should fail for an invalid password");
            } catch (Exception e) {
                fail("Exception during client request processing: " + e.getMessage());
            }
        }

        clientExecutor.shutdown();
        assertTrue(clientExecutor.awaitTermination(1, TimeUnit.SECONDS), "Client executor did not terminate");
    }
}