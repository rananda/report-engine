/**
 * 
 */
package com.redhat.reportengine.client;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jkandasa@redhat.com (Jeeva Kandasamy)
 * Mar 28, 2012
 */
public class LogHandler {
	protected static Logger _logger = Logger.getLogger(LogHandler.class.getName());
	protected static RemoteAPI remoteApi;
	protected static boolean isLoadedHandler = false;
	protected static JulReportEngineLogHandler reportEngineHandler = null;
	
	public static void julLoggerSetup() {
		// Create Logger
		Logger logger = Logger.getLogger("");
		//Logger logger = LogManager.getLogManager().getLogger("");
		
		if(remoteApi.getLogLevel().equalsIgnoreCase("ALL")){
			logger.setLevel(Level.ALL);
		}else if(remoteApi.getLogLevel().equalsIgnoreCase("FINEST")){
			logger.setLevel(Level.FINEST);
		}else if(remoteApi.getLogLevel().equalsIgnoreCase("FINER")){
			logger.setLevel(Level.FINER);
		}else if(remoteApi.getLogLevel().equalsIgnoreCase("FINE")){
			logger.setLevel(Level.FINE);
		}else if(remoteApi.getLogLevel().equalsIgnoreCase("INFO")){
			logger.setLevel(Level.INFO);
		}else if(remoteApi.getLogLevel().equalsIgnoreCase("WARNING")){
			logger.setLevel(Level.WARNING);
		}else if(remoteApi.getLogLevel().equalsIgnoreCase("SEVERE")){
			logger.setLevel(Level.SEVERE);
		}else if(remoteApi.getLogLevel().equalsIgnoreCase("CONFIG")){
			logger.setLevel(Level.CONFIG);
		}else{
			//Ignore
		}
		
		_logger.log(Level.INFO, "Log Level: "+logger.getLevel());
		
		if(isLoadedHandler){
			logger.removeHandler(reportEngineHandler);
			reportEngineHandler = new JulReportEngineLogHandler(getRemoteApi());
			logger.addHandler(reportEngineHandler);	
			_logger.log(Level.INFO, "JUL Handler reloaded...");
		}else{
			reportEngineHandler = new JulReportEngineLogHandler(getRemoteApi());
			logger.addHandler(reportEngineHandler);	
			_logger.log(Level.INFO, "JUL Handler added...");
			isLoadedHandler = true;
		}
	}

	public static RemoteAPI getRemoteApi() {
		return remoteApi;
	}

	public static void setRemoteApi(RemoteAPI remoteApi) {
		LogHandler.remoteApi = remoteApi;
	}
	
	public static void initLogger(){
		if(remoteApi.getLoggerType().equalsIgnoreCase("JUL")){
			julLoggerSetup();
		}else{
			_logger.log(Level.WARNING, "Un-supported Logger Type: "+remoteApi.getLoggerType());
		}
	}
}
