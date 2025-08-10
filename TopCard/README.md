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
- **SocketGameController**: Handles server-side logic and routes messages between clients.
- **GameClient**: Connects to the server, renders the GUI, and enables player interaction.

## Communication Flow
1. The server starts and waits for client connections.
2. Clients connect and launch individual GUIs.
3. The server processes incoming messages and updates the game state.
4. All players interact in real time with synchronized game updates.

## How to Launch the Game
- Build the project: `mvn clean install`. It generates TopCard-1.0-SNAPSHOT-jar-with-dependencies.jar
- Start the server service: `./run-server.sh` (or mannually with this command. Before launching, make sure that the classpath ($CP) includes all required dependencies and compiled classes. Then run: `java -cp "$CP" com.topcard.network.GameServer
  `)
- Start the client service: `./run-client_playgame.sh` (Alternatively, run them manually: `for i in a b c d; do
  java -jar ./target/TopCard-1.0-SNAPSHOT-jar-with-dependencies.jar "$i" &
  done`)
- For this game, all four users need to log in and click on "Play Game"