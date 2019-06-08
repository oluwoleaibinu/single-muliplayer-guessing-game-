# single-muliplayer-guessing-game-
The game is implemented with separate client and server machines. Implementation--> (one player per client), and the server administers the game. Single-player game: one client communications with the server.Multiplayer:  the server handles several clients simultaneously.

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
