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

public class WorldGenIce implements IWorldGenerator
{
    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (world.provider.getDimension() == 0)
        {
            int chunkBaseX = chunkX << 4;
            int chunkBaseZ = chunkZ << 4;
            
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    int worldX = chunkBaseX + x;
                    int worldZ = chunkBaseZ + z;
                    
                    BlockPos topPos = world.getPrecipitationHeight(new BlockPos(worldX, 0, worldZ));
                    
                    if (topPos.getY() <= 0)
                    {
                        continue;
                    }
                    
                    BlockPos posDown = topPos.down();
                    IBlockState stateAt = world.getBlockState(posDown);
                    Block blockAt = stateAt.getBlock();
                    
                    if (blockSaltWater.canFreeze(world, posDown))
                    {
                        world.setBlockState(posDown, iceSaltWater.getDefaultState(), 2);
                    }
                    else if (blockPurifiedWater.canFreeze(world, posDown) || blockAt == Blocks.ICE)
                    {
                        world.setBlockState(posDown, icePurifiedWater.getDefaultState(), 2);
                    }
                }
            }
        }
    }
}