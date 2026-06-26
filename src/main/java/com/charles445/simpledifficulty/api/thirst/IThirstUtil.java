package com.charles445.simpledifficulty.api.thirst;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Interface defining utility methods for the Thirst system.
 * <p>
 * Addon developers can use these methods to simulate drinking, find water sources,
 * or create water buckets programmatically.
 * </p>
 *
 */
public interface IThirstUtil {

    /**
     * Traces a water source block near the player that they can drink from.
     *
     * @param player The player attempting to drink.
     * @return A {@link ThirstEnumBlockPos} containing the water type and position, or {@code null} if no valid water source is found.
     */
    @Nullable
    ThirstEnumBlockPos traceWater(EntityPlayer player);

    /**
     * Applies the effects of drinking to a player, including a chance of getting parasites from dirty water.
     *
     * @param player The player drinking.
     * @param thirst The amount of thirst to restore.
     * @param saturation The amount of saturation to restore.
     * @param dirtyChance The probability (0.0f to 1.0f) of applying the parasites effect.
     */
    void takeDrink(EntityPlayer player, int thirst, float saturation, float dirtyChance);

    /**
     * Applies the effects of drinking clean water to a player (no parasite chance).
     *
     * @param player The player drinking.
     * @param thirst The amount of thirst to restore.
     * @param saturation The amount of saturation to restore.
     */
    void takeDrink(EntityPlayer player, int thirst, float saturation);

    /**
     * Applies the effects of drinking based on a predefined {@link ThirstEnum} type.
     *
     * @param player The player drinking.
     * @param type The type of water being consumed (determines thirst, saturation, and parasite chance).
     */
    void takeDrink(EntityPlayer player, ThirstEnum type);

    /**
     * Creates an {@link ItemStack} of a purified water bucket.
     *
     * @return A new ItemStack of purified water.
     */
    ItemStack createPurifiedWaterBucket();

    /**
     * Creates an {@link ItemStack} of a salt water bucket.
     *
     * @return A new ItemStack of salt water.
     */
    ItemStack createSaltWaterBucket();

    /**
     * Creates an {@link ItemStack} of a normal (vanilla) water bucket.
     *
     * @return A new ItemStack of normal water.
     */
    ItemStack createNormalWaterBucket();
}
