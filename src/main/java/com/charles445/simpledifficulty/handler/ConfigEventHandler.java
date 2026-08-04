package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles configuration loading and reloading events.
 * This class is in a separate package to avoid naming conflicts with the local ModConfig class.
 * 
 * In Forge 1.16.5, config events are:
 * - net.minecraftforge.fml.config.ModConfig.Loading (first load)
 * - net.minecraftforge.fml.config.ModConfig.Reloading (config file changed)
 */
@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigEventHandler {

    /**
     * Called when the mod configuration is loaded for the first time.
     */
    @SubscribeEvent
    public static void onConfigLoading(final net.minecraftforge.fml.config.ModConfig.Loading event) {
        handleConfigEvent(event.getConfig());
    }

    /**
     * Called when the mod configuration file is reloaded.
     */
    @SubscribeEvent
    public static void onConfigReloading(final net.minecraftforge.fml.config.ModConfig.Reloading event) {
        handleConfigEvent(event.getConfig());
    }

    /**
     * Syncs the configuration values to the API.
     * Uses fully qualified name for the local ModConfig class to avoid conflicts.
     */
    private static void handleConfigEvent(net.minecraftforge.fml.config.ModConfig config) {
        if (config.getModId().equals(SimpleDifficulty.MODID)) {
            // Use fully qualified name to avoid conflict with Forge's ModConfig
            com.charles445.simpledifficulty.config.ModConfig.sendLocalClientConfigToAPI();
            com.charles445.simpledifficulty.config.ModConfig.sendLocalServerConfigToAPI();
        }
    }
}