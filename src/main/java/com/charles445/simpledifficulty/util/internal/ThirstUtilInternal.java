package com.charles445.simpledifficulty.util.internal;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.thirst.*;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraft.fluid.FlowingFluid;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Internal implementation of IThirstUtil.
 * Handles water tracing, drinking mechanics, and bucket creation.
 */
public class ThirstUtilInternal implements IThirstUtil {
    // Cached HashSet for river blocks lookup - O(1) instead of O(n) array iteration
    private static final Set<String> RIVER_BLOCKS_SET = new HashSet<>();

    static {
        RIVER_BLOCKS_SET.add("river/tile.water/-1/-2");
        RIVER_BLOCKS_SET.add("river/tile.water/-1/2");
        RIVER_BLOCKS_SET.add("river/tile.water/-2/-1");
        RIVER_BLOCKS_SET.add("river/tile.water/-2/-2");
        RIVER_BLOCKS_SET.add("river/tile.water/-2/0");
        RIVER_BLOCKS_SET.add("river/tile.water/-2/1");
        RIVER_BLOCKS_SET.add("river/tile.water/-2/2");
        RIVER_BLOCKS_SET.add("river/tile.water/0/-2");
        RIVER_BLOCKS_SET.add("river/tile.water/0/0");
        RIVER_BLOCKS_SET.add("river/tile.water/0/2");
        RIVER_BLOCKS_SET.add("river/tile.water/1/-2");
        RIVER_BLOCKS_SET.add("river/tile.water/1/2");
        RIVER_BLOCKS_SET.add("river/tile.water/2/-1");
        RIVER_BLOCKS_SET.add("river/tile.water/2/-2");
        RIVER_BLOCKS_SET.add("river/tile.water/2/0");
        RIVER_BLOCKS_SET.add("river/tile.water/2/1");
        RIVER_BLOCKS_SET.add("river/tile.water/2/2");
    }

