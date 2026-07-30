package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.config.compat.ConfigServerCompatibility;
import com.charles445.simpledifficulty.config.ConfigSyncHelper;
import com.charles445.simpledifficulty.network.MessageConfigLAN;
import com.charles445.simpledifficulty.network.MessageUpdateConfig;
import com.charles445.simpledifficulty.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid = SimpleDifficulty.MODID)
public class ModConfig 
{
	@Config.Comment("Client-side configuration (visual and interface settings)")
	@Config.Name("Client")
	public static final ConfigClientConfig client = new ConfigClientConfig();
	
	@Config.Comment("Server-side configuration (gameplay and mechanics)")
	@Config.Name("Server")
	public static final ConfigServerConfig server = new ConfigServerConfig();
	
	public static class ConfigServerConfig
	{
		@Config.Comment("Compatibility settings for other mods")
		@Config.Name("Compatibility")
		public final ConfigServerCompatibility compatibility = new ConfigServerCompatibility();
		
		@Config.Comment("General gameplay settings (campfires, drops, potions, etc.)")
		@Config.Name("General")
		public final ConfigGeneral general = new ConfigGeneral();
		
		@Config.Comment("Temperature system settings")
		@Config.Name("Temperature")
		public final ConfigTemperature temperature = new ConfigTemperature();
		
		@Config.Comment("Thirst and hydration system settings")
		@Config.Name("Thirst")
		public final ConfigThirst thirst = new ConfigThirst();
		
		///
		/// Core Server Options
		///
		@Config.Comment("Enable or disable the entire thirst system")
		@Config.Name("Enable Thirst System")
		public boolean thirstEnabled = true;
		
		@Config.Comment("Allow players to drink directly from water blocks")
		@Config.Name("Allow Drinking From Blocks")
		public boolean thirstDrinkBlocks = true;
		
		@Config.Comment("Allow players to drink rainwater by looking up during rain")
		@Config.Name("Allow Drinking Rain")
		public boolean thirstDrinkRain = true;
		
		@Config.Comment("Make the mod dangerous even on Peaceful difficulty (parasites, temperature damage)")
		@Config.Name("Enable Danger on Peaceful")
		public boolean peacefulDanger = false;
		
		@Config.Comment("Enable or disable the entire temperature system")
		@Config.Name("Enable Temperature System")
		public boolean temperatureEnabled = true;
		
		@Config.Comment("Enable temperature-affecting tile entities (heaters, chillers)")
		@Config.Name("Enable Temperature Tile Entities")
		public boolean temperatureTEEnabled = true;
		
		@Config.Comment("Maximum doses for the basic leather canteen")
		@Config.Name("Canteen Capacity")
		@Config.RangeInt(min=1, max=100)
		public int canteenDoses = 3;
		
		@Config.Comment("Maximum doses for the iron canteen")
		@Config.Name("Iron Canteen Capacity")
		@Config.RangeInt(min=1, max=100)
		public int ironCanteenDoses = 8;
		
		@Config.Comment("Maximum doses for the dragon canteen (end-game item)")
		@Config.Name("Dragon Canteen Capacity")
		@Config.RangeInt(min=1, max=100)
		public int dragonCanteenDoses = 30;
		
		@Config.Comment("Require heaters and chillers to be placed indoors to function")
		@Config.Name("Strict Indoor Heaters")
		public boolean strictHeaters = true;
		
		@Config.Comment("Purified water blocks are infinite (do not deplete when collected)")
		@Config.Name("Infinite Purified Water")
		public boolean infinitePurifiedWater = false;

		@Config.Comment("Reduce light opacity of purified water blocks (1 instead of 3) for better visibility")
		@Config.Name("Brighter Purified Water")
		public boolean purifiedWaterOpacity = false;

		@Config.Comment("Enable debug logging (spam console with detailed messages)")
		@Config.Name("Debug Mode")
		public boolean debug = false;
		
		public class ConfigGeneral
		{
			@Config.Comment("Campfire fuel decay chance (1 in X ticks, default: 2 = 50% chance)")
			@Config.Name("Campfire Fuel Decay Chance")
			@Config.RangeInt(min=1, max=100)
			public int campfireDecayChance = 2;
			
