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
 * higher than the current logging level are actually written to the log. The default logging level is warning since
 * warnings or higher indicate that something has gone wrong which should not have.
 *
 * Additionally, DEBUG level will always be logged. They should be used when debugging to be able to grep the logs more
 * easily.
 *
 * Created by Clayton on 25/11/2014.
 */
public final class Log {

    /**
     * The available logging levels.
     */
    public enum LogLevel {
        /** Error is something that should never happen and which can't be recovered from. It will generally be near
         * the end of a log because the program will have crashed. */
        ERROR,
        /** Warning is something that should never happen but we can safely ignore when it does. This is the default
         * log level, since a properly constructed system will never generate warnings or above. */
        WARNING,
        /** Info messages are used for things that can reasonably happen, but which the user might want to know about.*/
        INFO,
        /** Verbose statements are placed in a variety of places to tell what the program is doing. Changing to this log
         * level can be useful when debugging.*/
        VERBOSE,
        /** Debug statements are used only when debugging code. They are printed regardless of the current log levels.*/
        DEBUG;


        /**
         * Converts a string log level to a enum log level. If the string cannot be parsed, ERROR log level will be
         * returned.
         *
         * @param logLevel
         *        A string representation of the log level.
         *
         * @return The equivalent enum log level.
         */
        public static LogLevel parse(String logLevel) {

            String localLogLevel = logLevel.trim();

            switch (localLogLevel) {
                case "ERROR":
                case "ERR":
                    return ERROR;
                case "WARNING":
                case "WARN":
                case "WRN":
                    return WARNING;
                case "INFO":
                    return INFO;
                case "VERBOSE":
                case "VERB":
                case "VRB":
                    return VERBOSE;
                case "DEBUG":
                case "DBUG":
                case "DEBG":
                case "DBG":
                    return DEBUG;
                default:
                    warning("Log level could not be parsed.");
                    return Log.DEFAULT_LOG_LEVEL;
            }
        }

        /**
         * Returns the string representation of the enum.
         *
         * @return Returns the string representation of the enum.
         */
        public String toString() {
            switch (this) {
                case ERROR:
                    return "ERROR";
                case WARNING:
                    return "WARN";
                case INFO:
                    return "INFO";
                case VERBOSE:
                    return "VERB";
                case DEBUG:
                    return "DBUG";
            }

            // Java is not smart enough to know that the above switch is complete and always returns.
            return "UNKNOWN LEVEL";
        }
    }

    public  static final String   DEFAULT_FILENAME  = "Life.log";
    private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.WARNING;

    private static PrintWriter    printWriter = null;
    private static LogLevel       levelToLog  = DEFAULT_LOG_LEVEL;
    private static boolean        isInit      = false;

    private Log () {}

    /**
     * When the program first starts, the logging system must be initialised with this method. Uses the default log
     * filename "Life.log".
     */
    public static synchronized void init () {
        init(DEFAULT_FILENAME);
    }

    /**
     * When the program first starts, the logging system must be initialised with this method.
     *
     * @param filename
     *        The filename of the log file to use.
     */
    public static synchronized void init (String filename) {
        if (isInit) {
            return;
        }
        isInit = true;

        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
        } catch (IOException e) {
            System.out.println("Could not open log file: " + filename);
            System.out.println(e.toString());
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
     * Logs a warning to the logfile. Warning logs should be generated when something happens that should never happen
     * Warning level messages should be used when something has gone wrong but recovery
     * is possible, or if the program runs in a way that is generally ill advised.
     *
     * @param logString
     *        The string which describes the warning.
     */
    public static synchronized void warning (String logString) {
        if (levelToLog == LogLevel.WARNING ||
                levelToLog == LogLevel.INFO ||
                levelToLog == LogLevel.VERBOSE ||
                levelToLog == LogLevel.DEBUG) {
            logToFile(LogLevel.WARNING, logString);
        }
    }

    /**
     * Logs an info message to the logfile. An info message should be logged when an even occurs that is reasonable
     * (not "should never happen" as with warning) but which the user might want to know about. The default logging
     * level hides these messages.
     *
     * @param logString
     *        The string which describes the info.
     */
    public static synchronized void info (String logString) {
        if (levelToLog == LogLevel.INFO ||
                levelToLog == LogLevel.VERBOSE) {
            logToFile(LogLevel.INFO, logString);
        }
    }

    /**
     * Logs a verbose message to the logfile. An verbose message should be logged whenever a change of program state
     * occurs. This level can be turned on when debugging to see exactly what the program is doing.
     *
     * @param logString
     *        The string to log in the verbose message.
     */
    public static synchronized void verbose (String logString) {
        if (levelToLog == LogLevel.VERBOSE) {
            logToFile(LogLevel.VERBOSE, logString);
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
     * Allows the user to dynamically set the logging level. Only messages which are at or above the set logToFile
     * level are actually printed to the logToFile file. With the exception of debug messages which are always printing
     * in the log. For this reason, the log level cannot be set to DEBUG.
     *
     * @param logLevel
     *        The new desired logToFile level.
     */
    public static synchronized void setLogLevel(LogLevel logLevel) {
        if (logLevel == LogLevel.DEBUG) {
            warning("Attempted to set the log level to DEBUG which cannot be done.");
            return;
        }

        levelToLog = logLevel;
    }

    /**
     * Allows the user to dynamically set the logging level using a string instead the enum. If the string cannot be
     * parsed then the log level will be set to the default level of WARNING.
     *
     * @param logLevel
     *        A string representation of the desired log level.
     */
    public static synchronized void setLogLevel (String logLevel) {
        LogLevel enumLogLevel = LogLevel.parse(logLevel);

        setLogLevel(enumLogLevel);
    }

    /**
     * The common method which actually does the logging to the file. Writes in the format:
     * <second_since_epoch> <human_readable_date-time> <log_level> <log_message>
     *
     * This method is only called from a synchronized context and is itself synchronized, so there should be no issues
     * with multiple threads trying to write to file at the same time.
     *
     * @param logLevel
     *        The level of the logging. This will be added to the log message.
     *
     * @param logString
     *        The actual message of the log.
     */
    private static synchronized void logToFile(LogLevel logLevel, String logString) {
        if (!isInit) {return;}
        // Already checked if we need to log this type, so just add the date stamp and output.
        String output = "";

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        output += String.valueOf(date.getTime());
        output += " ";
        output += dateFormat.format(date);
        output += " ";
        output += logLevel.toString();
        output += " ";
        output += logString;

        // Write the log entry to the file.
        printWriter.println(output);
        // We also do a flush so that any errors are logged before the program finishes crashing.
        printWriter.flush();
    }
}
