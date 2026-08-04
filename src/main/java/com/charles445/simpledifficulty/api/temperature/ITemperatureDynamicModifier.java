package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Temperature modifier that runs after standard modifiers.
 * <p>
 * Dynamic modifiers can take the accumulated temperature and replace it with a new value,
 * allowing for smart decision-making. However, they should be used sparingly as multiple
 * dynamic modifiers may conflict and produce unexpected results.
 * </p>
 */
public interface ITemperatureDynamicModifier {

    /**
     * Temperature change that relies on the player.
     * Takes the current accumulated temperature and replaces it with a new value.
     *
     * @param player The player being affected.
     * @param currentTemperature The current accumulated temperature value.
     * @return The new temperature value to use.
     */
    float applyDynamicPlayerInfluence(PlayerEntity player, float currentTemperature);

    /**
     * Temperature change that relies on the world.
     * Takes the current accumulated temperature and replaces it with a new value.
     *
     * @param world The world context.
     * @param pos The position to calculate temperature influence for.
     * @param currentTemperature The current accumulated temperature value.
     * @return The new temperature value to use.
     */
    float applyDynamicWorldInfluence(World world, BlockPos pos, float currentTemperature);

    /**
     * Gets the unique name of this modifier.
     * <p>
     * It is recommended to include your Mod ID in the name to avoid conflicts.
     * </p>
     *
     * @return The unique modifier name.
     */
    String getName();
}