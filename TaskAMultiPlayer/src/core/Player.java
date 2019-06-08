package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Player entity. Client status will reflect into this class
 * 
 * @author oluwole Aibiu
 *s3479719
 */
public class Player implements Comparable<Player> {
	
	public enum PlayerStatus {
		NOT_STARTED,
		STARTED,
		PLAYING,
		WON,
		LOST,
		FORFEITED,
		CHOSEN_TO_CONTINUE,
		QUITED
	}
	
	private String name;
	private List<String> guesses = new ArrayList<>();
	private String lastGuess = null;
	private PlayerStatus status = PlayerStatus.NOT_STARTED;
	
	public Player(String name) {
		this.name = name;
	}
	
	/**
	 * Empty player guesses. Guesses are usually emptied 
	 * before each round starts, including the first round
	 */
	public void resetGuesses() {
		guesses = new ArrayList<>();
		lastGuess = null;
	}
	
	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}
	
	public List<String> getGuesses() {
		return guesses;
	}
	
	public String getLastGuess() {
		return lastGuess;
	}
	
	/**
	 * Add player guess. 
	 * Every time you call this method, the last guess will be updated
	 * 
	 * @param guess
	 */
	public void addGuess(String guess) {
		guesses.add(guess);
		lastGuess = guess;
	}
	
	public int getNumGuesses() {
		return guesses.size();
	}
	
	/**
	 * Removes all guesses
	 */
	public void clearAllGuesses() {
		guesses.clear();
	}
	
	/**
	 * Convenience method to check if player has won in a given round
	 * 
	 * @param round
	 * @return
	 */
	public boolean hasWon(GameRound round) {
		return round.hasWinner(this);
	}
	
	/**
	 * Convenience method to check if player has lost in a given round
	 * @param round
	 * @return
	 */
	public boolean hasLost(GameRound round) {
		return round.hasLoser(this);
	}
	
	/**
	 * Convenience method to check if player has forfeited the round.
	 * @param round
	 * @return
	 */
	public boolean hasForfeited(GameRound round) {
		return round.hasForfeited(this);
	}

	@Override
	public int compareTo(Player o) {
		return getNumGuesses() - o.getNumGuesses();
	}
}
