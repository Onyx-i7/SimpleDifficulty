package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.block.BlockFluidBasicMixable;
import com.charles445.simpledifficulty.block.BlockFluidSaltWater;
import com.charles445.simpledifficulty.fluid.FluidBasic;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterFluids {
    public static final DeferredRegister<FlowingFluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, SimpleDifficulty.MODID);

    public static final RegistryObject<FlowingFluid> PURIFIED_WATER = FLUIDS.register("purifiedwater", FluidBasic::new);
    public static final RegistryObject<FlowingFluid> SALT_WATER = FLUIDS.register("saltwater", FluidBasic::new);

    // Fluid Blocks
    public static final RegistryObject<FlowingFluidBlock> BLOCK_PURIFIED_WATER = RegisterBlocks.BLOCKS.register("purifiedwater", () -> new BlockFluidBasicMixable(PURIFIED_WATER, Material.WATER));
    public static final RegistryObject<FlowingFluidBlock> BLOCK_SALT_WATER = RegisterBlocks.BLOCKS.register("saltwater", () -> new BlockFluidSaltWater(SALT_WATER, Material.WATER));

    public static final RegistryObject<Item> PURIFIED_WATER_ITEM = RegisterItems.ITEMS.register("purifiedwater", () -> new BlockItem(BLOCK_PURIFIED_WATER.get(), new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> SALT_WATER_ITEM = RegisterItems.ITEMS.register("saltwater", () -> new BlockItem(BLOCK_SALT_WATER.get(), new Item.Properties().tab(ModCreativeTab.TAB)));

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}