singleplayer-server: compile-singleplayer
	cd TaskASinglePlayer && java -cp bin server.SinglePlayerServer

singleplayer-client: compile-singleplayer
	cd TaskASinglePlayer && java -cp bin client.Client

multiplayer-server: compile-multiplayer
	cd TaskAMultiPlayer && java -cp bin server.MultiPlayerServer

multiplayer-client: compile-multiplayer
	cd TaskAMultiPlayer && java -cp bin client.Client

compile: compile-singleplayer compile-multiplayer

compile-singleplayer: 
	javac -d TaskASinglePlayer/bin TaskASinglePlayer/src/*/*.java

compile-multiplayer:
	javac -d TaskAMultiPlayer/bin TaskAMultiPlayer/src/*/*.java