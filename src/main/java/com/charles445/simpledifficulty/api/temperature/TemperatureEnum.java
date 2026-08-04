package com.charles445.simpledifficulty.api.temperature;

/**
 * Enum representing temperature ranges and their corresponding states.
 * <p>
 * The enum values must be declared in order from coldest to hottest.
 * A temperature matches an enum value if it falls within the inclusive bounds:
 * {@code lowerBound <= temperature <= upperBound}.
 * </p>
 * <p>
 * Example ranges:
 * <ul>
 *   <li>FREEZING: 0-5</li>
 *   <li>COLD: 6-10</li>
 *   <li>NORMAL: 11-14</li>
 *   <li>HOT: 15-19</li>
 *   <li>BURNING: 20-25</li>
 * </ul>
 * </p>
 */
public enum TemperatureEnum {
    FREEZING(0, 5),
    COLD(6, 10),
    NORMAL(11, 14),
    HOT(15, 19),
    BURNING(20, 25);

    private final int lowerBound;
    private final int upperBound;

    TemperatureEnum(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Checks if the given temperature falls within this enum's boundaries.
     *
     * @param temperature The temperature value to check.
     * @return {@code true} if the temperature is within bounds, {@code false} otherwise.
     */
    public boolean matches(int temperature) {
        return temperature >= this.lowerBound && temperature <= this.upperBound;
    }

    /**
     * Gets the midpoint temperature of this range.
     *
     * @return The average of the lower and upper bounds.
     */
    public int getMiddle() {
        return (this.upperBound + this.lowerBound) / 2;
    }

    /**
     * Gets the lower boundary of this temperature range (inclusive).
     *
     * @return The lower bound value.
     */
    public int getLowerBound() {
        return this.lowerBound;
    }

    /**
     * Gets the upper boundary of this temperature range (inclusive).
     *
     * @return The upper bound value.
     */
    public int getUpperBound() {
        return this.upperBound;
    }
}