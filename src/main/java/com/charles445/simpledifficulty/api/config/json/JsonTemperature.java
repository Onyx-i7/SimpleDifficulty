package com.charles445.simpledifficulty.api.config.json;

import com.google.gson.annotations.SerializedName;

/**
 * JSON data class representing a simple temperature value.
 * Used for dimensions and fluids where only a base temperature is needed.
 */
public class JsonTemperature {
    @SerializedName("temperature")
    public float temperature;

    public JsonTemperature(float temperature) {
        this.temperature = temperature;
    }
}