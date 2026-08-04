package com.charles445.simpledifficulty.config;

/**
 * Enum representing JSON configuration file names used by SimpleDifficulty.
 * Each enum value corresponds to a specific JSON file in the config directory.
 */
public enum JsonFileName {
    armorTemperatures("armorTemperatures.json"),
    blockTemperatures("blockTemperatures.json"),
    consumableTemperature("consumableTemperature.json"),
    consumableThirst("consumableThirst.json"),
    dimensionTemperature("dimensionTemperature.json"),
    fluidTemperatures("fluidTemperatures.json"),
    heldItemTemperatures("heldItemTemperatures.json"),
    materialTemperature("materialTemperature.json"),
    extraItems("extraItems.json"),

    // Migration file names (legacy support)
    armorTemperatures_MIGRATE("armorTemperatures.json"),
    consumableTemperature_MIGRATE("consumableTemperature.json"),
    consumableThirst_MIGRATE("consumableThirst.json"),
    heldItemTemperatures_MIGRATE("heldItemTemperatures.json");

    private final String fileName;

    JsonFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return this.fileName;
    }

    /**
     * Gets the file name string.
     *
     * @return The file name.
     */
    public String get() {
        return this.toString();
    }
}