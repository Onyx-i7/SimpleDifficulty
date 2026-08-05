package com.charles445.simpledifficulty.setup;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.client.gui.TemperatureGui;
import com.charles445.simpledifficulty.client.gui.TemperatureInfoGui;
import com.charles445.simpledifficulty.client.gui.ThirstGui;
import com.charles445.simpledifficulty.register.RegisterItems;
import com.charles445.simpledifficulty.handler.TooltipHandler;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.item.ItemJuice;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;

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

        event.enqueueWork(() -> {
            ItemModelsProperties.register(RegisterItems.juice.get(), new ResourceLocation(SimpleDifficulty.MODID, "type"),  
                (itemStack, clientWorld, livingEntity) -> {
                    if (itemStack.hasTag() && itemStack.getTag().contains("JuiceType")) {
                        String juiceName = itemStack.getTag().getString("JuiceType");
                        for (int i = 0; i < ItemJuice.JuiceEnum.values().length; i++) {
                            if (ItemJuice.JuiceEnum.values()[i].getName().equals(juiceName)) {
                                return (float) i; 
                            }
                        }
                    }
                    return 0.0F; 
                }
            );
        });

        // Note: Tile Entity Renderers (RenderSpit) and Particles will be registered 
        // here once i port the Block/TileEntity and Particle classes.
    }
}
