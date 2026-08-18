package com.charles445.simpledifficulty.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for all SimpleDifficulty items and armor materials.
 * <p>
 * This class holds references to all items added by the mod. These fields are initialized
 * during the mod's registration phase (preInit) and should not be modified by addons.
 * </p>
 *
 */
public class SDItems {

    /**
     * A map of all registered SimpleDifficulty armor materials, keyed by their name.
     * <p>
     * <b>Do not modify this map directly.</b> It is intended for read-only access.
     * </p>
     */
    public static final Map<String, ArmorMaterial> armorMaterials = new LinkedHashMap<>();

    /**
     * A map of all registered SimpleDifficulty items, keyed by their registry name.
     * <p>
     * This map is populated during mod initialization and can be used by addons to
     * iterate over or look up specific items by name.
     * </p>
     * <p>
     * <b>Do not modify this map directly.</b> It is intended for read-only access.
     * </p>
     */
    public static final Map<String, Item> items = new LinkedHashMap<>();

    // ============================================
    // Armor Materials
    // ============================================

    /**
     * The wool armor material. Provides warmth and temperature insulation.
     */
    public static ArmorMaterial woolArmorMaterial;

    /**
     * The ice armor material. Provides cooling and heat resistance.
     */
    public static ArmorMaterial iceArmorMaterial;

    // ============================================
    // Hydration Items
    // ============================================

    /**
     * The canteen item. A basic container for storing and drinking water.
     * <p>
     * Holds a limited amount of water and can be refilled from water sources.
     * </p>
     */
    public static Item canteen;

    /**
     * The iron canteen item. An upgraded container with higher capacity.
     * <p>
     * Holds more water than the standard canteen and is more durable.
     * </p>
     */
    public static Item ironCanteen;

    /**
     * The charcoal filter item. Used to purify contaminated water.
     * <p>
     * Can be applied to water containers to remove parasites and impurities.
     * </p>
     */
    public static Item charcoalFilter;

    /**
     * The juice item. A consumable drink that restores thirst.
     * <p>
     * Can be crafted from fruits and provides hydration with optional saturation.
     * </p>
     */
    public static Item juice;

    /**
     * The purified water bottle item. Safe to drink and provides hydration.
     * <p>
     * Obtained by purifying water through filters or boiling.
     * </p>
     */
    public static Item purifiedWaterBottle;

    /**
     * The salt water bottle item. Unsafe to drink directly.
     * <p>
     * Drinking salt water increases thirst instead of restoring it.
     * Must be desalinated before consumption.
     * </p>
     */
    public static Item saltWaterBottle;

    // ============================================
    // Resource Items
    // ============================================

    /**
     * The ice chunk item. Dropped when mining ice blocks.
     * <p>
     * Can be used for crafting or as a cooling resource.
     * </p>
     */
    public static Item ice_chunk;

    /**
     * The magma chunk item. Dropped when mining magma blocks.
     * <p>
     * Can be used for crafting or as a heating resource.
     * </p>
     */
    public static Item magma_chunk;

    // ============================================
    // Utility Items
    // ============================================

    /**
     * The thermometer item. Displays the current ambient temperature.
     * <p>
     * Useful for monitoring temperature changes in different biomes and altitudes.
     * </p>
     */
    public static Item thermometer;

    // ============================================
    // Wool Armor
    // ============================================

    /** Wool armor helmet. Provides warmth insulation for the head. */
    public static Item wool_helmet;

    /** Wool armor chestplate. Provides warmth insulation for the torso. */
    public static Item wool_chestplate;

    /** Wool armor leggings. Provides warmth insulation for the legs. */
    public static Item wool_leggings;

    /** Wool armor boots. Provides warmth insulation for the feet. */
    public static Item wool_boots;

    // ============================================
    // Ice Armor
    // ============================================

    /** Ice armor helmet. Provides cooling for the head. */
    public static Item ice_helmet;

    /** Ice armor chestplate. Provides cooling for the torso. */
    public static Item ice_chestplate;

    /** Ice armor leggings. Provides cooling for the legs. */
    public static Item ice_leggings;

    /** Ice armor boots. Provides cooling for the feet. */
    public static Item ice_boots;
}
