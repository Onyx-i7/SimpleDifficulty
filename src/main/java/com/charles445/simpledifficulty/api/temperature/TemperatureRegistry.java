package com.charles445.simpledifficulty.api.temperature;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry for temperature modifiers used by the temperature system.
 * <p>
 * Addon developers can register custom modifiers here to integrate with
 * SimpleDifficulty's temperature calculation system.
 * </p>
 */
public class TemperatureRegistry {

    /**
     * Map of standard temperature modifiers.
     * <p>
     * Standard modifiers are accumulated and their influences are added together.
     * Register custom modifiers using {@link #registerModifier(ITemperatureModifier)}.
     * </p>
     */
    public static final Map<String, ITemperatureModifier> modifiers = new LinkedHashMap<>();

    /**
     * Map of dynamic temperature modifiers.
     * <p>
     * Dynamic modifiers run after standard modifiers and can replace the accumulated temperature.
     * Use sparingly as multiple dynamic modifiers may conflict.
     * Register custom modifiers using {@link #registerDynamicModifier(ITemperatureDynamicModifier)}.
     * </p>
     */
    public static final Map<String, ITemperatureDynamicModifier> dynamicModifiers = new LinkedHashMap<>();

    /**
     * Registers a standard temperature modifier.
     * <p>
     * Custom modifiers must implement {@link ITemperatureModifier} and have a unique name.
     * </p>
     *
     * @param modifier The modifier to register.
     */
    public static void registerModifier(ITemperatureModifier modifier) {
        modifiers.put(modifier.getName(), modifier);
    }

    /**
     * Registers a dynamic temperature modifier.
     * <p>
     * Dynamic modifiers should be avoided if possible. Use standard modifiers instead.
     * Custom modifiers must implement {@link ITemperatureDynamicModifier} and have a unique name.
     * </p>
     *
     * @param modifier The dynamic modifier to register.
     */
    public static void registerDynamicModifier(ITemperatureDynamicModifier modifier) {
        dynamicModifiers.put(modifier.getName(), modifier);
    }
}