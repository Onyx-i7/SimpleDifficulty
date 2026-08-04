package com.charles445.simpledifficulty.api.temperature;

/**
 * Container class for a temporary temperature modifier with a duration.
 * <p>
 * Used by the temperature capability to track temporary effects from food,
 * drinks, or other sources that expire after a certain number of ticks.
 * </p>
 */
public class TemporaryModifier {

    /**
     * The temperature change value.
     */
    public float temperature;

    /**
     * The remaining duration in ticks.
     */
    public int duration;

    /**
     * Creates a new temporary modifier.
     *
     * @param temperature The temperature change value.
     * @param duration The duration in ticks.
     */
    public TemporaryModifier(float temperature, int duration) {
        this.temperature = temperature;
        this.duration = duration;
    }
}