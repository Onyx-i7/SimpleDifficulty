package com.charles445.simpledifficulty.api.thirst;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

/**
 * Interface defining the contract for the Thirst capability attached to players.
 * Addon developers should use the getter and setter methods to read or modify a player's thirst state.
 */
public interface IThirstCapability {

    // ============================================
    // Getters
    // ============================================
    float getThirstExhaustion();
    int getThirstLevel();
    float getThirstSaturation();
    int getThirstTickTimer();
    int getThirstDamageCounter();

    // ============================================
    // Setters
    // ============================================
    void setThirstExhaustion(float exhaustion);
    void setThirstLevel(int thirst);
    void setThirstSaturation(float saturation);
    void setThirstTickTimer(int ticktimer);
    void setThirstDamageCounter(int damagecounter);

    // ============================================
    // Adders
    // ============================================
    void addThirstExhaustion(float exhaustion);
    void addThirstLevel(int thirst);
    void addThirstSaturation(float saturation);
    void addThirstTickTimer(int ticktimer);
    void addThirstDamageCounter(int damagecounter);

    // ============================================
    // State Checks
    // ============================================
    boolean isThirsty();

    // ============================================
    // Internal Methods
    // ============================================
    boolean isDirty();
    void setClean();
    void tickUpdate(PlayerEntity player, World world, TickEvent.Phase phase);
    int getPacketTimer();
}