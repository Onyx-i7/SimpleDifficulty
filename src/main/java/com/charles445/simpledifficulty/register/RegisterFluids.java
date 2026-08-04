package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.block.BlockFluidBasic;
import com.charles445.simpledifficulty.block.BlockFluidBasicMixable;
import com.charles445.simpledifficulty.block.BlockFluidSaltWater;
import com.charles445.simpledifficulty.fluid.FluidBasic;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, SimpleDifficulty.MODID);

    // Properties of Purified Water
    public static final ForgeFlowingFluid.Properties PURIFIED_WATER_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> PURIFIED_WATER_SOURCE.get(),
            () -> PURIFIED_WATER.get(),
            FluidBasic.createAttributes("purified_water_still", "purified_water_flow")
    ).bucket(() -> PURIFIED_WATER_ITEM.get()).block(() -> BLOCK_PURIFIED_WATER.get());

    // Properties for Salt Water
    public static final ForgeFlowingFluid.Properties SALT_WATER_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> SALT_WATER_SOURCE.get(),
            () -> SALT_WATER.get(),
            FluidBasic.createAttributes("salt_water_still", "salt_water_flow")
    ).bucket(() -> SALT_WATER_ITEM.get()).block(() -> BLOCK_SALT_WATER.get());

    // Fluids
    public static final RegistryObject<Fluid> PURIFIED_WATER_SOURCE = FLUIDS.register("purified_water_source", 
            () -> new FluidBasic.Source(() -> PURIFIED_WATER_PROPERTIES));
    
    public static final RegistryObject<Fluid> PURIFIED_WATER = FLUIDS.register("purified_water_flowing", 
            () -> new FluidBasic.Flowing(() -> PURIFIED_WATER_PROPERTIES));

    public static final RegistryObject<Fluid> SALT_WATER_SOURCE = FLUIDS.register("saltwater_source", 
            () -> new FluidBasic.Source(() -> SALT_WATER_PROPERTIES));
    
    public static final RegistryObject<Fluid> SALT_WATER = FLUIDS.register("saltwater", 
            () -> new FluidBasic.Flowing(() -> SALT_WATER_PROPERTIES));

    // Fluid Blocks
    public static final RegistryObject<FlowingFluidBlock> BLOCK_PURIFIED_WATER = RegisterBlocks.BLOCKS.register("purifiedwater", 
            () -> new BlockFluidBasicMixable(() -> PURIFIED_WATER_SOURCE.get(), 
                    AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops(), 
                    "purifiedwater"));
    
    public static final RegistryObject<FlowingFluidBlock> BLOCK_SALT_WATER = RegisterBlocks.BLOCKS.register("saltwater", 
            () -> new BlockFluidSaltWater(() -> SALT_WATER_SOURCE.get(), 
                    AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops(), 
                    "saltwater"));

    // Items (buckets)
    public static final RegistryObject<Item> PURIFIED_WATER_ITEM = RegisterItems.ITEMS.register("purifiedwater_bucket", 
            () -> new BlockItem(BLOCK_PURIFIED_WATER.get(), new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1)));
    
    public static final RegistryObject<Item> SALT_WATER_ITEM = RegisterItems.ITEMS.register("saltwater_bucket", 
            () -> new BlockItem(BLOCK_SALT_WATER.get(), new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}