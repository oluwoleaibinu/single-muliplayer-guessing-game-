package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Game class represents the state of the whole game. Game is played via 
 * rounds ({@link GameRound}) and can have multiple players ({@link Player}).
 * <p>Note: This class does not make any calls to the GameCallback interface.
 * That functionality is handled via {@link GameManager} 
 * @author Oluwole Aibinu
 *S3479719
 */
public class Game {
	
	public static final int MAX_PLAYERS = 6;
	public static final int MIN_PLAYERS = 3;
	
	/**
	 * Many rounds per game
	 */
	List<GameRound> rounds = new ArrayList<>();
	private GameRound currentRound;
	
	/**
	 * Many players per game
	 */
	private List<Player> players = new ArrayList<>();

	public Game() {
		
	}
	
	public void start() {
		
	}

	
	/**
	 * Helper method that generates a new secret code.
	 * <p>A code can have digits from 0-9 .
	 *
	 * @return
	 */
	public String createSecretCode() {
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
	public void removePlayer(Player player) {
		players.remove(player);
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
		
		if (players.size() == 0) {
			throw new Exception("There are currently no players available");
		}
		
		// create code
		String guessCode = createSecretCode();

		// start new round
		currentRound = new GameRound(guessCode);
		
		// add players
		for (Player p : players) {
			p.resetGuesses();
			currentRound.addPlayer(p);
		}
		
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
	public synchronized Player signUpPlayer(String playerName) throws Exception {
		if (getPlayers().size() == MAX_PLAYERS) {
			try{}
			catch(Exception e){
				System.err.print("Cannot add more than " + MAX_PLAYERS + " players");
			}
		}
		
		Player player = new Player(playerName);
		players.add(player);
		
		return player;
	}

	// get players in current round
	public List<Player> getPlayers() {
		return players;
	}
}