			@Config.Comment("Chance to ignite campfire with a stick (1 in X attempts, default: 5 = 20% chance)")
			@Config.Name("Campfire Stick Ignition Chance")
			@Config.RangeInt(min=1, max=100)
			public int campfireStickIgniteChance = 5;
			
			@Config.Comment("Time in seconds to cook food on a campfire spit")
			@Config.Name("Campfire Spit Cooking Time")
			@Config.RangeInt(min=1, max=120)
			public int campfireSpitDelay = 35;
			
			@Config.Comment("Maximum food items that can fit on a campfire spit")
			@Config.Name("Campfire Spit Capacity")
			@Config.RangeInt(min=1, max=10)
			public int campfireSpitSize = 3;
			
			@Config.Comment("Grant experience points when cooking food on a campfire spit (like a furnace)")
			@Config.Name("Campfire Spit Experience")
			public boolean campfireSpitExperience = true;
			
			@Config.Comment("Items that cannot be cooked on a campfire spit (e.g., minecraft:beef)")
			@Config.Name("Campfire Spit Blacklist")
			public String[] campfireSpitBlacklist = new String[0];
			
			@Config.Comment("Treat the blacklist as a whitelist instead (only listed items can be cooked)")
			@Config.Name("Blacklist is Whitelist")
			public boolean campfireSpitBlacklistIsWhitelist = false;
			
			@Config.Comment("Golden Apple Juice grants the Golden Apple effect when consumed")
			@Config.Name("Golden Apple Juice Effect")
			public boolean goldenAppleJuiceEffect = true;
			
			@Config.Comment("Ice blocks drop Ice Chunks when broken")
			@Config.Name("Ice Blocks Drop Chunks")
			public boolean iceDropsChunks = true;
			
			@Config.Comment("Magma blocks drop Magma Chunks when broken")
			@Config.Name("Magma Blocks Drop Chunks")
			public boolean magmaDropsChunks = true;
			
			@Config.Comment("Rain collector fill chance (1 in X ticks, default: 6)")
			@Config.Name("Rain Collector Fill Chance")
			@Config.RangeInt(min=1, max=100)
			public int rainCollectorFillChance = 6;
			
			@Config.Comment("Register the Cooling and Heating enchantments")
			@Config.Name("Register Temperature Enchantments")
			@Config.RequiresMcRestart
			public boolean registerEnchantments = true;
			
			@Config.Comment("Duration of short Heat/Cold Resistance potions (in ticks, 20 ticks = 1 second)")
			@Config.Name("Short Resistance Potion Duration")
			@Config.RequiresMcRestart
			@Config.RangeInt(min=1, max=72000)
			public int resistancePotionDurationShort = 1200;
			
			@Config.Comment("Duration of long Heat/Cold Resistance potions (in ticks, 20 ticks = 1 second)")
			@Config.Name("Long Resistance Potion Duration")
			@Config.RequiresMcRestart
			@Config.RangeInt(min=1, max=72000)
			public int resistancePotionDurationLong = 2400;
			
			@Config.Comment("How often player temperature and thirst data is synced to clients (in ticks)")
			@Config.Name("Data Sync Interval")
			@Config.RangeInt(min=0, max=200)
			public int routinePacketDelay = 30;
		}
		
		public class ConfigTemperature
		{
			@Config.Comment("How strongly altitude affects temperature (higher = more extreme changes)")
			@Config.Name("Altitude Effect Multiplier")
			@Config.RangeInt(min=-100, max=100)
			public int altitudeMultiplier = 3;
			
			@Config.Comment("Maximum temperature change from biome effects")
			@Config.Name("Biome Effect Multiplier")
			@Config.RangeInt(min=-100, max=100)
			public int biomeMultiplier = 10;
			
			@Config.Comment("Being underground reduces surface temperature effects")
			@Config.Name("Enable Underground Insulation")
			public boolean undergroundEffect = true;
			
			@Config.Comment("Y-level where surface temperature effects are completely blocked")
			@Config.Name("Underground Insulation Depth")
			@Config.RangeInt(min=0, max=64)
			public int undergroundEffectCutoff = 30;
			
