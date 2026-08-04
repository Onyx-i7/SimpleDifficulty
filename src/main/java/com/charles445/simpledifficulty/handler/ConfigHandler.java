package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.network.MessageUpdateConfig;
import com.charles445.simpledifficulty.network.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;

/**
 * Handler for configuration synchronization events.
 */
public class ConfigHandler {

    /**
     * Sends server configuration to player when they log in.
     *
     * @param event The player login event.
     */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.level;

        if (world.isClientSide) {
            return;
        }

        // Server Side
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            // Send config packet to player
            PacketHandler.INSTANCE.sendTo(
                    new MessageUpdateConfig(),
                    serverPlayer.connection.getConnection(),
                    NetworkDirection.PLAY_TO_CLIENT
            );
        }
    }

    /**
     * Resets configuration when player returns to title screen.
     *
     * @param event The world unload event.
     */
    @SubscribeEvent
    public void onWorldEventUnload(WorldEvent.Unload event) {
        if (event.getWorld().isClientSide()) {
            // Client Side - Reset to local config when not connected to server
            // Note: In 1.16.5, i can't easily check connection status here
            // The config will be reset on next login anyway
            ModConfig.sendLocalClientConfigToAPI();
            ModConfig.sendLocalServerConfigToAPI();
        }
    }
}