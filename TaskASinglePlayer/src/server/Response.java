package server;

import java.io.Serializable;

/**
 * Immutable data wrapper class that will contain command types and messages. 
 * Server will send instances of this class to the client. 
 * The client will read and  perform operations based on the contents of this object.
 * 
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class Response implements Serializable {

	private static final long serialVersionUID = 4983949626381604239L;
	
	// Tells the client to print the message in the console
	public static final int PRINTMESSAGE = 0;
	
	// Tells the client to scanner console input
	public static final int READLINE = 1;
	
	// Tells the client to disconnect
	public static final int QUIT = 2;
	
	// Store message content here
	public String message;
	
	public int type = PRINTMESSAGE;
	
	public Response(String message) {
		this(message, PRINTMESSAGE);
	}
	
	public Response(String message, int type) {
		this.message = message;
		this.type = type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getType() {
		return type;
	}
	
	
	// Convenience methods to create 'Response' objects
	
	
	public static Response message(String message) {
		return new Response(message, PRINTMESSAGE);
	}
	
	public static Response readLine(String message) {
		return new Response(message, READLINE);
	}
	
	public static Response quit(String message) {
		return new Response(message, QUIT);
	}
}
