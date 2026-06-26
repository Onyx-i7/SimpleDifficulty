package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import java.util.Map;

public class TemperatureStorage implements IStorage<ITemperatureCapability> {
    private static final String temperatureLevel = "temperatureLevel";
    private static final String temperatureTickTimer = "temperatureTickTimer";
    private static final String temporaryModifiers = "temporaryModifiers";
    private static final String temperatureDamageCounter = "temperatureDamageCounter";
    private static final String NBT_name = "name";
    private static final String NBT_temperature = "temperature";
    private static final String NBT_duration = "duration";
    
    @Override
    public NBTBase writeNBT(Capability<ITemperatureCapability> capability, ITemperatureCapability instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList temporaryList = new NBTTagList();
        Map<String, TemporaryModifier> tempModMap = instance.getTemporaryModifiers();
        
        for (Map.Entry<String, TemporaryModifier> entry : tempModMap.entrySet()) {
            TemporaryModifier tempMod = entry.getValue();
            NBTTagCompound tempModCompound = new NBTTagCompound();
            tempModCompound.setString(NBT_name, entry.getKey());
            tempModCompound.setFloat(NBT_temperature, tempMod.temperature);
            tempModCompound.setInteger(NBT_duration, tempMod.duration);
            temporaryList.appendTag(tempModCompound);
        }
        
        compound.setInteger(temperatureLevel, instance.getTemperatureLevel());
        compound.setInteger(temperatureTickTimer, instance.getTemperatureTickTimer());
        compound.setInteger(temperatureDamageCounter, instance.getTemperatureDamageCounter());
        compound.setTag(temporaryModifiers, temporaryList);
        return compound;
    }

    @Override
    public void readNBT(Capability<ITemperatureCapability> capability, ITemperatureCapability instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            
            if (compound.hasKey(temperatureLevel)) {
                instance.setTemperatureLevel(compound.getInteger(temperatureLevel));
            }
            if (compound.hasKey(temperatureTickTimer)) {
                instance.setTemperatureTickTimer(compound.getInteger(temperatureTickTimer));
            }
            if (compound.hasKey(temperatureDamageCounter)) {
                instance.setTemperatureDamageCounter(compound.getInteger(temperatureDamageCounter));
            }
            if (compound.hasKey(temporaryModifiers)) {
                instance.clearTemporaryModifiers();
                // 10 is the ID for NBT.TAG_COMPOUND
                NBTTagList temporaryModList = compound.getTagList(temporaryModifiers, 10);
                for (int i = 0; i < temporaryModList.tagCount(); i++) {
                    NBTTagCompound tempComp = temporaryModList.getCompoundTagAt(i);
                    if (tempComp.hasKey(NBT_name) && tempComp.hasKey(NBT_temperature) && tempComp.hasKey(NBT_duration)) {
                        instance.setTemporaryModifier(tempComp.getString(NBT_name), tempComp.getFloat(NBT_temperature), tempComp.getInteger(NBT_duration));
                    }
                }
            }
        }
    }
}
