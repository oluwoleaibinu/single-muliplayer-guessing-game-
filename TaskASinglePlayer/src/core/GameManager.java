package core;

import core.Player.PlayerStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Classes that bridges game object to all callbacks. 
 * You can say this is the brains of the game. 
 * Each connection to the client will create a new instance of the game manager.
 * Game manager should never be shared on all clients (1 game manager per client)
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class GameManager {
	
	// To bridge the game with the callbacks, you will need the game object and the callbacks
	private Game game;
	private List<IGameCallback> cbs = new ArrayList<>();
	
	// To keep track of the client's state
	private Player currentPlayer;

	/**
	 * Game is required. GameManager should still work even without callbacks
	 * @param game
	 */
	public GameManager(Game game) {
		this.game = game;
	}

	
	// getter and setter for the game
	
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}

	
	// getter and setter for the currentPlayer
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}
	
	
	/**
	 * Adds callback
	 * @param cb
	 */
	public void addCallback(IGameCallback cb) {
		cbs.add(cb);
	}
	
	
	/**
	 * Signs up the client to the game.
	 * <p>This will trigger a onPlayerSignedUp() event.
	 * @param playerName
	 * @throws Exception
	 */
	public void setCurrentPlayerName(String playerName) throws Exception {
		Player player = game.signUpPlayer(playerName);
		setCurrentPlayer(player);
		cbs.forEach(c -> c.onPlayerSignedUp(game, null, player));
	}

	/**
	 * Starts a new round if there are no current rounds running.
	 * 
	 * A new code is created on each new round created.
	 * When every time a code is created, it will fire a onSecretCodeCreated() event.
	 * When you call this method, the player status will automatically set to STARTED 
	 * and an onRoundStarted() will be fired
	 * 
	 * @throws Exception
	 */
	public void startNextRound() throws Exception {
		GameRound round = game.getCurrentRound();
		
		// game has just begun. Create new secret code
		if (round == null) {
			round = game.startNextRound();
			final String secretCode = round.getSecretCode();
			cbs.forEach(c -> c.onSecretCodeCreated(game, secretCode));
		}
		// if there are rounds and the round has already ended
		// Create new secret code
		else if (round.hasEnded()) {
			round = game.startNextRound();
			final String nextSecretCode = round.getSecretCode();
			cbs.forEach(c -> c.onSecretCodeCreated(game, nextSecretCode));
		}
		
		game.getPlayer().setStatus(PlayerStatus.STARTED);
		
		for (IGameCallback c : cbs) {
			c.onRoundStarted(game, round, currentPlayer);
		}
	}
	
	/**
	 * Add player's guess to the round. The outcome of the round will be determined by this method.
	 * <p>If player has added a guess, a onGuessAdded() event is fired. 
	 * However if player forfeits by pressing "f", the onForfeit() 
	 * event is called instead without calling the onGuessAdded(). 
	 * <p>If guess is not correct, the onIncorrectGuess() is fired, 
	 * but it is correct, the player wins and the onPlayerWon() event will be fired.
	 * <p>After the player made its 4th incorrect guess, the player
	 * automatically loses and the onPlayerLost() is fired.
	 * <p>Every time a guess is added, this method will check if 
	 * the guess made has caused the round to be ended (eg. if all players have lost, won or forfeited).
	 * 
	 * @param guess
	 */
	public void addGuess(String guess) {
		GameRound round = game.getCurrentRound();
		
		// add guess
		currentPlayer.setStatus(PlayerStatus.PLAYING);
		round.addGuess(currentPlayer, guess);
		cbs.forEach(c -> c.onGuessAdded(round, currentPlayer, guess));

		if(!round.isInvalidRangeGuess(guess)){
		// guess is incorrect
		if (! round.isGuessMatch(guess)) {
		    if(round.isHigherGuess(guess)){
			cbs.forEach(c -> c.onHigherIncorrectGuess(round, currentPlayer, guess));
		    }else if (round.isLowerGuess(guess)){
                cbs.forEach(c -> c.onLowerIncorrectGuess(round, currentPlayer, guess));
            }
		}
		}else{
			cbs.forEach(c -> c.onisInvalidRangeGuess(round, currentPlayer, guess));
		}
		
		// guess was correct
		if (playerWins()) {
			currentPlayer.setStatus(PlayerStatus.WON);
			cbs.forEach(c -> c.onPlayerWon(round, currentPlayer, currentPlayer.getNumGuesses()));
		}
		
		// the 4th guess was incorrect (player loses)
		if (playerLoses()){
			currentPlayer.setStatus(PlayerStatus.LOST);
			cbs.forEach(c -> c.onPlayerLost(round, currentPlayer, round.getSecretCode()));
		}
		
		// player might end the round by winning or losing
		checkRoundEnded(round);
	}
	
	/**
	 * Helper method to check if round has ended.
	 * @param round
	 */
	private void checkRoundEnded(GameRound round) {
		Player player = round.getPlayer();
		
		if (player.hasWon(round) || player.hasLost(round)) {
			round.end();
		}
	}
	
	/**
	 * Force the round to end. In turn, the event onRoundEnded() will be fired
	 */
	public void endCurrentRound() {
		GameRound round = game.getCurrentRound();
		if (! round.hasEnded()) {
			round.end();
		}
		
		for (IGameCallback c : cbs) {
			c.onRoundEnded(game, round);
		}
	}
	
	/**
	 * Convenience method to check if current player wins the current round
	 * @return
	 */
	public boolean playerWins() {
		return currentPlayer.hasWon(game.getCurrentRound());
	}
	
	/**
	 * Convenience method to check if current player loses the current round
	 * @return
	 */
	public boolean playerLoses() {
		return currentPlayer.hasLost(game.getCurrentRound());
	}
	
	public void removeCurrentPlayer() {
		game.removePlayer();
	}
	
	/**
	 * Player has chosen to quit. This fires the onPlayerQuited() event
	 */
	public void quitPlayer() {
		currentPlayer.setStatus(PlayerStatus.QUITED);
		cbs.forEach(c -> c.onPlayerQuited(game, currentPlayer));
		removeCurrentPlayer();
	}
	
	/**
	 * Check if current game round has ended
	 * @return
	 */
	public boolean isRoundEnded() {
		return game.getCurrentRound().hasEnded();
	}
	
	/**
	 * Convenience method to check if player decided to quit or continue. 
	 * If players have not started or still 
	 * adding their guesses, this returns false.
	 * 
	 * @return
	 */
	public boolean isPlayerChosenToContinueOrQuit() {
		Player player = game.getPlayer();
		
		switch (player.getStatus()) {
			case NOT_STARTED:
			case STARTED:
			case LOST:
			case PLAYING:
			case WON:
				return false;
			case QUITED:
			case CHOSEN_TO_CONTINUE:
			default: 
				return true;
		}
	}
	
	/**
	 * Check if player status is CHOSEN_TO_CONTINUE
	 */
	public void chooseToContinue() {
		currentPlayer.setStatus(PlayerStatus.CHOSEN_TO_CONTINUE);
	}
	

}

