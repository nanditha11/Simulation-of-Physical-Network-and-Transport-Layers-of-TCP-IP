package edu.fit.cs.cn.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * <b>LogFormatFormatter.java</b><BR>
 * Usage: This class is custom formatter to design the messages of the logger in a desired way.<BR> 
 * 
 * @author Group7
 */
public class LogFormatFormatter extends Formatter {
	
	//Customized message format
	private static final MessageFormat messageFormat = new MessageFormat("{0,date,yyyy-mm-dd HH:mm:ss.SSS} {1} {2} {4}: {5} \n");
	
	/**
	 * Constructor
	 */
	public LogFormatFormatter() {
		super();
	}
	
    /**<BR>
     * 
	 * <strong>format</strong><BR>
	 * Usage: Formats the log record into customized message format.<BR> 
	 * @param LogRecord
	 * @return String
	 * 
	 */	
	@Override public String format(LogRecord record) {
		Object[] arguments = new Object[6];
		arguments[0] = new Date(record.getMillis());
		arguments[1] = record.getLoggerName();
		arguments[2] = record.getSourceClassName();
		arguments[3] = record.getSourceMethodName();//Thread.currentThread().getName();
		arguments[4] = record.getLevel();
		
		arguments[5] = record.getMessage();
		return messageFormat.format(arguments);
	}	
 
}

//public class Group7LogFormatter extends Formatter{
//	
//	private static final DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//	private static final String lineSep = System.getProperty("line.separator");

//	public String format(LogRecord record) {
//		String loggerName = record.getLoggerName();
//		if(loggerName == null) {
//			loggerName = "Group7";
//		}
//		StringBuilder output = new StringBuilder()
//			.append(loggerName)
//			.append("[")
//			.append(record.getLevel()).append('|')
//			.append(Thread.currentThread().getName()).append('|')
//			.append(format.format(new Date(record.getMillis())))
//			.append("]: ")
//			.append(record.getMessage()).append(' ')
//			.append(lineSep);
//		return output.toString();		
//	}
//}
