package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.block.BlockFluidBasicMixable;
import com.charles445.simpledifficulty.block.BlockFluidSaltWater;
import com.charles445.simpledifficulty.fluid.FluidBasic;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class RegisterFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, SimpleDifficulty.MODID);

    public static ForgeFlowingFluid.Properties PURIFIED_WATER_PROPERTIES; 

    public static final RegistryObject<Fluid> PURIFIED_WATER_SOURCE = FLUIDS.register("purified_water_source", 
        () -> new FluidBasic.Source(PURIFIED_WATER_PROPERTIES));
    
    public static final RegistryObject<Fluid> PURIFIED_WATER = FLUIDS.register("purified_water_flowing", 
        () -> new FluidBasic.Flowing(PURIFIED_WATER_PROPERTIES));
        
    public static final RegistryObject<Fluid> SALT_WATER = FLUIDS.register("saltwater", 
        () -> new FluidBasic.Flowing(PURIFIED_WATER_PROPERTIES)); // Cambiar propiedades si salada usa otras

    public static final RegistryObject<LiquidBlock> BLOCK_PURIFIED_WATER = RegisterBlocks.BLOCKS.register("purifiedwater", 
        () -> new BlockFluidBasicMixable(PURIFIED_WATER, LiquidBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));
        
    public static final RegistryObject<LiquidBlock> BLOCK_SALT_WATER = RegisterBlocks.BLOCKS.register("saltwater", 
        () -> new BlockFluidSaltWater(SALT_WATER, LiquidBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));

    // Items de Fluidos (Se usa CreativeModeTab en lugar de ModCreativeTab si es nativo)
    public static final RegistryObject<Item> PURIFIED_WATER_ITEM = RegisterItems.ITEMS.register("purifiedwater", 
        () -> new BlockItem(BLOCK_PURIFIED_WATER.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        
    public static final RegistryObject<Item> SALT_WATER_ITEM = RegisterItems.ITEMS.register("saltwater", 
        () -> new BlockItem(BLOCK_SALT_WATER.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
