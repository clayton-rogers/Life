package com.gmail.claytonrogers53.life.Configuration;

import com.gmail.claytonrogers53.life.Log.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the global configuration settings which are read from the configuration file. The configuration file by
 * default is stored in the current working directory and is named "Life.conf". Key value pairs may be added in the
 * following format:
 *
 * // comment
 * KEY=value
 *
 * Note that there is no space before or after the equal sign. Every thing after the equal sign and before the newline
 * is considered part of the "value". Key string will generally use block caps. Comments can be added to the
 * configuration file using double forward slash (//). A default configuration file can be found along with this
 * program under the name "Life.conf.default".
 *
 * Created by Clayton on 16/11/2014.
 */
public final class Configuration {

    /** The list of configuration items which has been read from the config file. */
    private static List<ConfigurationItem> configurationItems = new ArrayList<>();

    /** The default filename of the configuration file. */
    public static String DEFAULT_CONFIGURATION_FILENAME = "Life.conf";

    private Configuration () {
        Log.error("Configuration class should never be instantiated.");
        throw new AssertionError();
    }

    /**
     * Allows the user to query whether a given key is defined in the config file.
     *
     * @param key
     *        The key to be checked.
     *
     * @return True if the key is defined in the config file.
     */
    public static boolean isSet(String key) {
        for (ConfigurationItem c : configurationItems) {
            if (c.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Allows the user to get the value of any key, if it is defined. You should generally check whether the key is
     * defined first by calling isSet.
     *
     * @param key
     *        The key one wants to know the value of.
     * @return The value of the key.
     *
     * @throws ValueNotConfiguredException When the key has not been defined in the config file.
     *
     * @see #isSet
     */
    public static String getValue (String key) throws ValueNotConfiguredException {

        // TODO-IMPROVEMENT: This method and isSet methods will generally be slow, but we don't query them often.
        for (ConfigurationItem c : configurationItems) {
            if (c.getKey().equals(key)) {
                return c.getValue();
            }
        }
        
        // If we get through the whole list without finding the item, it's not there.
        Log.warning("The key " + key + " was queried, but does not exist in the config file.");
        throw new ValueNotConfiguredException();
    }

    /**
     * Allows the user to get the value of a key while assuming that the value is an integer. If the value queried is
     * not an integer, then the method throws a ConfigFormatException.
     *
     * @param key
     *        The key one wants to know the value of.
     * @return The value of the key as an integer if possible.
     *
     * @throws ValueNotConfiguredException When the key has not been defined in the config file.
     *
     * @throws ConfigFormatException When the key has been defined, but could not be interpreted as an integer.
     */
    public static int getValueInt (String key) throws ValueNotConfiguredException, ConfigFormatException {
        try {
            return Integer.parseInt(getValue(key));
        } catch (NumberFormatException e) {
            Log.warning("The key " + key + " could not be interpreted as an integer.");
            throw new ConfigFormatException();
        }
    }

    /**
     * Allows the user to get the value of a key while assuming that the value is a double. If the value queried is not
     * a double, then the method throws a ConfigFormatException.
     *
     * @param key
     *        The key one wants to know the value of.
     *
     * @return The value of the key as a double is possible.
     *
     * @throws ValueNotConfiguredException When the key has not been defined in the config file.
     *
     * @throws ConfigFormatException When the key has been defined, but could not be interpreted as a double.
     */
    public static double getValueDouble (String key) throws ValueNotConfiguredException, ConfigFormatException {
        try {
            return Double.parseDouble(getValue(key));
        } catch (NumberFormatException e) {
            Log.warning("The key " + key + " could not be interpreted as a double.");
            throw new ConfigFormatException();
        }
    }

    /**
     * Loads configuration file at the default location.
     *
     * @see #DEFAULT_CONFIGURATION_FILENAME
     */
    public static void loadConfigurationItems (){
        loadConfigurationItems(DEFAULT_CONFIGURATION_FILENAME);
    }

    /**
     * Loads the configuration file at a user specified location.
     *
     * @param filename
     *        The filename (with path) that should be read as the configuration file.
     */
    public static void loadConfigurationItems (String filename) {
        Log.info("Loading configuration items from: " + filename);

        // In case the user wants to redo the load. (Should generally not happen.)
        if (configurationItems.size() != 0) {
            Log.warning("Configuration database already exist. Overwriting entries.");
        }
        configurationItems.clear();

        List<String> configFileContents = null;

        Path filePath = Paths.get(filename);
        try {
            configFileContents = Files.readAllLines(filePath);
        } catch (NoSuchFileException e) {
            Log.warning("The configuration file " + filename + " was not found. No configuration settings will be read.");
            return;
        } catch (IOException e) {
            Log.error("There was an unexpected error reading " + filename + ". No configuration settings will be read.");
            return;
        }

        if (configFileContents != null) {
            int lineNumber = 0;
            for (String line : configFileContents) {
                // Extract a key value pair if possible, otherwise discard the line.

                // Trim any of that pesky leading whitespace.
                line = line.trim();

                // Check to see if the line is blank.
                if (line.length() == 0) {
                    ++lineNumber;
                    continue;
                }

                // Check to see if the line is a comment.
                if (line.charAt(0) == '/' && line.charAt(1) == '/') {
                    ++lineNumber;
                    continue;
                }

                // If the line is not blank and not a comment, expect it to be a key value pair.
                int equalPosition = line.indexOf('=');
                if (equalPosition == -1) {
                    Log.warning("No equal sign could be found on line " + String.valueOf(lineNumber) + ", skipping line.");
                    continue;
                }
                String key = line.substring(0, equalPosition);
                int eol = line.length();
                String value = line.substring(equalPosition + 1, eol);

                configurationItems.add(new ConfigurationItem(key, value, lineNumber));
                Log.info("Added to config database: KEY:<" + key + "> VALUE:<" + value + ">");
            }
        }

        Log.info("Loaded " + configurationItems.size() + " items from configuration file: " + filename);
    }
}
