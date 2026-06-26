package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.block.BlockCampfire;
import com.charles445.simpledifficulty.block.BlockRainCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MiscHandler {
    private static final int SCAN_RADIUS_XZ = 8;
    private static final int SCAN_RADIUS_Y = 2;
    private static final int MAX_PLAYERS_PER_TICK = 5;

    @SubscribeEvent
    public void onDismount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer) {
            ((EntityPlayer) event.getEntityMounting()).setJumping(false);
        }
    }

    /**
     * Global World Tick interceptor. Forces instant updates to nearby environmental blocks
     * when functional Weather2 front entities pass over player coordinates.
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        // Execute only on Server thread and at the end of the tick phase to prevent thread racings
        if (event.phase != TickEvent.Phase.END || event.world.isRemote || event.world.playerEntities.isEmpty()) {
            return;
        }

        World world = event.world;

        // Run check every 20 ticks (1 second) to maintain high performance
        if (world.getTotalWorldTime() % 20 != 0) {
            return;
        }

        // Limit players processed per tick to prevent lag spikes
        int playersProcessed = 0;

        // Scan around players to update operational campfires and rain collectors instantly
        for (EntityPlayer player : world.playerEntities) {
            if (playersProcessed >= MAX_PLAYERS_PER_TICK) {
                break;
            }

            BlockPos playerPos = player.getPosition();
            int minX = playerPos.getX() - SCAN_RADIUS_XZ;
            int minY = playerPos.getY() - SCAN_RADIUS_Y;
            int minZ = playerPos.getZ() - SCAN_RADIUS_XZ;
            int maxX = playerPos.getX() + SCAN_RADIUS_XZ;
            int maxY = playerPos.getY() + SCAN_RADIUS_Y;
            int maxZ = playerPos.getZ() + SCAN_RADIUS_XZ;

            // Use MutableBlockPos to avoid object allocation
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        mutablePos.setPos(x, y, z);
                        IBlockState state = world.getBlockState(mutablePos);
                        Block block = state.getBlock();

                        if (block == SDBlocks.campfire) {
                            ((BlockCampfire) block).extinguishCampfire(world, mutablePos, state);
                        } else if (block == SDBlocks.rainCollector) {
                            ((BlockRainCollector) block).tryFillFromWeather(world, mutablePos, state);
                        }
                    }
                }
            }

            playersProcessed++;
        }
    }
}
