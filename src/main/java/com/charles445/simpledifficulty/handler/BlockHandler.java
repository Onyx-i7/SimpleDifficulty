package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockHandler
{
	//Handler for net.minecraftforge.event.world.BlockEvent related events
	
	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event)
	{
		World world = event.getWorld();
		//Both Sides?
		
		if(!world.isRemote)
		{
			//Server Side
			
			if(event.getHarvester() == null || event.isSilkTouching())
				return;
			
			Block block = event.getState().getBlock();
			
			int fortune = event.getFortuneLevel();
			if(fortune < 0)
				fortune = 0;
			
			if(block == Blocks.ICE && ModConfig.server.miscellaneous.iceDropsChunks)
			{
				event.getDrops().clear(); //TODO Consider not overriding old drops?
				event.getDrops().add(new ItemStack(SDItems.ice_chunk, world.rand.nextInt(fortune+1) + world.rand.nextInt(2)));
			}
			else if(block == Blocks.MAGMA && ModConfig.server.miscellaneous.magmaDropsChunks)
			{
				event.getDrops().clear(); //TODO Consider not overriding old drops?
				event.getDrops().add(new ItemStack(SDItems.magma_chunk, world.rand.nextInt(fortune+1) + world.rand.nextInt(3)));
			}
		}
	}
}