			@Config.Comment("How strongly time of day affects temperature")
			@Config.Name("Time of Day Effect Multiplier")
			@Config.RangeInt(min=-100, max=100)
			public int timeMultiplier = 3;
			
			@Config.Comment("Time of day affects temperature during daytime")
			@Config.Name("Enable Daytime Temperature Changes")
			public boolean timeTemperatureDay = true;
			
			@Config.Comment("Time of day affects temperature during nighttime")
			@Config.Name("Enable Nighttime Temperature Changes")
			public boolean timeTemperatureNight = true;
			
			@Config.Comment("Temperature reduction when in shade during hot daytime (negative value)")
			@Config.Name("Shade Cooling Effect")
			@Config.RangeInt(min=-50, max=0)
			public int timeTemperatureShade = -2;
			
			@Config.Comment("How strongly different biomes amplify day/night temperature changes")
			@Config.Name("Biome Time Multiplier")
			@Config.RangeDouble(min=1.0, max=100.0)
			public double timeBiomeMultiplier = 1.25d;

			@Config.Comment("Temperature effect of snowfall (negative value)")
			@Config.Name("Snow Cooling Effect")
			@Config.RangeInt(min=-100, max=0)
			public int snowValue = -10;
			
			@Config.Comment("Temperature increase from sprinting")
			@Config.Name("Sprint Heating Effect")
			@Config.RangeInt(min=0, max=100)
			public int sprintingValue = 3;
			
			@Config.Comment("Temperature effect of being wet (negative value)")
			@Config.Name("Wet Cooling Effect")
			@Config.RangeInt(min=-100, max=0)
			public int wetValue = -7;
			
			@Config.Comment("Maximum time in ticks for temperature to change (slower = more stable)")
			@Config.Name("Temperature Change Max Speed")
			@Config.RangeInt(min=20, max=24000)
			public int temperatureTickMax = 400;
			
			@Config.Comment("Minimum time in ticks for temperature to change (faster = more volatile)")
			@Config.Name("Temperature Change Min Speed")
			@Config.RangeInt(min=20, max=24000)
			public int temperatureTickMin = 20;
			
			@Config.Comment("How much faster temperature changes when escaping dangerous temperatures (in ticks)")
			@Config.Name("Danger Recovery Speed Boost")
			@Config.RangeInt(min=0, max=1200)
			public int temperatureTickDangerBoost = 60;
			
			@Config.Comment("Temperature change from Cooling/Heating enchantments")
			@Config.Name("Enchantment Temperature Effect")
			@Config.RangeInt(min=-100, max=100)
			public int enchantmentTemperature = 1;
			
			@Config.Comment("Temperature change strength of heaters and chillers")
			@Config.Name("Heater/Chiller Strength")
			@Config.RangeInt(min=-1000, max=1000)
			public int heaterTemperature = 10;
			
			@Config.Comment("Distance where heaters/chillers start losing effectiveness")
			@Config.Name("Heater/Chiller Full Power Range")
			@Config.RangeDouble(min=0, max=50)
			public double heaterFullPowerRange = 16.0d;
			
			@Config.Comment("Maximum distance where heaters/chillers have any effect")
			@Config.Name("Heater/Chiller Max Range")
			@Config.RangeDouble(min=0, max=50)
			public double heaterMaxRange = 32.0d;
			
			@Config.Comment("Calculate block and tile entity temperatures separately (e.g., campfire + heater stack individually)")
			@Config.Name("Separate Block and Tile Entity Calculations")
			public boolean blocksTilesSeparate = true;
			
			@Config.Comment("Allow multiple heat/cold sources to combine their effects")
			@Config.Name("Enable Temperature Stacking")
			public boolean stackingTemperature = true;
			
			@Config.Comment("Maximum multiplier for stacked temperature effects")
			@Config.Name("Temperature Stacking Limit")
			@Config.RangeDouble(min=1.0, max=100.0)
			public double stackingTemperatureLimit = 3;
			
			@Config.Comment("Extra damage over time from extreme temperatures (0.0 = disabled)")
			@Config.Name("Temperature Damage Scaling")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double temperatureDamageScaling = 0.0d;
			
			@Config.Comment("Duration of Hypothermia and Hyperthermia effects (in ticks, 20 ticks = 1 second)")
			@Config.Name("Temperature Effect Duration")
			@Config.RangeInt(min=0, max=72000)
			public int temperatureDamageDuration = 400;
		}
		
