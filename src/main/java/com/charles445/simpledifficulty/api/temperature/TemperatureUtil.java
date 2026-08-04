package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Public utility class for temperature-related operations.
 * <p>
 * This class provides static methods for addon developers to interact with
 * the temperature system. The actual implementation is handled internally by SimpleDifficulty.
 * </p>
 */
public class TemperatureUtil {

    /**
     * Internal implementation of temperature utilities.
     * Initialized by SimpleDifficulty during mod loading.
     */
    public static ITemperatureUtil internal;

    /**
     * Calculates the target temperature of a player.
     * <p>
     * This is the target temperature, not the player's actual temperature!
     * Calling this infrequently is recommended for best performance.
     * To get the player's actual temperature, check the player's temperature capability via {@link com.charles445.simpledifficulty.api.SDCapabilities}.
     * </p>
     *
     * @param player The player to calculate temperature for.
     * @return The player's target temperature.
     */
    public static int getPlayerTargetTemperature(PlayerEntity player) {
        return internal.getPlayerTargetTemperature(player);
    }

    /**
     * Calculates the ambient world temperature at a position.
     * <p>
     * Calling this infrequently is recommended for best performance.
     * </p>
     *
     * @param world The world context.
     * @param pos The position to check.
     * @return The world temperature at the position.
     */
    public static int getWorldTemperature(World world, BlockPos pos) {
        return internal.getWorldTemperature(world, pos);
    }

    /**
     * Clamps a temperature value to fit within TemperatureEnum bounds.
     *
     * @param temperature The temperature value to clamp.
     * @return The clamped temperature value.
     */
    public static int clampTemperature(int temperature) {
        return internal.clampTemperature(temperature);
    }

    /**
     * Gets the TemperatureEnum category for a given temperature value.
     *
     * @param temperature The temperature value.
     * @return The corresponding TemperatureEnum.
     */
    public static TemperatureEnum getTemperatureEnum(int temperature) {
        return internal.getTemperatureEnum(temperature);
    }

    /**
     * Sets a temperature tag on an armor piece to make it heat or cool the wearer.
     *
     * @param stack The armor ItemStack.
     * @param temperature The temperature value to set (positive for heating, negative for cooling).
     */
    public static void setArmorTemperatureTag(final ItemStack stack, float temperature) {
        internal.setArmorTemperatureTag(stack, temperature);
    }

    /**
     * Gets the temperature tag from an armor piece.
     *
     * @param stack The armor ItemStack.
     * @return The temperature value, or 0.0f if no tag exists.
     */
    public static float getArmorTemperatureTag(final ItemStack stack) {
        return internal.getArmorTemperatureTag(stack);
    }

    /**
     * Removes the temperature tag from an armor piece if it exists.
     *
     * @param stack The armor ItemStack.
     */
    public static void removeArmorTemperatureTag(final ItemStack stack) {
        internal.removeArmorTemperatureTag(stack);
    }
}