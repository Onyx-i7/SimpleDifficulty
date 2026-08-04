package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Temperature modifier based on biome temperature.
 * Samples 9 points around the player for smooth biome transitions.
 */
public class ModifierBiome extends ModifierBase {
    public ModifierBiome() {
        super("Biome");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        // Takes 9 points in an octagon shape around the player and averages the biome temperature
        // This allows for biome temperatures to blend at their borders

        float biomeAverage =
                (getTempForBiome(world.getBiome(pos.offset(10, 0, 0)).value(), pos) +
                        getTempForBiome(world.getBiome(pos.offset(-10, 0, 0)).value(), pos) +
                        getTempForBiome(world.getBiome(pos.offset(0, 0, 10)).value(), pos) +
                        getTempForBiome(world.getBiome(pos.offset(0, 0, -10)).value(), pos) +
                        getTempForBiome(world.getBiome(pos.offset(7, 0, 7)).value(), pos) +
                        getTempForBiome(world.getBiome(pos.offset(7, 0, -7)).value(), pos) +
                        getTempForBiome(world.getBiome(pos.offset(-7, 0, 7)).value(), pos) +
                        getTempForBiome(world.getBiome(pos.offset(-7, 0, -7)).value(), pos) +
                        getTempForBiome(world.getBiome(pos).value(), pos)) / 9.0f;

        // Turn the range 0-1 into -1 to +1 and apply the config multiplier
        return applyUndergroundEffect(normalizeToPlusMinus(biomeAverage) * ModConfig.SERVER.biomeMultiplier.get(), world, pos);
    }
}