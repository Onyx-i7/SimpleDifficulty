package com.charles445.simpledifficulty.api.config.json;

import com.google.gson.annotations.SerializedName;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a block temperature entry that optionally matches specific block state properties.
 * <p>
 * The {@code properties} map holds a mapping of block state property names to their expected values.
 * When checking a block state, only the properties present in this map are compared; any property
 * not specified here is ignored, allowing the entry to apply to multiple block states that share
 * the specified property values.
 * </p>
 */
public class JsonPropertyTemperature
{
	/**
	 * Map of block state property names to their expected values.
	 * The keys correspond to {@link IProperty#getName()} of a block state property.
	 * Serialized under the JSON key "properties".
	 */
	@SerializedName("properties")
	public Map<String, String> properties;

	/**
	 * The temperature value associated with this entry.
	 * Serialized under the JSON key "temperature".
	 */
	@SerializedName("temperature")
	public float temperature;

	/**
	 * Constructs a new {@code JsonPropertyTemperature} from a temperature value and
	 * an optional list of property-value pairs.
	 *
	 * @param temperature the temperature to assign to this entry
	 * @param props       zero or more {@link JsonPropertyValue} instances defining the
	 *                    block state properties that must match
	 */
	public JsonPropertyTemperature(float temperature, JsonPropertyValue... props)
	{
		this.temperature = temperature;
		this.properties = new HashMap<String, String>();
		for (JsonPropertyValue prop : props)
		{
			properties.put(prop.property, prop.value);
		}
	}

	/**
	 * Converts the internal property map to an array of {@link JsonPropertyValue} objects.
	 * This is useful for iterating over the properties in a format compatible with other
	 * parts of the API.
	 *
	 * @return an array of {@link JsonPropertyValue} representing the property map
	 */
	public JsonPropertyValue[] getAsPropertyArray()
	{
		List<JsonPropertyValue> jpvList = new ArrayList<JsonPropertyValue>();
		for (Map.Entry<String, String> entry : properties.entrySet())
		{
			jpvList.add(new JsonPropertyValue(entry.getKey(), entry.getValue()));
		}
		return jpvList.toArray(new JsonPropertyValue[0]); // Necessary to avoid a ClassCastException
	}

	/**
	 * Checks whether the given {@link IBlockState} matches the property requirements of this entry.
	 * <p>
	 * Only the property names that exist in this entry's {@code properties} map are considered.
	 * If a property in this map is not present in the block state, it is ignored (the state is
	 * still considered a match for that property). If a property key exists in both the map and
	 * the state, their values are compared as strings. If any compared value does not match,
	 * the method returns {@code false}.
	 * </p>
	 *
	 * @param blockstate the block state to test
	 * @return {@code true} if all specified properties match the block state, {@code false} otherwise
	 */
	public boolean matchesState(IBlockState blockstate)
	{
		// Iterate over the properties present in the block state and check the ones we care about.
		for (IProperty<?> property : blockstate.getPropertyKeys())
		{
			String propname = property.getName();
			if (properties.containsKey(propname))
			{
				// A matching property key exists; compare the actual value with the expected one.
				String stateValue = blockstate.getValue(property).toString();
				if (!properties.get(propname).equals(stateValue))
				{
					return false;
				}
			}
			// Properties in map that are not part of the block state are simply ignored.
			// This allows partial matching and prevents configuration errors from breaking everything.
		}

		return true;
	}

	/**
	 * Checks whether this entry's property map exactly matches a given set of properties.
	 * The given array must contain the same number of properties and each property must have
	 * the same key and value as in this entry's map.
	 *
	 * @param props the properties to compare against
	 * @return {@code true} if the given properties exactly match this entry's property map,
	 *         {@code false} otherwise
	 */
	public boolean matchesDescribedProperties(JsonPropertyValue... props)
	{
		if (props.length != properties.keySet().size())
			return false;

		for (JsonPropertyValue prop : props)
		{
			if (!properties.containsKey(prop.property))
			{
				return false;
			}
			else
			{
				// Has key
				if (!prop.value.equals(properties.get(prop.property)))
				{
					return false;
				}
			}
		}

		return true;
	}
}