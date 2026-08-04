package com.charles445.simpledifficulty.tileentity;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;
import com.charles445.simpledifficulty.block.BlockTemperature;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

/**
 * TileEntity for temperature-affecting blocks (Heater and Chiller).
 * Calculates temperature influence on nearby players based on distance and environmental conditions.
 */
public class TileEntityTemperature extends TileEntity implements ITemperatureTileEntity {

    public TileEntityTemperature() {
        super(RegisterTileEntities.TEMPERATURE_TILE_ENTITY.get()); // Will need RegisterTileEntities class
    }

    public TileEntityTemperature(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public float getInfluence(BlockPos targetPos, double distance) {
        if (this.level == null) {
            return 0.0f;
        }

        BlockState state = level.getBlockState(worldPosition);
        Block block = state.getBlock();

        if (block instanceof BlockTemperature) {
            boolean enabled = state.getValue(BlockTemperature.ENABLED);
            if (enabled) {
                float activeTemp = ((BlockTemperature) block).getActiveTemperatureMult() * ModConfig.SERVER.heaterTemperature.get().floatValue();
                double fullPowerSq = sq(ModConfig.SERVER.heaterFullPowerRange.get());

                if (distance < fullPowerSq) {
                    return handleStrict(targetPos, activeTemp);
                } else {
                    double distanceDiv = sq(ModConfig.SERVER.heaterMaxRange.get()) - fullPowerSq;

                    if (distanceDiv <= 0d) {
                        return 0.0f;
                    }

                    return handleStrict(targetPos, activeTemp * Math.max(0.0f, 1.0f - (float) ((distance - fullPowerSq) / distanceDiv)));
                }
            } else {
                return 0.0f;
            }
        } else {
            // Failsafe: If the block disappears, destroy the TileEntity to prevent memory leaks
            if (!level.isClientSide) {
                level.removeBlockEntity(worldPosition);
            }
            return 0.0f;
        }
    }

    private float handleStrict(BlockPos targetPos, float distanceTemp) {
        if (!ServerConfig.instance.getBoolean(ServerOptions.STRICT_HEATERS)) {
            return distanceTemp;
        }

        BlockPos thisPos = this.getBlockPos();

        int curX = targetPos.getX();
        int curY = targetPos.getY();
        int curZ = targetPos.getZ();

        int destX = thisPos.getX();
        int destY = thisPos.getY();
        int destZ = thisPos.getZ();

        int xinc = curX < destX ? 1 : -1;
        int yinc = curY < destY ? 1 : -1;
        int zinc = curZ < destZ ? 1 : -1;

        if (isUnprotected(new BlockPos(curX, curY, curZ)) || isUnprotected(new BlockPos(destX, destY, destZ))) {
            return 0.0f;
        }

        // Safety limit for the three-dimensional loop (prevents infinite loops)
        int maxSteps = 128;
        int steps = 0;

        while ((curX != destX || curZ != destZ || curY != destY) && steps < maxSteps) {
            steps++;

            if (curX != destX) curX += xinc;
            if (curY != destY) curY += yinc;
            if (curZ != destZ) curZ += zinc;

            if (isUnprotected(new BlockPos(curX, curY, curZ))) {
                return 0.0f;
            }
        }

        return distanceTemp;
    }

    private boolean isUnprotected(BlockPos pos) {
        if (!WorldUtil.isChunkLoaded(this.level, pos)) {
            return true; // If the chunk is not loaded, assume it is not protected
        }

        // Check if the position can see the sky (is exposed to weather)
        if (!level.canSeeSky(pos)) {
            return false; // Protected (underground or under cover)
        }

        // Check precipitation height
        int precipitationHeight = level.getHeight(Heightmap.Type.MOTION_BLOCKING, pos).getY();
        if (precipitationHeight > pos.getY()) {
            return false; // Protected (something above)
        }

        return true; // Unprotected (exposed to sky)
    }

    private double sq(double d) {
        return d * d;
    }
}