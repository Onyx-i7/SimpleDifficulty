package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handler for fluid mixing mechanics (purified water + salt water = salt water).
 */
public class FluidHandler {
    public static final int MIX_TIME = 1;

    // Thread-safe queue for scheduled fluid mixtures
    public static final ConcurrentLinkedQueue<Entry> scheduledMixtures = new ConcurrentLinkedQueue<>();

    /**
     * Schedules a fluid mixture at the specified position.
     *
     * @param world The world.
     * @param pos The position to mix.
     */
    public static void scheduleMixing(World world, BlockPos pos) {
        if (world != null && pos != null) {
            scheduledMixtures.add(new Entry(world, pos));
        }
    }

    /**
     * Checks if a biome is valid for salt water generation (ocean, beach, river).
     *
     * @param biome The biome to check.
     * @return true if valid.
     */
    public static boolean isBiomeValid(Biome biome) {
        if (biome == null) return false;
        
        // In 1.16.5, use biome category and tags
        Biome.Category category = biome.getBiomeCategory();
        return category == Biome.Category.OCEAN
                || category == Biome.Category.BEACH
                || category == Biome.Category.RIVER;
    }

    /**
     * Checks if fluid can mix at the specified position.
     *
     * @param pos The position to check.
     * @param world The world.
     * @return true if can mix.
     */
    public static boolean canMix(BlockPos pos, World world) {
        if (!world.isLoaded(pos.north(32))
                || !world.isLoaded(pos.east(32))
                || !world.isLoaded(pos.south(32))
                || !world.isLoaded(pos.west(32))) {
            return false;
        }

        Block downBlock = world.getBlockState(pos.below()).getBlock();
        Block upBlock = world.getBlockState(pos.above()).getBlock();
        Block northBlock = world.getBlockState(pos.north()).getBlock();
        Block eastBlock = world.getBlockState(pos.east()).getBlock();
        Block southBlock = world.getBlockState(pos.south()).getBlock();
        Block westBlock = world.getBlockState(pos.west()).getBlock();

        int xChunkPos = world.getChunk(pos).getPos().x;
        int zChunkPos = world.getChunk(pos).getPos().z;
        BlockPos chunkPos = new BlockPos(xChunkPos * 16 + 8, 0, zChunkPos * 16 + 8);

        Biome biomeInNorthChunk = world.getBiome(chunkPos.north(32));
        Biome biomeInEastChunk = world.getBiome(chunkPos.east(32));
        Biome biomeInSouthChunk = world.getBiome(chunkPos.south(32));
        Biome biomeInWestChunk = world.getBiome(chunkPos.west(32));

        Block saltWaterBlock = SDFluids.blockSaltWater.get();

        return (isBiomeValid(biomeInNorthChunk)
                || isBiomeValid(biomeInEastChunk)
                || isBiomeValid(biomeInSouthChunk)
                || isBiomeValid(biomeInWestChunk))
                && (downBlock == saltWaterBlock
                || upBlock == saltWaterBlock
                || northBlock == saltWaterBlock
                || eastBlock == saltWaterBlock
                || southBlock == saltWaterBlock
                || westBlock == saltWaterBlock);
    }

    /**
     * Checks and performs fluid mixing at the specified position.
     *
     * @param pos The position.
     * @param world The world.
     */
    public void checkAndMixBlock(BlockPos pos, World world) {
        if (canMix(pos, world) && world.isLoaded(pos)) {
            BlockState saltWaterState = SDFluids.blockSaltWater.get().defaultBlockState();
            world.setBlock(pos, saltWaterState, 3);
        }
    }

    /**
     * Processes scheduled fluid mixtures each world tick.
     *
     * @param event The world tick event.
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.world.isClientSide) {
            int processedCount = 0;
            int maxPerTick = 50; // Limit processing to prevent lag spikes

            while (processedCount < maxPerTick) {
                Entry entry = scheduledMixtures.poll();
                if (entry == null) {
                    break; // Queue is empty
                }

                // Check if the world is still valid
                if (entry.world == null || entry.world.isClientSide) {
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
                int toRemove = Math.min(100, scheduledMixtures.size() - 500);
                for (int i = 0; i < toRemove; i++) {
                    scheduledMixtures.poll();
                }
            }
        }
    }

    /**
     * Entry class representing a scheduled fluid mixture.
     */
    public static class Entry {
        public World world;
        public BlockPos pos;
        public int ticksExisted;

        public Entry(World world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
            this.ticksExisted = 0;
        }
    }
}