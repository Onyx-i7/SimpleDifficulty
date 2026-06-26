package com.charles445.simpledifficulty.api;

import net.minecraft.enchantment.Enchantment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for all SimpleDifficulty enchantments.
 * <p>
 * This class holds references to all enchantments added by the mod. These fields are initialized
 * during the mod's registration phase (preInit) and should not be modified by addons.
 * </p>
 *
 */
public class SDEnchantments {

    /**
     * A map of all registered SimpleDifficulty enchantments, keyed by their registry name.
     * <p>
     * This map is populated during mod initialization and can be used by addons to
     * iterate over or look up specific enchantments by name.
     * </p>
     * <p>
     * <b>Do not modify this map directly.</b> It is intended for read-only access.
     * </p>
     */
    public static final Map<String, Enchantment> enchantments = new LinkedHashMap<>();

    /**
     * The chilling enchantment. Applied to armor to reduce the wearer's body temperature.
     * <p>
     * Useful for surviving in hot biomes or near heat sources like campfires and heaters.
     * </p>
     */
    public static Enchantment chilling;

    /**
     * The heating enchantment. Applied to armor to increase the wearer's body temperature.
     * <p>
     * Useful for surviving in cold biomes or near cold sources like chillers.
     * </p>
     */
    public static Enchantment heating;
}
