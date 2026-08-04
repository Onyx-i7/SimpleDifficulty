package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.compat.mod.Weather2Compat;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.block.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.IBlockReader;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Direction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class BlockRainCollector extends Block {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_1_8;
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockRainCollector(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    private void scheduleDynamicUpdate(World world, BlockPos pos) {
        if (!world.getBlockTicks().willTickThisTick(pos, this)) {
            int dynamicRate = 240 + world.random.nextInt(81);
            world.scheduleTick(pos, this, dynamicRate);
        }
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isClientSide) {
            scheduleDynamicUpdate(world, pos);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClientSide) return;
        tryFillFromWeather(world, pos, state, random);
        scheduleDynamicUpdate(world, pos);
    }

    public void tryFillFromWeather(World world, BlockPos pos, BlockState state, Random rand) {
        BlockPos checkPos = pos.above();
        
        if (rand.nextInt(4) == 0 && world.canSeeSky(checkPos) && Weather2Compat.isRainingAt(world, checkPos)) {
            int currentLevel = state.getValue(LEVEL);
            if (currentLevel < 3) {
                world.setBlock(pos, state.setValue(LEVEL, currentLevel + 1), 3);
                world.updateNeighborsAt(pos, this);
            }
        }
    }
    
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (itemstack.isEmpty()) {
            if (player.isCrouching()) {
                int amount = state.getValue(LEVEL);
                if (amount > 0) {
                    if (SDCapabilities.getThirstData(player).isThirsty()) {
                        SoundUtil.commonPlayPlayerSound(player, SoundEvents.GenericDrink);
                        if (!world.isClientSide) {
                            this.setWaterLevel(world, pos, state, player.isCreative() ? amount : amount - 1);
                            ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                        }
                    }
                }
            }
            return ActionResultType.SUCCESS;
        } else {
            int amount = state.getValue(LEVEL);
            Item item = itemstack.getItem();
            
            if (item == Items.BUCKET) {
                if (amount > 0 && !world.isClientSide) {
                    if (!player.isCreative()) {
                        itemstack.shrink(1);
                    }
                    ItemStack bucket = ThirstUtil.createNormalWaterBucket();
                    if (!player.isCreative()) {
                        if (itemstack.isEmpty()) {
                            player.setItemInHand(hand, bucket);
                        } else if (!player.getInventory.add(bucket)) {
                            player.drop(bucket, false);
                        }
                    }
                    this.setWaterLevel(world, pos, state, player.isCreative() ? amount : amount - 1);
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.BucketFill);
                }
                return ActionResultType.SUCCESS;
            } else if (item == Items.GLASS_BOTTLE) {
                if (amount > 0 && !world.isClientSide) {
                    if (!player.isCreative()) itemstack.shrink(1);
                    
                    ItemStack waterBottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                    
                    if (itemstack.isEmpty()) {
                        player.setItemInHand(hand, waterBottle);
                    } else if (!player.getInventory.add(waterBottle)) {
                        player.drop(waterBottle, false);
                    } else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) player).getInventory().setChanged();
                    }
                    
                    this.setWaterLevel(world, pos, state, player.isCreative() ? amount : amount - 1);
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.BottleFill);
                }
                return ActionResultType.SUCCESS;
            } else if (item == SDItems.canteen.get()) {
                if (amount > 0 && !world.isClientSide) {
                    IItemCanteen canteen = (IItemCanteen) item;
                    if (player.isCreative()) {
                        canteen.tryAddDose(itemstack, ThirstEnum.NORMAL);
                    } else {
                        if (canteen.tryAddDose(itemstack, ThirstEnum.NORMAL)) {
                            this.setWaterLevel(world, pos, state, amount - 1);
                        }
                    }
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.BucketFill);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
    
    public void setWaterLevel(World world, BlockPos pos, BlockState state, int level) {
        world.setBlock(pos, state.setValue(LEVEL, MathHelper.clamp(level, 0, 3)), 3);
    }
    
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) { 
        return true; 
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World world, BlockPos pos) {
        return blockState.getValue(LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(LEVEL, 0);
    }

    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this || super.skipRendering(state, adjacentBlockState, side);
    }
}