package com.charles445.simpledifficulty.api.temperature;

/**
 * Enum defining groups for temporary temperature modifiers.
 * <p>
 * Used to categorize temporary effects (e.g., food effects vs drink effects)
 * for easier management and debugging.
 * </p>
 */
public enum TemporaryModifierGroupEnum {
    FOOD("food"),
    DRINK("drink");

    private final String group;

    TemporaryModifierGroupEnum(String group) {
        this.group = group;
    }

    /**
     * Gets the string identifier for this group.
     *
     * @return The group identifier.
     */
    public String group() {
        return group;
    }
}