    @Nullable
    public static ThirstEnumBlockPos traceWaterToDrink(PlayerEntity player) {
        if (player.getMainHandItem().isEmpty()) {
            IThirstCapability capability = SDCapabilities.getThirstData(player);
            if (capability != null && capability.isThirsty()) {
                ThirstEnumBlockPos traceResult = ThirstUtil.traceWater(player);
                if (traceResult == null)
                    return null;

                if (traceResult.thirstEnum == ThirstEnum.PURIFIED) {
                    if (!ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS))
                        return null;

                    if (!ServerConfig.instance.getBoolean(ServerOptions.INFINITE_PURIFIED_WATER))
                        player.level.setBlock(traceResult.pos, Blocks.AIR.defaultBlockState(), 3);
                } else if (traceResult.thirstEnum == ThirstEnum.RAIN && !ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN)) {
                    return null;
                } else if (traceResult.thirstEnum == ThirstEnum.NORMAL) {
                    if (!ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS))
                        return null;

                    player.level.setBlock(traceResult.pos, Blocks.AIR.defaultBlockState(), 3);
                } else if (traceResult.thirstEnum == ThirstEnum.SALT && !ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS)) {
                    return null;
                }

                return traceResult;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public ThirstEnumBlockPos traceWater(PlayerEntity player) {
        // Check if player is looking up at rain
        if (player.xRot < -75.0f && player.level.isRainingAt(player.blockPosition()) && player.level.canSeeSky(player.blockPosition()) && ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN)) {
            return new ThirstEnumBlockPos(ThirstEnum.RAIN, player.blockPosition());
        }

        // Ray tracing
        double reach = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() * 0.5d;

        Vector3d eyevec = player.getEyePosition(1.0f);
        Vector3d lookvec = player.getViewVector(1.0f);
        Vector3d targetvec = eyevec.add(lookvec.x * reach, lookvec.y * reach, lookvec.z * reach);

        RayTraceContext context = new RayTraceContext(eyevec, targetvec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, player);
        BlockRayTraceResult trace = player.level.clip(context);

        if (trace == null || trace.getType() != RayTraceResult.Type.BLOCK)
            return null;

        BlockPos blockPos = trace.getBlockPos();
        Block traceBlock = player.level.getBlockState(blockPos).getBlock();

        if (traceBlock == Blocks.WATER) {
            if (isFreshWater(player, blockPos)) {
                return new ThirstEnumBlockPos(ThirstEnum.NORMAL, blockPos);
            }
            return new ThirstEnumBlockPos(ThirstEnum.SALT, blockPos);
        } else if (traceBlock == SDFluids.blockPurifiedWater.get()) {
            return new ThirstEnumBlockPos(ThirstEnum.PURIFIED, blockPos);
        } else if (traceBlock == SDFluids.blockSaltWater.get()) {
            return new ThirstEnumBlockPos(ThirstEnum.SALT, blockPos);
        }

        String blockRegistryName = traceBlock.getRegistryName().toString();
        if (RIVER_BLOCKS_SET.contains(blockRegistryName)) {
            return new ThirstEnumBlockPos(ThirstEnum.NORMAL, blockPos);
        }

        return null;
    }

    private boolean isFreshWater(PlayerEntity player, BlockPos waterPos) {
        if (!ServerConfig.instance.getBoolean(ServerOptions.SALT_WATER_THIRST)) {
            return true;
        }

        Biome biome = player.level.getBiome(waterPos);
        if (biome != null && biome.getRegistryName() != null) {
            String name = biome.getRegistryName().toString();
            if (name.contains("river")) {
                return true;
            }
        }

        return isEnclosedLake(player.level, waterPos);
    }

    private boolean isEnclosedLake(World world, BlockPos pos) {
        int maxDist = 4;
        int[] dirsX = {1, -1, 0, 0};
        int[] dirsZ = {0, 0, 1, -1};

        for (int d = 0; d < 4; d++) {
            boolean foundLand = false;
            for (int i = 1; i <= maxDist; i++) {
                BlockPos check = pos.offset(dirsX[d] * i, 0, dirsZ[d] * i);

                if (!world.isLoaded(check)) {
                    foundLand = true;
                    break;
                }

                Block b = world.getBlockState(check).getBlock();
                if (b != Blocks.WATER) {
                    foundLand = true;
                    break;
                }
            }
            if (!foundLand) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void takeDrink(PlayerEntity player, int thirst, float saturation, float dirtyChance) {
        if (!QuickConfig.isThirstEnabled())
            return;

        IThirstCapability capability = SDCapabilities.getThirstData(player);
        if (capability == null) return;

        if (capability.isThirsty()) {
            capability.addThirstLevel(thirst);
            capability.addThirstSaturation(saturation);

            if (dirtyChance == 0.75f && player.level.random.nextFloat() < dirtyChance) {
                if (ModConfig.SERVER.thirstParasites.get() && player.level.random.nextDouble() < ModConfig.SERVER.thirstParasitesChance.get()) {
                    player.addEffect(new EffectInstance(SDPotions.parasites.get(), ModConfig.SERVER.thirstParasitesDuration.get()));
                }
            }

            if (dirtyChance == 1.0f) {
                player.addEffect(new EffectInstance(SDPotions.thirsty.get(), 600));
            }
        } else {
            if (capability.getThirstSaturation() < saturation)
                capability.setThirstSaturation(saturation);
        }
    }

    @Override
    public void takeDrink(PlayerEntity player, int thirst, float saturation) {
        takeDrink(player, thirst, saturation, 0.0f);
    }

    @Override
    public void takeDrink(PlayerEntity player, ThirstEnum type) {
        takeDrink(player, type.getThirst(), type.getSaturation(), type.getThirstyChance());
    }

    @Override
    public ItemStack createPurifiedWaterBucket() {
        return FluidUtil.getFilledBucket(new FluidStack(SDFluids.purifiedWater.get(), FluidAttributes.BUCKET_VOLUME));
    }

    @Override
    public ItemStack createSaltWaterBucket() {
        return FluidUtil.getFilledBucket(new FluidStack(SDFluids.saltWater.get(), FluidAttributes.BUCKET_VOLUME));
    }

    @Override
    public ItemStack createNormalWaterBucket() {
        return FluidUtil.getFilledBucket(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME));
    }
}