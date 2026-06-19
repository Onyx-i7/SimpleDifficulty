package com.charles445.simpledifficulty.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

import static com.charles445.simpledifficulty.api.SDBlocks.icePurifiedWater;
import static com.charles445.simpledifficulty.api.SDBlocks.iceSaltWater;
import static com.charles445.simpledifficulty.api.SDFluids.blockPurifiedWater;
import static com.charles445.simpledifficulty.api.SDFluids.blockSaltWater;

// I don't know what the original purpose of this code was, but I imagine it was to generate saltwater or purified water

public class WorldGenIce implements IWorldGenerator {
    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            int chunkBaseX = chunkX << 4;
            int chunkBaseZ = chunkZ << 4;
            
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int worldX = chunkBaseX + x;
                    int worldZ = chunkBaseZ + z;
                    
                    // Highest position exposed to the sky (usually AIR just above the water/blocks)
                    BlockPos airPos = world.getPrecipitationHeight(new BlockPos(worldX, 0, worldZ));
                    BlockPos waterPos = airPos.down();
                    
                    if (waterPos.getY() <= 0) {
                        continue;
                    }
                    
                    IBlockState stateAtWater = world.getBlockState(waterPos);
                    Block blockAtWater = stateAtWater.getBlock();
                    
                    // Check for freezing by passing the AIR position (where the ice block will form if the water below is freezeable).
                    if (blockSaltWater.canFreeze(world, airPos)) {
                        world.setBlockState(waterPos, iceSaltWater.getDefaultState(), 2);
                    } else if (blockPurifiedWater.canFreeze(world, airPos)) {
                        world.setBlockState(waterPos, icePurifiedWater.getDefaultState(), 2);
                    } else if (blockAtWater == Blocks.ICE) {
                        // If the game has already generated regular Minecraft ice in the water, it replaces it
                        // Note: Make sure to check if the original fluid below was purified
                        world.setBlockState(waterPos, icePurifiedWater.getDefaultState(), 2);
                    }
                }
            }
        }
    }
}
