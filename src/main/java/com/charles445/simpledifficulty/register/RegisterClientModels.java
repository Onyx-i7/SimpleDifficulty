package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.block.IBlockStateIgnore;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SimpleDifficulty.MODID)
public class RegisterClientModels
{
	public static final RegisterClientModels instance = new RegisterClientModels();
	
	@SubscribeEvent
	public static void onModelRegistryEvent(ModelRegistryEvent event)
	{
		//Register new models here
		
		for(String key : SDFluids.fluidBlocks.keySet())
		{
			instance.registerFluidModel(SDFluids.fluidBlocks.get(key));
		}
		
		for(String key : SDItems.items.keySet())
		{
			instance.registerItemModel(SDItems.items.get(key));
		}
		
		for(String key : SDBlocks.blocks.keySet())
		{
			instance.tweakBlockModelState(SDBlocks.blocks.get(key));
			
			instance.registerBlockItemModel(Item.getItemFromBlock(SDBlocks.blocks.get(key)), SDBlocks.blocks.get(key).getTranslationKey());
		}
	}
	
	private void tweakBlockModelState(Block block)
	{
		if(block instanceof IBlockStateIgnore)
		{
			//I hate this, but it works
			//TODO use this for purified water, or some form of StateMap.Builder()
			ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(((IBlockStateIgnore)block).getIgnoredProperties()).build());
		}
	}
	
	private void registerBlockItemModel(Item item, String blockUnloc)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, 
				//new ModelResourceLocation(item.getRegistryName(),"inventory"));
				//new ModelResourceLocation(SimpleDifficulty.MODID + ":" + blockUnloc.substring(5), "inventory"));
				//You know you're in kludge town when you use substring for anything
				new ModelResourceLocation(blockUnloc.substring(5), "inventory"));
	}
	
	private void registerFluidModel(BlockFluidBase block)
	{
		//TODO this is terrible and looks bad
		
		ModelResourceLocation model = new ModelResourceLocation(SimpleDifficulty.MODID+":fluids",block.getFluid().getName());
		Item item = Item.getItemFromBlock(block);
		ModelLoader.setCustomMeshDefinition(item, meshDefinition -> model);
		ModelLoader.setCustomStateMapper(block, 
			new StateMapperBase()
			{
				@Override
				protected ModelResourceLocation getModelResourceLocation(final IBlockState state)
				{
					return model;
				}
			});
		
	}
	
	private void registerItemModel(Item item)
	{
		registerSingleItemModel(item, item.getRegistryName().toString());
		if(item.getHasSubtypes())
			item.addPropertyOverride(new ResourceLocation(SimpleDifficulty.MODID,"type"), new MetadataPropertyGetter());
	}
	
	private void registerSingleItemModel(Item item, String path)
	{
		//SimpleDifficulty.logger.debug("Registering item model: "+path);
		final ModelResourceLocation fullModelLocation = new ModelResourceLocation(path, "inventory");
		ModelBakery.registerItemVariants(item, fullModelLocation);
		ModelLoader.setCustomMeshDefinition(item, stack -> fullModelLocation);
	}
	
	public class MetadataPropertyGetter implements IItemPropertyGetter
	{
		@Override
		public float apply(ItemStack stack, World world, EntityLivingBase entity)
		{
			return stack.getMetadata();
		}
	}
}
