package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Compatibility handler for The Betweenlands mod
 * - Adds Clean Water fluid (similar to Purified Water)
 * - Registers Betweenlands water blocks for drinking
 * - Adds temperature modifiers for Betweenlands dimensions
 */
public class BetweenlandsHandler
{
	private static boolean enabled = false;
	
	// Betweenlands fluids
	private static Fluid betweenlandsCleanWater = null;
	
	// Cached set of Betweenlands water block registry names for O(1) lookup
	private static final Set<String> BETWEENLANDS_WATER_BLOCKS = new HashSet<>();
	
	static
	{
		BETWEENLANDS_WATER_BLOCKS.add("thebetweenlands:tar");
		BETWEENLANDS_WATER_BLOCKS.add("thebetweenlands:sludge");
		BETWEENLANDS_WATER_BLOCKS.add("thebetweenlands:swamp_water");
	}
	
	public BetweenlandsHandler()
	{
		try
		{
			// Register Clean Water fluid (similar to Purified Water)
			// This is a safe, drinkable water type for Betweenlands
			betweenlandsCleanWater = new Fluid(
				"clean_water",
				new ResourceLocation("thebetweenlands:blocks/fluid/clean_water_still"),
				new ResourceLocation("thebetweenlands:blocks/fluid/clean_water_flow")
			);
			betweenlandsCleanWater.setDensity(1000);
			betweenlandsCleanWater.setViscosity(1000);
			betweenlandsCleanWater.setTemperature(295);
			
			if(FluidRegistry.registerFluid(betweenlandsCleanWater))
			{
				SimpleDifficulty.logger.info("Registered Clean Water fluid for Betweenlands compatibility");
			}
			
			// Add OreDictionary entries for Betweenlands water items if they exist
			registerOreDictEntries();
			
			enabled = true;
			SimpleDifficulty.logger.info("The Betweenlands compatibility handler initialized");
		}
		catch (Exception e)
		{
			SimpleDifficulty.logger.error("Failed to initialize Betweenlands compatibility handler", e);
			enabled = false;
		}
	}
	
	/**
	 * Register OreDictionary entries for Betweenlands water-related items
	 * This allows them to work with SimpleDifficulty's thirst system
	 */
	private void registerOreDictEntries()
	{
		try
		{
			// Try to get Betweenlands items via reflection
			Class<?> modItems = Class.forName("net.thebetweenlands.api.item.IModItem");
			
			// Register clean water bottle if it exists
			registerItemOreDict("cleanWaterBottle", "itemCleanWaterBottle");
			
			// Note: Swamp water and sludge are intentionally NOT registered as drinkable
			// They should make the player thirsty or give negative effects
		}
		catch (ClassNotFoundException e)
		{
			// Betweenlands API not available, skip ore dict registration
		}
		catch (Exception e)
		{
			SimpleDifficulty.logger.debug("Could not register Betweenlands OreDict entries: " + e.getMessage());
		}
	}
	
	/**
	 * Helper to register an item with OreDictionary
	 */
	private void registerItemOreDict(String itemName, String oreDictName)
	{
		try
		{
			Class<?> modItems = Class.forName("net.thebetweenlands.common.registries.ModItems");
			Field field = modItems.getDeclaredField(itemName);
			Object itemObj = field.get(null);
			
			if(itemObj instanceof Item)
			{
				Item item = (Item)itemObj;
				OreDictionary.registerOre(oreDictName, new ItemStack(item));
				SimpleDifficulty.logger.debug("Registered " + itemName + " with OreDictionary as " + oreDictName);
			}
		}
		catch (Exception e)
		{
			// Item doesn't exist or other error, ignore silently
		}
	}
	
	/**
	 * Check if a block registry name is a Betweenlands water block
	 * Returns the appropriate ThirstEnum or null if not drinkable
	 */
	public static ThirstEnum getBetweenlandsWaterType(String blockRegistryName)
	{
		if(blockRegistryName == null)
			return null;
			
		switch(blockRegistryName)
		{
			case "thebetweenlands:swamp_water":
				// Swamp water is dirty - makes player thirsty
				return ThirstEnum.NORMAL; // Will apply dirty chance in ThirstUtilInternal
			case "thebetweenlands:tar":
			case "thebetweenlands:sludge":
				// Tar and sludge are not drinkable at all
				return null;
			default:
				return null;
		}
	}
	
	/**
	 * Get temperature modifier for Betweenlands dimension
	 * The Betweenlands is typically cooler due to its swampy environment
	 */
	public static float getDimensionTemperatureModifier()
	{
		if(ModConfig.server.compatibility.toggles.betweenlands)
		{
			return (float)ModConfig.server.compatibility.betweenlands.dimensionTemperature;
		}
		return 0.0f;
	}
	
	/**
	 * Check if the handler is enabled
	 */
	public static boolean isEnabled()
	{
		return enabled;
	}
}
