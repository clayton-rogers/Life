package com.gmail.claytonrogers53.life.Configuration;

/**
 * For every valid key - value pair found in the configuration file, a ConfigurationItem object will be created and
 * placed in the Configuration list.
 *
 * Created by Clayton on 16/11/2014.
 */
class ConfigurationItem{
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
