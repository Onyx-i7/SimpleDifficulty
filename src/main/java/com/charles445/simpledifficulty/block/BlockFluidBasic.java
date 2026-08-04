package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.compat.mod.SereneSeasonsReflectionBridge;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.Objects;

public class BlockFluidBasic extends FlowingFluidBlock {
    private final String iceBlock;
    private static final VoxelShape EMPTY_SHAPE = Shapes.empty();

    public BlockFluidBasic(Fluid fluid, Properties properties, String iceBlock) {
        super(fluid, properties);
        this.iceBlock = iceBlock;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, UseHand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && heldItem.getItem() == Items.GLASS_BOTTLE) {
            ItemStack resultBottle = getBottleResult();
            if (!resultBottle.isEmpty()) {
                if (!world.isClientSide) {
                    heldItem.shrink(1);
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(hand, resultBottle);
                    } else if (!player.getInventory().add(resultBottle)) {
                        player.drop(resultBottle, false);
                    }
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    protected ItemStack getBottleResult() {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.tick(state, world, pos, random);

        BlockPos posDown = new BlockPos(pos.getX(), 0, pos.getZ()).above(world.getHeight(PosKind.MOTION_BLOCKING, pos.getX(), pos.getZ()).getY()).below();

        if (this.canFreeze(world, posDown) && world.random.nextInt(16) == 0) {
            Block ice = SDFluids.fluidBlocks.get(iceBlock); // Asumiendo que SDFluids.fluidBlocks ahora maneja RegistryObject<Block> o similar, o usamos un mapa estático
            if (ice != null) {
                world.setBlock(posDown, ice.defaultBlockState(), 3);
            }
        }
    }

    public boolean canFreeze(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos).value();
        
        float f = SereneSeasonsReflectionBridge.getTemperatureSafe(world, biome, pos);

        if (f <= 0.15F) {
            if (pos.getY() >= 0 && pos.getY() < 256 && world.getBrightness(LightType.BLOCK_LIGHT, pos) < 10) {
                BlockState iblockstate1 = world.getBlockState(pos);
                Block block = iblockstate1.getBlock();

                return block == this && ((FlowingFluidBlock)block).getFluidState(iblockstate1).isSource();
            }
        }
        return false;
    }

    @Override
    public Vec3 getFluidColor(IBlockReader world, BlockPos pos, Vec3 originalColor) {
        int biomeWaterColor = BiomeColors.getAverageWaterColor(world, pos);
        float r = (float) ((biomeWaterColor >> 16) & 0xFF) / 255.0F;
        float g = (float) ((biomeWaterColor >> 8) & 0xFF) / 255.0F;
        float b = (float) (biomeWaterColor & 0xFF) / 255.0F;
        
        return new Vec3(0.37F + r, 0.53F + g, 0.53F + b);
    }

    @Override
    public int getLightBlock(BlockState state, IBlockReader world, BlockPos pos) {
        return ServerConfig.instance.getBoolean(ServerOptions.PURIFIED_WATER_OPACITY) ? 1 : 3;
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return EMPTY_SHAPE;
    }
}