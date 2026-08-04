package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Standard temperature modifier that contributes to the total temperature calculation.
 * <p>
 * Multiple standard modifiers can coexist and their influences are accumulated.
 * </p>
 */
public interface ITemperatureModifier {

    /**
     * Calculates temperature influence based on the player's state.
     *
     * @param player The player being affected.
     * @return The temperature influence value to add.
     */
    float getPlayerInfluence(PlayerEntity player);

    /**
     * Calculates temperature influence based on the world environment.
     *
     * @param world The world context.
     * @param pos The position to calculate temperature influence for.
     * @return The temperature influence value to add.
     */
    float getWorldInfluence(World world, BlockPos pos);

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