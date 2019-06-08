package core;

import server.Response;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Main implementation of the IGameCallback interface.
 * Instance of this class should not be shared on multiple clients.
 * <p>The class has following functionalities:<ul>
 * <li>Displays secret code in server console</li>
 * <li>Sends responses to the client. Response are objects of type server.Responses and serialized.</li>
 * <li>Sends a QUIT command to the client.</li>
 * </ul>
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class GameCallback implements IGameCallback {
	
	private ObjectOutputStream stream;
	
	public GameCallback(ObjectOutputStream stream) {
		this.stream = stream;
	}
	
	/**
	 * Equivalent to <pre>
	 * response(message, Response.PRINTMESSAGE)
	 * </pre>
	 * @param message
	 */
	private void respond(String message) {
		respond(message, Response.PRINTMESSAGE);
	}
	
	/**
	 * Helper method that sends instances of server.Response object to the client.
	 * 
	 * @param message
	 * @param responseType
	 */
	private void respond(String message, int responseType) {
		try {
			stream.writeObject(new Response(message, responseType));
		} catch (IOException e) {
			System.err.println("Sorry something went wrong while sending your message. " + e.getMessage());
		}
	}

	@Override
	public void onStart(Game game) {

	}

	/**
	 * CODE GENERATION event.
	 * <p>This will prints the secret code in the server's console (not client)
	 */
	@Override
	public void onSecretCodeCreated(Game game, String secretCode) {
		System.out.printf("New code generated (%s)\n", secretCode);
	}

	
	/**
	 * ROUND STARTED event. Tells the client that the round has started.
	 */
	@Override
	public void onRoundStarted(Game game, GameRound round, Player player) {
		respond("ROUND STARTED");
	}

	/**
	 * PLAYER SIGNED UP event. Tells the client that the player has registered.
	 */
	@Override
	public void onPlayerSignedUp(Game game, GameRound round, Player player) {
		respond(String.format("Player \"%s\" successfully registered.", player.getName()));
	}

	/**
	 * GUESS ADDED event. Sends back the client's guess
	 */
	@Override
	public void onGuessAdded(GameRound round, Player player, String guess) {
		respond("You guessed " + guess);
	}

	/**
     * On Incorrect guess it sends back message that guess is higher than user input
     */
	@Override
	public void onHigherIncorrectGuess(GameRound round, Player player, String guess) {
		respond("Your guess " + guess + " is bigger than the generated number");
	}

    /**
     * On Incorrect guess it sends back message that guess is lower than user input
     */
	@Override
	public void onLowerIncorrectGuess(GameRound round, Player player, String guess) {
		respond("Your guess " + guess + " is smaller than the generated number");
	}
    /**
     * On Incorrect guess it sends back message that guess is not between 0 - 9
     */
	@Override
	public void onisInvalidRangeGuess(GameRound round, Player player, String guess) {
		respond("Please enter a number between 0 and 9");
	}

	/**
	 * PLAYER WON event. This tells the client that it has won the round. 
	 * <p>It also sends the number of guesses made by the player
	 */
	@Override
	public void onPlayerWon(GameRound round, Player player, int numOfGuesses) {
		respond("Congratulations");
	}

	/**
	 * PLAYER LOST event. 
	 * <p>This tells the client that it has lost the round. It also sends the generated number.
	 */
	@Override
	public void onPlayerLost(GameRound round, Player player, String generatedCode) {
		respond(String.format("Your maximum attempts have finished. Generated number was: %s.", generatedCode));
	}
	
	/**
	 * ROUND ENDED event. 
	 * <p>Tell the client the round has ended
	 */
	@Override
	public void onRoundEnded(Game game, GameRound round) {
		respond("ROUND ENDED");
	}

	/**
	 * PLAYER FORFEITED event. 
	 * <p>Tells the client that they have forfeited the round.
	 */
	@Override
	public void onPlayerForfeited(Game game, GameRound round, Player player) {
		respond("You have forfeited this round.");
	}
	
	/**
	 * PLAYER QUITED event. 
	 * <p>Sends a kill switch to the client telling that they've quited the game.
	 */
	@Override
	public void onPlayerQuited(Game game, Player player) {
		respond("You have chosen to quit the game", Response.QUIT);
	}
}