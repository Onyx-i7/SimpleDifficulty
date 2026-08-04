package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.tileentity.TileEntitySpit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSpit extends Block {
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);

    public BlockSpit(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Nullable
    public TileEntity newBlockEntity(IBlockReader world) {
        return new TileEntitySpit();
    }
    
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(world.isClientSide) return ActionResultType.SUCCESS;
        
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof TileEntitySpit) {
            ((TileEntitySpit)te).handleRightClick(world, pos, state, player, hand, hit);
        }
        return ActionResultType.SUCCESS;
    }
    
    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (world.isClientSide) return;
        checkCampfireOrDestroy(world, pos, state);
    }
    
    @Override
    public boolean canSurvive(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos) {
        if (world instanceof World) {
            return hasCampfire((World) world, pos);
        }
        return false;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        if (!world.isClientSide) {
            checkCampfireOrDestroy(world, pos, state);
        }
    }
    
    private boolean hasCampfire(World world, BlockPos pos) {
        return world.getBlockState(pos.below()).is(SDBlocks.campfire.get());
    }
    
    private void checkCampfireOrDestroy(World world, BlockPos pos, BlockState state) {
        if(!hasCampfire(world, pos)) {
            popResource(world, pos, new ItemStack(this));
            world.removeBlock(pos, false);
        }
    }
    
    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity te = world.getBlockEntity(pos);
            if(te instanceof TileEntitySpit) {
                ((TileEntitySpit) te).dumpItems(world, pos);
                ((TileEntitySpit) te).dumpExperience(world, pos);
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return true;
    }
}