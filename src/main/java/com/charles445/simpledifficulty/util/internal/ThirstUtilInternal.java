package com.charles445.simpledifficulty.util.internal;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.thirst.*;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ThirstUtilInternal implements IThirstUtil
{
	// Cached HashSet for river blocks lookup - O(1) instead of O(n) array iteration
	private static final Set<String> RIVER_BLOCKS_SET = new HashSet<>();
	static
	{
		RIVER_BLOCKS_SET.add("river/tile.water/-1/-2");
		RIVER_BLOCKS_SET.add("river/tile.water/-1/2");
		RIVER_BLOCKS_SET.add("river/tile.water/-2/-1");
		RIVER_BLOCKS_SET.add("river/tile.water/-2/-2");
		RIVER_BLOCKS_SET.add("river/tile.water/-2/0");
		RIVER_BLOCKS_SET.add("river/tile.water/-2/1");
		RIVER_BLOCKS_SET.add("river/tile.water/-2/2");
		RIVER_BLOCKS_SET.add("river/tile.water/0/-2");
		RIVER_BLOCKS_SET.add("river/tile.water/0/0");
		RIVER_BLOCKS_SET.add("river/tile.water/0/2");
		RIVER_BLOCKS_SET.add("river/tile.water/1/-2");
		RIVER_BLOCKS_SET.add("river/tile.water/1/2");
		RIVER_BLOCKS_SET.add("river/tile.water/2/-1");
		RIVER_BLOCKS_SET.add("river/tile.water/2/-2");
		RIVER_BLOCKS_SET.add("river/tile.water/2/0");
		RIVER_BLOCKS_SET.add("river/tile.water/2/1");
		RIVER_BLOCKS_SET.add("river/tile.water/2/2");
	}
	
	//Returns an object based on what is being drunk
	//Not API visible
	@Nullable
	public static ThirstEnumBlockPos traceWaterToDrink(EntityPlayer player)
	{
		if(player.getHeldItemMainhand().isEmpty())
		{
			IThirstCapability capability = SDCapabilities.getThirstData(player);
			if(capability.isThirsty())
			{
				//Empty-handed and thirsty
				ThirstEnumBlockPos traceResult = ThirstUtil.traceWater(player);
				if(traceResult==null)
					return null;
				
				if(traceResult.thirstEnum == ThirstEnum.PURIFIED)
				{
					if(!ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS))
						return null;

					if(!ServerConfig.instance.getBoolean(ServerOptions.INFINITE_PURIFIED_WATER))
						player.world.setBlockToAir(traceResult.pos);
				}
				else if(traceResult.thirstEnum == ThirstEnum.RAIN && !ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN))
				{
					return null;
				}
				else if(traceResult.thirstEnum == ThirstEnum.NORMAL)
				{
					if(!ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS))
						return null;

					player.world.setBlockToAir(traceResult.pos);
				}
				else if(traceResult.thirstEnum == ThirstEnum.SALT && !ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS))
				{
					return null;
				}
				
				return traceResult;
			}
		}
		
		return null;
	}
	
	//API
	
	//Returns an object based on what is being looked at
	@Nullable
	@Override
	public ThirstEnumBlockPos traceWater(EntityPlayer player)
	{	
		//Check if player is looking up, if it's raining, if they can see sky, and if THIRST_DRINK_RAIN is enabled
		//This essentially means rain can't be a trace result for drinking or for a canteen
		
		if(player.rotationPitch < -75.0f && player.world.isRainingAt(player.getPosition()) && player.world.canSeeSky(player.getPosition()) && ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN))
		{
			//Drinking rain
			return new ThirstEnumBlockPos(ThirstEnum.RAIN, player.getPosition());
		}
		
		//Handle ray tracing
		
		//Get the player's reach distance attribute and cut it in half
		double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() * 0.5d;
		
		//Similar to Entity.rayTrace
		Vec3d eyevec = player.getPositionEyes(1.0f);
		Vec3d lookvec = player.getLook(1.0f);
		Vec3d targetvec = eyevec.add(lookvec.x * reach, lookvec.y * reach, lookvec.z * reach);
		
		//Ray trace from the player's eyepos to where they are looking, and stop at liquids
		RayTraceResult trace = player.getEntityWorld().rayTraceBlocks(eyevec, targetvec, true);
		
		if(trace==null || trace.typeOfHit != RayTraceResult.Type.BLOCK)
			return null;
		
		//Hit a block
		BlockPos blockPos = trace.getBlockPos();
		Block traceBlock = player.getEntityWorld().getBlockState(blockPos).getBlock();
		
		if(traceBlock == Blocks.WATER)
		{
			// Check if the water is in an ocean biome AND has enough water around it to be considered ocean
			// This prevents coastal water from being treated as salt water
			if (isTrueOceanWater(player, blockPos)) {
				return new ThirstEnumBlockPos(ThirstEnum.SALT, blockPos);
			}
			return new ThirstEnumBlockPos(ThirstEnum.NORMAL, blockPos);
		}
		else if(traceBlock == SDFluids.blockPurifiedWater)
		{
			return new ThirstEnumBlockPos(ThirstEnum.PURIFIED, blockPos);
		}
		else if(traceBlock == SDFluids.blockSaltWater)
		{
			return new ThirstEnumBlockPos(ThirstEnum.SALT, blockPos);
		}
		// Optimized lookup using HashSet instead of array iteration
		String blockRegistryName = traceBlock.getRegistryName().toString();
		if(RIVER_BLOCKS_SET.contains(blockRegistryName))
		{
			return new ThirstEnumBlockPos(ThirstEnum.NORMAL, blockPos);
		}
		
		return null;
	}
	
	// Helper method to check if water is truly ocean water (not coastal/lake water)
	private boolean isTrueOceanWater(EntityPlayer player, BlockPos waterPos) {
		// First check if it's in an ocean biome
		Biome biome = player.getEntityWorld().getBiome(waterPos);
		if (!isOceanBiome(biome)) {
			return false;
		}
		
		// Now check if there's enough water around to be considered ocean
		// If there's land nearby, it's likely coastal water or a lake
		int waterCount = 0;
		int radius = 5; // Check 5 blocks in each direction
		
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				BlockPos checkPos = waterPos.add(x, 0, z);
				IBlockState state = player.getEntityWorld().getBlockState(checkPos);
				
				// Count water blocks at the same level
				if (state.getBlock() == Blocks.WATER || state.getBlock() == SDFluids.blockSaltWater) {
					waterCount++;
				}
			}
		}
		
		// If there are many water blocks around, it's likely ocean
		// A 5-block radius gives us 11x11 = 121 possible positions
		// If more than 70% is water, it's ocean
		int totalPositions = (radius * 2 + 1) * (radius * 2 + 1);
		double waterPercentage = (double) waterCount / totalPositions;
		
		return waterPercentage > 0.7;
	}
	
	// Helper method to check if a biome is an ocean biome
	private boolean isOceanBiome(Biome biome) {
		if (biome == null) {
			return false;
		}
		
		// Check biome category/name for ocean types
		String biomeName = biome.getRegistryName() != null ? biome.getRegistryName().toString() : "";
		
		// Vanilla ocean biomes
		return biomeName.contains("ocean") || 
		       biomeName.equals("minecraft:frozen_ocean") ||
		       biomeName.equals("minecraft:deep_ocean") ||
		       biomeName.equals("minecraft:frozen_ocean") ||
		       biomeName.equals("minecraft:cold_ocean") ||
		       biomeName.equals("minecraft:deep_cold_ocean") ||
		       biomeName.equals("minecraft:lukewarm_ocean") ||
		       biomeName.equals("minecraft:deep_lukewarm_ocean") ||
		       biomeName.equals("minecraft:warm_ocean") ||
		       biomeName.equals("minecraft:deep_warm_ocean");
	}
	
	// Removed riverBlocks array - now using RIVER_BLOCKS_SET for O(1) lookups


	@Override
	public void takeDrink(EntityPlayer player, int thirst, float saturation, float dirtyChance)
	{
		if(!QuickConfig.isThirstEnabled())
			return;
		
		IThirstCapability capability = SDCapabilities.getThirstData(player);
		
		if(capability.isThirsty())
		{
			capability.addThirstLevel(thirst);
			capability.addThirstSaturation(saturation);
			
			//Test for dirtiness >> water (0.75f = dirty water chance)
			if(dirtyChance == 0.75f && player.world.rand.nextFloat() < dirtyChance)
			{
				//Test for parasites
				if(ModConfig.server.thirst.thirstParasites && player.world.rand.nextDouble() < ModConfig.server.thirst.thirstParasitesChance)
				{
					player.addPotionEffect(new PotionEffect(SDPotions.parasites, ModConfig.server.thirst.thirstParasitesDuration));
				}
			}

			//Test for dirtiness >> salt water (1.0f = salt water)
			if(dirtyChance == 1.0f)
			{
				player.addPotionEffect(new PotionEffect(SDPotions.thirsty, 600));
			}
		}
		else
		{
			//Player isn't thirsty, so check if the saturation of the drink itself is more, and set to that
			if(capability.getThirstSaturation() < saturation)
				capability.setThirstSaturation(saturation);
		}
	}

	@Override
	public void takeDrink(EntityPlayer player, int thirst, float saturation)
	{
		//Clean water
		takeDrink(player, thirst, saturation, 0.0f);
	}
	
	@Override
	public void takeDrink(EntityPlayer player, ThirstEnum type)
	{
		takeDrink(player, type.getThirst(), type.getSaturation(), type.getThirstyChance());
	}
	
	@Override
	public ItemStack createPurifiedWaterBucket()
	{
		return FluidUtil.getFilledBucket(new FluidStack(SDFluids.purifiedWater, Fluid.BUCKET_VOLUME));
	}

	@Override
	public ItemStack createSaltWaterBucket()
	{
		return FluidUtil.getFilledBucket(new FluidStack(SDFluids.saltWater, Fluid.BUCKET_VOLUME));
	}

	@Override
	public ItemStack createNormalWaterBucket()
	{
		return FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
	}
}
