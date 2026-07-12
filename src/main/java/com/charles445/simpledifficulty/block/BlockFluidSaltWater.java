package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDItems;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidSaltWater extends BlockFluidBasic {
    public BlockFluidSaltWater(Fluid fluid, Material material, String iceBlock) {
        super(fluid, material, iceBlock);
    }

    @Override
    protected ItemStack getBottleResult() {
        return new ItemStack(SDItems.saltWaterBottle);
    }
}
