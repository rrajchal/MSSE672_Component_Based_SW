package com.topcard.network;

import com.topcard.domain.Player;
import com.topcard.network.game.GameClient;
import com.topcard.network.game.GameMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;

public class GameClientTest {

    private GameClient client;
    private Player mockPlayer;
    private static final int PORT = 12345;

    @BeforeEach
    void setUp() {
        // Reset the singleton instance for each test
        setSingletonInstanceToNull();
        client = GameClient.getInstance();
        mockPlayer = new Player();
        mockPlayer.setUsername("testPlayer");
    }

    @AfterEach
    void tearDown() {
        // Ensure the client is disconnected after each test
        if (client != null) {
            client.disconnect();
        }
        // Reset the singleton instance again to ensure clean state for next test
        setSingletonInstanceToNull();
    }

    // Helper to reset the singleton instance using reflection
    private void setSingletonInstanceToNull() {
        try {
            Field instanceField = GameClient.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to reset singleton instance: " + e.getMessage());
        }
    }

    @Test
    void testGetInstance() {
        GameClient anotherClient = GameClient.getInstance();
        assertSame(client, anotherClient, "GameClient should be a singleton");
    }

    @Test
    void testConnectAndSend() throws IOException, InterruptedException {
        CountDownLatch joinMessageReceived = new CountDownLatch(1);
        CountDownLatch clientListenerTerminated = new CountDownLatch(1);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Thread serverThread = new Thread(() -> {
                try (
                        Socket socket = serverSocket.accept();
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()) // Need out to send shutdown
                ) {
                    // 1. Server reads the JOIN message
                    Object obj = in.readObject();
                    assertNotNull(obj);
                    assertInstanceOf(GameMessage.class, obj);
                    GameMessage msg = (GameMessage) obj;
                    assertEquals("JOIN", msg.getType());
                    assertEquals(mockPlayer.getUsername(), ((Player) msg.getPayload()).getUsername());
                    joinMessageReceived.countDown(); // Signal that JOIN was received

                    // 2. Server sends a SHUTDOWN message back to the client
                    GameMessage shutdownMsg = new GameMessage("SHUTDOWN", null);
                    out.writeObject(shutdownMsg);
                    out.flush();
                    System.out.println("Server sent SHUTDOWN message.");

                    // Keep socket open briefly for client to receive SHUTDOWN
                    Thread.sleep(100);

                } catch (Exception e) {
                    System.err.println("Server thread error: " + e.getMessage());
                    fail("Server failed during test: " + e.getMessage());
                } finally {
                    clientListenerTerminated.countDown();
                }
            });
            serverThread.start();
            client.connect("localhost", mockPlayer);

            // Wait for the JOIN message to be received by the server
            assertTrue(joinMessageReceived.await(5, TimeUnit.SECONDS), "Server did not receive JOIN message in time");

            // Wait for the client's listener to terminate
             assertTrue(clientListenerTerminated.await(5, TimeUnit.SECONDS), "Server thread did not complete in time.");

            // Give a moment for the client's listener to process the SHUTDOWN
            Thread.sleep(100);
        } catch (IOException e) {
            fail("IOException during test: " + e.getMessage());
        }
    }

    @Test
    void testSendWithoutConnection() {
        // Ensure client is truly disconnected for this test
        client.disconnect(); // Ensure 'out' is null

        GameMessage msg = new GameMessage("TEST", mockPlayer);
        // as the send method gracefully logs an error if out is null.
        assertDoesNotThrow(() -> client.send(msg), "Sending without connection should not throw");
    }
}