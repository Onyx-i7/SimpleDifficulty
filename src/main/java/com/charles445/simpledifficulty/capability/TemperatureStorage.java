package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import java.util.Map;

public class TemperatureStorage implements IStorage<ITemperatureCapability> {
    private static final String TEMPERATURE_LEVEL = "temperatureLevel";
    private static final String TEMPERATURE_TICK_TIMER = "temperatureTickTimer";
    private static final String TEMPORARY_MODIFIERS = "temporaryModifiers";
    private static final String TEMPERATURE_DAMAGE_COUNTER = "temperatureDamageCounter";
    private static final String NBT_NAME = "name";
    private static final String NBT_TEMPERATURE = "temperature";
    private static final String NBT_DURATION = "duration";

    @Override
    public INBT writeNBT(Capability<ITemperatureCapability> capability, ITemperatureCapability instance, Direction side) {
        CompoundNBT compound = new CompoundNBT();
        ListNBT temporaryList = new ListNBT();
        Map<String, TemporaryModifier> tempModMap = instance.getTemporaryModifiers();

        for (Map.Entry<String, TemporaryModifier> entry : tempModMap.entrySet()) {
            TemporaryModifier tempMod = entry.getValue();
            CompoundNBT tempModCompound = new CompoundNBT();
            tempModCompound.putString(NBT_NAME, entry.getKey());
            tempModCompound.putFloat(NBT_TEMPERATURE, tempMod.temperature);
            tempModCompound.putInt(NBT_DURATION, tempMod.duration);
            temporaryList.add(tempModCompound);
        }

        compound.putInt(TEMPERATURE_LEVEL, instance.getTemperatureLevel());
        compound.putInt(TEMPERATURE_TICK_TIMER, instance.getTemperatureTickTimer());
        compound.putInt(TEMPERATURE_DAMAGE_COUNTER, instance.getTemperatureDamageCounter());
        compound.put(TEMPORARY_MODIFIERS, temporaryList);
        return compound;
    }

    @Override
    public void readNBT(Capability<ITemperatureCapability> capability, ITemperatureCapability instance, Direction side, INBT nbt) {
        if (nbt instanceof CompoundNBT) {
            CompoundNBT compound = (CompoundNBT) nbt;

            if (compound.contains(TEMPERATURE_LEVEL)) {
                instance.setTemperatureLevel(compound.getInt(TEMPERATURE_LEVEL));
            }
            if (compound.contains(TEMPERATURE_TICK_TIMER)) {
                instance.setTemperatureTickTimer(compound.getInt(TEMPERATURE_TICK_TIMER));
            }
            if (compound.contains(TEMPERATURE_DAMAGE_COUNTER)) {
                instance.setTemperatureDamageCounter(compound.getInt(TEMPERATURE_DAMAGE_COUNTER));
            }
            if (compound.contains(TEMPORARY_MODIFIERS)) {
                instance.clearTemporaryModifiers();
                ListNBT temporaryModList = compound.getList(TEMPORARY_MODIFIERS, 10); // 10 = TAG_COMPOUND
                for (int i = 0; i < temporaryModList.size(); i++) {
                    CompoundNBT tempComp = temporaryModList.getCompound(i);
                    if (tempComp.contains(NBT_NAME) && tempComp.contains(NBT_TEMPERATURE) && tempComp.contains(NBT_DURATION)) {
                        instance.setTemporaryModifier(tempComp.getString(NBT_NAME), tempComp.getFloat(NBT_TEMPERATURE), tempComp.getInt(NBT_DURATION));
                    }
                }
            }
        }
    }
}