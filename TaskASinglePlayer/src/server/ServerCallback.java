package server;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of IServerCallback interface.
 * Will use the console and loggers to print and log message events. 
 * For convenience of the developer, each public override method will have a corresponding
 * private methods so that the method names will be logged. 
 * This way we could identify what events have happened.
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class ServerCallback implements IServerCallback {

	private Logger logger;

	/**
	 * This implementation required an instance of logger
	 *
	 */
	public ServerCallback(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * SERVER STARTED event.
	 */
	@Override
	public void onServerStarted(SinglePlayerServer server, int port) {
		SERVER_STARTED("Server started on port " + port);
	}
	private void SERVER_STARTED(String message) {
		System.out.println("SERVER STARTED - " + message);
		logger.log(Level.INFO, message);
	}
	

	/**
	 * CLIENT CONNECTED event
	 */
	@Override
	public void onClientConnected(SinglePlayerServer server, Socket socket) {
		CLIENT_CONNECTED("Client " + socket.getInetAddress().toString() + " connected.");
	}
	private void CLIENT_CONNECTED(String message) {
		System.out.println("CLIENT CONNECTED - " + message);
		logger.log(Level.INFO, message);
	}
	

	/**
	 * SERVER RESPONDED event
	 */
	@Override
	public void onSendResponse(ServerProcess process, Response response) {
		SERVER_RESPONDED("Server: " + response.getMessage());
	}
	private void SERVER_RESPONDED(String message) {
		System.out.println("SERVER RESPONDED - " + message);
		logger.log(Level.INFO, message);
	}

	/**
	 * CLIENT REPLIED event
	 */
	@Override
	public void onClientReply(ServerProcess process, String clientReply) {
		String address = process.getSocket().getInetAddress().toString();
		CLIENT_REPLIED(String.format("%s: %s\n", address, clientReply));
	}
	private void CLIENT_REPLIED(String message) {
		System.out.println("CLIENT REPLIED - " + message);
		logger.log(Level.INFO, message);
	}

	/**
	 * CLIENT DISCONNECTED event
	 */
	@Override
	public void onClientDisconnected(SinglePlayerServer server, Socket socket, ServerProcess process) {
		String address = socket.getInetAddress().toString();
		CLIENT_DISCONNECTED(String.format("CLIENT DISCONNECTED: %s disconnected.", address));
	}
	private void CLIENT_DISCONNECTED(String message) {
		System.out.println("CLIENT DISCONNECTED - " + message);
		logger.log(Level.INFO, message);
	}

	
	/**
	 * EXCEPTION THROWN event
	 */
	@Override
	public void onException(ServerProcess process, Exception e) {
		EXCEPTION_THROWN(e);
	}
	
	public void EXCEPTION_THROWN(Exception e) {
		System.err.println("EXCEPTION: " + e.getMessage());
		e.printStackTrace();
	}
	
}
