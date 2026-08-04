package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client-bound packet sent by the server to synchronize the player's thirst capability data.
 * Contains a CompoundNBT with all thirst-related values.
 */
public class MessageUpdateThirst {

    private CompoundNBT nbt;

    /**
     * Empty constructor required for packet reflection.
     */
    public MessageUpdateThirst() {
    }

    /**
     * Creates a new thirst update message with the specified NBT data.
     *
     * @param nbt The NBT data.
     */
    public MessageUpdateThirst(INBT nbt) {
        this.nbt = (CompoundNBT) nbt;
    }

    /**
     * Decodes the packet from the network buffer.
     *
     * @param buf The packet buffer.
     */
    public MessageUpdateThirst(PacketBuffer buf) {
        this.nbt = buf.readNbt();
    }

    /**
     * Encodes the packet into the network buffer.
     *
     * @param message The message to encode.
     * @param buf The packet buffer.
     */
    public static void encode(MessageUpdateThirst message, PacketBuffer buf) {
        buf.writeNbt(message.nbt);
    }

    /**
     * Gets the NBT compound from this message.
     *
     * @return The NBT compound.
     */
    public CompoundNBT getNBT() {
        return this.nbt;
    }

    /**
     * Handles the packet on the client side.
     *
     * @param message The message to handle.
     * @param contextSupplier The network context supplier.
     */
    public static void handle(MessageUpdateThirst message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> handleClient(message));
        }
        context.setPacketHandled(true);
    }

    /**
     * Client-side handler for the thirst update.
     *
     * @param message The message to handle.
     */
    @OnlyIn(Dist.CLIENT)
    private static void handleClient(MessageUpdateThirst message) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            Capability<IThirstCapability> capability = SDCapabilities.THIRST;
            if (capability != null) {
                IThirstCapability thirstCap = capability.orElse(null);
                if (thirstCap == null) {
                    // Try getting from player capability
                    thirstCap = player.getCapability(capability).orElse(null);
                }

                if (thirstCap != null && message.getNBT() != null) {
                    capability.getStorage().readNBT(capability, thirstCap, null, message.getNBT());
                }
            }
        }
    }
}