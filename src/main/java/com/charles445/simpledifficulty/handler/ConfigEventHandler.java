package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfigEvent;

/**
 * Handles configuration loading and reloading events.
 * This class is in a separate package to avoid naming conflicts with the local ModConfig class.
 * 
 * In Forge 1.16.5, ModConfigEvent is a standalone class (not nested inside ModConfig).
 * Using the base ModConfigEvent class captures both Loading and Reloading events.
 */
@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigEventHandler {

    /**
     * Called when the mod configuration is loaded or reloaded.
     * Syncs the configuration values to the API.
     * 
     * Using ModConfigEvent (base class) captures both:
     * - ModConfigEvent.Loading (first load)
     * - ModConfigEvent.Reloading (config file changed)
     */
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getModId().equals(SimpleDifficulty.MODID)) {
            ModConfig.sendLocalClientConfigToAPI();
            ModConfig.sendLocalServerConfigToAPI();
        }
    }
}