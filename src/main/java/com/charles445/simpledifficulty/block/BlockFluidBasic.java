package com.charles445.simpledifficulty.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

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

    /**
     * Checks if this fluid block can freeze at the given position.
     * Based on biome temperature and light levels.
     */
    public boolean canFreeze(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        if (biome != null) {
            float temperature = biome.getTemperature(pos);
            if (temperature <= 0.15F) {
                if (pos.getY() >= 0 && pos.getY() < 256 && world.getBrightness(LightType.BLOCK, pos) < 10) {
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block == this && ((FlowingFluidBlock) block).getFluidState(state).isSource()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}