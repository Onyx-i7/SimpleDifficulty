package com.charles445.simpledifficulty.setup;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.client.gui.TemperatureGui;
import com.charles445.simpledifficulty.client.gui.TemperatureInfoGui;
import com.charles445.simpledifficulty.client.gui.ThirstGui;
import com.charles445.simpledifficulty.handler.TooltipHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        SimpleDifficulty.LOGGER.info("SimpleDifficulty Client Setup");
        
        // Register Forge Event Bus handlers for Client (HUDs, Tooltips, Overlays)
        MinecraftForge.EVENT_BUS.register(new TemperatureGui());
        MinecraftForge.EVENT_BUS.register(new TemperatureInfoGui());
        MinecraftForge.EVENT_BUS.register(new ThirstGui());
        MinecraftForge.EVENT_BUS.register(new TooltipHandler());
        
        // Note: Tile Entity Renderers (RenderSpit) and Particles will be registered 
        // here once we port the Block/TileEntity and Particle classes.
    }
}