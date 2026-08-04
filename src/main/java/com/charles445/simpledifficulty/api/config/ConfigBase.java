package com.charles445.simpledifficulty.api.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for configuration storage.
 * Stores configuration values as strings and provides type-safe getters.
 */
public abstract class ConfigBase {
    public final Map<String, String> values = new HashMap<>();

    /**
     * Gets a boolean value from the configuration.
     *
     * @param option The configuration option to retrieve.
     * @return The boolean value, or false if not found.
     */
    public boolean getBoolean(IConfigOption option) {
        String value = values.get(option.getName());
        return value != null && Boolean.parseBoolean(value);
    }

    /**
     * Gets an integer value from the configuration.
     *
     * @param option The configuration option to retrieve.
     * @return The integer value, or 0 if not found or invalid.
     */
    public int getInteger(IConfigOption option) {
        String value = values.get(option.getName());
        if (value == null) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Gets a double value from the configuration.
     *
     * @param option The configuration option to retrieve.
     * @return The double value, or 0.0 if not found or invalid.
     */
    public double getDouble(IConfigOption option) {
        String value = values.get(option.getName());
        if (value == null) return 0.0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Stores a configuration value.
     *
     * @param option The configuration option to set.
     * @param obj The value to store (will be converted to string).
     */
    public void put(IConfigOption option, Object obj) {
        values.put(option.getName(), String.valueOf(obj));
        QuickConfig.updateValues();
    }
}