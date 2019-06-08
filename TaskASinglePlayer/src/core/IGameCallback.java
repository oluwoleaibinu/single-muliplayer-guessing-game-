package core;

/**
 * Series of game events. 
 * <p>Methods of this interface will be called in by the core.GameManager class
 * @author Oluwole Aibinu
 *S3479719
 */
public interface IGameCallback {

	void onStart(Game game);

	/**
	 * SECRET CODE GENERATED event
	 */
	void onSecretCodeCreated(Game game, String secretCode);

	/**
	 * ROUND STARTED event
	 */
	void onRoundStarted(Game game, GameRound round, Player player);

	/**
	 * PLAYER SIGNED UP event
	 */
	void onPlayerSignedUp(Game game, GameRound round, Player player);

	/**
	 * GUESS ADDED event
	 */
	void onGuessAdded(GameRound round, Player player, String guess);

	/**
	 * INCORRECT GUESS event
	 */
	void onHigherIncorrectGuess(GameRound round, Player player, String guess);

	/**
	 * INCORRECT GUESS event
	 */
	void onLowerIncorrectGuess(GameRound round, Player player, String guess);

	/**
	 * INVALID GUESS event
	 */
	void onisInvalidRangeGuess(GameRound round, Player player, String guess);

	/**
	 * PLAYER WON event
	 */
	void onPlayerWon(GameRound round, Player player, int numOfGuesses);

	/**
	 * PLAYER LOST event
	 */
	void onPlayerLost(GameRound round, Player player, String secretCode);

	/**
	 * ROUND ENDED event
	 */
	void onRoundEnded(Game game, GameRound round);

	/**
	 * PLAYER FORFEITED event
	 */
	void onPlayerForfeited(Game game, GameRound round, Player player);

	/**
	 * PLAYER QUITED event
	 */
	void onPlayerQuited(Game game, Player player);
}
