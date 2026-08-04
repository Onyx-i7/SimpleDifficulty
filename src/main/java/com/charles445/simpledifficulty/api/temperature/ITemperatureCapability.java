package com.charles445.simpledifficulty.api.temperature;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

public interface ITemperatureCapability {
    int getTemperatureLevel();
    int getTemperatureTickTimer();
    int getTemperatureDamageCounter();
    ImmutableMap<String, TemporaryModifier> getTemporaryModifiers();

    void setTemperatureLevel(int temperature);
    void setTemperatureTickTimer(int ticktimer);
    void setTemporaryModifier(String name, float temperature, int duration);
    void setTemperatureDamageCounter(int damagecounter);

    void addTemperatureLevel(int temperature);
    void addTemperatureTickTimer(int ticktimer);
    void addTemperatureDamageCounter(int damagecounter);

    void clearTemporaryModifiers();

    /**
     * Returns the capability's matching TemperatureEnum enum.
     * 
     * @return TemperatureEnum for the current temperature level.
     */
    TemperatureEnum getTemperatureEnum();

    /**
     * (Internal use only)
     * Runs a tick update for the player's temperature capability.
     */
    void tickUpdate(PlayerEntity player, World world, TickEvent.Phase phase);

    /**
     * (Internal use only)
     * Checks if the capability needs a network update.
     */
    boolean isDirty();

    /**
     * (Internal use only)
     * Marks the capability as synchronized.
     */
    void setClean();

    /**
     * (Internal use only)
     * Gets the current tick of the packet timer.
     */
    int getPacketTimer();
}