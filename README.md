# single-muliplayer-guessing-game-
The game is implemented with separate client and server machines. Implementation--> (one player per client), and the server administers the game. Single-player game: one client communications with the server.Multiplayer:  the server handles several clients simultaneously.
Requirements
- Java SE 1.8
- server machines ( Linux)
Program Structure
Single Player
The server generates a number between 0 and 9. The client’s then guess the number (up to a maximum of 4 guesses).
• If the client guesses the number correctly, then the server shows “Congratulation”.
• If the client fails to guess the number, for each incorrect guess, the client gets a clue:
• After four incorrect guesses, the server then shows the correct guess and prompt user to either continue playing or quit the game
Type of Socket Used: TCP for client to server connection.
Multiplayer Player
The server maintains a lobby queue. Clients register with the server (using their first
name) to be added to the lobby queue before they can play. Clients can register at any time.
The game is played in rounds. At the start of each round, the server takes the first three clients from the lobby (or all the clients if there are less than three clients in the lobby) and starts the round.
• First the server announces the name of 3 participants.
• Each player can guess at any time (with their number of guesses tracked by the server).
Once all plays have either:
• Correctly guessed the generated number,
• Reached their maximum guess of 4,
The server announces to all the clients the number of guesses for each client.
• The players that choose to play again are added back into the end of the lobby queue, and the process repeats with a new round.
Type of Socket Used (TCP and UDP): TCP is connection from client to server while UDP protocol was used as the different users’ needs to be broadcast information when some other player wins the game, or any new players join the game.
Overview of files
● Core - contains code to manage the game state
○ Game.java - the state of the game is reflected in this class
○ GameRound.java - a game can have multiple rounds.
○ Player.java - class that represents the player
○ GameManager.java - model class that bridges the game to the GameCallback
○ GameCallback.java - contains series of game event methods (eg. onGuessAdded() )
○ GameCallbackImpl.java - used to send replies to the client based on events
● Server - contains code to manage server processes
○ MultiPlayerServer/SinglePlayerServer.java - contains the main() method
○ ServerProcess.java - main controller class. Each client will have 1 instance
○ ServerCallback.java - has network related event (eg. onClientConnected() )
○ Response.java - Wraps server’s messages/commands and sent to clients as object
● Client
○ Client.java - contains the main() method.


How to Launch
Available Commands
Go up to the path where the folder TaskASinglePlayer and TaskAMultiPlayer are present and run the below make commands to execute:
Single-player version
- make singleplayer-server (to start the server)
-make singleplayer-client (to start the client)
By default, client connects to port 61995 and to server ip address of 10.102.128.22
Alternatively, client can specify port number and server address he wants to connect to by running the following command
- make singleplayer-client <server ipaddress> <port number>
Multi-player version
- make multiplayer-server (to start the server)
-make multiplayer-client (to start the client)
By default, client connects to port 61995 and to server ip address of 10.102.128.22
Alternatively, other clients can specify port number and server address he wants to connect to by running the following command
- make multiplayer-client <server ipaddress> <port number>

Logs
All game and communication logs are recorded for singleplayer and multiplayer respectively.
To view,use the following command
-vi communications.log (shows logs of all server to client communication)
-vi game.log (shows logs of all the game activities)