		public class ConfigThirst
		{
			@Config.Comment("Global multiplier for all thirst exhaustion (1.0 = normal, 2.0 = twice as fast, 0.0 = disabled)")
			@Config.Name("Global Thirst Exhaustion Multiplier")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstExhaustionMultiplier = 1.0d;
			
			@Config.Comment("How much exhaustion is needed before losing a thirst point")
			@Config.Name("Thirst Exhaustion Threshold")
			@Config.RangeDouble(min=1.0, max=20.0)
			public double thirstExhaustionLimit = 4.0d;
			
			@Config.Comment("Strength of the Thirsty effect (higher = faster dehydration)")
			@Config.Name("Thirsty Effect Strength")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstyStrength = 0.025d;
			
			@Config.Comment("Thirst exhaustion from attacking enemies")
			@Config.Name("Combat Exhaustion")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstAttacking = 0.3d;
			
			@Config.Comment("Thirst exhaustion from breaking blocks")
			@Config.Name("Block Breaking Exhaustion")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstBreakBlock = 0.025d;
			
			@Config.Comment("Thirst exhaustion from jumping while sprinting")
			@Config.Name("Sprint Jump Exhaustion")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstSprintJump = 0.8d;
			
			@Config.Comment("Thirst exhaustion from jumping without sprinting")
			@Config.Name("Jump Exhaustion")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstJump = 0.2d;
			
			@Config.Comment("Thirst exhaustion from any movement")
			@Config.Name("Base Movement Exhaustion")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstBaseMovement = 0.01d;
			
			@Config.Comment("Thirst exhaustion from swimming")
			@Config.Name("Swimming Exhaustion")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstSwimmingMovement = 0.015d;
			
			@Config.Comment("Thirst exhaustion from sprinting")
			@Config.Name("Sprinting Exhaustion")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstSprintingMovement = 0.1d;
			
			@Config.Comment("Thirst exhaustion from walking")
			@Config.Name("Walking Exhaustion")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstWalkingMovement = 0.01d;

			@Config.Comment("Allow players to get parasites from drinking unclean water")
			@Config.Name("Enable Water Parasites")
			public boolean thirstParasites = false;
			
			@Config.Comment("Chance of getting parasites from unclean water (0.0 = never, 1.0 = always)")
			@Config.Name("Parasite Infection Chance")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstParasitesChance = 0.04d;
			
			@Config.Comment("Duration of parasite effects (in ticks, 20 ticks = 1 second)")
			@Config.Name("Parasite Effect Duration")
			@Config.RangeInt(min=1, max=72000)
			public int thirstParasitesDuration = 1200;
			
			@Config.Comment("How strongly parasites increase hunger (0.005 = same as normal hunger, 0 = disabled)")
			@Config.Name("Parasite Hunger Effect")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstParasitesHunger = 0.02d;
			
			@Config.Comment("Chance of taking damage from parasites (1.0 = poison speed, 0 = disabled)")
			@Config.Name("Parasite Damage Chance")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstParasitesDamage = 0.2d;
			
			@Config.Comment("Extra damage over time from dehydration (0.0 = disabled)")
			@Config.Name("Dehydration Damage Scaling")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstDamageScaling = 0.0d;
			
			@Config.Comment("Drinking salt water (from oceans/large bodies) causes the Thirsty effect. Disable to treat all water as fresh")
			@Config.Name("Enable Salt Water Thirst Effect")
			public boolean saltWaterThirst = true;
		}
	}
	
	///
	/// Client Options
	///
	
	public static class ConfigClientConfig
	{
		@Config.Comment("Thermometer display settings")
		@Config.Name("Thermometer")
		public final ConfigClientThermometer thermometer = new ConfigClientThermometer();
		
		@Config.Comment("Show alternate temperature display (numeric value)")
		@Config.Name("Alternate Temperature Display")
		public boolean alternateTemp = true;
		
		@Config.Comment("Display thirst saturation overlay on the HUD")
		@Config.Name("Show Thirst Saturation")
		public boolean drawThirstSaturation = true;
		
