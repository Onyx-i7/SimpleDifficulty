package com.charles445.simpledifficulty.api.config;

/**
 * Enum defining all client-side configuration options.
 */
public enum ClientOptions implements IConfigOption {
    DEBUG("debug"),
    DRAW_THIRST_SATURATION("drawThirstSaturation"),
    ENABLE_THERMOMETER("enableThermometer"),
    ALTERNATE_TEMP("alternateTemp"),
    HUD_THERMOMETER("hudThermometer"),
    HUD_THERMOMETERX("hudThermometerX"),
    HUD_THERMOMETERY("hudThermometerY"),
    TEMPERATURE_READOUT("temperatureReadout"),
    CLASSICHUD_TEMPERATURE("classicHUDTemperature"),
    CLASSICHUD_THIRST("classicHUDThirst"),
    HEATER_PARTICLES("heaterParticles"),
    THIRST_HUD_X("thirstHudX"),
    THIRST_HUD_Y("thirstHudY");

    private final String name;

    ClientOptions(String name) {
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