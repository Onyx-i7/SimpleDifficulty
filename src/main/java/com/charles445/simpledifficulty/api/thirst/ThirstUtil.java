package com.charles445.simpledifficulty.api.thirst;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Utility class providing static methods for interacting with the thirst system.
 * <p>
 * This class acts as a wrapper around the internal {@link IThirstUtil} implementation,
 * providing a convenient API for addon developers to simulate drinking, trace water sources,
 * and create water buckets.
 * </p>
 *
 * @author Onyx_i7
 */
public class ThirstUtil {
    /**
     * Internal implementation of the thirst utility. Initialized by SimpleDifficulty during mod loading.
     */
    public static IThirstUtil internal;

    /**
     * Performs a ray trace from the player's eyes to find a drinkable water source block.
     *
     * @param player The player to trace from.
     * @return A {@link ThirstEnumBlockPos} containing the water type and block position, or {@code null} if no valid water source is found within range.
     */
    @Nullable
    public static ThirstEnumBlockPos traceWater(EntityPlayer player) {
        return internal.traceWater(player);
    }

    /**
     * Makes the player take a drink with the specified thirst and saturation values, with a chance to cause parasites.
     *
     * @param player The player drinking.
     * @param thirst The amount of thirst to restore.
     * @param saturation The amount of saturation to restore.
     * @param thirstyChance The probability (0.0f to 1.0f) of applying the "Thirsty" effect.
     */
    public static void takeDrink(EntityPlayer player, int thirst, float saturation, float thirstyChance) {
        internal.takeDrink(player, thirst, saturation, thirstyChance);
    }

    /**
     * Makes the player take a drink with the specified thirst and saturation values, with no chance to cause parasites.
     *
     * @param player The player drinking.
     * @param thirst The amount of thirst to restore.
     * @param saturation The amount of saturation to restore.
     */
    public static void takeDrink(EntityPlayer player, int thirst, float saturation) {
        internal.takeDrink(player, thirst, saturation);
    }

    /**
     * Makes the player take a drink using the predefined values from a {@link ThirstEnum} type.
     *
     * @param player The player drinking.
     * @param thirstEnum The type of water being consumed.
     */
    public static void takeDrink(EntityPlayer player, ThirstEnum thirstEnum) {
        internal.takeDrink(player, thirstEnum);
    }

    /**
     * Creates a new {@link ItemStack} containing a Purified Water Bucket.
     *
     * @return A new ItemStack of purified water.
     */
    public static ItemStack createPurifiedWaterBucket() {
        return internal.createPurifiedWaterBucket();
    }

    /**
     * Creates a new {@link ItemStack} containing a Salt Water Bucket.
     *
     * @return A new ItemStack of salt water.
     */
    public static ItemStack createSaltWaterBucket() {
        return internal.createSaltWaterBucket();
    }

    /**
     * Creates a new {@link ItemStack} containing a Vanilla Water Bucket.
     *
     * @return A new ItemStack of normal water.
     */
    public static ItemStack createNormalWaterBucket() {
        return internal.createNormalWaterBucket();
    }
}
