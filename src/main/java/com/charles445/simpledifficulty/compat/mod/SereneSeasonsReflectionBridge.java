package com.charles445.simpledifficulty.compat.mod;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Stub for Serene Seasons compatibility.
 * TODO: Implement full compatibility with Serene Seasons
 */
public class SereneSeasonsReflectionBridge {
    
    public static void init() {
        // Stub
    }
    
    public static float getTemperatureSafe(World world, Biome biome, BlockPos pos) {
        // Fallback to vanilla biome temperature
        return biome.getTemperature(pos);
    }
}