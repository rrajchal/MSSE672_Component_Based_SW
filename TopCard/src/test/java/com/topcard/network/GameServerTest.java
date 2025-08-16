package com.topcard.network;

import com.topcard.domain.Player;
import com.topcard.network.game.GameMessage;
import com.topcard.network.game.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GameServerTest {

    private GameServer server;

    @BeforeEach
    void setup() {
        server = new GameServer();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSendAllBroadcastsToClients() throws IOException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ObjectOutputStream out1 = mock(ObjectOutputStream.class);
        ObjectOutputStream out2 = mock(ObjectOutputStream.class);

        GameMessage message = new GameMessage("TEST_MESSAGE", "payload");

        // Simulate connected clients
        var clientOutputsField = server.getClass().getDeclaredField("clientOutputs");
        clientOutputsField.setAccessible(true);
        List<ObjectOutputStream> mockOutputs = (List<ObjectOutputStream>) clientOutputsField.get(server);
        mockOutputs.add(out1);
        mockOutputs.add(out2);

        // Manually trigger sendAll
        var sendAllMethod = server.getClass().getDeclaredMethod("sendAll", GameMessage.class);
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
        Field playersField = GameServer.class.getDeclaredField("connectedPlayers");
        playersField.setAccessible(true);
        List<Player> players = (List<Player>) playersField.get(server);

        Player mockPlayer = new Player();
        mockPlayer.setUsername("TestUser");
        mockPlayer.setPoints(100);

        players.add(mockPlayer);

        assert players.size() == 1;
        assert players.get(0).getUsername().equals("TestUser");
    }
}
