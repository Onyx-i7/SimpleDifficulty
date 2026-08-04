package com.charles445.simpledifficulty.api.config;

/**
 * Enum defining all server-side configuration options.
 */
public enum ServerOptions implements IConfigOption {
    DEBUG("debug"),
    THIRST_ENABLED("thirstEnabled"),
    THIRST_DRINK_BLOCKS("thirstDrinkBlocks"),
    THIRST_DRINK_RAIN("thirstDrinkRain"),
    PEACEFUL_DANGER("peacefulDanger"),
    TEMPERATURE_ENABLED("temperatureEnabled"),
    TEMPERATURE_TE_ENABLED("temperatureTEEnabled"),
    CANTEEN_DOSES("canteenDoses"),
    STRICT_HEATERS("strictHeaters"),
    IRON_CANTEEN_DOSES("ironCanteenDoses"),
    DRAGON_CANTEEN_DOSES("dragonCanteenDoses"),
    INFINITE_PURIFIED_WATER("infinitePurifiedWater"),
    PURIFIED_WATER_OPACITY("purifiedWaterOpacity"),
    THIRST_EXHAUSTION_MULTIPLIER("thirstExhaustionMultiplier"),
    SALT_WATER_THIRST("saltWaterThirst");

    private final String name;

    ServerOptions(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}