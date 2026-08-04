package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Temperature modifier based on altitude (Y coordinate).
 * Higher altitudes are colder, lower altitudes are warmer.
 * Only affects surface worlds (Overworld).
 */
public class ModifierAltitude extends ModifierBase {
    public ModifierAltitude() {
        super("Altitude");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        if (!isSurfaceWorld(world))
            return 0.0f;

        // 0 - 64 = (1 to 0) * multiplier + 1      (-4 to -1)
        // 64 - 128 = (0 to -1) * multiplier + 1    (-1 to -2)
        // 128 - 192 = (-1 to -2) * multiplier + 1  (-2 to -5)
        // 192 - 256 = (-2 to -3) * multiplier + 1  (-5 to -8)

        return -1.0f * (Math.abs(((64.0f - pos.getY()) / 64.0f * ModConfig.SERVER.altitudeMultiplier.get()) + 1.0f));
    }
}