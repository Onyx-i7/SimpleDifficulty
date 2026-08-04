package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDFluids;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class BlockIceBasic extends Block {
    private final String waterBlock;

    public BlockIceBasic(Properties properties, String waterBlock) {
        super(properties);
        this.waterBlock = waterBlock;
    }

    @Override
    public void playerDestroy(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.addExhaustion(0.005F);

        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
            spawnAsEntity(world, pos, new ItemStack(this));
        } else {
            if (world.dimensionType().isUltraWarm()) {
                world.removeBlock(pos, false);
                return;
            }

            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
            dropExperience(world, pos, 0);
            
            BlockState downState = world.getBlockState(pos.below());
            if (downState.getMaterial().blocksMotion() || downState.getMaterial().isLiquid()) {
                world.setBlock(pos, getFluidStateSafe(), 3);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (world.isClientSide) return;

        Biome biome = world.getBiome(pos).value();
        float f = biome.getTemperature(pos);

        f = com.charles445.simpledifficulty.compat.mod.SereneSeasonsReflectionBridge.getTemperatureSafe(world, biome, pos);

        if (f > 0.15F && world.getBrightness(LightType.BLOCK_LIGHT, pos) > 11 - state.getLightBlock(world, pos)) {
            turnIntoWater(world, pos);
        }
    }

    private void turnIntoWater(World world, BlockPos pos) {
        if (world.dimensionType().isUltraWarm()) {
            world.removeBlock(pos, false);
        } else {
            BlockState fluidState = getFluidStateSafe();
            world.setBlock(pos, fluidState, 3);
            world.neighborChanged(pos, fluidState.getBlock(), pos);
        }
    }

    private BlockState getFluidStateSafe() {
        if (SDFluids.fluidBlocks.containsKey(waterBlock)) {
             Block block = SDFluids.fluidBlocks.get(waterBlock);
             if (block != null) {
                 return block.defaultBlockState();
             }
        }
        return Blocks.WATER.defaultBlockState();
    }
}