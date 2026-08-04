package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.api.config.json.*;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableTemperatureMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableThirstMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonTemperatureMetadataMigrate;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.config.json.MaterialTemperature;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Provides Gson TypeTokens for JSON serialization and deserialization.
 * Used by JsonConfigInternal to properly handle generic types during JSON processing.
 */
public class JsonTypeToken {
    
    /**
     * Gets the appropriate TypeToken for the specified JSON file.
     *
     * @param jcfn The JSON file name enum.
     * @return The Type for the specified file, or null if not found.
     */
    public static Type get(JsonFileName jcfn) {
        return switch (jcfn) {
            case armorTemperatures -> new TypeToken<Map<String, List<JsonTemperatureIdentity>>>() {}.getType();
            case blockTemperatures -> new TypeToken<Map<String, List<JsonPropertyTemperature>>>() {}.getType();
            case consumableTemperature -> new TypeToken<Map<String, List<JsonConsumableTemperature>>>() {}.getType();
            case consumableThirst -> new TypeToken<Map<String, List<JsonConsumableThirst>>>() {}.getType();
            case dimensionTemperature -> new TypeToken<Map<String, JsonTemperature>>() {}.getType();
            case fluidTemperatures -> new TypeToken<Map<String, JsonTemperature>>() {}.getType();
            case heldItemTemperatures -> new TypeToken<Map<String, List<JsonTemperatureIdentity>>>() {}.getType();
            case materialTemperature -> new TypeToken<MaterialTemperature>() {}.getType();
            case extraItems -> new TypeToken<Map<String, ExtraItem>>() {}.getType();
            
            // Migration types (legacy support)
            case armorTemperatures_MIGRATE -> new TypeToken<Map<String, JsonTemperature>>() {}.getType();
            case consumableTemperature_MIGRATE -> new TypeToken<Map<String, List<JsonConsumableTemperatureMigrate>>>() {}.getType();
            case consumableThirst_MIGRATE -> new TypeToken<Map<String, List<JsonConsumableThirstMigrate>>>() {}.getType();
            case heldItemTemperatures_MIGRATE -> new TypeToken<Map<String, List<JsonTemperatureMetadataMigrate>>>() {}.getType();
            
            default -> null;
        };
    }
}