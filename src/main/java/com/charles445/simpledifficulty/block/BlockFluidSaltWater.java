package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;

import java.util.function.Supplier;

public class BlockFluidSaltWater extends BlockFluidBasic {
    public BlockFluidSaltWater(Supplier<? extends Fluid> fluid, AbstractBlock.Properties properties, String iceBlock) {
        super(fluid, properties, iceBlock);
    }

    @Override
    protected ItemStack getBottleResult() {
        return new ItemStack(SDItems.saltWaterBottle.get());
    }
}