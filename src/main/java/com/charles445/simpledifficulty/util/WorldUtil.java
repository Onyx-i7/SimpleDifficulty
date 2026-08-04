package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Utility class for world-related operations.
 * Provides helper methods for position calculations, temperature lookups, and chunk checks.
 */
public class WorldUtil {

    /**
     * Gets the appropriate BlockPos for an entity, accounting for side differences.
     * Server returns entity position, client applies offset corrections for players and item frames.
     *
     * @param world The world context.
     * @param entity The entity to get position for.
     * @return The corrected BlockPos.
     */
    public static BlockPos getSidedBlockPos(World world, Entity entity) {
        if (!world.isClientSide) {
            return entity.blockPosition();
        }

        // Client side
        if (entity instanceof PlayerEntity) {
            // Player - apply Y offset for accurate temperature calculation
            return new BlockPos(entity.position().x, entity.position().y + 0.5D, entity.position().z);
        } else if (entity instanceof ItemFrameEntity) {
            // Item Frame - apply negative Y offset
            return new BlockPos(entity.position().x, entity.position().y - 0.45D, entity.position().z);
        } else {
            // Default
            return entity.blockPosition();
        }
    }

    /**
     * Calculates the clamped world temperature at an entity's position.
     *
     * @param world The world context.
     * @param entity The entity to calculate temperature for.
     * @return The clamped temperature value.
     */
    public static int calculateClientWorldEntityTemperature(World world, Entity entity) {
        return TemperatureUtil.clampTemperature(
                TemperatureUtil.getWorldTemperature(world, getSidedBlockPos(world, entity))
        );
    }

    /**
     * Checks if a chunk is loaded at the specified position.
     *
     * @param world The world context.
     * @param pos The position to check.
     * @return true if the chunk is loaded, false otherwise.
     */
    public static boolean isChunkLoaded(World world, BlockPos pos) {
        if (world.isClientSide) {
            // Client world always considers chunks loaded
            return true;
        } else if (world instanceof ServerWorld) {
            // Server world - check if chunk exists
            return ((ServerWorld) world).getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
        }

        return false;
    }
}