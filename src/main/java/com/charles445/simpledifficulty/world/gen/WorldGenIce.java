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
                    
                    BlockPos airPos = world.getPrecipitationHeight(new BlockPos(worldX, 0, worldZ));
                    BlockPos waterPos = airPos.down();
                    
                    if (waterPos.getY() <= 0) {
                        continue;
                    }
                    
                    IBlockState stateAtWater = world.getBlockState(waterPos);
                    Block blockAtWater = stateAtWater.getBlock();
                    
                    // Only generate mod ice if the block below is the corresponding fluid
                    // Pass waterPos (not airPos) to canFreeze() so it checks the actual fluid block
                    if (blockAtWater == blockSaltWater && blockSaltWater.canFreeze(world, waterPos)) {
                        world.setBlockState(waterPos, iceSaltWater.getDefaultState(), 2);
                    } else if (blockAtWater == blockPurifiedWater && blockPurifiedWater.canFreeze(world, waterPos)) {
                        world.setBlockState(waterPos, icePurifiedWater.getDefaultState(), 2);
                    }
                    // Don't touch vanilla ice or vanilla water let Minecraft handle them naturally
                }
            }
        }
    }
}
