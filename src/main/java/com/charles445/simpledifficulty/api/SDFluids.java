package com.charles445.simpledifficulty.api;

import com.charles445.simpledifficulty.register.RegisterFluids;
import com.charles445.simpledifficulty.register.RegisterBlocks;
import com.charles445.simpledifficulty.register.RegisterItems;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for SimpleDifficulty fluids, their blocks, and bucket items.
 */
public class SDFluids {

    public static final Map<String, RegistryObject<Fluid>> fluids = new LinkedHashMap<String, RegistryObject<Fluid>>() {{
        put("purifiedwater", RegisterFluids.PURIFIED_WATER);
        put("saltwater", RegisterFluids.SALT_WATER);
    }};

    public static final Map<String, RegistryObject<FlowingFluidBlock>> fluidBlocks = new LinkedHashMap<String, RegistryObject<FlowingFluidBlock>>() {{
        put("purifiedwater", RegisterFluids.BLOCK_PURIFIED_WATER);
        put("saltwater", RegisterFluids.BLOCK_SALT_WATER);
    }};

    // Fluids
    public static final RegistryObject<FlowingFluid> purifiedWater = RegisterFluids.PURIFIED_WATER;
    public static final RegistryObject<FlowingFluid> saltWater = RegisterFluids.SALT_WATER;

    // Fluid Blocks
    public static final RegistryObject<FlowingFluidBlock> blockPurifiedWater = RegisterFluids.BLOCK_PURIFIED_WATER;
    public static final RegistryObject<FlowingFluidBlock> blockSaltWater = RegisterFluids.BLOCK_SALT_WATER;

    // Bucket Items
    public static final RegistryObject<Item> purifiedWaterBucket = RegisterFluids.PURIFIED_WATER_ITEM;
    public static final RegistryObject<Item> saltWaterBucket = RegisterFluids.SALT_WATER_ITEM;
}