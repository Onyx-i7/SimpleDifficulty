package com.charles445.simpledifficulty.config.json;

/**
 * JSON configuration for material-based temperature modifiers.
 * Currently only affects the temperature of fire-related materials.
 */
public class MaterialTemperature {
    public String _comment = "Adding materials is not supported, this just changes Material.FIRE temperature";
    
    /**
     * Temperature modifier for fire-related materials.
     */
    public float fire = 5.0f;
}