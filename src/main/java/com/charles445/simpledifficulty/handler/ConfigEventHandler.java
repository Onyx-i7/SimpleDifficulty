package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles configuration loading and reloading events.
 * This class is in a separate package to avoid naming conflicts with the local ModConfig class.
 */
@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigEventHandler {

    /**
     * Called when the mod configuration is loaded or reloaded.
     * Syncs the configuration values to the API.
     */
    @SubscribeEvent
    public static void onModConfigEvent(final net.minecraftforge.fml.config.ModConfig.Event event) {
        if (event.getConfig().getModId().equals(SimpleDifficulty.MODID)) {
            ModConfig.sendLocalClientConfigToAPI();
            ModConfig.sendLocalServerConfigToAPI();
        }
    }
}