package server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Helper class to provided utility methods such as logging.
 * 
 * @author Oluwole Aibinu
 *S3479719
 */
public class Utility {

	/**
	 * Create a logger object. The logger that is created will be file based. 
	 * If the log file doesn't exist, it will create one.
	 * 
	 * @param fileName
	 * @param className
	 * @return
	 */
	public static <T> Logger createLogger(String fileName, Class<T> className) {
		Logger logger = Logger.getLogger(className.getName());
		logger.setUseParentHandlers(false);
		
		
		FileHandler fileHandler = null;
		try {
			// handle logging through files. 'true' means append to existing log file
			fileHandler = new FileHandler(fileName, true);
			fileHandler.setLevel(Level.INFO);
			
			// use SimpleFormatter instead of default XMLFormatter
			fileHandler.setFormatter(new SimpleFormatter()); 
			
			logger.addHandler(fileHandler);
			return logger;
		} 
		catch (SecurityException e) {
			logger.log(Level.SEVERE, "Logger is not working. ", e.getMessage());
		}
		catch (IOException e) {
			logger.log(Level.SEVERE, "Logger is not working. ", e.getMessage());
		}
		
		return logger;
	}
	
}
