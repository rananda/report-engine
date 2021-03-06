/**
 * 
 */
package com.redhat.reportengine.client;

import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.log4j.Appender;

/**
 * @author jkandasa@redhat.com (Jeeva Kandasamy)
 * Mar 28, 2012
 */
public class LogHandler {
	
	protected static RemoteAPI remoteApi;
	protected static boolean isLoadedHandler = false;
	protected static JulReportEngineLogHandler reportEngineJulHandler = null;
	protected static Log4jReportEngineLogHandler reportEngineLog4jHandler = null;
	private static final Logger _logger = Logger.getLogger(LogHandler.class.getName());
	
	public static final String LOG_LEVEL_DEFAULT 	= "DEFAULT";
	
	//JUL
	public static final String LOG_LEVEL_FINEST 	= "FINEST";
	public static final String LOG_LEVEL_FINER 		= "FINER";
	public static final String LOG_LEVEL_FINE 		= "FINE";
	public static final String LOG_LEVEL_WARNING	= "WARNING";
	public static final String LOG_LEVEL_SEVERE		= "SEVERE";
	

	public static final String LOGGER_JUL 		= "JUL";
	public static final String LOGGER_LOG4J 	= "LOG4J";
	
	public static void teardown(){
		log4jTeardown();
		julTeardown();
		remoteApi = null;
		isLoadedHandler = false;
		reportEngineJulHandler = null;
		reportEngineLog4jHandler = null;
	}

	private static void log4jTeardown(){
		org.apache.log4j.Logger log4jLogger = getLog4jLogger();
		log4jLogger.removeAppender(reportEngineLog4jHandler);
		Enumeration appenders = log4jLogger.getAllAppenders();
		while(appenders.hasMoreElements()){
			Object appender = appenders.nextElement();
			if(appender.getClass().equals(Log4jReportEngineLogHandler.class));
				log4jLogger.removeAppender((Appender)appender);
		}
		
	}

	/**
	 * @return
	 */
	private static org.apache.log4j.Logger getLog4jLogger() {
		return org.apache.log4j.Logger.getRootLogger();
	}
	
	private static void julTeardown(){

		Logger julLogger = Logger.getLogger("");
		julLogger.removeHandler(reportEngineJulHandler);
		for(Handler handler: julLogger.getHandlers()){
			if(handler.getClass().equals(JulReportEngineLogHandler.class))
				julLogger.removeHandler(handler);
			
		}
		
	}
	
	public static void julLoggerSetup() {
		// Create JUL Logger
		Logger julLogger = Logger.getLogger("");
		
		if(!remoteApi.getLogLevel().equalsIgnoreCase(LOG_LEVEL_DEFAULT)){
			julLogger.setLevel(Level.parse(remoteApi.getLogLevel()));
		}
		
		if(isLoadedHandler){
			julLogger.addHandler(reportEngineJulHandler);	
			remoteApi.setJulLoggers(RemoteAPI.loggersToWarn, Level.WARNING);
			_logger.log(Level.INFO, "Report Engine Client: JUL Handler reloaded...");
		}else{
			reportEngineJulHandler = new JulReportEngineLogHandler(getRemoteApi());
			julLogger.addHandler(reportEngineJulHandler);
			remoteApi.setJulLoggers(RemoteAPI.loggersToWarn, Level.WARNING);
			_logger.log(Level.INFO, "Report Engine Client: JUL Handler added...");
			isLoadedHandler = true;
		}
		_logger.log(Level.INFO, "Report Engine Client: Log Level (JUL): "+julLogger.getLevel());
	}
	
	public static void log4jLoggerSetup() {
		// Create LOG4J Logger
		org.apache.log4j.Logger log4jLogger = getLog4jLogger();
		
		if(!remoteApi.getLogLevel().equalsIgnoreCase(LOG_LEVEL_DEFAULT)){
			log4jLogger.setLevel(org.apache.log4j.Level.toLevel(remoteApi.getLogLevel(), org.apache.log4j.Level.INFO));
		}
		
		if(isLoadedHandler){
			log4jLogger.removeAppender(reportEngineLog4jHandler);
			log4jLogger.addAppender(reportEngineLog4jHandler);	
			remoteApi.setLog4jLoggers(RemoteAPI.loggersToWarn, org.apache.log4j.Level.WARN);
			_logger.log(Level.INFO, "Report Engine Client: LOG4J Handler reloaded...");
		}else{
			reportEngineLog4jHandler = new Log4jReportEngineLogHandler(getRemoteApi());
			log4jLogger.addAppender(reportEngineLog4jHandler);	
			remoteApi.setLog4jLoggers(RemoteAPI.loggersToWarn, org.apache.log4j.Level.WARN);
			_logger.log(Level.INFO, "Report Engine Client: LOG4J Handler added...");
			isLoadedHandler = true;
		}
		_logger.log(Level.INFO, "Report Engine Client: Log Level (log4j): "+log4jLogger.getLevel());
	}

	public static RemoteAPI getRemoteApi() {
		return remoteApi;
	}

	public static void setRemoteApi(RemoteAPI remoteApi) {
		LogHandler.remoteApi = remoteApi;
	}
	
	public static void initLogger(){
		if(remoteApi.getLoggerType().equalsIgnoreCase(LOGGER_JUL)){
			julLoggerSetup();
		}else if(remoteApi.getLoggerType().equalsIgnoreCase(LOGGER_LOG4J)){
			log4jLoggerSetup();
		}else{
			_logger.log(Level.WARNING, "Report Engine Client: Un-supported Logger Type: "+remoteApi.getLoggerType());
		}
	}
}
