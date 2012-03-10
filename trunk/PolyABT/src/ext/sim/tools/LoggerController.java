package ext.sim.tools;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerController {

	public static void createLogger(String filename, String loggerName) throws Exception {

		// creating logger called ServerLog
		Logger logger = Logger.getLogger(loggerName);

		// creating log file
		Handler logFileHandler = null;

		int tError = 10;

		while (true) {

			try {

				logFileHandler = new FileHandler(filename);
				break;
			} catch (IOException e) {

				if (tError == 10)
					System.err
							.println("unable to open file for logging, will try again..");

				else if (tError == 0) {

					System.err.println("giving up.. exiting..");
					throw new Exception("LLLOOOOGGGGGGGG...");
				}

				tError--;
			}
		}

		for (Handler h : logger.getHandlers())
			logger.removeHandler(h);
		
		logger.setUseParentHandlers(false);
		
		logFileHandler.setFormatter(new VerySimpleLogFormatter());

		// logger output is written to a file in logFileHandler handler -
		// server.log
		logger.addHandler(logFileHandler);

		// Set the log level specifying which message levels will be logged by
		// this logger
		logger.setLevel(Level.INFO);
	}
}
