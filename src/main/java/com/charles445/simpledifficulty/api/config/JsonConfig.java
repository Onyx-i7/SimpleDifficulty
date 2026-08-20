package com.charles445.simpledifficulty.api.config;

import com.charles445.simpledifficulty.api.config.json.*;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central registry for JSON-based configuration values used by Simple Difficulty.
 * Provides static maps to store temperature and thirst data for various game elements,
 * along with registration methods for mods or internal code to populate them.
 */
public class JsonConfig
{
	/** Maps item registry names to a list of armor temperature identity entries. */
	public static Map<String, List<JsonTemperatureIdentity>> armorTemperatures = new HashMap<>();

	/** Maps block registry names to a list of block property temperature entries. */
	public static Map<String, List<JsonPropertyTemperature>> blockTemperatures = new HashMap<>();

	/** Maps item registry names to a list of consumable temperature entries. */
	public static Map<String, List<JsonConsumableTemperature>> consumableTemperature = new HashMap<>();

	/** Maps item registry names to a list of consumable thirst entries. */
	public static Map<String, List<JsonConsumableThirst>> consumableThirst = new HashMap<>();

	/** Maps dimension IDs (as strings) to dimension temperature entries. */
	public static Map<String, JsonTemperature> dimensionTemperature = new HashMap<>();

	/** Maps fluid registry names to fluid temperature entries. */
	public static Map<String, JsonTemperature> fluidTemperatures = new HashMap<>();

	/** Maps item registry names to a list of held item temperature identity entries. */
	public static Map<String, List<JsonTemperatureIdentity>> heldItemTemperatures = new HashMap<>();

	/**
	 * Registers an armor item's temperature modifier using its registry name and metadata.
	 * If an existing entry matches the item identity, it is replaced.
	 *
	 * @param stack the armor ItemStack
	 * @param temperature the temperature value to register
	 */
	public static void registerArmorTemperature(ItemStack stack, float temperature)
	{
		String registryName = stack.getItem().getRegistryName().toString();

		int metadata = -1;
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();

		registerArmorTemperature(stack.getItem().getRegistryName().toString(), temperature, new JsonItemIdentity(metadata));
	}

	/**
	 * Registers an armor temperature modifier for all metadata values of an item.
	 * If an existing entry matches the wildcard identity, it is replaced.
	 *
	 * @param registryName the item's registry name
	 * @param temperature the temperature value to register
	 */
	public static void registerArmorTemperature(String registryName, float temperature)
	{
		registerArmorTemperature(registryName, temperature, new JsonItemIdentity(-1));
	}

	/**
	 * Registers or replaces an armor temperature entry for the given registry name and identity.
	 *
	 * @param registryName the item's registry name
	 * @param temperature the temperature value to register
	 * @param identity the item identity used to match metadata
	 */
	public static void registerArmorTemperature(String registryName, float temperature, JsonItemIdentity identity)
	{
		if(!armorTemperatures.containsKey(registryName))
			armorTemperatures.put(registryName, new ArrayList<JsonTemperatureIdentity>());

		final List<JsonTemperatureIdentity> currentList = armorTemperatures.get(registryName);

		JsonTemperatureIdentity result = new JsonTemperatureIdentity(temperature, identity);

		for(int i=0; i<currentList.size(); i++)
		{
			JsonTemperatureIdentity jtm = currentList.get(i);
			if(jtm.matches(identity))
			{
				currentList.set(i, result);
				return;
			}
		}

		currentList.add(result);
	}

	/**
	 * Registers a block temperature entry with optional block state properties.
	 * If properties are specified and an existing entry matches them, that entry is replaced.
	 * If no properties are specified and an existing entry with properties exists, registration fails.
	 *
	 * @param block the block to register
	 * @param temperature the temperature value to register
	 * @param properties optional block state property values to match
	 * @return true if registration succeeded, false if blocked by an existing entry with properties
	 */
	public static boolean registerBlockTemperature(Block block, float temperature, JsonPropertyValue... properties)
	{
		return registerBlockTemperature(block.getRegistryName().toString(), temperature, properties);
	}

