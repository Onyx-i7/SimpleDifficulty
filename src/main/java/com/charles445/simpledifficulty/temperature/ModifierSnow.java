package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Temperature modifier when it's snowing at the player's location.
 */
public class ModifierSnow extends ModifierBase {
    public ModifierSnow() {
        super("Snow");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        if (world.isRaining() && world.canSeeSky(pos)) {
            Biome biome = world.getBiome(pos);
            
            if (biome.shouldSnow(world, pos)) {
                // Snow enabled variant
                return ModConfig.SERVER.snowValue.get();
            } else {
                // Check if snow can form at this location
                if (biome.shouldSnow(world, pos))
                    return ModConfig.SERVER.snowValue.get();
                else
                    return 0.0f;
            }
        } else {
            return 0.0f;
        }
    }
}