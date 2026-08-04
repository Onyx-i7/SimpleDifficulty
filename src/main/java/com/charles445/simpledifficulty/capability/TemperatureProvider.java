package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TemperatureProvider implements ICapabilitySerializable<CompoundNBT> {
    private final Capability<ITemperatureCapability> capability;
    private final ITemperatureCapability instance;
    private final LazyOptional<ITemperatureCapability> optional;

    public TemperatureProvider(Capability<ITemperatureCapability> newcapability) {
        this.capability = newcapability;
        this.instance = capability.getDefaultInstance();
        this.optional = LazyOptional.of(() -> this.instance);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> requestedcapability, @Nullable Direction facing) {
        if (requestedcapability == this.capability) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) this.capability.getStorage().writeNBT(this.capability, this.instance, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.capability.getStorage().readNBT(this.capability, this.instance, null, nbt);
    }
}