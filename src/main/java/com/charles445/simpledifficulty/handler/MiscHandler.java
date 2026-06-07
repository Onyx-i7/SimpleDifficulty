package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.block.BlockCampfire;
import com.charles445.simpledifficulty.block.BlockRainCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MiscHandler
{
    @SubscribeEvent
    public void onDismount(EntityMountEvent event)
    {
        if(event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer)
        {
            ((EntityPlayer) event.getEntityMounting()).setJumping(false);
        }
    }

    /**
     * Global World Tick interceptor. Forces instant updates to nearby environmental blocks
     * when functional Weather2 front entities pass over player coordinates.
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        // Execute only on Server thread and at the end of the tick phase to prevent thread racings
        if (event.phase != TickEvent.Phase.END || event.world.isRemote || event.world.playerEntities.isEmpty())
        {
            return;
        }

        World world = event.world;

        // Run check every 20 ticks (1 second) to maintain high performance
        if (world.getTotalWorldTime() % 20 != 0)
        {
            return;
        }

        // Scan around players to update operational campfires and rain collectors instantly
        for (EntityPlayer player : world.playerEntities)
        {
            BlockPos playerPos = player.getPosition();
            int radius = 16; // Tweak radius range if needed based on performance bounds

            for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(
                    playerPos.getX() - radius, playerPos.getY() - 4, playerPos.getZ() - radius,
                    playerPos.getX() + radius, playerPos.getY() + 4, playerPos.getZ() + radius))
            {
                IBlockState state = world.getBlockState(pos);
                
                if (state.getBlock() == SDBlocks.campfire)
                {
                    ((BlockCampfire) state.getBlock()).extinguishCampfire(world, pos.toImmutable(), state);
                }
                else if (state.getBlock() == SDBlocks.rainCollector)
                {
                    ((BlockRainCollector) state.getBlock()).tryFillFromWeather(world, pos.toImmutable(), state);
                }
            }
        }
    }
}