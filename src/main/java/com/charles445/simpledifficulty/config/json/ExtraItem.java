package com.charles445.simpledifficulty.config.json;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON configuration for extra/optional items.
 * Allows enabling/disabling special items and configuring their properties.
 */
public class ExtraItem {
    public String _comment = "Unspecified";
    public boolean enabled = false;
    public Map<String, String> settings = new HashMap<>();

    public ExtraItem(String comment, boolean isEnabled) {
        this._comment = comment;
        this.enabled = isEnabled;
    }

    /**
     * Builder-style method to add a setting.
     *
     * @param key The setting key.
     * @param value The setting value.
     * @return This ExtraItem instance for chaining.
     */
    public ExtraItem put(String key, String value) {
        settings.put(key, value);
        return this;
    }

    /**
     * Gets a string setting value.
     *
     * @param key The setting key.
     * @return The value, or null if not found.
     */
    @Nullable
    public String get(String key) {
        return settings.get(key);
    }

    /**
     * Gets an integer setting value.
     *
     * @param key The setting key.
     * @return The integer value, or null if not found or invalid.
     */
    @Nullable
    public Integer getInteger(String key) {
        String val = settings.get(key);
        if (val == null) return null;

        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}