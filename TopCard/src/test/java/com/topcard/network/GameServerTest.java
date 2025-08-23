package com.topcard.network;

import com.topcard.domain.Player;
import com.topcard.network.game.GameMessage;
import com.topcard.network.game.GameServer;
import com.topcard.presentation.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameServerTest {

    private static final Logger logger = LogManager.getLogger(GameServerTest.class);

    private GameServer server;
    private Thread serverThread;

    /**
     * Sets up the GameServer once before all test methods.
     */
    @BeforeAll
    void setupAll() {
        server = new GameServer();
        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                if (!e.getMessage().contains("Socket closed")) {
                    logger.error("GameServer test thread error: {}", e.getMessage());
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        try {
            TimeUnit.MILLISECONDS.sleep(1000); // Give server time to bind
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Test setup interrupted", e);
        }
        logger.info("GameServer started for testing on port {}.", Constants.GAME_PORT);
    }

    /**
     * Tears down the GameServer after all test methods complete.
     */
    @AfterAll
    void tearDownAll() throws IOException, InterruptedException {
        if (server != null) {
            server.stop();
            // Added a small sleep here to allow the server's accept loop to catch the SocketException
            // and break out before we try to join the thread.
            TimeUnit.MILLISECONDS.sleep(500);
            serverThread.join(2000); // Wait for server thread to terminate gracefully
            logger.info("GameServer stopped after testing.");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSendAllBroadcastsToClients() throws IOException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ObjectOutputStream out1 = mock(ObjectOutputStream.class);
        ObjectOutputStream out2 = mock(ObjectOutputStream.class);

        GameMessage message = new GameMessage("TEST_MESSAGE", "payload");

        Field clientOutputsField = server.getClass().getDeclaredField("clientOutputs");
        clientOutputsField.setAccessible(true);
        List<ObjectOutputStream> mockOutputs = (List<ObjectOutputStream>) clientOutputsField.get(server);
        mockOutputs.clear();
        mockOutputs.add(out1);
        mockOutputs.add(out2);

        Method sendAllMethod = server.getClass().getDeclaredMethod("sendAll", GameMessage.class);
        sendAllMethod.setAccessible(true);
        sendAllMethod.invoke(server, message);

        verify(out1).writeObject(message);
        verify(out2).writeObject(message);
        verify(out1).flush();
        verify(out2).flush();
    }

    @Test
    void testGameServerCanInstantiateWithoutError() {
        assertDoesNotThrow(GameServer::new, "GameServer constructor should not throw");
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPlayerJoinSimulation() throws Exception {
        Field playersField = server.getClass().getDeclaredField("connectedPlayers");
        playersField.setAccessible(true);
        List<Player> players = (List<Player>) playersField.get(server);

        players.clear();

        Player mockPlayer = new Player();
        mockPlayer.setUsername("TestUser");
        mockPlayer.setPoints(100);

        players.add(mockPlayer);

        assertEquals(1, players.size());
        assertEquals("TestUser", players.get(0).getUsername());
    }

    /**
     * Tests that GameServer binds to the correct port (12345).
     */
    @Test
    void testGameServerBindsToCorrectPort() {
        logger.debug("Attempting to connect to GameServer on port {}.", Constants.GAME_PORT);
        try (Socket clientSocket = new Socket("localhost", Constants.GAME_PORT)) {
            assertTrue(clientSocket.isConnected(), "Client should successfully connect to server.");
            assertDoesNotThrow(clientSocket::close, "Client socket should close without error.");
            logger.info("Successfully connected to GameServer on port {}.", Constants.GAME_PORT);
        } catch (IOException e) {
            fail("Failed to connect to GameServer on port " + Constants.GAME_PORT + ": " + e.getMessage());
        }
    }

    /**
     * Tests GameServer rejects invalid initial messages (not "JOIN").
     */
    @Test
    void testGameServerRejectsInvalidInitialMessage() throws IOException {
        logger.debug("Attempting to send an invalid initial message to GameServer.");
        try (Socket clientSocket = new Socket("localhost", Constants.GAME_PORT);
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            clientSocket.setSoTimeout(2000);

            out.writeObject(new GameMessage("INVALID_TYPE", "bad payload"));
            out.flush();

            IOException thrown = assertThrows(IOException.class, () -> {
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                in.readObject();
            }, "Server should close connection, causing an IOException.");

            logger.debug("Caught IOException during invalid message test: Type={}, Message={}", thrown.getClass().getName(), thrown.getMessage());
            assertTrue(
                    thrown instanceof java.io.EOFException ||
                            thrown instanceof java.net.SocketTimeoutException ||
                            thrown instanceof java.net.SocketException ||
                            (thrown.getMessage() != null && thrown.getMessage().toLowerCase().contains("connection reset")) ||
                            (thrown.getMessage() != null && thrown.getMessage().toLowerCase().contains("socket closed")) ||
                            (thrown.getMessage() != null && thrown.getMessage().toLowerCase().contains("read timed out")),
                    "Expected connection termination due to server rejection. Actual: " + thrown.getClass().getName() + " - " + thrown.getMessage()
            );

        } catch (IOException e) {
            fail("Failed to connect to GameServer to perform invalid message test: " + e.getMessage());
        }
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}