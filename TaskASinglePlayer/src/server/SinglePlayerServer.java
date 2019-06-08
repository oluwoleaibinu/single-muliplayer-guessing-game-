package server;

import core.Game;
import core.GameCallbackLogger;
import core.IGameCallback;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controls the logic and server operation of the Simple Guessing game. 
 * 
 * <p>This server can accept 1 client at a time. The rules of the game is that the 
 * player has to guess a secret code in order to win. This game is played in 
 * rounds.  Player has to enter his/her name to join. The player is then asked 
 * for number of digits to generate the secret code. The randomly generated 
 * random code which will be between 0-9. Player has
 * 4 attempts before they lose the round.
 * 
 * <p>After each round, you can choose to continue to the next round by sending a
 * 'p' or quit the game by sending 'q'. New secret codes will be created for 
 * each round.
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class SinglePlayerServer {
	
	public static final int DEFAULT_PORT = 61995;
	public static final String GAME_LOG = "game.log";
	public static final String COMMUNICATIONS_LOG = "communications.log";

	private int port;
	private Game game;
	
	// use multiple callbacks, not just one
	private List<IServerCallback> ServerCallbacks = new ArrayList<>();
	private List<IGameCallback> GameCallbacks = new ArrayList<>();
	
	// this will still be null after you instantiated this class
	private ServerSocket serverSocket;
	
	public SinglePlayerServer(int port, Game game) {
		this.port = port;
		this.game = game;
	}
	
	/**
	 * Starts the server. Behind the scenes it will launch a new instance of 
	 * ServerSocket
	 * @throws IOException
	 */
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		ServerCallbacks.forEach(c -> c.onServerStarted(this, port));
	}
	
	/**
	 * Add new server callback
	 * 
	 * @param callback
	 */
	public void addServerCallback(IServerCallback callback) {
		ServerCallbacks.add(callback);
	}
	
	
	// GETTERS
	
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public Game getGame() {
		return game;
	}
	
	public List<IGameCallback> getGameCallbacks() {
		return GameCallbacks;
	}
	
	public List<IServerCallback> getServerCallbacks() {
		return ServerCallbacks;
	}
	
	
	/**
	 * Closes the ServerSocket
	 * If closing the ServerSocket throws an Exception, 
	 * it will call all ServerCallbacks' onException() method
	 */
	public void close() {
		try {
			if (serverSocket != null) serverSocket.close();  
		} catch (IOException e) {
			ServerCallbacks.forEach(c -> c.onException(null, e));
		}
	}
	
	
	/**
	 * Program entry point
	 * 
	 * @param args You can pass a optional custom port number as the first parameter. Port defaults to 15376
	 */
	public static void main(String[] args) {
		
		// You can optionally pass the first argument as the port number
		// port number defaults to port 15376
		int port = getPortNumber(args);
		
		Game game = new Game();

		// Setup server logs
		Logger commLog = Utility.createLogger(COMMUNICATIONS_LOG, ServerCallback.class);
		ServerCallback serverCallback = new ServerCallback(commLog);

		// Game log
		Logger gameLog = Utility.createLogger(GAME_LOG, GameCallbackLogger.class);
		GameCallbackLogger gameCallbackLogger = new GameCallbackLogger(gameLog);


		// For this project, there will be 1 server callback. There may be more in the future
		SinglePlayerServer singlePlayerServer = new SinglePlayerServer(port, game);
		singlePlayerServer.addServerCallback(serverCallback);
		
		try {
			// start a server socket
			singlePlayerServer.start();
			
			ServerSocket serverSocket = singlePlayerServer.getServerSocket();
			
			// server's main loop, will accept new clients until the user kills the server
			do {
				
				Socket socket = serverSocket.accept();
				
				// keep alive
				socket.setKeepAlive(true);
				
				serverCallback.onClientConnected(singlePlayerServer, socket);
				
				// launch the game handler
				ServerProcess process = new ServerProcess(game, socket, serverCallback, gameCallbackLogger);
				process.begin();
				
			} while (true);
			
		}
		// In case there are exceptions, we can use callbacks to log them
		catch (SocketException e) {
			serverCallback.onException(null, e);
		} catch (IOException e) {
			serverCallback.onException(null, e);
		} finally {
			singlePlayerServer.close();
		}
		
	}
	
	
	/**
	 * Gets the main port number from command line arguments.<br/>
	 * The main port number must be the first command line argument
	 * 
	 * @param args
	 * @return int
	 */
	public static int getPortNumber(String[] args) {
		int portNumber = DEFAULT_PORT;
		
		if (args.length < 1) {
			return portNumber;
		}
		
		try {
			//throw exception if args[0] is not a number
			portNumber = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("%s is not a valid port number", args[0]);
		}
		
		return portNumber;
	}

}