	/**
	 * Registers a block temperature entry with optional block state properties.
	 * If properties are specified and an existing entry matches them, that entry is replaced.
	 * If no properties are specified and an existing entry with properties exists, registration fails.
	 *
	 * @param registryName the block's registry name
	 * @param temperature the temperature value to register
	 * @param properties optional block state property values to match
	 * @return true if registration succeeded, false if blocked by an existing entry with properties
	 */
	public static boolean registerBlockTemperature(String registryName, float temperature, JsonPropertyValue... properties)
	{
		if(!blockTemperatures.containsKey(registryName))
			blockTemperatures.put(registryName, new ArrayList<JsonPropertyTemperature>());

		final List<JsonPropertyTemperature> currentList = blockTemperatures.get(registryName);
		JsonPropertyTemperature result = new JsonPropertyTemperature(temperature,properties);

		if(properties.length>0)
		{
			//With property
			for(int i=0;i<currentList.size();i++)
			{
				JsonPropertyTemperature jpt = currentList.get(i);
				if(jpt.matchesDescribedProperties(properties))
				{
					currentList.set(i, result);
					return true;
				}
			}

			currentList.add(result);
			return true;
		}
		else
		{
			//No property
			//Do NOT interfere with it if one with a property specification exists, and return false

			for(int i=0;i<currentList.size();i++)
			{
				JsonPropertyTemperature jpt = currentList.get(i);
				if(jpt.properties.keySet().size() > 0)
				{
					return false;
				}
			}

			//Okay, none with properties got found, go through it again and look for the one to replace as usual
			for(int i=0;i<currentList.size();i++)
			{
				JsonPropertyTemperature jpt = currentList.get(i);
				if(jpt.properties.keySet().size() == 0)
				{
					currentList.set(i, result);
					return true;
				}
			}

			currentList.add(result);
			return true;
		}

	}

	/**
	 * Registers a fluid temperature by fluid name.
	 * If a temperature already exists for the fluid, it is replaced.
	 *
	 * @param fluidName the fluid's registry name
	 * @param temperature the temperature value to register
	 */
	public static void registerFluidTemperature(String fluidName, float temperature)
	{
		fluidTemperatures.put(fluidName, new JsonTemperature(temperature));
	}

	/**
	 * Registers a consumable's temperature effect for a specified group.
	 * Uses the item's registry name and metadata to identify the consumable.
	 *
	 * @param group the consumable group
	 * @param stack the item stack
	 * @param temperature the temperature value to apply
	 * @param duration the effect duration in ticks
	 */
	public static void registerConsumableTemperature(String group, ItemStack stack, float temperature, int duration)
	{
		String registryName = stack.getItem().getRegistryName().toString();

		int metadata = -1;
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();

		registerConsumableTemperature(group, registryName, temperature, duration, new JsonItemIdentity(metadata));
	}

	/**
	 * Registers or replaces a consumable temperature entry for the given group, registry name, and identity.
	 *
	 * @param group the consumable group
	 * @param registryName the item's registry name
	 * @param temperature the temperature value to apply
	 * @param duration the effect duration in ticks
	 * @param identity the item identity used to match metadata
	 */
	public static void registerConsumableTemperature(String group, String registryName, float temperature, int duration, JsonItemIdentity identity)
	{
		if(!consumableTemperature.containsKey(registryName))
			consumableTemperature.put(registryName, new ArrayList<JsonConsumableTemperature>());

		final List<JsonConsumableTemperature> currentList = consumableTemperature.get(registryName);

		JsonConsumableTemperature result = new JsonConsumableTemperature(group, temperature, duration, identity);

		for(int i=0; i<currentList.size(); i++)
		{
			JsonConsumableTemperature jct = currentList.get(i);
			if(jct.matches(identity))
			{
				currentList.set(i, result);
				return;
			}
		}

		currentList.add(result);
	}

