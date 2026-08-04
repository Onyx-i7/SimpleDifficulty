package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Abstract base class for dynamic temperature modifiers.
 * Dynamic modifiers run after standard modifiers and can modify the accumulated temperature.
 */
public abstract class ModifierDynamicBase implements ITemperatureDynamicModifier {
    private final String name;
    protected final float defaultTemperature;

    protected ModifierDynamicBase(String name) {
        this.name = name;
        this.defaultTemperature = (TemperatureEnum.NORMAL.getUpperBound() + TemperatureEnum.COLD.getUpperBound()) / 2.0f;
    }

    @Override
    public float applyDynamicPlayerInfluence(PlayerEntity player, float currentTemperature) {
        return currentTemperature;
    }

    @Override
    public float applyDynamicWorldInfluence(World world, BlockPos pos, float currentTemperature) {
        return currentTemperature;
    }

    @Override
    public String getName() {
        return name;
    }
}