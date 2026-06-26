package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDFluids;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FluidHandler {
    public static final int MIX_TIME = 1;
    
    // Use ConcurrentLinkedQueue for thread safety and better performance
    // Weak references to World are handled by checking world validity
    public static final ConcurrentLinkedQueue<Entry> scheduledMixtures = new ConcurrentLinkedQueue<>();

    public static void scheduleMixing(World world, BlockPos pos) {
        if (world != null && pos != null) {
            scheduledMixtures.add(new Entry(world, pos));
        }
    }

    public static boolean isBiomeValid(Biome biome) {
        return BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER);
    }

    public static boolean canMix(BlockPos pos, World world) {
        if (!world.isBlockLoaded(pos.north(32))
                || !world.isBlockLoaded(pos.east(32))
                || !world.isBlockLoaded(pos.south(32))
                || !world.isBlockLoaded(pos.west(32))) {
            return false;
        }

        Block downBlock = world.getBlockState(pos.down()).getBlock();
        Block upBlock = world.getBlockState(pos.up()).getBlock();
        Block northBlock = world.getBlockState(pos.north()).getBlock();
        Block eastBlock = world.getBlockState(pos.east()).getBlock();
        Block southBlock = world.getBlockState(pos.south()).getBlock();
        Block westBlock = world.getBlockState(pos.west()).getBlock();

        int xChunkPos = world.getChunk(pos).x;
        int zChunkPos = world.getChunk(pos).z;
        BlockPos chunkPos = new BlockPos(xChunkPos * 16 + 8, 0, zChunkPos * 16 + 8);

        Biome biomeInNorthChunk = world.getBiomeForCoordsBody(chunkPos.north(32));
        Biome biomeInEastChunk = world.getBiomeForCoordsBody(chunkPos.east(32));
        Biome biomeInSouthChunk = world.getBiomeForCoordsBody(chunkPos.south(32));
        Biome biomeInWestChunk = world.getBiomeForCoordsBody(chunkPos.west(32));

        return (isBiomeValid(biomeInNorthChunk)
                || isBiomeValid(biomeInEastChunk)
                || isBiomeValid(biomeInSouthChunk)
                || isBiomeValid(biomeInWestChunk))
                && (downBlock == SDFluids.blockSaltWater
                || upBlock == SDFluids.blockSaltWater
                || northBlock == SDFluids.blockSaltWater
                || eastBlock == SDFluids.blockSaltWater
                || southBlock == SDFluids.blockSaltWater
                || westBlock == SDFluids.blockSaltWater);
    }

    public void checkAndMixBlock(BlockPos pos, World world) {
        if (canMix(pos, world) && world.isBlockLoaded(pos)) {
            world.setBlockState(pos, SDFluids.blockSaltWater.getDefaultState());
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.world.isRemote == false) {
            // Process entries with O(n) complexity instead of O(n²)
            int processedCount = 0;
            int maxPerTick = 50; // Limit processing to prevent lag spikes
            
            while (processedCount < maxPerTick) {
                Entry entry = scheduledMixtures.poll();
                if (entry == null) {
                    break; // Queue is empty
                }
                
                // Check if the world is still valid
                if (entry.world == null || entry.world.isRemote) {
                    continue; // Skip invalid entries
                }
                
                entry.ticksExisted++;
                
                if (entry.ticksExisted >= MIX_TIME) {
                    checkAndMixBlock(entry.pos, entry.world);
                    processedCount++;
                } else {
                    // Re-add to queue if not ready yet
                    scheduledMixtures.add(entry);
                }
            }
            
            // Safety cleanup: prevent unbounded growth
            if (scheduledMixtures.size() > 1000) {
                // Remove oldest entries
                int toRemove = Math.min(100, scheduledMixtures.size() - 500);
                for (int i = 0; i < toRemove; i++) {
                    scheduledMixtures.poll();
                }
            }
        }
    }

    public static class Entry {
        public World world;
        public BlockPos pos;
        public int ticksExisted;

        public Entry(World world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
        }
    }
}
