package com.charles445.simpledifficulty.api.config;

import com.charles445.simpledifficulty.api.config.json.*;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API class for registering JSON-based configuration overrides for various game elements.
 * <p>
 * This class allows addon developers to register custom temperature values for armor,
 * blocks, fluids, consumables, held items, and dimensions.
 * </p>
 */
public class JsonConfig {
    public static Map<String, List<JsonTemperatureIdentity>> armorTemperatures = new HashMap<>();
    public static Map<String, List<JsonPropertyTemperature>> blockTemperatures = new HashMap<>();
    public static Map<String, List<JsonConsumableTemperature>> consumableTemperature = new HashMap<>();
    public static Map<String, List<JsonConsumableThirst>> consumableThirst = new HashMap<>();
    public static Map<String, JsonTemperature> dimensionTemperature = new HashMap<>();
    public static Map<String, JsonTemperature> fluidTemperatures = new HashMap<>();
    public static Map<String, List<JsonTemperatureIdentity>> heldItemTemperatures = new HashMap<>();

    // ============================================
    // Armor
    // ============================================

    /**
     * Registers a temperature value for an armor item.
     *
     * @param stack The armor ItemStack.
     * @param temperature The temperature value to apply.
     */
    public static void registerArmorTemperature(ItemStack stack, float temperature) {
        String registryName = stack.getItem().getRegistryName().toString();
        registerArmorTemperature(registryName, temperature, new JsonItemIdentity());
    }

    /**
     * Registers a temperature value for an armor item by registry name.
     *
     * @param registryName The item's registry name (e.g., "minecraft:diamond_helmet").
     * @param temperature The temperature value to apply.
     */
    public static void registerArmorTemperature(String registryName, float temperature) {
        registerArmorTemperature(registryName, temperature, new JsonItemIdentity());
    }

    /**
     * Registers a temperature value for an armor item with a specific identity.
     *
     * @param registryName The item's registry name.
     * @param temperature The temperature value to apply.
     * @param identity The item identity for matching specific variants.
     */
    public static void registerArmorTemperature(String registryName, float temperature, JsonItemIdentity identity) {
        armorTemperatures.computeIfAbsent(registryName, k -> new ArrayList<>());

        final List<JsonTemperatureIdentity> currentList = armorTemperatures.get(registryName);
        JsonTemperatureIdentity result = new JsonTemperatureIdentity(temperature, identity);

        for (int i = 0; i < currentList.size(); i++) {
            JsonTemperatureIdentity jtm = currentList.get(i);
            if (jtm.matches(identity)) {
                currentList.set(i, result);
                return;
            }
        }

        currentList.add(result);
    }

    // ============================================
    // Blocks
    // ============================================

    /**
     * Registers a temperature value for a block.
     *
     * @param block The block to register.
     * @param temperature The temperature value to apply.
     * @param properties Optional block state properties to match.
     * @return true if registration was successful.
     */
    public static boolean registerBlockTemperature(Block block, float temperature, JsonPropertyValue... properties) {
        return registerBlockTemperature(block.getRegistryName().toString(), temperature, properties);
    }

    /**
     * Registers a temperature value for a block by registry name.
     *
     * @param registryName The block's registry name.
     * @param temperature The temperature value to apply.
     * @param properties Optional block state properties to match.
     * @return true if registration was successful.
     */
    public static boolean registerBlockTemperature(String registryName, float temperature, JsonPropertyValue... properties) {
        blockTemperatures.computeIfAbsent(registryName, k -> new ArrayList<>());

        final List<JsonPropertyTemperature> currentList = blockTemperatures.get(registryName);
        JsonPropertyTemperature result = new JsonPropertyTemperature(temperature, properties);

        if (properties.length > 0) {
            // With property
            for (int i = 0; i < currentList.size(); i++) {
                JsonPropertyTemperature jpt = currentList.get(i);
                if (jpt.matchesDescribedProperties(properties)) {
                    currentList.set(i, result);
                    return true;
                }
            }

            currentList.add(result);
            return true;
        } else {
            // No property - do NOT interfere if one with property specification exists
            for (JsonPropertyTemperature jpt : currentList) {
                if (!jpt.properties.isEmpty()) {
                    return false;
                }
            }

            // Look for existing entry without properties to replace
            for (int i = 0; i < currentList.size(); i++) {
                JsonPropertyTemperature jpt = currentList.get(i);
                if (jpt.properties.isEmpty()) {
                    currentList.set(i, result);
                    return true;
                }
            }

            currentList.add(result);
            return true;
        }
    }

    // ============================================
    // Fluid
    // ============================================

    /**
     * Registers a temperature value for a fluid.
     *
     * @param fluidName The fluid's registry name.
     * @param temperature The temperature value to apply.
     */
    public static void registerFluidTemperature(String fluidName, float temperature) {
        fluidTemperatures.put(fluidName, new JsonTemperature(temperature));
    }

    // ============================================
    // Consumable Temperature
    // ============================================

