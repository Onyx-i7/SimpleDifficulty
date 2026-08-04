package com.charles445.simpledifficulty.api;

import com.charles445.simpledifficulty.register.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for all SimpleDifficulty blocks.
 * <p>
 * These fields are {@link RegistryObject} pointers that resolve to the actual Block
 * instances once Forge completes the registry phase.
 * <b>Important:</b> Always call {@code .get()} to retrieve the actual Block instance in-game.
 * </p>
 */
public class SDBlocks {

    /**
     * Map of all registered SimpleDifficulty blocks, keyed by their registry name.
     * Useful for iterating over all mod blocks.
     */
    public static final Map<String, RegistryObject<Block>> blocks = new LinkedHashMap<String, RegistryObject<Block>>() {{
        put("campfire", RegisterBlocks.CAMPFIRE);
        put("rain_collector", RegisterBlocks.RAIN_COLLECTOR);
        put("heater", RegisterBlocks.HEATER);
        put("chiller", RegisterBlocks.CHILLER);
        put("spit", RegisterBlocks.SPIT);
        put("purifiedwater_ice", RegisterBlocks.ICE_PURIFIED_WATER);
        put("saltwater_ice", RegisterBlocks.ICE_SALT_WATER);
    }};

    public static final RegistryObject<Block> campfire = RegisterBlocks.CAMPFIRE;
    public static final RegistryObject<Block> rainCollector = RegisterBlocks.RAIN_COLLECTOR;
    public static final RegistryObject<Block> heater = RegisterBlocks.HEATER;
    public static final RegistryObject<Block> chiller = RegisterBlocks.CHILLER;
    public static final RegistryObject<Block> spit = RegisterBlocks.SPIT;
    public static final RegistryObject<Block> icePurifiedWater = RegisterBlocks.ICE_PURIFIED_WATER;
    public static final RegistryObject<Block> iceSaltWater = RegisterBlocks.ICE_SALT_WATER;
}