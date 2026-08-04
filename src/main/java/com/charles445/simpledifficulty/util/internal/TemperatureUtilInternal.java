package com.charles445.simpledifficulty.util.internal;

import com.charles445.simpledifficulty.api.temperature.*;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Internal implementation of ITemperatureUtil.
 * Handles temperature calculations and NBT tag operations for armor.
 */
public class TemperatureUtilInternal implements ITemperatureUtil {
    private final String ARMOR_TEMPERATURE_TAG = "SDArmorTemp";

    @Override
    public int getPlayerTargetTemperature(PlayerEntity player) {
        float sum = 0.0f;
        World world = player.level;
        BlockPos pos = WorldUtil.getSidedBlockPos(world, player);

        for (ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
            sum += modifier.getWorldInfluence(world, pos);
            sum += modifier.getPlayerInfluence(player);
        }
        for (ITemperatureDynamicModifier dynmodifier : TemperatureRegistry.dynamicModifiers.values()) {
            sum = dynmodifier.applyDynamicWorldInfluence(world, pos, sum);
            sum = dynmodifier.applyDynamicPlayerInfluence(player, sum);
        }
        return (int) sum;
    }

    @Override
    public int getWorldTemperature(World world, BlockPos pos) {
        float sum = 0.0f;
        for (ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
            sum += modifier.getWorldInfluence(world, pos);
        }
        for (ITemperatureDynamicModifier dynmodifier : TemperatureRegistry.dynamicModifiers.values()) {
            sum = dynmodifier.applyDynamicWorldInfluence(world, pos, sum);
        }

        return (int) sum;
    }

    @Override
    public int clampTemperature(int temperature) {
        return MathHelper.clamp(temperature, TemperatureEnum.FREEZING.getLowerBound(), TemperatureEnum.BURNING.getUpperBound());
    }

    @Override
    public TemperatureEnum getTemperatureEnum(int temp) {
        for (TemperatureEnum temp_enum : TemperatureEnum.values()) {
            if (temp_enum.matches(temp))
                return temp_enum;
        }

        // Temperature invalid, assume extremes
        if (temp < 0)
            return TemperatureEnum.FREEZING;
        else
            return TemperatureEnum.BURNING;
    }

    @Override
    public void setArmorTemperatureTag(final ItemStack stack, float temperature) {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putFloat(ARMOR_TEMPERATURE_TAG, temperature);
    }

    @Override
    public float getArmorTemperatureTag(final ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT compound = stack.getTag();
            if (compound != null && compound.contains(ARMOR_TEMPERATURE_TAG)) {
                INBT tempTag = compound.get(ARMOR_TEMPERATURE_TAG);
                if (tempTag instanceof FloatNBT) {
                    return ((FloatNBT) tempTag).getAsFloat();
                }
            }
        }

        return 0.0f;
    }

    @Override
    public void removeArmorTemperatureTag(final ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT compound = stack.getTag();
            if (compound != null && compound.contains(ARMOR_TEMPERATURE_TAG)) {
                compound.remove(ARMOR_TEMPERATURE_TAG);
            }
        }
    }
}