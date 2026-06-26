package com.charles445.simpledifficulty.api.item;

import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Interface that defines the contract for any item acting as a canteen (water container).
 * <p>
 * Addon developers should implement this interface on their custom items to allow them
 * to interact with SimpleDifficulty's thirst system, including filling, drinking, and
 * mixing water types.
 * </p>
 *
 */
public interface IItemCanteen {

    /**
     * Gets the current type of water stored in the canteen.
     *
     * @param stack The canteen's {@link ItemStack}.
     * @return The {@link ThirstEnum} representing the water type, or {@code null} if the canteen is empty.
     */
    @Nullable
    ThirstEnum getThirstEnum(ItemStack stack);

    /**
     * Sets both the water type and the number of doses in the canteen.
     *
     * @param stack The canteen's {@link ItemStack}.
     * @param thirstEnum The {@link ThirstEnum} representing the water type to set.
     * @param amount The number of doses to set.
     */
    void setDoses(ItemStack stack, ThirstEnum thirstEnum, int amount);

    /**
     * Sets the number of doses in the canteen without changing the current water type.
     *
     * @param stack The canteen's {@link ItemStack}.
     * @param amount The number of doses to set.
     */
    void setDoses(ItemStack stack, int amount);

    /**
     * Attempts to add a single dose of the specified water type to the canteen.
     *
     * @param stack The canteen's {@link ItemStack}.
     * @param thirstEnum The {@link ThirstEnum} representing the water type to add.
     * @return {@code true} if the dose was successfully added (i.e., the canteen was not full), {@code false} otherwise.
     */
    boolean tryAddDose(ItemStack stack, ThirstEnum thirstEnum);

    /**
     * Removes a single dose from the canteen.
     * <p>
     * If the canteen is already empty, this method should handle it gracefully (e.g., do nothing or set water type to null).
     * </p>
     *
     * @param stack The canteen's {@link ItemStack}.
     */
    void removeDose(ItemStack stack);

    /**
     * Fills the canteen to its maximum capacity with the current water type.
     *
     * @param stack The canteen's {@link ItemStack}.
     */
    void setCanteenFull(ItemStack stack);

    /**
     * Empties the canteen completely, removing all doses and resetting the water type.
     *
     * @param stack The canteen's {@link ItemStack}.
     */
    void setCanteenEmpty(ItemStack stack);

    /**
     * Checks if the canteen is currently at its maximum capacity.
     *
     * @param stack The canteen's {@link ItemStack}.
     * @return {@code true} if the current doses equal the maximum doses, {@code false} otherwise.
     */
    boolean isCanteenFull(ItemStack stack);

    /**
     * Checks if the canteen is currently empty.
     *
     * @param stack The canteen's {@link ItemStack}.
     * @return {@code true} if the current doses are 0, {@code false} otherwise.
     */
    boolean isCanteenEmpty(ItemStack stack);

    /**
     * Returns the maximum number of doses this specific canteen can hold.
     *
     * @param stack The canteen's {@link ItemStack}.
     * @return The maximum capacity in doses.
     */
    int getMaxDoses(ItemStack stack);

    /**
     * Returns the current number of doses stored in the canteen.
     *
     * @param stack The canteen's {@link ItemStack}.
     * @return The current amount of doses.
     */
    int getDoses(ItemStack stack);
}
