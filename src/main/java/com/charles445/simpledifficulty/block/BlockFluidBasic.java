package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.compat.mod.SereneSeasonsReflectionBridge;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockFluidBasic extends FlowingFluidBlock {
    private final String iceBlock;

    public BlockFluidBasic(Fluid fluid, AbstractBlock.Properties properties, String iceBlock) {
        super(fluid, properties);
        this.iceBlock = iceBlock;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && heldItem.getItem() == Items.GLASS_BOTTLE) {
            ItemStack resultBottle = getBottleResult();
            if (!resultBottle.isEmpty()) {
                if (!world.isClientSide) {
                    heldItem.shrink(1);
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(hand, resultBottle);
                    } else if (!player.inventory.add(resultBottle)) {
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
        BlockPos posDown = new BlockPos(pos.getX(), 0, pos.getZ()).above(world.getHeightmapPos(net.minecraft.world.gen.Heightmap.Type.MOTION_BLOCKING, pos).getY()).below();

        if (this.canFreeze(world, posDown) && world.random.nextInt(16) == 0) {
            // Logic to freeze
        }
    }

    public boolean canFreeze(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos).value();
        float f = SereneSeasonsReflectionBridge.getTemperatureSafe(world, biome, pos);

        if (f <= 0.15F) {
            if (pos.getY() >= 0 && pos.getY() < 256 && world.getBrightness(net.minecraft.world.LightType.BLOCK, pos) < 10) {
                BlockState iblockstate1 = world.getBlockState(pos);
                Block block = iblockstate1.getBlock();
                return block == this && ((FlowingFluidBlock) block).getFluidState(iblockstate1).isSource();
            }
        }
        return false;
    }

    public Vector3d getFluidColor(IBlockReader world, BlockPos pos, Vector3d originalColor) {
        int biomeWaterColor = BiomeColors.getAverageWaterColor(world, pos);
        float r = (float) ((biomeWaterColor >> 16) & 0xFF) / 255.0F;
        float g = (float) ((biomeWaterColor >> 8) & 0xFF) / 255.0F;
        float b = (float) (biomeWaterColor & 0xFF) / 255.0F;
        return new Vector3d(0.37F + r, 0.53F + g, 0.53F + b);
    }

    @Override
    public int getLightBlock(BlockState state, IBlockReader world, BlockPos pos) {
        return ServerConfig.instance.getBoolean(ServerOptions.PURIFIED_WATER_OPACITY) ? 1 : 3;
    }
}