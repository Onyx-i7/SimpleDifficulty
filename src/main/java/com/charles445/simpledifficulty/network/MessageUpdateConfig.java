package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client-bound packet sent by the server to synchronize configuration values.
 * Contains a CompoundNBT with all server configuration options.
 */
public class MessageUpdateConfig {

    private CompoundNBT nbt;

    /**
     * Empty constructor required for packet reflection.
     */
    public MessageUpdateConfig() {
    }

    /**
     * Creates a new config update message with the specified NBT data.
     *
     * @param compound The NBT compound containing configuration values.
     */
    public MessageUpdateConfig(CompoundNBT compound) {
        this.nbt = compound;
    }

    /**
     * Decodes the packet from the network buffer.
     *
     * @param buf The packet buffer.
     */
    public MessageUpdateConfig(PacketBuffer buf) {
        this.nbt = buf.readNbt();
    }

    /**
     * Encodes the packet into the network buffer.
     *
     * @param message The message to encode.
     * @param buf The packet buffer.
     */
    public static void encode(MessageUpdateConfig message, PacketBuffer buf) {
        buf.writeNbt(message.nbt);
    }

    /**
     * Handles the packet on the client side.
     *
     * @param message The message to handle.
     * @param contextSupplier The network context supplier.
     */
    public static void handle(MessageUpdateConfig message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> handleClient(message));
        }
        context.setPacketHandled(true);
    }

    /**
     * Client-side handler for the configuration update.
     *
     * @param message The message to handle.
     */
    @OnlyIn(Dist.CLIENT)
    private static void handleClient(MessageUpdateConfig message) {
        // Security filter: Prevents crashes if the configuration package arrives corrupted
        if (message.nbt != null) {
            ServerConfig.instance.updateValues(message.nbt);
        }
    }
}