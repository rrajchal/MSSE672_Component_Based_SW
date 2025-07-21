package com.topcard.network;

import java.io.Serializable;

/**
 * Represents a serializable message used for communication between client and server.
 * Can carry various game-related instructions and data payloads.
 */
public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String type;
    private final Object payload;

    public GameMessage(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
