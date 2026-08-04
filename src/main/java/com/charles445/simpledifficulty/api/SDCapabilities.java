package com.charles445.simpledifficulty.api;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

/**
 * Main API class for accessing SimpleDifficulty's custom capabilities.
 * <p>
 * Addon developers should use the static helper methods in this class to retrieve
 * temperature and thirst data for players. The capabilities are automatically
 * attached to all {@link PlayerEntity} instances by the mod during the AttachCapabilities event.
 * </p>
 */
public class SDCapabilities {

    /**
     * The Temperature capability instance. Injected by Forge at runtime via {@link CapabilityInject}.
     * <p>
     * Do not modify this field. Use {@link #getTemperatureData(PlayerEntity)} to safely access the data.
     * </p>
     */
    @CapabilityInject(ITemperatureCapability.class)
    public static Capability<ITemperatureCapability> TEMPERATURE = null;

    /**
     * The unique identifier for the Temperature capability, used for NBT serialization and capability attachment.
     */
    public static final String TEMPERATURE_IDENTIFIER = "temperature";

    /**
     * The Thirst capability instance. Injected by Forge at runtime via {@link CapabilityInject}.
     * <p>
     * Do not modify this field. Use {@link #getThirstData(PlayerEntity)} to safely access the data.
     * </p>
     */
    @CapabilityInject(IThirstCapability.class)
    public static Capability<IThirstCapability> THIRST = null;

    /**
     * The unique identifier for the Thirst capability, used for NBT serialization and capability attachment.
     */
    public static final String THIRST_IDENTIFIER = "thirst";

    /**
     * Retrieves the {@link ITemperatureCapability} instance attached to the specified player.
     *
     * @param player The player to get the temperature data from. Must not be null.
     * @return The player's temperature capability instance, or {@code null} if not attached.
     */
    @Nullable
    public static ITemperatureCapability getTemperatureData(PlayerEntity player) {
        return player.getCapability(TEMPERATURE).orElse(null);
    }

    /**
     * Retrieves the {@link IThirstCapability} instance attached to the specified player.
     *
     * @param player The player to get the thirst data from. Must not be null.
     * @return The player's thirst capability instance, or {@code null} if not attached.
     */
    @Nullable
    public static IThirstCapability getThirstData(PlayerEntity player) {
        return player.getCapability(THIRST).orElse(null);
    }
}