package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Temperature modifier based on time of day.
 * Daytime is warmer, nighttime is cooler.
 * Only affects surface worlds (Overworld).
 */
public class ModifierTime extends ModifierBase {
    public ModifierTime() {
        super("Time");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        // Overworld only
        if (!isSurfaceWorld(world))
            return 0.0f;

        // 0 to 23999
        long time = world.getDayTime() % 24000;

        // Day = 0 - 11999
        // Night = 12000 - 23999
        // Noon = 6000
        // Midnight = 18000

        // Daytime and Nighttime config
        if (time < 12000 && !ModConfig.SERVER.timeTemperatureDay.get())
            return 0.0f;

        if (time >= 12000 && !ModConfig.SERVER.timeTemperatureNight.get())
            return 0.0f;

        // Calculate time temperature
        float timetemperature = (Math.abs(((time % 12000.0f) - 6000.0f) / 6000.0f) - 1.0f) * ModConfig.SERVER.timeMultiplier.get();

        // Daytime sign flip
        if (time < 12000)
            timetemperature *= -1.0f;

        // Biome multiplier
        float biomeMultiplier = 1.0f + (Math.abs(normalizeToPlusMinus(getTempForBiome(world.getBiome(pos), pos))) * (ModConfig.SERVER.timeBiomeMultiplier.get().floatValue() - 1.0f));
        timetemperature *= biomeMultiplier;

        // Shade calculation
        int shadeConf = ModConfig.SERVER.timeTemperatureShade.get();
        if (timetemperature > 0 && shadeConf != 0 && !world.canSeeSky(pos) && !world.canSeeSky(pos.above())) {
            timetemperature = Math.max(0, timetemperature + shadeConf);
        }

        // Underground effect and result
        return applyUndergroundEffect(timetemperature, world, pos);
    }
}