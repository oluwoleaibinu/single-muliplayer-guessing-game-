package server;

import core.Game;
import core.GameCallback;
import core.GameCallbackLogger;
import core.GameManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class that orchestrates how the game will run.
 * You can treat this as the main controller of the game project.
 * 
 * <p>This class will run in a separate thread and will use synchronization 
 * to sync with other threads running this ServerProcess class.
 * 
 * <p>The game will use serialization and {@link ObjectOutputStream}.
 * This allows for multi-line printing in the client side and make sending 
 * commands to the client possible.
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class ServerProcess implements Runnable {
	
	/**
	 * LOCK object that will be shared among all threads running this class
	 */
	public static final Object LOCK = new Object();
	
	public static final int WAITING_TIME_SECONDS = 20;
	
	// required fields
	private Game game;
	private Socket socket;
	private IServerCallback cb;
	
	// derived fields
	private ObjectOutputStream stream;
	private BufferedReader reader;
	private GameManager manager;
	
	
	public ServerProcess(Game game, Socket socket, IServerCallback callback, GameCallbackLogger gameLoggerCallback) throws IOException {
		this.game = game;
		this.socket = socket;
		this.cb = callback;
		this.stream = new ObjectOutputStream(socket.getOutputStream());
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.manager = new GameManager(game);
		
		GameCallback cb = new GameCallback(stream);
		manager.addCallback(cb);
		manager.addCallback(gameLoggerCallback);
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public Game getGame() {
		return game;
	}
	
	public GameManager getGameManager() {
		return manager;
	}
	
	
	@Override
	public void run() {
		
		try {
			
			// ask for player name
			if (manager.getCurrentPlayer() == null) {
				String name = readline("Enter your name: ", true);
				manager.setCurrentPlayerName(name);
			}

			
			// Begin the synchronized block because we will use wait() 
			// and notifyAll() in this block of code
			synchronized (LOCK) {
				
				// first 2 players will enter this block, waiting for the 3rd player
				// the third player will not enter this code
				// if there is no 3rd player within 20 seconds, wait() will return.
				if (manager.getNumPlayers() < Game.MIN_PLAYERS) {
					reply("Waiting for other players...");
					LOCK.wait(WAITING_TIME_SECONDS * 1000);
				}
				
				// only the third and the next players will enter this block of code
				// it notifies other waiting players, that they have joined
				if (manager.getNumPlayers() >= Game.MIN_PLAYERS) {
					reply("minimum number of players needed fulfilled");
					LOCK.notifyAll();
				}
			}
			
			
			// only 1 person allowed to start a new round
			synchronized (LOCK) {
				manager.startNextRound();
				
				// in case the round had already started, 
				// join the player in the current round
				manager.joinCurrentPlayer();
			}
			
			
			do {
				
				// Enter guess
				String guess = readline("Enter your guess: ");
				manager.addGuess(guess);
				
				// Player forfeits if it enters 'f'
				if (manager.isCurrentPlayerForfeited()) {
					break;
				}
				
				// player wins if it guessed the code correctly
				// otherwise if didnt guess by the 4th time, the player loses
				if (manager.playerWins() || manager.playerLoses()) {
					break;
				}
				
			} while (true);
			
			
			// Use synchronized block because we are using wait() and notify() methods
			synchronized (LOCK) {
				// if round has not ended, wait for others to finish until they forfeit, win or lose
				if ( ! manager.isRoundEnded()) {
					reply("Wait for other players to finish...");
					LOCK.wait();
				}
				else {
					// tells other thread that we've finished
					LOCK.notifyAll();
				}
			}
			
			// when the player reached this point means other players 
			// have won, lost or forfeited
			manager.endCurrentRound();
					
			// Ask to continue or quit
			boolean shouldContinue = continueOrQuit("Press (p) to continue to play, or (q) to quit: ");
			
			// again we will use wait() and notify() here, so use synchronized block
			synchronized (LOCK) {
				
				// player quits, in that case tell other 
				// clients that the client has quited
				if (!shouldContinue) {
					manager.quitPlayer();
					LOCK.notifyAll();
				}
				else {
					manager.chooseToContinue();
					
					// If there are other players that are not finished, 
					// The players that are finished will enter this block of code
					if ( !manager.isRoundEnded() || !manager.isAllOtherPlayersChosenToContinueOrQuit() ) {
						reply("Please wait for other players to finish before next round begins...");
						LOCK.wait();
					}
					
					// Only the last player finished will run this code
					if ( manager.isRoundEnded() && manager.isAllOtherPlayersChosenToContinueOrQuit() ) {
						LOCK.notifyAll();
					}
				}
			}
			
			// if player decided to continue, run the process all over again
			if (shouldContinue) {
				run(); // recursive call
			}
			
			
			// when player reach this point, means player had quit
			
			
		} 
		// in case an IOException occurred, fire onException() event
		catch (IOException e) {
			cb.onException(this, e);
		} 
		// in case an Exception occurred, fire onException() event
		catch (Exception e) {
			cb.onException(this, e);
		} 
		// close the process
		finally {
			close();
		}
	}
	
	/**
	 * Ask client if they want to continue or not. 
	 * Will ask again until 'p' or 'q' is entered
	 * 
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public boolean continueOrQuit(String message) throws IOException {
		String reply = readline(message, true);
		reply = reply.trim().toLowerCase();
		if (reply.equals("p")) {
			return true;
		}
		else if (reply.equals("q")) {
			return false;
		}
		else {
			sendError("Please enter 'p' or 'q'.");
			return continueOrQuit(message);
		}
	}

	/**
	 * Close connection
	 */
	public void close() {
		manager.removeCurrentPlayer();
		
		try {
			if (stream != null) stream.close();
			if (reader != null) reader.close();
			if (socket != null) socket.close();
			
			cb.onClientDisconnected(null, socket, this);
			
		} catch (IOException e) {
			cb.onException(this, e);
		}
	}
	
	
	/**
	 * Helper method to reply messages to the client. 
	 * Uses serialization to send objects to the client. 
	 * The client then unpacks the contents of the serialized object
	 * 
	 * @param message
	 * @throws IOException
	 */
	private void reply(String message) throws IOException {
		Response response = Response.message(message);
		stream.writeObject(response);
		cb.onSendResponse(this, response);
	}
	
	/**
	 * Overload of readline(String, boolean) method
	 * 
	 * @param message
	 * @return
	 * @throws IOException
	 */
	private String readline(String message) throws IOException {
		return readline(message, false);
	}

	/**
	 * Helper methods that asks users for input. 
	 * You can pass a message as first argument, and boolean value 
	 * for the second argument to check if input is required. 
	 * If the client sends an empty string, the method will recursively 
	 * call itself again until input sent is not an empty string 
	 * 
	 * @param message
	 * @param isRequired
	 * @return
	 * @throws IOException
	 */
	private String readline(String message, boolean isRequired) throws IOException {
		Response response = Response.readLine(message);
		stream.writeObject(response);
		cb.onSendResponse(this, response);
		
		String line = reader.readLine();
		cb.onClientReply(this, line);
		
		if (isRequired && line.trim().isEmpty()) {
			sendError("Please enter non empty input.");
			return readline(message, isRequired);
		}
		
		return line.trim();
	}
	
	/**
	 * Send a message to the client. Message is prepended with "ERROR: "
	 * 
	 * @param message
	 * @throws IOException
	 */
	private void sendError(String message) throws IOException {
		Response response = Response.message("ERROR: " + message);
		stream.writeObject(response);
		cb.onSendResponse(this, response);
	}
}
