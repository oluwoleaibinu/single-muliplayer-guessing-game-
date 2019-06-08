package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the game round. One game can have many rounds. 
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class GameRound {
	
	public static final int MAX_ATTEMPTS = 4;
	
	/**
	 * Store secret code here. Each round will have 1 code
	 */
	private String secretCode;
	
	/**
	 * For single player game, each round will have 1 player
	 */
	private Player player;
	
	/**
	 * Because there's only 1-to-1 relation between player and the GameRound,
	 * we can use these field to determine the outcome of the round.
	 */
	private boolean isPlayerWon = false;
	private boolean isPlayerLost = false;

	List<String> guesses = new ArrayList<>();
	private boolean hasEnded = false;
	

	public GameRound(String secretCode) {
		this.secretCode = secretCode;
	}
	
	
	// getter and setter for isPlayerWon
	
	public boolean isPlayerWon() {
		return isPlayerWon;
	}
	public void setPlayerWon(boolean isPlayerWon) {
		this.isPlayerWon = isPlayerWon;
	}


	// getter and setter for isPlayerLost
	
	public boolean isPlayerLost() {
		return isPlayerLost;
	}
	public void setPlayerLost(boolean isPlayerLost) {
		this.isPlayerLost = isPlayerLost;
	}

	/**
	 * Set's the player for this round. 
	 * Round can be created without participating player
	 * <p>When you set an existing player, their guesses will be removed automatically
	 * @param player
	 */
	public void setPlayer(Player player) {
		this.player = player;
		player.clearAllGuesses();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	
	public String getSecretCode() {
		return secretCode;
	}
	
	/**
	 * Handles how guesses will be recorded. 
	 * By adding new guess you can change the state of this round (WINNING, LOSING, FORFEIT)
	 * 
	 * @param player
	 * @param guess
	 */
	public void addGuess(Player player, String guess) {
		// if player won, no need to add guess
		if (hasWinner(player) || hasLoser(player)) {
			return;
		}
		
		// add guess
		if (player != null && player.getNumGuesses() < MAX_ATTEMPTS) {
			player.addGuess(guess);
			guesses.add(guess);
		}
		
		// if guess if correct, player won
		if (isGuessMatch(guess)) {
			setPlayerWon(true);
			return;
		}
		
		// if the 4th (this guess) is incorrect, player lost
		if (player.getNumGuesses() >= MAX_ATTEMPTS && isGuessMatch(guess) == false) {
			setPlayerLost(true);
			return;
		}
	}
	
	public boolean hasWinner(Player player) {
		return this.player == player && isPlayerWon;
	}
	
	
	public boolean hasLoser(Player player) {
		return this.player == player && isPlayerLost;
	}
	
	
	public boolean isGuessMatch(String guess) {
		return secretCode.equals(guess);
	}

	public boolean isHigherGuess(String guess) {
		return Integer.parseInt(guess) > Integer.parseInt(secretCode);
	}

	public boolean isInvalidRangeGuess(String guess) {
		return Integer.parseInt(guess) >10 || Integer.parseInt(secretCode) <0;
	}

	public boolean isLowerGuess(String guess) {
		return Integer.parseInt(guess) < Integer.parseInt(secretCode);
	}
	
	public boolean hasEnded() {
		return hasEnded;
	}
	
	// end the game
	// If player is still playing, he/she will automatically lose the round
	/**
	 * Ends the game.
	 * By calling this method, all players who have not won this round in 
	 * this round will automatically lose.
	 */
	public void end() {
		setPlayerLost(true);
		this.hasEnded = true;
	}
}