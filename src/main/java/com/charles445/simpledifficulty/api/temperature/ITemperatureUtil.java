package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Internal interface for temperature utility functions.
 * <p>
 * This interface is implemented by SimpleDifficulty and provides the actual
 * logic for temperature calculations. Addon developers should use the static
 * methods in {@link TemperatureUtil} instead.
 * </p>
 */
public interface ITemperatureUtil {

    /**
     * Calculates the target temperature for a player based on their environment and equipment.
     *
     * @param player The player to calculate temperature for.
     * @return The target temperature value.
     */
    int getPlayerTargetTemperature(PlayerEntity player);

    /**
     * Calculates the ambient world temperature at a specific position.
     *
     * @param world The world context.
     * @param pos The position to check.
     * @return The world temperature at the position.
     */
    int getWorldTemperature(World world, BlockPos pos);

    /**
     * Clamps a temperature value to valid bounds.
     *
     * @param temperature The temperature value to clamp.
     * @return The clamped temperature value.
     */
    int clampTemperature(int temperature);

    /**
     * Gets the TemperatureEnum category for a given temperature value.
     *
     * @param temp The temperature value.
     * @return The corresponding TemperatureEnum.
     */
    TemperatureEnum getTemperatureEnum(int temp);

    /**
     * Sets a custom temperature tag on an armor piece.
     *
     * @param stack The armor ItemStack.
     * @param temperature The temperature value to set.
     */
    void setArmorTemperatureTag(final ItemStack stack, float temperature);

    /**
     * Gets the temperature tag from an armor piece.
     *
     * @param stack The armor ItemStack.
     * @return The temperature value, or 0.0f if no tag exists.
     */
    float getArmorTemperatureTag(final ItemStack stack);

    /**
     * Removes the temperature tag from an armor piece if it exists.
     *
     * @param stack The armor ItemStack.
     */
    void removeArmorTemperatureTag(final ItemStack stack);
}