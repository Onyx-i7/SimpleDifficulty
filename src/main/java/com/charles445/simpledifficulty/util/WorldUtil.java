package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WorldUtil {
    public static BlockPos getSidedBlockPos(World world, Entity entity) {
        // Annoying inconsistency between logical sides and EntityPlayer.getPosition()
        // EntityPlayerMP returns: new BlockPos(this.posX, this.posY + 0.5D, this.posZ)
        // EntityPlayerSP returns: new BlockPos(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D)
        // MP offset = 0, +0.5, 0
        // SP offset = +0.5, +0.5, +0.5
        
        if (!world.isRemote) {
            return entity.getPosition();
        }
        
        // Client side
        if (entity instanceof EntityPlayer) {
            // Player - avoid creating intermediate Vec3d
            return new BlockPos(entity.posX, entity.posY + 0.5D, entity.posZ);
        } else if (entity instanceof EntityItemFrame) {
            // Item Frame - avoid creating intermediate Vec3d
            return new BlockPos(entity.posX, entity.posY - 0.45D, entity.posZ);
        } else {
            // Default
            return entity.getPosition();
        }
    }
    
    public static int calculateClientWorldEntityTemperature(World world, Entity entity) {
        return TemperatureUtil.clampTemperature(
            TemperatureUtil.getWorldTemperature(world, getSidedBlockPos(world, entity))
        );
    }
    
    public static boolean isChunkLoaded(World world, BlockPos pos) {
        if (world.isRemote) {
            // WorldClient doesn't care
            return true;
        } else if (world instanceof WorldServer) {
            // WorldServer - Fix for Advanced Rocketry's dummy world
            return ((WorldServer) world).getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
        }
        
        return false;
    }
}
