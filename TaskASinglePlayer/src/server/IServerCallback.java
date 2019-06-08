package server;

import java.net.Socket;

/**
 * Series of server events.
 * These methods will be called by the SinglePlayerServer and ServerProcess classes
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
	void onServerStarted(SinglePlayerServer server, int port);
	
	/**
	 * Callback when client has established a new connection
	 * 
	 * @param server
	 * @param socket
	 */
	void onClientConnected(SinglePlayerServer server, Socket socket);
	
	/**
	 * Call back when server sends a response to the client.
	 * <p>Note the response is not a string but an object of type Response
	 * 
	 * @param process
	 * @param response
	 */
	void onSendResponse(ServerProcess process, Response response);
	
	/**
	 * Call back when server receives a reply from client
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
	void onClientDisconnected(SinglePlayerServer server, Socket socket, ServerProcess process);
	
	/**
	 * Callback when an exception occurs
	 * 
	 * @param process
	 * @param e
	 */
	void onException(ServerProcess process, Exception e);
}