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
			// LOGIC: Default to Salt Water. Only Fresh Water if it's a River or a small enclosed lake.
			if (isFreshWater(player, blockPos)) {
				return new ThirstEnumBlockPos(ThirstEnum.NORMAL, blockPos);
			}
			return new ThirstEnumBlockPos(ThirstEnum.SALT, blockPos);
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
	
	// Determines if water is fresh (drinkable without filter)
	private boolean isFreshWater(EntityPlayer player, BlockPos waterPos) {
		// 1. Check if it's a River biome
		Biome biome = player.getEntityWorld().getBiome(waterPos);
		if (biome != null && biome.getRegistryName() != null) {
			String name = biome.getRegistryName().toString();
			if (name.contains("river")) {
				return true;
			}
		}
		
		// 2. Check if it's a small enclosed lake/pond
		// If land is found within 4 blocks in ALL 4 cardinal directions, it's a small lake.
		// If ANY direction is open water, it's part of a large body (ocean/coast) -> Salt.
		return isEnclosedLake(player.getEntityWorld(), waterPos);
	}
	
	// Checks if the water body is small and enclosed by land
	private boolean isEnclosedLake(net.minecraft.world.World world, BlockPos pos) {
		int maxDist = 4; // Check up to 4 blocks away
		int[] dirsX = {1, -1, 0, 0};
		int[] dirsZ = {0, 0, 1, -1};

		for (int d = 0; d < 4; d++) {
			boolean foundLand = false;
			for (int i = 1; i <= maxDist; i++) {
				BlockPos check = pos.add(dirsX[d] * i, 0, dirsZ[d] * i);
				
				// Prevent chunk loading
				if (!world.isBlockLoaded(check)) {
					foundLand = true;
					break;
				}
				
				Block b = world.getBlockState(check).getBlock();
				// If it's not water, we hit land/edge
				if (b != Blocks.WATER) {
					foundLand = true;
					break;
				}
			}
			// If we didn't find land in this direction, it's open water (Ocean/Coast)
			if (!foundLand) {
				return false;
			}
		}
		// All 4 directions hit land -> Small enclosed lake/pond
		return true;
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