	/**
	 * Registers thirst values for a consumable item using its registry name and metadata.
	 *
	 * @param stack the item stack
	 * @param amount the thirst amount restored
	 * @param saturation the thirst saturation restored
	 * @param thirstyChance the chance that consuming the item causes thirst
	 */
	public static void registerConsumableThirst(ItemStack stack, int amount, float saturation, float thirstyChance)
	{
		String registryName = stack.getItem().getRegistryName().toString();

		int metadata = -1;
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();

		registerConsumableThirst(stack.getItem().getRegistryName().toString(), amount, saturation, thirstyChance, new JsonItemIdentity(metadata));
	}

	/**
	 * Registers or replaces a consumable thirst entry for the given registry name and identity.
	 *
	 * @param registryName the item's registry name
	 * @param amount the thirst amount restored
	 * @param saturation the thirst saturation restored
	 * @param thirstyChance the chance that consuming the item causes thirst
	 * @param identity the item identity used to match metadata
	 */
	public static void registerConsumableThirst(String registryName, int amount, float saturation, float thirstyChance, JsonItemIdentity identity)
	{
		if(!consumableThirst.containsKey(registryName))
			consumableThirst.put(registryName, new ArrayList<JsonConsumableThirst>());

		final List<JsonConsumableThirst> currentList = consumableThirst.get(registryName);

		JsonConsumableThirst result = new JsonConsumableThirst(amount, saturation, thirstyChance, identity);

		for(int i=0; i<currentList.size(); i++)
		{
			JsonConsumableThirst jct = currentList.get(i);
			if(jct.matches(identity))
			{
				currentList.set(i, result);
				return;
			}
		}

		currentList.add(result);
	}

	/**
	 * Registers a held item temperature modifier using its registry name and metadata.
	 *
	 * @param stack the item stack
	 * @param temperature the temperature value to apply while held
	 */
	public static void registerHeldItem(ItemStack stack, float temperature)
	{
		String registryName = stack.getItem().getRegistryName().toString();

		int metadata = -1;
		if(stack.getHasSubtypes())
			metadata = stack.getMetadata();

		registerHeldItem(stack.getItem().getRegistryName().toString(), temperature, new JsonItemIdentity(metadata));
	}

	/**
	 * Registers or replaces a held item temperature entry for the given registry name and identity.
	 *
	 * @param registryName the item's registry name
	 * @param temperature the temperature value to apply while held
	 * @param identity the item identity used to match metadata
	 */
	public static void registerHeldItem(String registryName, float temperature, JsonItemIdentity identity)
	{
		if(!heldItemTemperatures.containsKey(registryName))
			heldItemTemperatures.put(registryName, new ArrayList<JsonTemperatureIdentity>());

		final List<JsonTemperatureIdentity> currentList = heldItemTemperatures.get(registryName);

		JsonTemperatureIdentity result = new JsonTemperatureIdentity(temperature, identity);

		for(int i=0; i<currentList.size(); i++)
		{
			JsonTemperatureIdentity jtm = currentList.get(i);
			if(jtm.matches(identity))
			{
				currentList.set(i, result);
				return;
			}
		}

		currentList.add(result);
	}

	/**
	 * Registers a dimension temperature by dimension ID.
	 * If a temperature already exists for the dimension, it is replaced.
	 *
	 * @param dimension the dimension ID
	 * @param temperature the temperature value to register
	 */
	public static void registerDimensionTemperature(int dimension, float temperature)
	{
		registerDimensionTemperature(""+dimension, temperature);
	}

	/**
	 * Registers a dimension temperature by dimension ID as a string.
	 * If a temperature already exists for the dimension, it is replaced.
	 *
	 * @param dimensionNumber the dimension ID as a string
	 * @param temperature the temperature value to register
	 */
	public static void registerDimensionTemperature(String dimensionNumber, float temperature)
	{
		dimensionTemperature.put(dimensionNumber, new JsonTemperature(temperature));
	}
}