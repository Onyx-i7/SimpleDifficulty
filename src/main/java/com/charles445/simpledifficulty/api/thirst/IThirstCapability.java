package com.charles445.simpledifficulty.api.thirst;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Interface defining the contract for the Thirst capability attached to players.
 * <p>
 * Addon developers should use the getter and setter methods to read or modify a player's
 * thirst state. Methods marked as {@code Internal} are used by SimpleDifficulty for
 * networking and ticking, and should not be called directly by addons.
 * </p>
 *
 */
public interface IThirstCapability {

    // ============================================
    // Getters
    // ============================================

    /**
     * Returns the current thirst exhaustion level.
     * <p>
     * Exhaustion increases with movement and actions, triggering thirst loss when it reaches a threshold.
     * </p>
     *
     * @return The current exhaustion value (0.0f or higher).
     */
    float getThirstExhaustion();

    /**
     * Returns the current thirst level (0-20, similar to hunger).
     *
     * @return The current thirst level.
     */
    int getThirstLevel();

    /**
     * Returns the current thirst saturation level.
     * <p>
     * Saturation is consumed before the actual thirst level decreases.
     * </p>
     *
     * @return The current saturation value (0.0f or higher).
     */
    float getThirstSaturation();

    /**
     * Returns the internal tick timer used for damage calculation when thirst is zero.
     *
     * @return The current tick timer value.
     */
    int getThirstTickTimer();

    /**
     * Returns the counter for consecutive damage ticks taken from dehydration.
     *
     * @return The current damage counter value.
     */
    int getThirstDamageCounter();

    // ============================================
    // Setters
    // ============================================

    /**
     * Sets the thirst exhaustion level.
     *
     * @param exhaustion The new exhaustion value.
     */
    void setThirstExhaustion(float exhaustion);

    /**
     * Sets the thirst level.
     *
     * @param thirst The new thirst level (will be clamped between 0 and 20).
     */
    void setThirstLevel(int thirst);

    /**
     * Sets the thirst saturation level.
     *
     * @param saturation The new saturation value.
     */
    void setThirstSaturation(float saturation);

    /**
     * Sets the internal tick timer.
     *
     * @param ticktimer The new tick timer value.
     */
    void setThirstTickTimer(int ticktimer);

    /**
     * Sets the dehydration damage counter.
     *
     * @param damagecounter The new damage counter value.
     */
    void setThirstDamageCounter(int damagecounter);

    // ============================================
    // Adders (Increment/Decrement)
    // ============================================

    /**
     * Adds to the current thirst exhaustion level.
     *
     * @param exhaustion The amount to add (can be negative to reduce exhaustion).
     */
    void addThirstExhaustion(float exhaustion);

    /**
     * Adds to the current thirst level.
     *
     * @param thirst The amount to add (can be negative to reduce thirst).
     */
    void addThirstLevel(int thirst);

    /**
     * Adds to the current thirst saturation level.
     *
     * @param saturation The amount to add (can be negative to reduce saturation).
     */
    void addThirstSaturation(float saturation);

    /**
     * Adds to the internal tick timer.
     *
     * @param ticktimer The amount to add.
     */
    void addThirstTickTimer(int ticktimer);

    /**
     * Adds to the dehydration damage counter.
     *
     * @param damagecounter The amount to add.
     */
    void addThirstDamageCounter(int damagecounter);

    // ============================================
    // State Checks
    // ============================================

    /**
     * Checks if the player's thirst level is below the maximum (20).
     * <p>
     * <b>Note:</b> This is not the same as having the "Thirsty" potion effect!
     * It simply indicates that the thirst bar is not completely full.
     * </p>
     *
     * @return {@code true} if the thirst level is less than 20, {@code false} otherwise.
     */
    boolean isThirsty();

    // ============================================
    // Internal Methods (Do Not Use in Addons)
    // ============================================

    /**
     * Checks if the capability data has changed since the last network sync.
     * <p>
     * <b>Internal use only.</b> Used by SimpleDifficulty to optimize packet sending.
     * </p>
     *
     * @return {@code true} if data has changed, {@code false} otherwise.
     */
    boolean isDirty();

    /**
     * Marks the capability data as synchronized.
     * <p>
     * <b>Internal use only.</b> Resets the dirty flag after a packet is sent.
     * </p>
     */
    void setClean();

    /**
     * Performs a server-side tick update for the player's thirst mechanics.
     * <p>
     * <b>Internal use only.</b> Called automatically by the mod's event handler.
     * </p>
     *
     * @param player The player entity.
     * @param world The world the player is in.
     * @param phase The current tick phase (START or END).
     */
    void tickUpdate(EntityPlayer player, World world, TickEvent.Phase phase);

    /**
     * Returns the current value of the internal packet timer.
     * <p>
     * <b>Internal use only.</b> Used to throttle network updates.
     * </p>
     *
     * @return The current packet timer value.
     */
    int getPacketTimer();
}
