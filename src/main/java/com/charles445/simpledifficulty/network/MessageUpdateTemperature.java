package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateTemperature {
    private final CompoundNBT nbt;

    public MessageUpdateTemperature(PacketBuffer buf) {
        this.nbt = buf.readNbt();
    }

    public MessageUpdateTemperature(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public static void encode(MessageUpdateTemperature message, PacketBuffer buf) {
        buf.writeNbt(message.nbt);
    }

    public static void handle(MessageUpdateTemperature message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection() == net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientPlayerEntity player = Minecraft.getInstance().player;
                if (player != null) {
                    Capability<ITemperatureCapability> capability = SDCapabilities.TEMPERATURE;
                    ITemperatureCapability tempCap = player.getCapability(capability).orElse(null);

                    if (tempCap != null && message.nbt != null) {
                        capability.getStorage().readNBT(capability, tempCap, null, message.nbt);
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}