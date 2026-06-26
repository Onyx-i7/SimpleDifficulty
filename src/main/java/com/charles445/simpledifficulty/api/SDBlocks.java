package com.charles445.simpledifficulty.api;

import net.minecraft.block.Block;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for all SimpleDifficulty blocks.
 * <p>
 * This class holds references to all blocks added by the mod. These fields are initialized
 * during the mod's registration phase (preInit) and should not be modified by addons.
 * </p>
 * <p>
 * <b>Note:</b> Fluid blocks are stored in {@link SDFluids}, not in this class.
 * </p>
 */
public class SDBlocks {

    /**
     * A map of all registered SimpleDifficulty blocks, keyed by their registry name.
     * <p>
     * This map is populated during mod initialization and can be used by addons to
     * iterate over or look up specific blocks by name.
     * </p>
     * <p>
     * <b>Do not modify this map directly.</b> It is intended for read-only access.
     * </p>
     */
    public static final Map<String, Block> blocks = new LinkedHashMap<>();

    /**
     * The campfire block. Used for cooking food and providing warmth.
     * <p>
     * Can be extinguished by rain or water, and interacts with Weather2 weather systems.
     * </p>
     */
    public static Block campfire;

    /**
     * The rain collector block. Collects rainwater over time to fill containers.
     * <p>
     * Interacts with Weather2 weather systems to detect rain events.
     * </p>
     */
    public static Block rainCollector;

    /**
     * The heater block. Increases temperature in a radius around it.
     * <p>
     * Requires fuel to operate and affects nearby players' temperature.
     * </p>
     */
    public static Block heater;

    /**
     * The chiller block. Decreases temperature in a radius around it.
     * <p>
     * Requires fuel to operate and affects nearby players' temperature.
     * </p>
     */
    public static Block chiller;

    /**
     * The spit block. Used for cooking food over a campfire.
     * <p>
     * Must be placed above a campfire to function.
     * </p>
     */
    public static Block spit;

    /**
     * The purified water ice block. Melts into purified water when heated.
     * <p>
     * Generated in cold biomes and can be harvested for drinking water.
     * </p>
     */
    public static Block icePurifiedWater;

    /**
     * The salt water ice block. Melts into salt water when heated.
     * <p>
     * Generated in ocean biomes near water sources.
     * </p>
     */
    public static Block iceSaltWater;
}
