package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockHandler {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        World world = (World) event.getWorld();

        if (!world.isClientSide) {
            if (event.getPlayer() == null) {
                return;
            }

            Block block = event.getState().getBlock();
            
            if (block == Blocks.ICE && ModConfig.SERVER.iceDropsChunks.get()) {
            } else if (block == Blocks.MAGMA_BLOCK && ModConfig.SERVER.magmaDropsChunks.get()) {
            }
        }
    }
}