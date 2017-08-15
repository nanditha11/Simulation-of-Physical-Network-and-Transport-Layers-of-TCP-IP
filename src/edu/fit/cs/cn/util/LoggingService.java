package edu.fit.cs.cn.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * <b>LoggingService.java</b><BR>
 * Usage: This class maintains the node map and transmission queue.<BR> 
 * Also offers methods to convert data from int to byte and vice versa.<BR>
 * @author Group7
 */
public class LoggingService {

	//Logger to write the key messages of the project execution status to a log file	
	public static Logger LOGGER = null;
	//Log file name formatter
	public static SimpleDateFormat fileNameformatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    /**<BR>
     * 
	 * <strong>init</strong><BR>
	 * Usage: Initialize the logger with custom handlers.<BR> 
	 * @param Logger Global Name
	 * @return void
	 * 
	 */	
	private static void init(String globalLoggerName){
		
		//File handler to write logs to log files
    	FileHandler fileHandler;
    	
    	//Console handler to write logs to console
    	ConsoleHandler consoleHandler;
    	
    	try{
    		
    		//Create logger with desired name
    		LOGGER = Logger.getLogger(globalLoggerName);

    		/**
    		 * Customize the Logging service with a file handler, console handleer and a custom formatter
    		 */
    		
    		//Add file handler
	    	Date now = new Date();	    	
	    	fileHandler = new FileHandler(Constants.LOG_FILES_PATH+Constants.A2_LOG_FILE_NAME_PREFIX+fileNameformatter.format(now)+Constants.A2_LOG_FILE_NAME_SUFFIX);  
	    	fileHandler.setFormatter(new LogFormatFormatter());	    	
	    	LOGGER.addHandler(fileHandler);
	    	
	    	//Add console handler
	    	consoleHandler = new ConsoleHandler();
	    	consoleHandler.setFormatter(new LogFormatFormatter());
	    	LOGGER.addHandler(consoleHandler);
	    	
	    	//Turn off parent handlers
	    	LOGGER.setUseParentHandlers(false);
	    	
	    	
    	}catch(Exception e){
    		System.out.println("Exception in intiliazing logger"+e.getMessage());
    		e.printStackTrace();
    	}
	}
	

    /**<BR>
     * 
	 * <strong>getLogger</strong><BR>
	 * Usage: Returns the global logger.<BR> 
	 * @param Logger Global Name
	 * @return Logger
	 * 
	 */	
	public static Logger getLogger(String globalLoggerName){
		//Check if logger is not created yet
		if(LOGGER==null)
			init(globalLoggerName);//Call init to create the logger
		return LOGGER;
		
	}
}
