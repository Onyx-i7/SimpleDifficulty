package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.util.math.BlockPos;

/**
 * Interface for tile entities that affect the temperature of nearby blocks.
 * <p>
 * Implement this interface on your TileEntity class to make it influence
 * the temperature system (e.g., heaters, chillers, campfires).
 * </p>
 */
public interface ITemperatureTileEntity {

    /**
     * Returns the temperature effect this TileEntity has on a target position.
     *
     * @param targetPos The BlockPos requesting the temperature influence.
     * @param distance The squared distance between the target position and this TileEntity.
     * @return The temperature change value (positive for heating, negative for cooling).
     */
    float getInfluence(BlockPos targetPos, double distance);
}