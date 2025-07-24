package com.topcard.network;

import com.topcard.domain.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SocketGameControllerTest {

    private Player testPlayer;
    private SocketGameController controller; // Declare here to access in @AfterEach
    private static final int TEST_PORT = 12345;


    @BeforeEach
    void setup() {
        testPlayer = new Player();
        testPlayer.setUsername("TestPlayer");
        testPlayer.setPoints(100);
        // Ensure getHand() returns a non-null array, even if empty, to avoid NPE in displayCards filter
        testPlayer.setHand(new com.topcard.domain.Card[]{});
    }

    @AfterEach
    void tearDown() {
        // Ensure the controller is disconnected after each test
        if (controller != null) {
            controller.disconnect();
            // Give a moment for the listener to terminate
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    void testSocketGameControllerSendsJoinMessage() throws Exception {
        // Latch to confirm JOIN message received by server
        CountDownLatch joinMessageReceivedLatch = new CountDownLatch(1);
        // Latch to confirm server has sent SHUTDOWN and finished its part
        CountDownLatch serverDoneLatch = new CountDownLatch(1);

        try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
            AtomicReference<GameMessage> receivedMessage = new AtomicReference<>();

            // Server thread to accept connection, read join message, and send SHUTDOWN
            Thread serverThread = new Thread(() -> {
                try (
                        Socket socket = serverSocket.accept();
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()) // For sending SHUTDOWN
                ) {
                    // 1. Read JOIN message from client
                    GameMessage msg = (GameMessage) in.readObject();
                    receivedMessage.set(msg);
                    joinMessageReceivedLatch.countDown(); // Signal JOIN received

                    // 2. Send SHUTDOWN message back to client
                    GameMessage shutdownMsg = new GameMessage("SHUTDOWN", null);
                    out.writeObject(shutdownMsg);
                    out.flush();
                    System.out.println("Server: Sent SHUTDOWN message to client.");

                    // Give client a moment to process SHUTDOWN before server closes socket
                    Thread.sleep(100);

                } catch (Exception e) {
                    System.err.println("Server thread error: " + e.getMessage());
                    fail("Server failed during test: " + e.getMessage());
                } finally {
                    serverDoneLatch.countDown(); // Signal server thread is done
                }
            });
            serverThread.start();

            // Client triggers constructor, which connects, sends JOIN, and starts listener
            controller = new SocketGameController(testPlayer, "localhost");

            // Wait for the JOIN message to be received by the server
            assertTrue(joinMessageReceivedLatch.await(5, TimeUnit.SECONDS), "Server did not receive JOIN message in time.");

            // Wait for the server thread to complete its process (send SHUTDOWN, etc.)
            assertTrue(serverDoneLatch.await(5, TimeUnit.SECONDS), "Server thread did not complete in time.");

            // Assertions on the received JOIN message
            GameMessage msg = receivedMessage.get();
            assertNotNull(msg, "JOIN message should be received by server.");
            assertEquals("JOIN", msg.getType(), "Message type should be JOIN.");
            assertEquals(testPlayer.getUsername(), ((Player) msg.getPayload()).getUsername(), "Player username in JOIN message should match.");
        } catch (IOException e) {
            fail("IOException during test: " + e.getMessage());
        }
    }
}