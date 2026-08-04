package com.charles445.simpledifficulty.world.gen;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.block.BlockFluidBasic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles world generation of mod ice blocks.
 * Converts surface water blocks from this mod into ice blocks when temperature conditions are met.
 */
@Mod.EventBusSubscriber
public class WorldGenIce {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        World world;
        if (event.getWorld() instanceof World) {
            world = (World) event.getWorld();
        } else {
            return;
        }
        if (world == null || world.isClientSide) {
            return;
        }

        // Only process in the Overworld
        if (!world.dimension().location().toString().equals("minecraft:overworld")) {
            return;
        }

        int chunkBaseX = event.getChunk().getPos().x << 4;
        int chunkBaseZ = event.getChunk().getPos().z << 4;

        Block blockPurifiedWater = SDFluids.blockPurifiedWater.get();
        Block blockSaltWater = SDFluids.blockSaltWater.get();
        BlockState icePurifiedState = SDBlocks.icePurifiedWater.get().defaultBlockState();
        BlockState iceSaltState = SDBlocks.iceSaltWater.get().defaultBlockState();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkBaseX + x;
                int worldZ = chunkBaseZ + z;

                BlockPos topPos = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, new BlockPos(worldX, 0, worldZ));
                BlockPos waterPos = topPos.below();

                if (waterPos.getY() <= 0) {
                    continue;
                }

                BlockState stateAtWater = world.getBlockState(waterPos);
                Block blockAtWater = stateAtWater.getBlock();

                if (blockAtWater == blockSaltWater && blockSaltWater instanceof BlockFluidBasic) {
                    if (((BlockFluidBasic) blockSaltWater).canFreeze(world, waterPos)) {
                        world.setBlock(waterPos, iceSaltState, 2);
                    }
                } else if (blockAtWater == blockPurifiedWater && blockPurifiedWater instanceof BlockFluidBasic) {
                    if (((BlockFluidBasic) blockPurifiedWater).canFreeze(world, waterPos)) {
                        world.setBlock(waterPos, icePurifiedState, 2);
                    }
                }
            }
        }
    }
}