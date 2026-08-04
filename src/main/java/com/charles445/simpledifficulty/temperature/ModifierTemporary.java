package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Temperature modifier for temporary effects (food, drinks, etc.).
 */
public class ModifierTemporary extends ModifierBase {
    public ModifierTemporary() {
        super("Temporary");
    }

    @Override
    public float getPlayerInfluence(PlayerEntity player) {
        ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
        if (capability == null) return 0.0f;
        
        float sum = 0.0f;
        for (TemporaryModifier tm : capability.getTemporaryModifiers().values()) {
            sum += tm.temperature;
        }
        return sum;
    }
}