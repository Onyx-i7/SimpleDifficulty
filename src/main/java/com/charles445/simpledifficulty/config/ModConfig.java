package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.config.compat.ConfigServerCompatibility;
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
		/// Core Server Options (Names MUST match ServerOptions enum exactly)
		///
		@Config.Comment("Enable or disable the entire thirst system")
		@Config.Name("thirstEnabled")
		public boolean thirstEnabled = true;
		
		@Config.Comment("Allow players to drink directly from water blocks")
		@Config.Name("thirstDrinkBlocks")
		public boolean thirstDrinkBlocks = true;
		
		@Config.Comment("Allow players to drink rainwater by looking up during rain")
		@Config.Name("thirstDrinkRain")
		public boolean thirstDrinkRain = true;
		
		@Config.Comment("Make the mod dangerous even on Peaceful difficulty (parasites, temperature damage)")
		@Config.Name("peacefulDanger")
		public boolean peacefulDanger = false;
		
		@Config.Comment("Enable or disable the entire temperature system")
		@Config.Name("temperatureEnabled")
		public boolean temperatureEnabled = true;
		
		@Config.Comment("Enable temperature-affecting tile entities (heaters, chillers)")
		@Config.Name("temperatureTEEnabled")
		public boolean temperatureTEEnabled = true;
		
		@Config.Comment("Maximum doses for the basic leather canteen")
		@Config.Name("canteenDoses")
		@Config.RangeInt(min=1, max=100)
		public int canteenDoses = 3;
		
		@Config.Comment("Maximum doses for the iron canteen")
		@Config.Name("ironCanteenDoses")
		@Config.RangeInt(min=1, max=100)
		public int ironCanteenDoses = 8;
		
		@Config.Comment("Maximum doses for the dragon canteen (end-game item)")
		@Config.Name("dragonCanteenDoses")
		@Config.RangeInt(min=1, max=100)
		public int dragonCanteenDoses = 30;
		
		@Config.Comment("Require heaters and chillers to be placed indoors to function")
		@Config.Name("strictHeaters")
		public boolean strictHeaters = true;
		
		@Config.Comment("Purified water blocks are infinite (do not deplete when collected)")
		@Config.Name("infinitePurifiedWater")
		public boolean infinitePurifiedWater = false;

		@Config.Comment("Reduce light opacity of purified water blocks (1 instead of 3) for better visibility")
		@Config.Name("purifiedWaterOpacity")
		public boolean purifiedWaterOpacity = false;

		@Config.Comment("Enable debug logging (spam console with detailed messages)")
		@Config.Name("debug")
		public boolean debug = false;
		
		public class ConfigGeneral
		{
			@Config.Comment("Campfire fuel decay chance (1 in X ticks, default: 2 = 50% chance)")
			@Config.Name("campfireDecayChance")
			@Config.RangeInt(min=1, max=100)
			public int campfireDecayChance = 2;
			
			@Config.Comment("Chance to ignite campfire with a stick (1 in X attempts, default: 5 = 20% chance)")
			@Config.Name("campfireStickIgniteChance")
			@Config.RangeInt(min=1, max=100)
			public int campfireStickIgniteChance = 5;
			
			@Config.Comment("Time in seconds to cook food on a campfire spit")
			@Config.Name("campfireSpitDelay")
			@Config.RangeInt(min=1, max=120)
			public int campfireSpitDelay = 35;
			
			@Config.Comment("Maximum food items that can fit on a campfire spit")
			@Config.Name("campfireSpitSize")
			@Config.RangeInt(min=1, max=10)
			public int campfireSpitSize = 3;
			
			@Config.Comment("Grant experience points when cooking food on a campfire spit (like a furnace)")
			@Config.Name("campfireSpitExperience")
			public boolean campfireSpitExperience = true;
			
			@Config.Comment("Items that cannot be cooked on a campfire spit (e.g., minecraft:beef)")
			@Config.Name("campfireSpitBlacklist")
			public String[] campfireSpitBlacklist = new String[0];
			
			@Config.Comment("Treat the blacklist as a whitelist instead (only listed items can be cooked)")
			@Config.Name("campfireSpitBlacklistIsWhitelist")
			public boolean campfireSpitBlacklistIsWhitelist = false;
			
			@Config.Comment("Golden Apple Juice grants the Golden Apple effect when consumed")
			@Config.Name("goldenAppleJuiceEffect")
			public boolean goldenAppleJuiceEffect = true;
			
			@Config.Comment("Ice blocks drop Ice Chunks when broken")
			@Config.Name("iceDropsChunks")
			public boolean iceDropsChunks = true;
			
			@Config.Comment("Magma blocks drop Magma Chunks when broken")
			@Config.Name("magmaDropsChunks")
			public boolean magmaDropsChunks = true;
			
			@Config.Comment("Rain collector fill chance (1 in X ticks, default: 6)")
			@Config.Name("rainCollectorFillChance")
			@Config.RangeInt(min=1, max=100)
			public int rainCollectorFillChance = 6;
			
			@Config.Comment("Register the Cooling and Heating enchantments")
			@Config.Name("registerEnchantments")
			@Config.RequiresMcRestart
			public boolean registerEnchantments = true;
			
			@Config.Comment("Duration of short Heat/Cold Resistance potions (in ticks, 20 ticks = 1 second)")
			@Config.Name("resistancePotionDurationShort")
			@Config.RequiresMcRestart
			@Config.RangeInt(min=1, max=72000)
			public int resistancePotionDurationShort = 1200;
			
			@Config.Comment("Duration of long Heat/Cold Resistance potions (in ticks, 20 ticks = 1 second)")
			@Config.Name("resistancePotionDurationLong")
			@Config.RequiresMcRestart
			@Config.RangeInt(min=1, max=72000)
			public int resistancePotionDurationLong = 2400;
			
			@Config.Comment("How often player temperature and thirst data is synced to clients (in ticks)")
			@Config.Name("routinePacketDelay")
			@Config.RangeInt(min=0, max=200)
			public int routinePacketDelay = 30;
		}
		
		public class ConfigTemperature
		{
			@Config.Comment("How strongly altitude affects temperature (higher = more extreme changes)")
			@Config.Name("altitudeMultiplier")
			@Config.RangeInt(min=-100, max=100)
			public int altitudeMultiplier = 3;
			
			@Config.Comment("Maximum temperature change from biome effects")
			@Config.Name("biomeMultiplier")
			@Config.RangeInt(min=-100, max=100)
			public int biomeMultiplier = 10;
			
			@Config.Comment("Being underground reduces surface temperature effects")
			@Config.Name("undergroundEffect")
			public boolean undergroundEffect = true;
			
			@Config.Comment("Y-level where surface temperature effects are completely blocked")
			@Config.Name("undergroundEffectCutoff")
			@Config.RangeInt(min=0, max=64)
			public int undergroundEffectCutoff = 30;
			
			@Config.Comment("How strongly time of day affects temperature")
			@Config.Name("timeMultiplier")
			@Config.RangeInt(min=-100, max=100)
			public int timeMultiplier = 3;
			
			@Config.Comment("Time of day affects temperature during daytime")
			@Config.Name("timeTemperatureDay")
			public boolean timeTemperatureDay = true;
			
			@Config.Comment("Time of day affects temperature during nighttime")
			@Config.Name("timeTemperatureNight")
			public boolean timeTemperatureNight = true;
			
			@Config.Comment("Temperature reduction when in shade during hot daytime (negative value)")
			@Config.Name("timeTemperatureShade")
			@Config.RangeInt(min=-50, max=0)
			public int timeTemperatureShade = -2;
			
			@Config.Comment("How strongly different biomes amplify day/night temperature changes")
			@Config.Name("timeBiomeMultiplier")
			@Config.RangeDouble(min=1.0, max=100.0)
			public double timeBiomeMultiplier = 1.25d;

			@Config.Comment("Temperature effect of snowfall (negative value)")
			@Config.Name("snowValue")
			@Config.RangeInt(min=-100, max=0)
			public int snowValue = -10;
			
			@Config.Comment("Temperature increase from sprinting")
			@Config.Name("sprintingValue")
			@Config.RangeInt(min=0, max=100)
			public int sprintingValue = 3;
			
			@Config.Comment("Temperature effect of being wet (negative value)")
			@Config.Name("wetValue")
			@Config.RangeInt(min=-100, max=0)
			public int wetValue = -7;
			
			@Config.Comment("Maximum time in ticks for temperature to change (slower = more stable)")
			@Config.Name("temperatureTickMax")
			@Config.RangeInt(min=20, max=24000)
			public int temperatureTickMax = 400;
			
			@Config.Comment("Minimum time in ticks for temperature to change (faster = more volatile)")
			@Config.Name("temperatureTickMin")
			@Config.RangeInt(min=20, max=24000)
			public int temperatureTickMin = 20;
			
			@Config.Comment("How much faster temperature changes when escaping dangerous temperatures (in ticks)")
			@Config.Name("temperatureTickDangerBoost")
			@Config.RangeInt(min=0, max=1200)
			public int temperatureTickDangerBoost = 60;
			
			@Config.Comment("Temperature change from Cooling/Heating enchantments")
			@Config.Name("enchantmentTemperature")
			@Config.RangeInt(min=-100, max=100)
			public int enchantmentTemperature = 1;
			
			@Config.Comment("Temperature change strength of heaters and chillers")
			@Config.Name("heaterTemperature")
			@Config.RangeInt(min=-1000, max=1000)
			public int heaterTemperature = 10;
			
			@Config.Comment("Distance where heaters/chillers start losing effectiveness")
			@Config.Name("heaterFullPowerRange")
			@Config.RangeDouble(min=0, max=50)
			public double heaterFullPowerRange = 16.0d;
			
			@Config.Comment("Maximum distance where heaters/chillers have any effect")
			@Config.Name("heaterMaxRange")
			@Config.RangeDouble(min=0, max=50)
			public double heaterMaxRange = 32.0d;
			
			@Config.Comment("Calculate block and tile entity temperatures separately (e.g., campfire + heater stack individually)")
			@Config.Name("blocksTilesSeparate")
			public boolean blocksTilesSeparate = true;
			
			@Config.Comment("Allow multiple heat/cold sources to combine their effects")
			@Config.Name("stackingTemperature")
			public boolean stackingTemperature = true;
			
			@Config.Comment("Maximum multiplier for stacked temperature effects")
			@Config.Name("stackingTemperatureLimit")
			@Config.RangeDouble(min=1.0, max=100.0)
			public double stackingTemperatureLimit = 3;
			
			@Config.Comment("Extra damage over time from extreme temperatures (0.0 = disabled)")
			@Config.Name("temperatureDamageScaling")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double temperatureDamageScaling = 0.0d;
			
			@Config.Comment("Duration of Hypothermia and Hyperthermia effects (in ticks, 20 ticks = 1 second)")
			@Config.Name("temperatureDamageDuration")
			@Config.RangeInt(min=0, max=72000)
			public int temperatureDamageDuration = 400;
		}
		
		public class ConfigThirst
		{
			@Config.Comment("Global multiplier for all thirst exhaustion (1.0 = normal, 2.0 = twice as fast, 0.0 = disabled)")
			@Config.Name("thirstExhaustionMultiplier")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstExhaustionMultiplier = 1.0d;
			
			@Config.Comment("How much exhaustion is needed before losing a thirst point")
			@Config.Name("thirstExhaustionLimit")
			@Config.RangeDouble(min=1.0, max=20.0)
			public double thirstExhaustionLimit = 4.0d;
			
			@Config.Comment("Strength of the Thirsty effect (higher = faster dehydration)")
			@Config.Name("thirstyStrength")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstyStrength = 0.025d;
			
			@Config.Comment("Thirst exhaustion from attacking enemies")
			@Config.Name("thirstAttacking")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstAttacking = 0.3d;
			
			@Config.Comment("Thirst exhaustion from breaking blocks")
			@Config.Name("thirstBreakBlock")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstBreakBlock = 0.025d;
			
			@Config.Comment("Thirst exhaustion from jumping while sprinting")
			@Config.Name("thirstSprintJump")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstSprintJump = 0.8d;
			
			@Config.Comment("Thirst exhaustion from jumping without sprinting")
			@Config.Name("thirstJump")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstJump = 0.2d;
			
			@Config.Comment("Thirst exhaustion from any movement")
			@Config.Name("thirstBaseMovement")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstBaseMovement = 0.01d;
			
			@Config.Comment("Thirst exhaustion from swimming")
			@Config.Name("thirstSwimmingMovement")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstSwimmingMovement = 0.015d;
			
			@Config.Comment("Thirst exhaustion from sprinting")
			@Config.Name("thirstSprintingMovement")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstSprintingMovement = 0.1d;
			
			@Config.Comment("Thirst exhaustion from walking")
			@Config.Name("thirstWalkingMovement")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstWalkingMovement = 0.01d;

			@Config.Comment("Allow players to get parasites from drinking unclean water")
			@Config.Name("thirstParasites")
			public boolean thirstParasites = false;
			
			@Config.Comment("Chance of getting parasites from unclean water (0.0 = never, 1.0 = always)")
			@Config.Name("thirstParasitesChance")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstParasitesChance = 0.04d;
			
			@Config.Comment("Duration of parasite effects (in ticks, 20 ticks = 1 second)")
			@Config.Name("thirstParasitesDuration")
			@Config.RangeInt(min=1, max=72000)
			public int thirstParasitesDuration = 1200;
			
			@Config.Comment("How strongly parasites increase hunger (0.005 = same as normal hunger, 0 = disabled)")
			@Config.Name("thirstParasitesHunger")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstParasitesHunger = 0.02d;
			
			@Config.Comment("Chance of taking damage from parasites (1.0 = poison speed, 0 = disabled)")
			@Config.Name("thirstParasitesDamage")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstParasitesDamage = 0.2d;
			
			@Config.Comment("Extra damage over time from dehydration (0.0 = disabled)")
			@Config.Name("thirstDamageScaling")
			@Config.RangeDouble(min=0.0, max=10.0)
			public double thirstDamageScaling = 0.0d;
			
			@Config.Comment("Drinking salt water (from oceans/large bodies) causes the Thirsty effect. Disable to treat all water as fresh")
			@Config.Name("saltWaterThirst")
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
		@Config.Name("alternateTemp")
		public boolean alternateTemp = true;
		
		@Config.Comment("Display thirst saturation overlay on the HUD")
		@Config.Name("drawThirstSaturation")
		public boolean drawThirstSaturation = true;
		
		@Config.Comment("Enable client-side debug messages")
		@Config.Name("clientdebug")
		public boolean clientdebug = false;
		
		@Config.Comment("Show detailed temperature readout in debug mode")
		@Config.Name("temperatureReadout")
		public boolean temperatureReadout = false;
		
		@Config.Comment("Use the classic temperature icon style")
		@Config.Name("classicHUDTemperature")
		public boolean classicHUDTemperature = false;
		
		@Config.Comment("Use the classic thirst bar style")
		@Config.Name("classicHUDThirst")
		public boolean classicHUDThirst = false;
		
		@Config.Comment("Show particle effects for heaters and chillers")
		@Config.Name("heaterParticles")
		public boolean heaterParticles = true;
		
		public class ConfigClientThermometer
		{
			@Config.Comment("Enable thermometer functionality (disable only for debugging performance issues)")
			@Config.Name("enableThermometer")
			public boolean enableThermometer = true;
			
			@Config.Comment("Display thermometer reading on HUD when in inventory")
			@Config.Name("hudThermometer")
			public boolean hudThermometer = true;
			
			@Config.Comment("Horizontal offset for the Thermometer HUD position")
			@Config.Name("hudThermometerX")
			public int hudThermometerX = 0;
			
			@Config.Comment("Vertical offset for the Thermometer HUD position")
			@Config.Name("hudThermometerY")
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