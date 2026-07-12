package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import java.util.Random;

import static com.charles445.simpledifficulty.handler.FluidHandler.canMix;
import static com.charles445.simpledifficulty.handler.FluidHandler.scheduleMixing;

public class BlockFluidBasicMixable extends BlockFluidBasic {
    public BlockFluidBasicMixable(Fluid fluid, Material material, String iceBlock) {
        super(fluid, material, iceBlock);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        super.updateTick(world, pos, state, random);
        
        if (world.isRemote) return;

        if (canMix(pos, world)) {
            scheduleMixing(world, pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighbourPos) {
        if (world.isRemote) return;
        
        if (!world.isBlockLoaded(pos)) return;
        
        world.scheduleUpdate(pos, this, tickRate);
        
        if (canMix(pos, world)) {
            scheduleMixing(world, pos);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) return;

        world.scheduleUpdate(pos, this, tickRate);
        
        if (canMix(pos, world)) {
            scheduleMixing(world, pos);
        }
    }

    @Override
    public int getLightOpacity(IBlockState state) {
        return ServerConfig.instance.getBoolean(ServerOptions.PURIFIED_WATER_OPACITY) ? 1 : 3;
    }

    @Override
    protected ItemStack getBottleResult() {
        return new ItemStack(SDItems.purifiedWaterBottle);
    }
}