		@Config.Comment("Enable client-side debug messages")
		@Config.Name("Client Debug Mode")
		public boolean clientdebug = false;
		
		@Config.Comment("Show detailed temperature readout in debug mode")
		@Config.Name("Temperature Debug Readout")
		public boolean temperatureReadout = false;
		
		@Config.Comment("Use the classic temperature icon style")
		@Config.Name("Classic Temperature Icon")
		public boolean classicHUDTemperature = false;
		
		@Config.Comment("Use the classic thirst bar style")
		@Config.Name("Classic Thirst Bar")
		public boolean classicHUDThirst = false;
		
		@Config.Comment("Show particle effects for heaters and chillers")
		@Config.Name("Heater/Chiller Particles")
		public boolean heaterParticles = true;
		
		public class ConfigClientThermometer
		{
			@Config.Comment("Enable thermometer functionality (disable only for debugging performance issues)")
			@Config.Name("Enable Thermometer")
			public boolean enableThermometer = true;
			
			@Config.Comment("Display thermometer reading on HUD when in inventory")
			@Config.Name("HUD Thermometer Display")
			public boolean hudThermometer = true;
			
			@Config.Comment("Horizontal offset for the Thermometer HUD position")
			@Config.Name("HUD Thermometer X Offset")
			public int hudThermometerX = 0;
			
			@Config.Comment("Vertical offset for the Thermometer HUD position")
			@Config.Name("HUD Thermometer Y Offset")
			public int hudThermometerY = 0;
		}
	}
	
	///
	/// Event Handler
	///
	
	@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID)
	private static class EventHandler
	{
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if(event.getModID().equals(SimpleDifficulty.MODID))
			{
				ConfigManager.sync(SimpleDifficulty.MODID, Config.Type.INSTANCE);
				sendLocalClientConfigToAPI();
				
				if(event.isWorldRunning())
				{
					MessageConfigLAN message = new MessageConfigLAN();
					PacketHandler.instance.sendToServer(message);
				}
				else
				{
					sendLocalServerConfigToAPI();
				}
			}
		}
	}
	
	public static void sendLocalClientConfigToAPI()
	{
		ClientConfig.instance.put(ClientOptions.DEBUG, client.clientdebug);
		ClientConfig.instance.put(ClientOptions.DRAW_THIRST_SATURATION, client.drawThirstSaturation);
		ClientConfig.instance.put(ClientOptions.ENABLE_THERMOMETER, client.thermometer.enableThermometer);
		ClientConfig.instance.put(ClientOptions.ALTERNATE_TEMP, client.alternateTemp);
		ClientConfig.instance.put(ClientOptions.HUD_THERMOMETER, client.thermometer.hudThermometer);
		ClientConfig.instance.put(ClientOptions.HUD_THERMOMETERX, client.thermometer.hudThermometerX);
		ClientConfig.instance.put(ClientOptions.HUD_THERMOMETERY, client.thermometer.hudThermometerY);
		ClientConfig.instance.put(ClientOptions.TEMPERATURE_READOUT, client.temperatureReadout);
		ClientConfig.instance.put(ClientOptions.CLASSICHUD_TEMPERATURE, client.classicHUDTemperature);
		ClientConfig.instance.put(ClientOptions.CLASSICHUD_THIRST, client.classicHUDThirst);
		ClientConfig.instance.put(ClientOptions.HEATER_PARTICLES, client.heaterParticles);
	}
	
	public static void sendLocalServerConfigToAPI()
	{
		// Automated sync using reflection
		ConfigSyncHelper.autoSyncServerConfig(server);
	}
	
	private static MessageUpdateConfig getNewConfigMessage()
	{
		return new MessageUpdateConfig(ConfigSyncHelper.autoGenerateConfigNBT(server));
	}
	
	public static void sendServerConfigToPlayer(EntityPlayerMP player)
	{
		SimpleDifficulty.logger.info("Sending configuration to player: " + player.getName());
		PacketHandler.instance.sendTo(getNewConfigMessage(), player);
	}
	
	public static void sendServerConfigToAllPlayers()
	{
		SimpleDifficulty.logger.info("Sending configuration to all players");
		PacketHandler.instance.sendToAll(getNewConfigMessage());
	}
}
