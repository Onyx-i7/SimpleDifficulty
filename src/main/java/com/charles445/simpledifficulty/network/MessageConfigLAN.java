package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

/**
 * Server-bound packet sent by LAN hosts to synchronize server configuration to all players.
 * This packet contains no data; it's a signal for the server to push the current config.
 */
public class MessageConfigLAN {

    /**
     * Empty constructor required for packet reflection.
     */
    public MessageConfigLAN() {
    }

    /**
     * Decodes the packet from the network buffer.
     *
     * @param buf The packet buffer.
     */
    public MessageConfigLAN(PacketBuffer buf) {
        // No data to read
    }

    /**
     * Encodes the packet into the network buffer.
     *
     * @param message The message to encode.
     * @param buf The packet buffer.
     */
    public static void encode(MessageConfigLAN message, PacketBuffer buf) {
        // No data to write
    }

    /**
     * Handles the packet on the server side.
     *
     * @param message The message to handle.
     * @param contextSupplier The network context supplier.
     */
    public static void handle(MessageConfigLAN message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.enqueueWork(() -> {
                ServerPlayerEntity sender = context.getSender();
                if (sender != null) {
                    // Check if operating on a physical client (Integrated Server / LAN host)
                    boolean isClientPhysical = context.getDirection().getReceptionSide().isClient();

                    // Security check: Verify the sender is the LAN host or has OP permissions
                    boolean isHost = false;
                    if (ServerLifecycleHooks.getCurrentServer() != null) {
                        boolean isHost = ServerLifecycleHooks.getCurrentServer().isSingleplayer();
                    }

                    boolean hasPermission = isHost || sender.hasPermissions(2);
                }
            });
        }
        context.setPacketHandled(true);
    }
}