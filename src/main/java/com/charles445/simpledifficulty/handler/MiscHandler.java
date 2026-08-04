package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.block.BlockCampfire;
import com.charles445.simpledifficulty.block.BlockRainCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handler for miscellaneous game events.
 */
public class MiscHandler {
    private static final int SCAN_RADIUS_XZ = 8;
    private static final int SCAN_RADIUS_Y = 2;
    private static final int MAX_PLAYERS_PER_TICK = 5;

    /**
     * Prevents automatic jumping when dismounting entities.
     *
     * @param event The living jump event.
     */
    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        // In 1.16.5, can't easily prevent jumping on dismount
        // This would require mixin or different approach
        // For now, leaving as stub
    }

    /**
     * Global World Tick interceptor. Forces instant updates to nearby environmental blocks
     * when functional Weather2 front entities pass over player coordinates.
     *
     * @param event The world tick event.
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        // Execute only on Server thread and at the end of the tick phase
        if (event.phase != TickEvent.Phase.END || event.world.isClientSide || event.world.players().isEmpty()) {
            return;
        }

        World world = event.world;

        // Run check every 20 ticks (1 second) to maintain high performance
        if (world.getGameTime() % 20 != 0) {
            return;
        }

        int playersProcessed = 0;

        // Scan around players to update operational campfires and rain collectors instantly
        for (PlayerEntity player : world.players()) {
            if (playersProcessed >= MAX_PLAYERS_PER_TICK) {
                break;
            }

            BlockPos playerPos = player.blockPosition();
            int minX = playerPos.getX() - SCAN_RADIUS_XZ;
            int minY = playerPos.getY() - SCAN_RADIUS_Y;
            int minZ = playerPos.getZ() - SCAN_RADIUS_XZ;
            int maxX = playerPos.getX() + SCAN_RADIUS_XZ;
            int maxY = playerPos.getY() + SCAN_RADIUS_Y;
            int maxZ = playerPos.getZ() + SCAN_RADIUS_XZ;

            BlockPos.Mutable mutablePos = new BlockPos.Mutable();

            Block campfireBlock = SDBlocks.campfire.get();
            Block rainCollectorBlock = SDBlocks.rainCollector.get();

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        mutablePos.set(x, y, z);
                        BlockState state = world.getBlockState(mutablePos);
                        Block block = state.getBlock();

                        if (block == campfireBlock) {
                            ((BlockCampfire) block).extinguishCampfire(world, mutablePos, state);
                        } else if (block == rainCollectorBlock) {
                            ((BlockRainCollector) block).tryFillFromWeather(world, mutablePos, state, world.random);
                        }
                    }
                }
            }

            playersProcessed++;
        }
    }
}