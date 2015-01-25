package com.gmail.claytonrogers53.life.Log;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements basic logging which can be called from any thread. Note that only messages with log levels of equal or
 * higher than the current logging level are actually written to the log.
 *
 * Created by Clayton on 25/11/2014.
 */
public final class Log {

    /**
     * The available logging levels.
     */
    public enum LogLevel {
        ERROR,
        WARNING,
        INFO,
        DEBUG
    }

    public  static final String DEFAULT_FILENAME = "Life.log";
    private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.ERROR;

    private static PrintWriter   printWriter = null;
    private static LogLevel      levelToLog  = DEFAULT_LOG_LEVEL;
    private static boolean       isInit      = false;

    private Log () {

    }

    /**
     * When the program first starts, the logging system must be initialised with this method. This must be done after
     * you have loaded the configuration items.
     *
     * @param filename
     *        The filename of the log file to use.
     *
     * @throws IOException
     *         When there is a problem opening the log file. Typically fatal.
     */
    public static synchronized void init (String filename) throws IOException {
        if (isInit) {
            return;
        }
        isInit = true;

        // This may throw an IOException, but we let the main program deal with that
        printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
    }

    /**
     * Extracts the log level from a string which comes from the configuration file. If anything goes wrong
     * interpreting the String, then it will return the DEFAULT_LOG_LEVEL (typically ERROR).
     *
     * @param logLevel
     *        The logging string from the config file.
     *
     * @return The desired log level.
     */
    private static LogLevel extractLogLevel (String logLevel) {

        final String trimmedLogLevel = logLevel.trim();

        switch (trimmedLogLevel) {
            case "ERROR":
                return LogLevel.ERROR;
            case "WARNING":
                return LogLevel.WARNING;
            case "INFO":
                return LogLevel.INFO;
            default:
                return LogLevel.ERROR;
        }
    }

    /**
     * Logs an error to the logfile. Error level messages are reserved for critical failures which the system would not
     * be expected to recover from.
     *
     * @param logString
     *        The string which describes the error.
     */
    public static synchronized void error (String logString) {
        // Don't even need to check the log level, since we always log errors.
        logToFile(LogLevel.ERROR, logString);
    }

    /**
     * Logs a warning to the logfile. Warning level messages should be used when something has gone wrong but recovery
     * is possible, or if the program runs in a way that is generally ill advised.
     *
     * @param logString
     *        The string which describes the warning.
     */
    public static synchronized void warning (String logString) {
        if (levelToLog == LogLevel.INFO || levelToLog == LogLevel.WARNING) {
            logToFile(LogLevel.INFO, logString);
        }
    }

    /**
     * Logs an info message to the logfile. An info log message should be generated any time a change of state happens
     * in the program. They should generally not be generated more often than a couple of times a second for any given
     * component.
     *
     * @param logString
     *        The string which describes the info.
     */
    public static synchronized void info (String logString) {
        if (levelToLog == LogLevel.INFO) {
            logToFile(LogLevel.INFO, logString);
        }
    }

    /**
     * Logs a debug message to the logfile. NOTE: Debug messages are only for development use and should be removed
     * prior to merging a feature. Debug messages are written to the log regardless of the current logging level.
     *
     * @param logString
     *        The string which describes the debug message.
     */
    public static synchronized void debug (String logString) {
        logToFile(LogLevel.DEBUG, logString);
    }

    /**
     * The common method which actually does the logging to the file. Writes in the format:
     * <second_since_epoch> <human_readable_date-time> <log_level> <log_message>
     *
     * @param logLevel
     *        The level of the logging. This will be added to the log message.
     *
     * @param logString
     *        The actual message of the log.
     */
    private static void logToFile(LogLevel logLevel, String logString) {
        if (!isInit) {return;}
        // Already checked if we need to log this type, so just add the date stamp and output.
        String output = "";

        final Date date = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        output += String.valueOf(date.getTime());
        output += " ";
        output += dateFormat.format(date);
        output += " ";
        output += logLevel.name();
        output += " ";
        output += logString;

        // Write the log entry to the file.
        printWriter.println(output);
        // We also do a flush so that any errors are logged before the program finishes crashing.
        printWriter.flush();
    }

    /**
     * Allows the user to dynamically set the logging level. Only messages which are at or above the set logToFile level are
     * actually printed to the logToFile file.
     *
     * @param levelToLog
     *        The new desired logToFile level.
     */
    public static synchronized void setLevelToLog(LogLevel levelToLog) {
        Log.levelToLog = levelToLog;
    }
}
