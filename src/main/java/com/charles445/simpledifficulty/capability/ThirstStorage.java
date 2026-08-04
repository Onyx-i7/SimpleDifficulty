package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ThirstStorage implements IStorage<IThirstCapability> {
    private static final String THIRST_EXHAUSTION = "thirstExhaustion";
    private static final String THIRST_LEVEL = "thirstLevel";
    private static final String THIRST_SATURATION = "thirstSaturation";
    private static final String THIRST_TICK_TIMER = "thirstTickTimer";
    private static final String THIRST_DAMAGE_COUNTER = "thirstDamageCounter";

    @Override
    public INBT writeNBT(Capability<IThirstCapability> capability, IThirstCapability instance, Direction side) {
        CompoundNBT compound = new CompoundNBT();
        compound.putFloat(THIRST_EXHAUSTION, instance.getThirstExhaustion());
        compound.putInt(THIRST_LEVEL, instance.getThirstLevel());
        compound.putFloat(THIRST_SATURATION, instance.getThirstSaturation());
        compound.putInt(THIRST_TICK_TIMER, instance.getThirstTickTimer());
        compound.putInt(THIRST_DAMAGE_COUNTER, instance.getThirstDamageCounter());
        return compound;
    }

    @Override
    public void readNBT(Capability<IThirstCapability> capability, IThirstCapability instance, Direction side, INBT nbt) {
        if (nbt instanceof CompoundNBT) {
            CompoundNBT compound = (CompoundNBT) nbt;

            if (compound.contains(THIRST_EXHAUSTION))
                instance.setThirstExhaustion(compound.getFloat(THIRST_EXHAUSTION));
            if (compound.contains(THIRST_LEVEL))
                instance.setThirstLevel(compound.getInt(THIRST_LEVEL));
            if (compound.contains(THIRST_SATURATION))
                instance.setThirstSaturation(compound.getFloat(THIRST_SATURATION));
            if (compound.contains(THIRST_TICK_TIMER))
                instance.setThirstTickTimer(compound.getInt(THIRST_TICK_TIMER));
            if (compound.contains(THIRST_DAMAGE_COUNTER))
                instance.setThirstDamageCounter(compound.getInt(THIRST_DAMAGE_COUNTER));
        }
    }
}