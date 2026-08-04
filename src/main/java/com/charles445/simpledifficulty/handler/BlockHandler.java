package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handler for block-related events such as harvest drops.
 */
public class BlockHandler {

    /**
     * Intercepts block harvest events to add custom drops for ice and magma blocks.
     *
     * @param event The harvest drops event.
     */
    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        World world = event.getWorld();

        if (!world.isClientSide) {
            // Server Side
            if (event.getHarvester() == null || event.isSilkTouching()) {
                return;
            }

            Block block = event.getState().getBlock();
            int fortune = event.getFortuneLevel();
            if (fortune < 0) {
                fortune = 0;
            }

            if (block == Blocks.ICE && ModConfig.SERVER.iceDropsChunks.get()) {
                event.getDrops().clear();
                int amount = world.random.nextInt(fortune + 1) + world.random.nextInt(2);
                event.getDrops().add(new ItemStack(SDItems.ice_chunk.get(), amount));
            } else if (block == Blocks.MAGMA_BLOCK && ModConfig.SERVER.magmaDropsChunks.get()) {
                event.getDrops().clear();
                int amount = world.random.nextInt(fortune + 1) + world.random.nextInt(3);
                event.getDrops().add(new ItemStack(SDItems.magma_chunk.get(), amount));
            }
        }
    }
}