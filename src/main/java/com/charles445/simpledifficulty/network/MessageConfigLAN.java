package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class MessageConfigLAN {

    public MessageConfigLAN() {
    }

    public MessageConfigLAN(PacketBuffer buf) {
    }

    public static void encode(MessageConfigLAN message, PacketBuffer buf) {
    }

    public static void handle(MessageConfigLAN message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.enqueueWork(() -> {
                ServerPlayerEntity sender = context.getSender();
                if (sender != null) {
                    boolean isHost = false;
                    if (ServerLifecycleHooks.getCurrentServer() != null) {
                        isHost = ServerLifecycleHooks.getCurrentServer().isSingleplayer();
                    }

                    boolean hasPermission = isHost || sender.hasPermissions(2);
                    
                    if (hasPermission) {
                        // TODO: Implement sending configuration files to all players
                        SimpleDifficulty.LOGGER.info("Config sync requested by {}", sender.getName().getString());
                    } else {
                        SimpleDifficulty.LOGGER.warn("Player {} attempted to force a LAN config update without proper permissions.", sender.getName().getString());
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}