    /**
     * Registers a temperature effect for a consumable item.
     *
     * @param group The effect group name.
     * @param stack The consumable ItemStack.
     * @param temperature The temperature change to apply.
     * @param duration The duration of the effect in ticks.
     */
    public static void registerConsumableTemperature(String group, ItemStack stack, float temperature, int duration) {
        String registryName = stack.getItem().getRegistryName().toString();
        registerConsumableTemperature(group, registryName, temperature, duration, new JsonItemIdentity());
    }

    /**
     * Registers a temperature effect for a consumable item by registry name.
     *
     * @param group The effect group name.
     * @param registryName The item's registry name.
     * @param temperature The temperature change to apply.
     * @param duration The duration of the effect in ticks.
     * @param identity The item identity for matching specific variants.
     */
    public static void registerConsumableTemperature(String group, String registryName, float temperature, int duration, JsonItemIdentity identity) {
        consumableTemperature.computeIfAbsent(registryName, k -> new ArrayList<>());

        final List<JsonConsumableTemperature> currentList = consumableTemperature.get(registryName);
        JsonConsumableTemperature result = new JsonConsumableTemperature(group, temperature, duration, identity);

        for (int i = 0; i < currentList.size(); i++) {
            JsonConsumableTemperature jct = currentList.get(i);
            if (jct.matches(identity)) {
                currentList.set(i, result);
                return;
            }
        }

        currentList.add(result);
    }

    // ============================================
    // Consumable Thirst
    // ============================================

    /**
     * Registers thirst restoration values for a consumable item.
     *
     * @param stack The consumable ItemStack.
     * @param amount The amount of thirst to restore.
     * @param saturation The saturation to restore.
     * @param thirstyChance The chance (0.0-1.0) of getting the thirsty effect.
     */
    public static void registerConsumableThirst(ItemStack stack, int amount, float saturation, float thirstyChance) {
        String registryName = stack.getItem().getRegistryName().toString();
        registerConsumableThirst(registryName, amount, saturation, thirstyChance, new JsonItemIdentity());
    }

    /**
     * Registers thirst restoration values for a consumable item by registry name.
     *
     * @param registryName The item's registry name.
     * @param amount The amount of thirst to restore.
     * @param saturation The saturation to restore.
     * @param thirstyChance The chance (0.0-1.0) of getting the thirsty effect.
     * @param identity The item identity for matching specific variants.
     */
    public static void registerConsumableThirst(String registryName, int amount, float saturation, float thirstyChance, JsonItemIdentity identity) {
        consumableThirst.computeIfAbsent(registryName, k -> new ArrayList<>());

        final List<JsonConsumableThirst> currentList = consumableThirst.get(registryName);
        JsonConsumableThirst result = new JsonConsumableThirst(amount, saturation, thirstyChance, identity);

        for (int i = 0; i < currentList.size(); i++) {
            JsonConsumableThirst jct = currentList.get(i);
            if (jct.matches(identity)) {
                currentList.set(i, result);
                return;
            }
        }

        currentList.add(result);
    }

    // ============================================
    // Held Item
    // ============================================

    /**
     * Registers a temperature value for a held item.
     *
     * @param stack The held ItemStack.
     * @param temperature The temperature value to apply.
     */
    public static void registerHeldItem(ItemStack stack, float temperature) {
        String registryName = stack.getItem().getRegistryName().toString();
        registerHeldItem(registryName, temperature, new JsonItemIdentity());
    }

    /**
     * Registers a temperature value for a held item by registry name.
     *
     * @param registryName The item's registry name.
     * @param temperature The temperature value to apply.
     * @param identity The item identity for matching specific variants.
     */
    public static void registerHeldItem(String registryName, float temperature, JsonItemIdentity identity) {
        heldItemTemperatures.computeIfAbsent(registryName, k -> new ArrayList<>());

        final List<JsonTemperatureIdentity> currentList = heldItemTemperatures.get(registryName);
        JsonTemperatureIdentity result = new JsonTemperatureIdentity(temperature, identity);

        for (int i = 0; i < currentList.size(); i++) {
            JsonTemperatureIdentity jtm = currentList.get(i);
            if (jtm.matches(identity)) {
                currentList.set(i, result);
                return;
            }
        }

        currentList.add(result);
    }

    // ============================================
    // Dimension
    // ============================================

    /**
     * Registers a base temperature for a dimension.
     *
     * @param dimension The dimension ID.
     * @param temperature The base temperature value.
     */
    public static void registerDimensionTemperature(int dimension, float temperature) {
        registerDimensionTemperature(String.valueOf(dimension), temperature);
    }

    /**
     * Registers a base temperature for a dimension by string key.
     *
     * @param dimensionKey The dimension key (e.g., "minecraft:overworld").
     * @param temperature The base temperature value.
     */
    public static void registerDimensionTemperature(String dimensionKey, float temperature) {
        dimensionTemperature.put(dimensionKey, new JsonTemperature(temperature));
    }
}