/**
 * 
 */
package com.redhat.reportengine.server.queue.actions;

import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.redhat.reportengine.server.dbdata.TestLogsTable;
import com.redhat.reportengine.server.dbmap.TestLogs;
import com.redhat.reportengine.server.queue.TestLogsQueue;


/**
 * @author jkandasa@redhat.com (Jeeva Kandasamy)
 * Apr 2, 2012
 */
public class InsertUpdateTestLogs extends InsertParent implements Runnable{
	private static Logger _logger = Logger.getLogger(InsertUpdateTestLogs.class);

	private TestLogs testLogs = null;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		_logger.info("Started Test Logs Queue Manager...");
		while(!isStopMeImmeditate()){
			if(TestLogsQueue.getTestLogSize() > 0){
				insertTestLogs();
			}else{
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					_logger.error("Thread sleep exception,", ex);
				}
			}
			if(isStopMe() && TestLogsQueue.getTestLogSize() == 0){
				_logger.info("Stopping Test Logs Queue Manager...");
				break;
			}
		}
		_logger.info("Stopped Test Logs Queue Manager..");

	}

	private void insertTestLogs(){
		try {
			testLogs = TestLogsQueue.getFirstTestLog();
			_logger.debug("Original Log: "+testLogs);
			if((testLogs.getThrowable() != null) && (testLogs.getThrowable().length() > 10000)){
				testLogs.setThrowable(testLogs.getThrowable().substring(0, (10000-1)));
				_logger.warn("Test Log 'Throwable' size has been truncated to 10000 chars, Portion of data has been dropped!!");
			}
			if(testLogs.getMessage().length()>10000){
				//TODO: add code to manage if log file size goes more than 10000 chars...
				_logger.warn("Limited message size to 10000 chars length! Portion of data has been dropped!!");
				testLogs.setMessage(testLogs.getMessage().substring(0, 9999));
			}			
			new TestLogsTable().add(testLogs);

		} catch (SQLException ex) {
			_logger.error("Error on Test Log insertion,", ex);
		}
	}
}
