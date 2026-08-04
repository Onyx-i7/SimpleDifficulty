package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.compat.mod.Weather2Compat;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockCampfire extends Block {
    private static final int AGE_MIN = 0;
    private static final int AGE_MAX = 7;
    private static final int LOG_REFUEL = 3;
    private static final int RAIN_CHECK_RATE = 20;

    // Modern state properties replacing IProperty
    public static final IntegerProperty AGE = IntegerProperty.create("age", AGE_MIN, AGE_MAX);
    public static final BooleanProperty BURNING = BooleanProperty.create("burning");

    // VoxelShape replaces AxisAlignedBB for collision and selection
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.4D, 16.0D);

    public BlockCampfire(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, AGE_MIN)
                .setValue(BURNING, false));
    }

    private boolean isRainingAt(World world, BlockPos pos) {
        BlockPos checkPos = pos.above();
        if (!world.canSeeSky(checkPos)) {
            return false;
        }
        return Weather2Compat.isRainingAt(world, checkPos);
    }

    private void scheduleRainCheck(World world, BlockPos pos) {
        if (world instanceof net.minecraft.world.server.ServerWorld) {
            ((net.minecraft.world.server.ServerWorld) world).getBlockTicks().scheduleTick(pos, this, RAIN_CHECK_RATE);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE, BURNING);
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isClientSide && state.getValue(BURNING)) {
            scheduleRainCheck(world, pos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.isEmpty()) {
            return ActionResultType.SUCCESS;
        }

        Block heldBlock = Block.byItem(heldItem.getItem());
        if (heldBlock == com.charles445.simpledifficulty.register.RegisterBlocks.SPIT.get()) {
            return ActionResultType.PASS;
        }

        boolean isRaining = isRainingAt(world, pos);

        if (world.isClientSide) {
            if (heldItem.getItem() == Items.FLINT_AND_STEEL) {
                int age = state.getValue(AGE);
                boolean burning = state.getValue(BURNING);
                if (!burning && age < AGE_MAX && !isRaining) {
                    world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
                }
            }
            return ActionResultType.SUCCESS;
        }

        int age = state.getValue(AGE);
        boolean burning = state.getValue(BURNING);

        // Check if holding a log (simplified, you'll need to update OreDictUtil for tags)
        if (heldItem.getItem().getTags().contains(new net.minecraft.util.ResourceLocation("forge", "logs"))) {
            if (age > AGE_MIN) {
                if (!player.abilities.instabuild) {
                    heldItem.shrink(1);
                }
                int refuelAmount = LOG_REFUEL + (age == AGE_MAX ? 1 : 0);
                world.setBlock(pos, state.setValue(AGE, Math.max(AGE_MIN, age - refuelAmount)), 3);

                if (burning) {
                    scheduleRainCheck(world, pos);
                }
            }
            return ActionResultType.SUCCESS;
        } else if (!burning && age < AGE_MAX && !isRaining) {
            boolean ignited = false;

            if (heldItem.getItem().getTags().contains(new net.minecraft.util.ResourceLocation("forge", "rods/wooden")) 
                    || heldItem.getItem() == Items.STICK) {
                if (!player.abilities.instabuild) {
                    heldItem.shrink(1);
                }
                if (world.random.nextInt(ModConfig.SERVER.campfireStickIgniteChance.get()) == 0) {
                    world.setBlock(pos, state.setValue(BURNING, true), 3);
                    ignited = true;
                }
            } else if (heldItem.getItem() == Items.FLINT_AND_STEEL) {
                world.setBlock(pos, state.setValue(BURNING, true), 3);
                heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                ignited = true;
            }

            if (ignited) {
                scheduleRainCheck(world, pos);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        int age = state.getValue(AGE);
        boolean burning = state.getValue(BURNING);

        if (burning) {
            if (isRainingAt(world, pos)) {
                extinguishCampfire(world, pos, state);
                return;
            }

            if (rand.nextInt(ModConfig.SERVER.campfireDecayChance.get()) == 0) {
                age++;
                if (age >= AGE_MAX) {
                    world.setBlock(pos, state.setValue(AGE, AGE_MAX).setValue(BURNING, false), 3);
                    effectExtinguish(world, pos);
                } else {
                    world.setBlock(pos, state.setValue(AGE, age), 3);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide && state.getValue(BURNING)) {
            if (isRainingAt(world, pos)) {
                extinguishCampfire(world, pos, state);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (!state.getValue(BURNING)) return;

        if (isRainingAt(world, pos)) {
            extinguishCampfire(world, pos, state);
            return;
        }

        scheduleRainCheck(world, pos);
    }

    public void extinguishCampfire(World world, BlockPos pos, BlockState state) {
        if (state.getValue(BURNING)) {
            if (!isRainingAt(world, pos)) {
                return;
            }

            world.setBlock(pos, state.setValue(BURNING, false), 3);
            effectExtinguish(world, pos);
        }
    }

    @SuppressWarnings("deprecation")
    public int getTickDelay(BlockState state, net.minecraft.world.IWorldReader world) {
        return RAIN_CHECK_RATE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClientSide && state.getValue(BURNING) && entity instanceof LivingEntity) {
            entity.setSecondsOnFire(1);
        }
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        super.animateTick(state, world, pos, rand);
        if (state.getValue(BURNING)) {
            int age = state.getValue(AGE);
            float strength = 1.0f - ((float) age / (float) (AGE_MAX - AGE_MIN));
            if (rand.nextFloat() < strength) {
                int loop = rand.nextInt(6) + 1;
                for (int i = 0; i < loop; i++) {
                    createFlameParticle(world, pos, rand);
                }
            }
            if (rand.nextInt(30) == 0) {
                world.playLocalSound(0.5d + pos.getX(), 0.5d + pos.getY(), 0.5d + pos.getZ(), 
                        SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
            }
        }
    }

    private void effectExtinguish(World world, BlockPos pos) {
        SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.FIRE_EXTINGUISH);
        if (!world.isClientSide) {
            for (int i = 0; i < 4; i++) {
                double xOffset = pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 0.4;
                double yOffset = pos.getY() + 0.4 + world.random.nextDouble() * 0.3;
                double zOffset = pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 0.4;
                world.addParticle(ParticleTypes.LARGE_SMOKE, xOffset, yOffset, zOffset, 0.0, 0.05, 0.0);
                world.addParticle(ParticleTypes.SMOKE, xOffset, yOffset, zOffset, 0.0, 0.03, 0.0);
            }
        }
    }

    private void createFlameParticle(World world, BlockPos pos, Random rand) {
        double yOffset = rand.nextDouble() * 0.35d + 0.35d;
        double offAdj = (0.7d - yOffset) * 2.28571428d;
        double xOffset = (rand.nextDouble() - 0.5d) * offAdj + 0.5d;
        double zOffset = (rand.nextDouble() - 0.5d) * offAdj + 0.5d;
        world.addParticle(ParticleTypes.FLAME, xOffset + pos.getX(), yOffset + pos.getY(), zOffset + pos.getZ(), 
                0.0d, (rand.nextDouble() * 0.015d) + 0.005d, 0.0d);
    }

    public boolean isPathfindable(BlockState state, net.minecraft.world.IBlockReader world, BlockPos pos, net.minecraft.pathfinding.PathNodeType type) {
        return false;
    }
}