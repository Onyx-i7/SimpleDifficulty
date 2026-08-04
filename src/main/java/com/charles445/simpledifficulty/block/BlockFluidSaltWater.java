package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

public class BlockFluidSaltWater extends BlockFluidBasic {
    public BlockFluidSaltWater(Fluid fluid, Properties properties, String iceBlock) {
        super(fluid, properties, iceBlock);
    }

    @Override
    protected ItemStack getBottleResult() {
        return new ItemStack(SDItems.saltWaterBottle.get());
    }
}