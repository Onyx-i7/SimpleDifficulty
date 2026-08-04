package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.tileentity.TileEntityTemperature;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTemperature extends Block {
    public static final BooleanProperty ENABLED = BlockStateProperties.POWERED;
    
    private final float temperature;
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockTemperature(float temperature, Properties properties) {
        super(properties);
        this.temperature = temperature;
        this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, false));
    }
    
    public float getActiveTemperatureMult() {
        return temperature;
    }
    
    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new TileEntityTemperature();
    }
    
    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isClientSide) {
            boolean enabled = state.getValue(ENABLED);
            boolean powered = world.hasNeighborSignal(pos);
            
            if (enabled && !powered) turnOff(world, pos, state);
            else if(!enabled && powered) turnOn(world, pos, state);
        }
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        if (!world.isClientSide) {
            boolean enabled = state.getValue(ENABLED);
            boolean powered = world.hasNeighborSignal(pos);
        
            if (enabled && !powered) {
                if (world instanceof ServerWorld) {
                    ((ServerWorld) world).getBlockTicks().scheduleTick(pos, this, 4);
                }
            }
            else if (!enabled && powered) turnOn(world, pos, state);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (!world.isClientSide) {
            if (state.getValue(ENABLED) && !world.hasNeighborSignal(pos)) turnOff(world, pos, state);
        }
    }
    
    private void turnOff(final World world, final BlockPos pos, final BlockState state) {
        world.setBlock(pos, state.setValue(ENABLED, false), 3);
    }
    
    private void turnOn(final World world, final BlockPos pos, final BlockState state) {
        world.setBlock(pos, state.setValue(ENABLED, true), 3);
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        super.animateTick(state, world, pos, rand);
        
        if(state.getValue(ENABLED) && rand.nextFloat() <= 0.33f) {
            // Spawn particles based on temperature type
            if (temperature >= 0.0f) {
                world.addParticle(ParticleTypes.FLAME, 
                    pos.getX() + 0.5D, pos.getY() + 0.775D, pos.getZ() + 0.5D, 
                    0.0D, 0.05D, 0.0D);
            } else {
                world.addParticle(ParticleTypes.CLOUD, 
                    pos.getX() + 0.5D, pos.getY() + 0.775D, pos.getZ() + 0.5D, 
                    0.0D, 0.05D, 0.0D);
            }
        }
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }
    
    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return true;
    }
}