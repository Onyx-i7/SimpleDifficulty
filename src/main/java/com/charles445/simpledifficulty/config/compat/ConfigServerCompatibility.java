package com.charles445.simpledifficulty.config.compat;

import net.minecraftforge.common.config.Config;

public class ConfigServerCompatibility
{
	//Server Compatibility Settings
	
	@Config.Comment("Built-In Compatibility Toggles - Turn compatibility for mods on or off")
	@Config.Name("Built-In Compatibility Toggles")
	public final ConfigToggles toggles = new ConfigToggles();
	
	@Config.Comment("Armor Underwear Configurations")
	@Config.Name("Armor Underwear")
	public final ConfigArmorUnderwear auw = new ConfigArmorUnderwear();
	
	@Config.Comment("Betweenlands Configurations")
	@Config.Name("The Betweenlands")
	public final ConfigBetweenlands betweenlands = new ConfigBetweenlands();
	
	@Config.Comment("Harvest Festival Configurations")
	@Config.Name("Harvest Festival")
	public final ConfigHarvestFestival harvestfestival = new ConfigHarvestFestival();
	
	@Config.Comment("Serene Seasons Configurations")
	@Config.Name("Serene Seasons")
	public final ConfigSereneSeasons sereneseasons = new ConfigSereneSeasons();
	
	public class ConfigToggles
	{
		@Config.Comment("Enable Armor Underwear - Built-In Compatibility")
		@Config.Name("EnableArmorUnderwear")
		public boolean armorUnderwear = true;
		
		@Config.Comment("Enable Baubles - Built-In Compatibility")
		@Config.Name("EnableBaubles")
		public boolean baubles = true;
		
		@Config.Comment("Enable The Betweenlands - Built-In Compatibility")
		@Config.Name("EnableBetweenlands")
		public boolean betweenlands = true;
		
		@Config.Comment("Enable Harvest Festival - Built-In Compatibility")
		@Config.Name("EnableHarvestFestival")
		public boolean harvestFestival = true;
		
		@Config.Comment("Enable Inspirations - Built-In Compatibility")
		@Config.Name("EnableInspirations")
		public boolean inspirations = true;
		
		@Config.Comment("Enable OreExcavation - Built-In Compatibility")
		@Config.Name("EnableOreExcavation")
		public boolean oreExcavation = true;
		
		@Config.Comment("Enable Serene Seasons - Built-In Compatibility")
		@Config.Name("EnableSereneSeasons")
		public boolean sereneSeasons = true;
	}
	
	public class ConfigArmorUnderwear
	{
		//Armor Underwear
		
		@Config.Comment("Goopak Temperature Modifier - Effect of a Goopak on temperature (won't change tooltip)")
		@Config.Name("GoopakTemperatureModifier")
		@Config.RangeDouble(min=0.0)
		public double goopakTemperatureModifier = 2.0d;
		
		@Config.Comment("Goopak Maximum Active - How many Goopaks can stack their effects at once")
		@Config.Name("GoopakMaximumActive")
		@Config.RangeInt(min=1)
		public int goopakMaximumActive = 5;
		
		@Config.Comment("Ozzy Base Range - The base temperature range of an Ozzy Liner")
		@Config.Name("OzzyBaseRange")
		@Config.RangeDouble(min = 0.0)
		public double ozzyBaseRange = 3.0d;
		
		@Config.Comment("Ozzy Extra Range - The added temperature range when upgrading an Ozzy Liner")
		@Config.Name("OzzyExtraRange")
		@Config.RangeDouble(min = 0.0)
		public double ozzyExtraRange = 3.0d;
		
		@Config.Comment("Liner Multiplier - Multiplier for the effect of normal liners")
		@Config.Name("LinerMultiplier")
		@Config.RangeDouble(min = 0.0)
		public double linerMultiplier = 1.0d;
	}
	
	public class ConfigHarvestFestival
	{
		//Harvest Festival
		
		@Config.Comment("Season Winter - Temperature change during the Harvest Festival season")
		@Config.Name("SeasonWinter")
		public int seasonWinter = -10;
		
		@Config.Comment("Season Spring - Temperature change during the Harvest Festival season")
		@Config.Name("SeasonSpring")
		public int seasonSpring = 0;
		
		@Config.Comment("Season Summer - Temperature change during the Harvest Festival season")
		@Config.Name("SeasonSummer")
		public int seasonSummer = 4;
		
		@Config.Comment("Season Autumn - Temperature change during the Harvest Festival season")
		@Config.Name("SeasonAutumn")
		public int seasonAutumn = 0;

	}
	
	public class ConfigSereneSeasons
	{
		//Serene Seasons
		
		@Config.Comment("Season Early Winter - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonEarlyWinter")
		@Config.RangeInt
		public int seasonEarlyWinter = -7;
		
		@Config.Comment("Season Mid Winter - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonMidWinter")
		@Config.RangeInt
		public int seasonMidWinter = -14;
		
		@Config.Comment("Season Late Winter - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonLateWinter")
		@Config.RangeInt
		public int seasonLateWinter = -7;
		
		@Config.Comment("Season Early Spring - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonEarlySpring")
		@Config.RangeInt
		public int seasonEarlySpring = -3;
		
		@Config.Comment("Season Mid Spring - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonMidSpring")
		@Config.RangeInt
		public int seasonMidSpring = 0;
		
		@Config.Comment("Season Late Spring - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonLateSpring")
		@Config.RangeInt
		public int seasonLateSpring = 2;
		
		@Config.Comment("Season Early Summer - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonEarlySummer")
		@Config.RangeInt
		public int seasonEarlySummer = 3;
		
		@Config.Comment("Season Mid Summer - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonMidSummer")
		@Config.RangeInt
		public int seasonMidSummer = 5;
		
		@Config.Comment("Season Late Summer - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonLateSummer")
		@Config.RangeInt
		public int seasonLateSummer = 3;
		
		@Config.Comment("Season Early Autumn - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonEarlyAutumn")
		@Config.RangeInt
		public int seasonEarlyAutumn = 2;
		
		@Config.Comment("Season Mid Autumn - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonMidAutumn")
		@Config.RangeInt
		public int seasonMidAutumn = 0;
		
		@Config.Comment("Season Late Autumn - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonLateAutumn")
		@Config.RangeInt
		public int seasonLateAutumn = -3;
	}
	
	public class ConfigBetweenlands
	{
		//The Betweenlands
		
		@Config.Comment("Dimension Temperature - Temperature modifier for The Betweenlands dimension (negative = cooler)")
		@Config.Name("DimensionTemperature")
		@Config.RangeInt
		public int dimensionTemperature = -3;
		
		@Config.Comment("Enable Clean Water - Allow Clean Water from Betweenlands to be drinkable")
		@Config.Name("EnableCleanWater")
		public boolean enableCleanWater = true;
		
		@Config.Comment("Swamp Water Dirty Chance - Chance that drinking swamp water will make you thirsty (0.0-1.0)")
		@Config.Name("SwampWaterDirtyChance")
		@Config.RangeDouble(min=0.0, max=1.0)
		public double swampWaterDirtyChance = 0.85;
	}
}
