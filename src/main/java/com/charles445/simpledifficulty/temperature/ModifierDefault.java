package com.charles445.simpledifficulty.temperature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Default temperature modifier.
 * Provides the baseline temperature for all locations.
 */
public class ModifierDefault extends ModifierBase {
    public ModifierDefault() {
        super("Default");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        return defaultTemperature;
    }
}