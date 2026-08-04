package com.charles445.simpledifficulty.api.config.json;

import com.google.gson.annotations.SerializedName;
import net.minecraft.state.Property;
import net.minecraft.block.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON data class representing a temperature value for blocks with specific state properties.
 */
public class JsonPropertyTemperature {
    @SerializedName("properties")
    public Map<String, String> properties;

    @SerializedName("temperature")
    public float temperature;

    public JsonPropertyTemperature(float temperature, JsonPropertyValue... props) {
        this.temperature = temperature;
        this.properties = new HashMap<>();
        for (JsonPropertyValue prop : props) {
            properties.put(prop.property, prop.value);
        }
    }

    /**
     * Converts the properties map to an array of JsonPropertyValue objects.
     *
     * @return Array of property values.
     */
    public JsonPropertyValue[] getAsPropertyArray() {
        List<JsonPropertyValue> jpvList = new ArrayList<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            jpvList.add(new JsonPropertyValue(entry.getKey(), entry.getValue()));
        }
        return jpvList.toArray(new JsonPropertyValue[0]);
    }

    /**
     * Checks if this temperature configuration matches the given block state.
     *
     * @param blockState The block state to check.
     * @return true if all required properties match.
     */
    public boolean matchesState(BlockState blockState) {
        for (Property<?> property : blockState.getProperties()) {
            String propName = property.getName();
            if (properties.containsKey(propName)) {
                String stateValue = blockState.getValue(property).toString();
                if (!properties.get(propName).equals(stateValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if this configuration matches the described properties exactly.
     *
     * @param props The properties to compare against.
     * @return true if the properties match exactly.
     */
    public boolean matchesDescribedProperties(JsonPropertyValue... props) {
        if (props.length != properties.size()) {
            return false;
        }

        for (JsonPropertyValue prop : props) {
            if (!properties.containsKey(prop.property)) {
                return false;
            } else {
                if (!prop.value.equals(properties.get(prop.property))) {
                    return false;
                }
            }
        }

        return true;
    }
}