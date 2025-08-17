# TopCard Multiplayer Game
## Overview
This project transforms the original single-player TopCard game into a multiplayer experience using Java socket-based communication. Players connect through a client-server architecture and interact in real time.

## Features
- **Multiplayer Support**: Up to four players can join and play simultaneously.
- **Real-Time Communication**: Instant message exchange between clients and the server.
- **Dedicated GUIs**: Each player launches a separate game interface.
- **Automation Scripts**: Scripts simplify launching the server and clients for testing.
- **Database Integration**: Ensures persistent data handling during gameplay.

## Key Components
- **GameServer**: Listens for incoming client connections and manages the shared game state.
- **GameServerHandler**: A dedicated thread handler that manages the communication for a single client connection. This design prevents the server from blocking and allows it to handle multiple players concurrently.
- **SocketGameController**: Manages client-side communication with the server, sending and receiving game messages.
- **GameClient**: Connects to the server, renders the GUI, and enables player interaction.

## Communication Flow
1. The server starts and waits for client connections.
2. Clients connect and launch individual GUIs.
3. The server processes incoming messages and updates the game state.
4. All players interact in real time with synchronized game updates.

## How to Launch the Game
- Build the project: `mvn clean install`. It generates TopCard-1.0-SNAPSHOT-jar-with-dependencies.jar
- For offline game, simply run the TopCard-1.0-SNAPSHOT-jar-with-dependencies.jar. For offline game, run the servers as in the following steps. 
- Start the server service: `./run-servers.sh` (or manually with this command. Before launching, make sure that the classpath ($CP) includes all required dependencies and compiled classes. Then run: `java -cp "$CP" com.topcard.network.game.GameServer
  `)
- Start the client service: `./run-client_playgame.sh` (Alternatively, run them manually: 
  java -jar ./target/TopCard-1.0-SNAPSHOT-jar-with-dependencies.jar &`
- When any player clicks "Play Game", a 5-second countdown begins. During this time, other players may join. If fewer than four players are present when the countdown ends, the server automatically fills the remaining slots with up to three auto-generated players from the database. This ensures the game always starts with four participants, even if only one real player is available.

