package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.handler.FluidHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockReader;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;

import java.util.function.Supplier;
import java.util.Random;

public class BlockFluidBasicMixable extends BlockFluidBasic {
    public BlockFluidBasicMixable(Supplier<? extends Fluid> fluid, AbstractBlock.Properties properties, String iceBlock) {
        super(fluid, properties, iceBlock);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.tick(state, world, pos, random);
        
        if (world.isClientSide) return;

        if (FluidHandler.canMix(pos, world)) {
            FluidHandler.scheduleMixing(world, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (world.isClientSide) return;
        
        if (!world.isLoaded(pos)) return;
        
        if (world instanceof net.minecraft.world.server.ServerWorld) {
            ((net.minecraft.world.server.ServerWorld) world).getBlockTicks().scheduleTick(pos, this, 5);
        }
        
        if (FluidHandler.canMix(pos, world)) {
            FluidHandler.scheduleMixing(world, pos);
        }
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (world.isClientSide) return;

        if (world instanceof ServerWorld) {
            ((ServerWorld) world).getBlockTicks().scheduleTick(pos, this, 5);
        }
        
        if (FluidHandler.canMix(pos, world)) {
            FluidHandler.scheduleMixing(world, pos);
        }
    }

    @Override
    public int getLightBlock(BlockState state, IBlockReader world, BlockPos pos) {
        return ServerConfig.instance.getBoolean(ServerOptions.PURIFIED_WATER_OPACITY) ? 1 : 3;
    }

    @Override
    protected ItemStack getBottleResult() {
        return new ItemStack(SDItems.purifiedWaterBottle.get());
    }
}