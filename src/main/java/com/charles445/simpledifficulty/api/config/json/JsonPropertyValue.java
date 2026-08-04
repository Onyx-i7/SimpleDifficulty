package com.charles445.simpledifficulty.api.config.json;

/**
 * JSON data class representing a block state property key-value pair.
 * Used for matching specific block states (e.g., "facing=north", "lit=true").
 */
public class JsonPropertyValue {
    public String property;
    public String value;

    public JsonPropertyValue(String property, String value) {
        this.property = property;
        this.value = value;
    }
}