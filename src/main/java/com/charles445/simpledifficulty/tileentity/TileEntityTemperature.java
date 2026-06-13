package com.charles445.simpledifficulty.tileentity;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;
import com.charles445.simpledifficulty.block.BlockTemperature;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class TileEntityTemperature extends TileEntity implements ITemperatureTileEntity {

    @Override
    public float getInfluence(BlockPos targetPos, double distance) {
        if (this.world == null) {
            return 0.0f;
        }

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        
        if (block instanceof BlockTemperature) {
            // Optimization: Use the 'state' variable that we already consulted above
            boolean enabled = state.getValue(BlockTemperature.ENABLED);
            if (enabled) {
                float activeTemp = ((BlockTemperature)block).getActiveTemperatureMult() * ModConfig.server.temperature.heaterTemperature;
                double fullPowerSq = sq(ModConfig.server.temperature.heaterFullPowerRange);
                
                if (distance < fullPowerSq) {
                    return handleStrict(targetPos, activeTemp);
                } else {
                    double distanceDiv = sq(ModConfig.server.temperature.heaterMaxRange) - fullPowerSq;
                    
                    if (distanceDiv <= 0d) {
                        return 0.0f;
                    }
                    
                    return handleStrict(targetPos, activeTemp * Math.max(0.0f, 1.0f - (float)((distance - fullPowerSq) / distanceDiv)));
                }
            } else {
                return 0.0f;
            }
        } else {
            // Failsafe implemented: If the block disappears, we destroy the TileEntity to prevent memory leaks.
            if (!world.isRemote) {
                world.removeTileEntity(pos);
            }
            return 0.0f;
        }
    }
    
    private float handleStrict(BlockPos targetPos, float distanceTemp) {
        if (!ServerConfig.instance.getBoolean(ServerOptions.STRICT_HEATERS)) {
            return distanceTemp;
        }
        
        BlockPos thisPos = this.getPos();
        
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
        
        // Safety limit for the three-dimensional loop (Prevents infinite loops due to coordinate corruption)
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
        if (!WorldUtil.isChunkLoaded(this.world, pos)) {
            return true; // If the chunk is not loaded, we assume it is not protected to prevent pulls
        }
        
        Chunk chunk = this.world.getChunk(pos);
        
        if (!chunk.canSeeSky(pos)) {
            return false;
        }
        
        if (chunk.getPrecipitationHeight(pos).getY() > pos.getY()) {
            return false;
        }
        
        return true;
    }
    
    private double sq(double d) {
        return d * d;
    }
}
