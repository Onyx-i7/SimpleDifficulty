package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Weather2Modifier implements ITemperatureDynamicModifier {

    // This method is required by the interface but we don't need player-specific data, 
    // so we just return the temperature unmodified.
    @Override
    public float applyDynamicPlayerInfluence(EntityPlayer player, float currentTemperature) {
        return currentTemperature;
    }
    
    // This is where modify the world temperature based on Weather2
    @Override
    public float applyDynamicWorldInfluence(World world, BlockPos pos, float currentTemperature) {
        if (!Weather2Compat.isLoaded()) {
            return currentTemperature;
        }

        // 1. Check for explicit thermal weather fronts (Sandstorm / Blizzard)
        int thermalModifier = Weather2Compat.getThermalIntensityAt(world, pos);
        if (thermalModifier != 0) {
            // We apply the modifier directly to the incoming temperature
            return currentTemperature + (float) thermalModifier;
        }

        // 2. Dynamic wind chill fallback based on heavy rain and gales
        float windSpeed = Weather2Compat.getWindSpeedAt(world, pos);
        if (windSpeed > 0.8F) {
            if (Weather2Compat.isRainingAt(world, pos)) {
                return currentTemperature - 3.0F; // Severe storm chills the existing temperature
            } else {
                return currentTemperature - 1.0F; // Strong winds alone slightly lower it
            }
        }

        return currentTemperature;
    }

    @Override
    public String getName() {
        return "weather2";
    }
}
