package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Abstract base class for temperature modifiers.
 * Provides common functionality and helper methods for all modifiers.
 */
public abstract class ModifierBase implements ITemperatureModifier {
    
    /* Organization of modifiers:
     * 
     * Unique World Modifiers (Ambience, Intangible Environment, Natural):
     * - Altitude, Biome, Default, Season, Snow, Time, Wet
     * 
     * Proximity World Modifiers (Blocks, Tile Entities, Unnatural, Radiates Heat):
     * - Proximity (Blocks, Tile Entities)
     * 
     * Unique Player Modifiers (Armor, Items, Effects, State):
     * - Armor, Sprint, Temporary
     */

    private final String name;
    protected final float defaultTemperature;

    protected ModifierBase(String name) {
        this.name = name;
        this.defaultTemperature = (TemperatureEnum.NORMAL.getUpperBound() + TemperatureEnum.COLD.getUpperBound()) / 2.0f;
    }

    @Override
    public float getPlayerInfluence(PlayerEntity player) {
        return 0.0f;
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        return 0.0f;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Checks if the world is a surface world (Overworld).
     */
    protected boolean isSurfaceWorld(World world) {
        return world.dimension() == World.OVERWORLD;
    }

    /**
     * Applies the underground effect to a temperature value.
     * Underground areas are insulated from surface temperature changes.
     */
    protected float applyUndergroundEffect(float temperature, World world, BlockPos pos) {
        // Y 64+ is always unchanged temperature
        if (pos.getY() >= 64)
            return temperature;

        if (!ModConfig.SERVER.undergroundEffect.get() || !isSurfaceWorld(world))
            return temperature;

        // Check if the position can see the sky (not underground)
        if (world.canSeeSky(pos) || world.canSeeSky(pos.above()))
            return temperature;

        // The position is underground, apply the effect
        int cutoff = ModConfig.SERVER.undergroundEffectCutoff.get();

        // If Y is past cutoff, or if cutoff is 64, apply the effect fully
        if (pos.getY() <= cutoff || cutoff == 64)
            return 0.0f;

        return temperature * (float) (pos.getY() - cutoff) / (64.0f - cutoff);
    }

    /**
     * Gets the normalized temperature for a biome (0.0 to 1.0).
     */
    protected float getTempForBiome(Biome biome, BlockPos pos) {
        // Take a biome's temperature, clamp it, and normalize to 0-1 range
        return MathHelper.clamp(biome.getTemperature(pos), 0.0f, 1.35f) / 1.35f;
    }

    /**
     * Converts a 0-1 range to -1 to +1 range.
     */
    protected float normalizeToPlusMinus(float value) {
        return (value * 2.0f) - 1.0f;
    }
}