package core;

import server.Response;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Main implementation of the GameCallback interface.
 * Instance of this class should not be shared on multiple clients.
 * <p>The class has following functionalities:<ul>
 * <li>Displays generated code in server console</li>
 * <li>Sends responses to the client. Response are objects of type server.Responses and serialized.</li>
 * <li>Sends a QUIT command to the client.</li>
 * </ul>
 *
 * @author Oluwole Aibinu
 *S3479719
 */
public class GameCallback implements IGameCallback {

    /**
     * Instead of stream of string objects, We will send instances
     * of {@link Response} to the client via this stream
     */
    private ObjectOutputStream stream;

    public GameCallback(ObjectOutputStream stream) {
        this.stream = stream;
    }

    /**
     * Equivalent to <pre>response(message, Response.PRINTMESSAGE)</pre>
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


    /**
     * GAME STARTED event.
     * <p>Sends a reply to the client saying the game has started.
     */
    @Override
    public void onStart(Game game) {
        respond("**GAME STARTED**");
    }

    /**
     * CODE GENERATED event.
     * <p>This will prints the generated code in the server's console (not client)
     */
    @Override
    public void onSecretCodeCreated(Game game, String code) {
        System.out.printf("New code generated (%s)\n", code);
    }

    /**
     * ROUND STARTED event. Tells the client that the round has started.
     * This announce all the players in this game
     */
    @Override
    public void onRoundStarted(Game game, GameRound round, Player player) {
        StringBuilder response = new StringBuilder("----------------ROUND STARTED----------------\n");
        for (Player p : round.getPlayers()) {
            response.append(String.format(" - %s", p.getName()));
            if (p == player) {
                response.append("(you)");
            }

            response.append("\n");
        }

        response.append("\n");

        respond(response.toString());

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

    @Override
    public void onHigherIncorrectGuess(GameRound round, Player player, String guess) {
        respond("Your guess " + guess + " is bigger than the generated number");
    }

    @Override
    public void onLowerIncorrectGuess(GameRound round, Player player, String guess) {
        respond("Your guess " + guess + " is smaller than the generated number");
    }

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
        respond("YOU WIN. Number of guesses made: " + numOfGuesses);
    }

    /**
     * PLAYER LOST event.
     * <p>This tells the client that it has lost the round. It also sends the generated code.
     */
    @Override
    public void onPlayerLost(GameRound round, Player player, String secretCode) {
        respond(String.format("You lose. Answer is %s.", secretCode));
    }

    /**
     * ROUND ENDED event.
     * <p>Tell the client the round has ended. It also announce the winners, the losers and players that forfeited
     */
    @Override
    public void onRoundEnded(Game game, GameRound round) {
        StringBuilder response = new StringBuilder("----------------ROUND ENDED----------------\n");

        // Use new reference of winners collection using functional programming (lambdas)
        // using a new reference, we can sort winners
        // without the ConcurrentModificationException
        // when we sort the original reference
        List<Player> winners = round.getWinners().stream().filter(w -> w != null).collect(Collectors.toList());
        winners.sort(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o1.compareTo(o2);
            }
        });

        // winners
        response.append("WINNERS: \n");
        if (round.getWinners().size() > 0) {
            for (int i = 0; i < winners.size(); i++) {
                response.append(String.format(" %d. %s (%d guesses)\n", i + 1, winners.get(i).getName(), winners.get(i).getNumGuesses()));
            }
        } else {
            response = new StringBuilder(response.toString().concat(" * There are no winners for this round *\n"));
        }

        // losers
        response.append("LOSERS: \n");
        if (round.getLosers().size() > 0) {
            for (Player player : round.getLosers()) {
                response.append(String.format(" - %s\n", player.getName()));
            }
        } else {
            response.append(" * There are no losers for this round *\n");
        }

        // forfeiters
        response.append("FORFEITED: \n");
        if (round.getForfeiters().size() > 0) {
            for (Player player : round.getForfeiters()) {
                response.append(String.format("- %s\n", player.getName()));
            }
        } else {
            response.append(" * There are no players forfeited for this round *");
        }

        response.append("\n");

        respond(response.toString());
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
