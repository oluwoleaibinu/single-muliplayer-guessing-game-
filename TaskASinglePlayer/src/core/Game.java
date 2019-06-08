package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Game class manages the state of the game. 
 * <p>During the life cycle of the server, only 1 instance of game class is 
 * created. For this project, a game can only have 1 player but can have 
 * multiple game rounds. 
 * <p>This class will not make any calls to the callbacks. That functionality 
 * is handled via GameManager class
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class Game {
	List<GameRound> rounds = new ArrayList<>();
	private GameRound currentRound;
	
	private Player player;

	public Game() {}

	/**
	 * Helper method that generates a new secret code.
	 * <p>A random code can have digits from 0-9.
	 * @return
	 */
	public String generateRandomNumber( ) {

		int digit = getRandomNum(0, 9);
		String digitString = Integer.toString(digit);

		return digitString;
	}
	
	
	/**
	 * Convenience helper to create random numbers
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public int getRandomNum(int min, int max) {
		return min + (int)(Math.random() * (max - min + 1));
	}
	
	/**
	 * Gets the current round. 
	 * <p>If there are no calls of startNextRound() method before this method 
	 * is called, this returns null.
	 * 
	 * @return
	 */
	public GameRound getCurrentRound() {
		return currentRound;
	}
	
	/**
	 * Sets the current player to null
	 */
	public void removePlayer() {
		player = null;
	}
	
	
	/**
	 * End the current round and start a new round. 
	 * <p>A new code is created before creating a new round
	 * 
	 * @return
	 * @throws Exception
	 */
	public GameRound startNextRound() throws Exception {
		if (currentRound != null && !currentRound.hasEnded() ) {
			throw new Exception("Current round has not yet ended");
		}
		
		if (player == null) {
			throw new Exception("Player is not set");
		}
		
		// generate random code
		String secretCode = generateRandomNumber();
		
		
		// start new round
		currentRound = new GameRound(secretCode);
		
		// set player
		player.resetGuesses();
		currentRound.setPlayer(player);
	
		rounds.add(currentRound);
		
		return getCurrentRound();
	}
	
	/**
	 * Creates new instance of Player and sets it as the current player in this game
	 * 
	 * @param playerName
	 * @return
	 * @throws Exception If player is already set
	 */
	public Player signUpPlayer(String playerName) throws Exception {
		if (player != null) {
			throw new Exception("Player already signed up");
		}
		
		player = new Player(playerName);
		return player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}