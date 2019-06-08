package server;

import java.net.Socket;

/**
 * Series of server events.
 * These methods will be called by the {@link MultiPlayerServer} and {@link IServerCallback} classes
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public interface IServerCallback {
	/**
	 * Callback when server has started on particular port
	 * 
	 * @param server
	 * @param port
	 */
	void onServerStarted(MultiPlayerServer server, int port);
	
	/**
	 * Callback when client has established a connection
	 * @param server
	 * @param socket
	 */
	void onClientConnected(MultiPlayerServer server, Socket socket);
	
	/**
	 * Callback when server sends a response to the client
	 * <p>Note the response is not a string but an object of type {@link Response}
	 * 
	 * @param process
	 * @param response
	 */
	void onSendResponse(ServerProcess process, Response response);
	
	/**
	 * 
	 * @param process
	 * @param clientReply
	 */
	void onClientReply(ServerProcess process, String clientReply);
	
	/**
	 * Call back when server closes the connection to the client or 
	 * client closes the connection to the server.
	 * 
	 * @param server
	 * @param socket
	 * @param process
	 */
	void onClientDisconnected(MultiPlayerServer server, Socket socket, ServerProcess process);
	
	/**
	 * Callback when an exception occurs
	 * 
	 * @param process
	 * @param e
	 */
	void onException(ServerProcess process, Exception e);
}
