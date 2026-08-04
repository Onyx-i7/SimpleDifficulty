package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.register.RegisterFluids;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.block.IBlockStateIgnore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side model registration and block state handling.
 * In 1.16.5, most models are handled via JSON files automatically.
 * This class handles special cases like render layers and tile entity renderers.
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = SimpleDifficulty.MODID)
public class RegisterClientModels {
    public static final RegisterClientModels instance = new RegisterClientModels();

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onModelRegistryEvent(ModelRegistryEvent event) {
        // In 1.16.5, item models are automatically loaded from JSON files
        // Block models are also automatically loaded from JSON files
        
        // Set render layers for transparent/cutout blocks
        RenderTypeLookup.setRenderLayer(SDBlocks.campfire.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(SDBlocks.spit.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(SDBlocks.rainCollector.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(SDBlocks.heater.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(SDBlocks.chiller.get(), RenderType.cutout());
        
        // Fluids use translucent render layer
        RenderTypeLookup.setRenderLayer(RegisterFluids.BLOCK_PURIFIED_WATER.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(RegisterFluids.BLOCK_SALT_WATER.get(), RenderType.translucent());
        
        // Ice blocks
        RenderTypeLookup.setRenderLayer(SDBlocks.icePurifiedWater.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(SDBlocks.iceSaltWater.get(), RenderType.translucent());
    }
    
    /**
     * Helper method to create a StateMap that ignores certain properties for model rendering.
     * Used for blocks like campfire where the 'burning' state shouldn't affect the model.
     *
     * @param block The block to create the state map for.
     * @param properties The properties to ignore.
     * @return The StateMap builder.
     */
    public static net.minecraftforge.client.model.generators.BlockStateProvider createStateProvider(
            net.minecraftforge.client.model.generators.BlockStateProvider provider,
            Block block,
            Property<?>... properties) {
        // This would be used in a BlockStateProvider for data generation
        // For now, handle it via JSON files in assets/<modid>/blockstates/
        return provider;
    }
}