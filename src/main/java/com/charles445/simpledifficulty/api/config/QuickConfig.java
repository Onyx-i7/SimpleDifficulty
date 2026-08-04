package com.charles445.simpledifficulty.api.config;

import com.charles445.simpledifficulty.config.ModConfig;

/**
 * Quick access cache for commonly used configuration values.
 * <p>
 * This class provides faster access to frequently checked configuration options
 * by caching their values instead of performing repeated lookups.
 * </p>
 */
public class QuickConfig {
    private static boolean temperatureEnabled;
    private static boolean thirstEnabled;

    // Cached global thirst multiplier to avoid repeated config lookups
    private static double thirstExhaustionMultiplier;

    /**
     * Checks if the temperature system is enabled.
     *
     * @return true if temperature is enabled.
     */
    public static boolean isTemperatureEnabled() {
        return temperatureEnabled;
    }

    /**
     * Checks if the thirst system is enabled.
     *
     * @return true if thirst is enabled.
     */
    public static boolean isThirstEnabled() {
        return thirstEnabled;
    }

    /**
     * Gets the cached thirst exhaustion multiplier.
     *
     * @return The thirst exhaustion multiplier value.
     */
    public static double getThirstExhaustionMultiplier() {
        return thirstExhaustionMultiplier;
    }

    /**
     * Updates the cached values from the main configuration.
     * Called automatically when configuration values change.
     */
    protected static void updateValues() {
        temperatureEnabled = ServerConfig.instance.getBoolean(ServerOptions.TEMPERATURE_ENABLED);
        thirstEnabled = ServerConfig.instance.getBoolean(ServerOptions.THIRST_ENABLED);

        // Update cached multiplier from the main config
        thirstExhaustionMultiplier = ModConfig.SERVER.thirstExhaustionMultiplier.get();
    }
}