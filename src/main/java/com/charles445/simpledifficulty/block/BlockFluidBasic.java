package com.charles445.simpledifficulty.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;

import java.util.function.Supplier;

public class BlockFluidBasic extends FlowingFluidBlock {
    protected final String iceBlock;

    public BlockFluidBasic(Supplier<? extends FlowingFluid> fluid, AbstractBlock.Properties properties, String iceBlock) {
        super(fluid, properties);
        this.iceBlock = iceBlock;
    }

    protected ItemStack getBottleResult() {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
    }
}