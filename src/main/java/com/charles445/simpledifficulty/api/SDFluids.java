package com.charles445.simpledifficulty.api;

import com.charles445.simpledifficulty.block.BlockFluidBasic;
import com.charles445.simpledifficulty.block.BlockFluidBasicMixable;
import net.minecraftforge.fluids.Fluid;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for all SimpleDifficulty fluids and their corresponding blocks.
 * <p>
 * This class holds references to all fluids added by the mod. These fields are initialized
 * during the mod's registration phase (preInit) and should not be modified by addons.
 * </p>
 * <p>
 * <b>Note:</b> The {@code fluids} map contains the {@link Fluid} instances (used for fluid containers,
 * tanks, etc.), while the {@code fluidBlocks} map contains the actual world blocks that represent
 * these fluids in the world.
 * </p>
 *
 */
public class SDFluids {

    /**
     * A map of all registered SimpleDifficulty fluids, keyed by their registry name.
     * <p>
     * This map contains {@link Fluid} instances that can be used with fluid containers,
     * tanks, and other fluid-handling systems.
     * </p>
     * <p>
     * <b>Do not modify this map directly.</b> It is intended for read-only access.
     * </p>
     */
    public static final Map<String, Fluid> fluids = new LinkedHashMap<>();

    /**
     * A map of all registered SimpleDifficulty fluid blocks, keyed by their registry name.
     * <p>
     * This map contains the actual world blocks that represent these fluids in the world.
     * These blocks handle rendering, physics, and interactions with other blocks.
     * </p>
     * <p>
     * <b>Do not modify this map directly.</b> It is intended for read-only access.
     * </p>
     */
    public static final Map<String, BlockFluidBasic> fluidBlocks = new LinkedHashMap<>();

    /**
     * The purified water fluid. Safe to drink and provides hydration.
     * <p>
     * Can be obtained from rain collectors or by melting purified water ice.
     * </p>
     */
    public static Fluid purifiedWater;

    /**
     * The salt water fluid. Unsafe to drink directly (causes thirst).
     * <p>
     * Found in ocean biomes and can be desalinated to obtain purified water.
     * </p>
     */
    public static Fluid saltWater;

    /**
     * The purified water fluid block. Represents purified water in the world.
     * <p>
     * This is a {@link BlockFluidBasicMixable}, meaning it can mix with other fluids
     * (e.g., salt water mixing to create brine or other combinations).
     * </p>
     */
    public static BlockFluidBasicMixable blockPurifiedWater;

    /**
     * The salt water fluid block. Represents salt water in the world.
     * <p>
     * This is a standard {@link BlockFluidBasic} that does not have special mixing behavior.
     * </p>
     */
    public static BlockFluidBasic blockSaltWater;
}
