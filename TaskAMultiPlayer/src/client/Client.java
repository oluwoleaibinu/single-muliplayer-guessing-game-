package client;

import server.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class the represent the player's client machine. This client expects to receive serialized form of server.Response object. 
 * <p>Note: You must compile the whole project and not only this file.
 * <p>After the client deserialized the payload, it can do one of these 3 commands:<ul>
 * <li>PRINTMESSAGE - Tells the client to print message to the screen</li>
 * <li>READLINE -  Read inputs from the console and send the contents via output stream.</li>
 * <li>QUIT - Tells the client to quit the program</li></ul>
 * 
 * @author oluwole Aibinu
 *s3479719
 */
public class Client {

	// Loopback address. You can customize the host by 
	// specifying the host as the first argument in main(String[] args)
	public static final String DEFAULT_SERVER_HOST = "10.102.128.22";

	// Default port. You can use a custom port by 
	// passing the second argument into the main(String[] args)
	public static final int DEFAULT_SERVER_PORT = 61995;
	

	// You can pass 2 arguments, 
	// - the first one is the host name
	// - the second is the port number to listen from
	public static void main(String[] args) {
		
		int port = getPortNumber(args);
		String host = getHostAddress(args);
		
		Socket socket = null;
		ObjectInputStream objectInputStream = null;
		PrintWriter writer = null;
		BufferedReader console = null;
		
		try {
			
			// By default this connects to 10.102.128.22:61995
			socket = new Socket();
			socket.connect(new InetSocketAddress(host, port), 60 * 1000);
			socket.setSoTimeout(60 * 1000);
			System.out.println("Connected to " + host + " on port " + port);
			
			// client will receive an instance of server.Response object in serialized form
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			
			// this client will send normal sequence of characters to the server
			writer = new PrintWriter(socket.getOutputStream(), true);
			console = new BufferedReader(new InputStreamReader(System.in));
			
			String line;
			Response command;
			
			do {
				command = (Response) objectInputStream.readObject();
				
				// print response's message and read input from console
				if (command.getType() == Response.READLINE) {
					System.out.print(command.getMessage());
					line = console.readLine();
					writer.println(line);
				}

				// Print messages as usual
				else {
					System.out.println(command.getMessage());
				}
			}

			// server may send a QUIT command. If that's the case, then exit the loop
			while(command.getType() != Response.QUIT);
			
		}
		// Most likely will throw this exception in case you only compiled this file
		// Remember, compile the whole project
		catch (ClassNotFoundException e) {
			System.err.println("The command data sent by the server cannot be read by this client. " + e.getMessage());
		}
		// Server not started or you are not connected
		catch (UnknownHostException e) {
			System.err.printf("Server %s:%d cannot be found\n", host, port);
		}
		// Something cuts the connection
		catch (EOFException e) {
			System.err.print("Client disconnected. ");
		} 
		// Other exceptions
		catch (IOException e) {
			System.err.println("Sorry, something went wrong. " + e.getMessage());
		}
		finally {
			// Close and display a message
			try {
				if (console != null) console.close();
				if (writer != null) writer.close();
				if (objectInputStream != null) objectInputStream.close();
				if (socket != null) socket.close();
				
				System.out.println("Connection closed.");
				
			}
			catch (IOException e) {
				System.err.println("Sorry, something went wrong while closing the connection. " + e.getMessage());
			}
		}
	}
	
	/**
	 * Gets the host address from command line arguments
	 * Host address must be the first command line argument
	 * 
	 * @param args
	 * @return
	 */
	public static String getHostAddress(String[] args) {
		String address = DEFAULT_SERVER_HOST;
		
		if (args.length < 1) {
			return address;
		}
		
		address = args[0];
		return address;
	}
	
	
	/**
	 * Gets the main port number from command line arguments.<br/>
	 * The main port number must be the second command line argument
	 * 
	 * @param args
	 * @return int
	 */
	public static int getPortNumber(String[] args) {
		int portNumber = DEFAULT_SERVER_PORT;
		
		if (args.length < 2) {
			return portNumber;
		}
		
		try {
			portNumber = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("%s is not a valid port number", args[0]);
		}
		
		return portNumber;
	}
}