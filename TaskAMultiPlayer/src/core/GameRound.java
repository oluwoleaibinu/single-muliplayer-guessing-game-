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
	 * Store generated code here. Each round will have 1 unique generated code
	 */
	private String code;

	/**
	 * For this round there will be multiple players
	 */
	List<Player> players = new ArrayList<>();
	
	List<Player> winners = new ArrayList<>();
	List<Player> losers = new ArrayList<>();
	List<Player> forfeited = new ArrayList<>();
	
	List<String> guesses = new ArrayList<>();
	
	private boolean hasEnded = false;
	
	
	public GameRound(String code) {
		this.code = code;
	}
	
	/**
	 * Adds the player to this round. 
	 * If player has made guesses before, 
	 * those guesses will be cleared automatically
	 * 
	 * @param player
	 */
	public void addPlayer(Player player) {
		player.clearAllGuesses();
		players.add(player);
	}
	
	/**
	 * Gets the generated code
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

	
	/**
	 * Gets all the players in this round
	 * @return
	 */
	public List<Player> getPlayers() {
		return players;
	}

	
	/**
	 * Gets all the guesses made on this round
	 * @return
	 */
	public List<String> getGuesses() {
		return guesses;
	}

	/**
	 * Adds guesses. By adding new guess, you can 
	 * change the outcome of the players for this round
	 * (eg. WINNING, LOSING, FORFEIT)
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
		if (players.contains(player) && player.getNumGuesses() < MAX_ATTEMPTS) {
			player.addGuess(guess);
			guesses.add(guess);
		}
		
		// if guess if correct, player won
		if (isGuessMatch(guess)) {
			addWinner(player);
			return;
		}
		
		// if the 4th (this guess) is incorrect, player lost
		if (player.getNumGuesses() >= MAX_ATTEMPTS && isGuessMatch(guess) == false) {
			addLoser(player);
			return;
		}
	}
	
	/**
	 * Sets the player to be the one of the winners of this round
	 * @param player
	 */
	public void addWinner(Player player) {
		// add the player to the winners if
		// - player is registered in this round
		// - player has not yet won
		// - player has not yet lost
		if (players.contains(player) && winners.contains(player) == false && losers.contains(player) == false) {
			winners.add(player);
		}
	}
	
	/**
	 * Sets the player to be one of the losers of this round
	 * @param player
	 */
	public void addLoser(Player player) {
		// add player to the losers if
		// - player is registered in this round
		// - player has not yet won
		// - player has not yet lost
		if (players.contains(player) && losers.contains(player) == false && losers.contains(player) == false) {
			losers.add(player);
		}
	}
	
	/**
	 * Check if the player is in the list of the winners for this round.
	 * 
	 * @param player
	 * @return
	 */
	public boolean hasWinner(Player player) {
		return winners.contains(player);
	}
	
	
	/**
	 * Checks if player is in the list of the losers for this round.
	 * @param player
	 * @return
	 */
	public boolean hasLoser(Player player) {
		return losers.contains(player);
	}
	
	
	/**
	 * Checks if guess and generated code for this round is correct
	 * @param guess
	 * @return
	 */
	public boolean isGuessMatch(String guess) {
		return code.equals(guess);
	}
	
	/**
	 * Queries the list of players by name
	 * @param playerName
	 * @return
	 */
	public Player getPlayerByName(String playerName) {
		for (Player player : players) {
			if (player.getName().equals(playerName)) {
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Adds the player to the list of forfeiters for this round.
	 * @param player
	 */
	private void addForfeiter(Player player) {
		if (players.contains(player) && !losers.contains(player) && !winners.contains(player)) {
			forfeited.add(player);
		}
	}
	
	
	/**
	 * Check if the player is in the list of players forfeited
	 * @param player
	 * @return
	 */
	public boolean hasForfeited(Player player) {
		return forfeited.contains(player);
	}
	
	/**
	 * Forfeit this player in this round. 
	 * Will add guesses to the player until the player has 11 guesses
	 * @param player
	 */
	public void forfeit(Player player) {
		for (int i = player.getNumGuesses(); i < MAX_ATTEMPTS + 1; i++) {
			player.addGuess("");
		}
		addForfeiter(player);
	}
	public boolean isHigherGuess(String guess) {
		return Integer.parseInt(guess) > Integer.parseInt(code);
	}

	public boolean isInvalidRangeGuess(String guess) {
		return Integer.parseInt(guess) >10 || Integer.parseInt(code) <0;
	}

	public boolean isLowerGuess(String guess) {
		return Integer.parseInt(guess) < Integer.parseInt(code);
	}

	/**
	 * Get all winners from this round
	 * @return
	 */
	public synchronized List<Player> getWinners() {
		return winners;
	}
	
	/**
	 * Get all losers from this round
	 * @return
	 */
	public List<Player> getLosers() {
		return losers;
	}
	
	/**
	 * Get all players forfeited from this round
	 * @return
	 */
	public List<Player> getForfeiters() {
		return forfeited;
	}
	
	/**
	 * Check if this round has ended
	 * @return
	 */
	public boolean hasEnded() {
		return hasEnded;
	}
	
	/**
	 * End the game manually. All non-winner players will lose automatically.
	 */
	public void end() {
		for(Player player : players) {
			if (this.hasWinner(player) == false) {
				addLoser(player);
			}
		}
		
		this.hasEnded = true;
	}
}
