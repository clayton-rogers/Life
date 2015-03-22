package com.gmail.claytonrogers53.life.Util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the global configuration settings which are read from the configuration file. The configuration file by
 * default is stored in the current working directory and is named "Life.conf".
 *
 * You need to call loadConfigurationItems() before any threads try to read from the config.
 *
 * Key value pairs may be added in the following format:
 *
 * // comment
 * KEY=value
 *
 * Note that leading and trailing white space on a line will be automatically removed. Note that there is no space
 * before or after the equal sign. Every thing after the equal sign and before the newline is considered part of the
 * "value". Key string will generally use block caps. Comments can be added to the configuration file using double
 * forward slash (//). A default configuration file can be found along with this program under the name
 * "Life.conf.default".
 *
 * Configuration init should generally be called after Log init.
 *
 * Created by Clayton on 16/11/2014.
 */
public final class Configuration {

    /** The list of configuration items which has been read from the config file. */
    private static final List<ConfigurationItem> configurationItems = new ArrayList<>(10);

    /** The default filename of the configuration file. */
    private static final String DEFAULT_CONFIGURATION_FILENAME = "Life.conf";

    /**
     * For every valid key - value pair found in the configuration file, a ConfigurationItem object will be created and
     * placed in the Configuration list.
     *
     * Created by Clayton on 16/11/2014.
     */
    private static class ConfigurationItem{
        private final String key;
        private final String value;

        /**
         * Creates a new ConfigurationItem with the required values.
         *
         * @param key
         *        The key of the item. This is the value it will be retrieved by.
         *
         * @param value
         *        The value of the item. This is the data.
         */
        ConfigurationItem(String key, String value) {
            this.key         = key;
            this.value       = value;
        }

        /**
         * Allows the configuration system to query the key of the item.
         *
         * @return The key of the item.
         */
        String getKey() {
            return key;
        }

        /**
         * Allows the configuration system to query the value of the item. Note that the value is always stored as a string
         * even if it will later be interpreted as something else.
         *
         * @return The value of the item.
         */
        String getValue() {
            return value;
        }
    }

    private Configuration () {}

    /**
     * Internal call to retrieve the contents of a particular key. Returns null if the key cannot be found.
     *
     * @param key
     *        The key that we want to find the value for.
     *
     * @return The value associated with the key, or null if the key is not found.
     */
    private static String getValue(String key) {
        for (ConfigurationItem configurationItem : configurationItems) {
            if (configurationItem.getKey().equals(key)) {
                return configurationItem.getValue();
            }
        }
        Log.info("A call was made to getValue for a key that doesn't exit. key=" + key);
        return null;
    }

    /**
     * Allows the user to get the value of a given key. Returns the defaultValue if there is any problem retrieving or
     * parsing the value. Used when the desired value is a string.
     *
     * @param key
     *        The key one wants to know the value of.
     *
     * @param defaultValue
     *        If the key cannot be found, this string is returned instead.
     *
     * @return The value of the key or the default value.
     */
    public static String getValue(String key, String defaultValue) {

        String s = getValue(key);

        if (s == null) {
            s = defaultValue;
            Log.info("Did not find key: " + key + " returning default.");
        }

        return s;
    }

    /**
     * Allows the user to get the value of a given key. Returns the defaultValue if there is any problem retrieving or
     * parsing the value. Used when the desired value is an int.
     *
     * @param key
     *        The key one wants to know the value of.
     *
     * @param defaultValue
     *        If the key cannot be found, this int is returned instead.
     *
     * @return The value of the key as an integer if possible, or the default value.
     */
    public static int getValueInt (String key, int defaultValue){

        String s = getValue(key);
        int x;

        if (s == null) {
            x = defaultValue;
        } else {
            try {
                x = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Log.warning("Error parsing key " + key + " as int.");
                Log.warning(e.toString());
                x = defaultValue;
            }
        }

        return x;
    }

    /**
     * Allows the user to get the value of a given key. Returns the defaultValue if there is any problem retrieving or
     * parsing the value. Used when the desired value is a double.
     *
     * @param key
     *        The key one wants to know the value of.
     *
     * @param defaultValue
     *        If the key cannot be found, this double is returned instead.
     *
     * @return The value of the key as a double is possible, or the default value.
     */
    public static double getValueDouble (String key, double defaultValue) {

        String s = getValue(key);
        double x;

        if (s == null) {
            x = defaultValue;
        } else {
            try {
                x = Double.parseDouble(s);
            } catch (NumberFormatException e) {
                Log.warning("Error parsing key " + key + " as double.");
                Log.warning(e.toString());
                x = defaultValue;
            }
        }

        return x;
    }

    /**
     * Loads configuration file at the default location.
     */
    public static void loadConfigurationItems (){
        loadConfigurationItems(DEFAULT_CONFIGURATION_FILENAME);
    }

    /**
     * Loads the configuration file from a user specified location.
     *
     * @param filename
     *        The filename (with path) that should be read as the configuration file.
     */
    public static void loadConfigurationItems (String filename) {
        Log.info("Loading configuration items from: " + filename);

        // In case the user wants to redo the load. (Should generally not happen.)
        if (!configurationItems.isEmpty()) {
            Log.warning("Configuration database already exist. Overwriting entries.");
            configurationItems.clear();
        }

        List<String> configFileContents;

        Path filePath = Paths.get(filename);
        try {
            configFileContents = Files.readAllLines(filePath, Charset.defaultCharset());
        } catch (NoSuchFileException e) {
            Log.warning("The configuration file " + filename + " was not found. No configuration settings will be read.");
            Log.warning(e.toString());
            return;
        } catch (IOException e) {
            Log.error("There was an unexpected error reading " + filename + ". No configuration settings will be read.");
            Log.error(e.toString());
            return;
        }

        if (configFileContents != null) {
            int lineNumber = 0;
            for (String line : configFileContents) {
                // Extract a key value pair if possible, otherwise discard the line.

                // Trim any of that pesky leading and trailing whitespace.
                line = line.trim();

                // Check to see if the line is blank.
                if (line.isEmpty()) {
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
                    Log.warning("No equal sign could be found on line " + lineNumber + ", skipping line.");
                    continue;
                }
                String key = line.substring(0, equalPosition);
                int eol = line.length();
                String value = line.substring(equalPosition + 1, eol);

                configurationItems.add(new ConfigurationItem(key, value));
                Log.info("Added to config database: KEY:<" + key + "> VALUE:<" + value + '>');
            }
        }

        Log.info("Loaded " + configurationItems.size() + " items from configuration file: " + filename);
    }
}
