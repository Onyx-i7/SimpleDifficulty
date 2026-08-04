package com.charles445.simpledifficulty.world.gen;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

/**
 * Handles world generation of mod ice blocks.
 * Converts surface water blocks from this mod into ice blocks when temperature conditions are met.
 * 
 * In 1.16.5, IWorldGenerator was removed, so we use ChunkEvent.Load to process
 * newly generated chunks and convert water to ice based on biome temperature.
 */
@Mod.EventBusSubscriber
public class WorldGenIce {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        World world = event.getWorld();
        if (world == null || world.isClientSide) {
            return;
        }

        // Only process in the Overworld (dimension 0 equivalent)
        if (!world.dimension().location().toString().equals("minecraft:overworld")) {
            return;
        }

        // Process the chunk
        int chunkBaseX = event.getChunk().getPos().x << 4;
        int chunkBaseZ = event.getChunk().getPos().z << 4;

        Random rand = world.random;

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

                // Only generate mod ice if the block below is the corresponding fluid
                // and the fluid's canFreeze method returns true
                if (blockAtWater == blockSaltWater) {
                    if (blockSaltWater instanceof com.charles445.simpledifficulty.block.BlockFluidBasic) {
                        if (((com.charles445.simpledifficulty.block.BlockFluidBasic) blockSaltWater).canFreeze(world, waterPos)) {
                            world.setBlock(waterPos, iceSaltState, 2);
                        }
                    }
                } else if (blockAtWater == blockPurifiedWater) {
                    if (blockPurifiedWater instanceof com.charles445.simpledifficulty.block.BlockFluidBasic) {
                        if (((com.charles445.simpledifficulty.block.BlockFluidBasic) blockPurifiedWater).canFreeze(world, waterPos)) {
                            world.setBlock(waterPos, icePurifiedState, 2);
                        }
                    }
                }
                // Don't touch vanilla ice or vanilla water - let Minecraft handle them naturally
            }
        }
    }
}