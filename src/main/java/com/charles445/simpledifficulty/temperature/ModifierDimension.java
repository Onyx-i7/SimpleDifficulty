package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Temperature modifier based on the current dimension.
 * Allows pack makers to set custom base temperatures for dimensions.
 */
public class ModifierDimension extends ModifierBase {
    public ModifierDimension() {
        super("Dimension");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        String dimensionKey = world.dimension().location().toString();
        JsonTemperature tempInfo = JsonConfig.dimensionTemperature.get(dimensionKey);
        
        if (tempInfo != null) {
            return tempInfo.temperature;
        }

        return 0.0f;
    }
}