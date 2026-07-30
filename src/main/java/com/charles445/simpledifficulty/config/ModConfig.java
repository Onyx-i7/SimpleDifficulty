package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.config.ConfigSyncHelper;
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
	@Config.Comment("Client configuration")
	@Config.Name("Client")
	public static final ConfigClientConfig client = new ConfigClientConfig();
	
	@Config.Comment("Server configuration")
	@Config.Name("Server")
	public static final ConfigServerConfig server = new ConfigServerConfig();
	
	//TODO Lang
	
	public static class ConfigServerConfig
	{
		@Config.Comment("Built-in mod compatibility options")
		@Config.Name("Compatibility")
		public final ConfigServerCompatibility compatibility = new ConfigServerCompatibility();
		
		@Config.Comment("Miscellaneous gameplay configurations")
		@Config.Name("Miscellaneous")
		public final ConfigMiscellaneous miscellaneous = new ConfigMiscellaneous();
		
		@Config.Comment("Temperature related configurations")
		@Config.Name("Temperature")
		public final ConfigTemperature temperature = new ConfigTemperature();
		
		@Config.Comment("Thirst related configurations")
		@Config.Name("Thirst")
		public final ConfigThirst thirst = new ConfigThirst();
		
		///
		/// Server Options
		///
		@Config.Comment("Whether thirst is enabled.")
		@Config.Name("ThirstEnabled")
		public boolean thirstEnabled = true;
		
		@Config.Comment("Whether the player is allowed to drink from normal water blocks.")
		@Config.Name("ThirstDrinkBlocks")
		public boolean thirstDrinkBlocks = true;
		
		@Config.Comment("Whether the player is allowed to drink from the rain.")
		@Config.Name("ThirstDrinkRain")
		public boolean thirstDrinkRain = true;
		
		@Config.Comment("Whether the mod should be dangerous on Peaceful difficulty.")
		@Config.Name("PeacefulDanger")
		public boolean peacefulDanger = false;
		
		@Config.Comment("Whether temperature is enabled.")
		@Config.Name("TemperatureEnabled")
		public boolean temperatureEnabled = true;
		
		@Config.Comment("Whether temperature tile entities are enabled.")
		@Config.Name("TemperatureTileEntities")
		public boolean temperatureTEEnabled = true;
		
		@Config.Comment("Maximum number of doses in a canteen")
		@Config.Name("CanteenDoses")
		@Config.RangeInt(min=1)
		public int canteenDoses = 3;
		
		@Config.Comment("Whether heaters and chillers only work indoors")
		@Config.Name("StrictHeaters")
		public boolean strictHeaters = true;
		
		@Config.Comment("Maximum number of doses in an iron canteen")
		@Config.Name("IronCanteenDoses")
		@Config.RangeInt(min=1)
		public int ironCanteenDoses = 8;

		@Config.Comment("Maximum number of doses in a dragon canteen")
		@Config.Name("DragonCanteenDoses")
		@Config.RangeInt(min=1)
		public int dragonCanteenDoses = 30;
		
		@Config.Comment("Whether purified water blocks are infinite")
		@Config.Name("Infinite Purified Water")
		public boolean infinitePurifiedWater = false;

		@Config.Comment("Make Purified Water only reduce light level by 1 per Y-level, instead of 3.")
		@Config.Name("Brighter Purified Water")
		public boolean purifiedWaterOpacity = false;

		@Config.Comment("Spams chat with debug messages, do not enable this unless you are testing!")
		@Config.Name("DebugMode")
		public boolean debug = false;
		
		public class ConfigMiscellaneous
		{
			//Not synchronized with clients
			@Config.Comment("Campfire has a 1/X chance to lose fuel when ticked (default is 2, a 1/2 chance")
			@Config.Name("CampfireDecayChance")
			@Config.RangeInt(min=1)
			public int campfireDecayChance = 2;
			
			@Config.Comment("Campfire has a 1/X chance to ignite with a stick (default is 5, a 1/5 chance")
			@Config.Name("CampfireStickIgniteChance")
			@Config.RangeInt(min=1)
			public int campfireStickIgniteChance = 5;
			
			@Config.Comment("How many seconds it takes for a campfire spit to cook food")
			@Config.Name("CampfireSpitDelay")
			@Config.RangeInt(min=1)
			public int campfireSpitDelay = 35;
			
			@Config.Comment("How many pieces of food can fit on a campfire spit (any existing spits won't change size)")
			@Config.Name("CampfireSpitSize")
			@Config.RangeInt(min=1, max=10)
			public int campfireSpitSize = 3;
			
			@Config.Comment("Should cooking food on a campfire spit give experience like a furnace")
			@Config.Name("CampfireSpitExperience")
			public boolean campfireSpitExperience = true;
			
			@Config.Comment("Blacklisted items in the campfire spit (ex. minecraft:beef")
			@Config.Name("CampfireSpitBlacklist")
			public String[] campfireSpitBlacklist = new String[0];
			
			@Config.Comment("Whether the campfire spit blacklist is a whitelist instead")
			@Config.Name("CampfireSpitBlacklistIsWhitelist")
			public boolean campfireSpitBlacklistIsWhitelist = false;
			
			@Config.Comment("Whether Golden Apple Juice gives the golden apple effect")
			@Config.Name("GoldenAppleJuiceEffect")
			public boolean goldenAppleJuiceEffect = true;
			
			@Config.Comment("Whether Ice Blocks drop Ice Chunks")
			@Config.Name("IceDropsChunks")
			public boolean iceDropsChunks = true;
			
			@Config.Comment("Whether Magma Blocks drop Magma Chunks")
			@Config.Name("MagmaDropsChunks")
			public boolean magmaDropsChunks = true;
			
			@Config.Comment("Chance for rain collector to fill (1/n chance, default is 6")
			@Config.Name("RainCollectorFillChance")
			@Config.RangeInt(min=1)
			public int rainCollectorFillChance = 6;
			
			@Config.Comment("Whether to register the cooling and heating enchantments")
			@Config.Name("RegisterEnchantments")
			@Config.RequiresMcRestart
			public boolean registerEnchantments = true;
			
			@Config.Comment("Duration of short heat/cold resistance potions, in ticks")
			@Config.Name("ResistancePotionDurationShort")
			@Config.RequiresMcRestart
			@Config.RangeInt(min=1)
			public int resistancePotionDurationShort = 1200;
			
			@Config.Comment("Duration of long heat/cold resistance potions, in ticks")
			@Config.Name("ResistancePotionDurationLong")
			@Config.RequiresMcRestart
			@Config.RangeInt(min=1)
			public int resistancePotionDurationLong = 2400;
			
			@Config.Comment("How often player temperature and thirst are regularly synced, in ticks")
			@Config.Name("RoutinePacketDelay")
			@Config.RangeInt(min=0)
			public int routinePacketDelay = 30;
		}
		
		public class ConfigTemperature
		{
			//Not synchronized with clients
			//TODO it probably should be though, because of thermometers and such
			
			@Config.Comment("Altitude Temperature Multiplier - How strongly altitude affects temperature")
			@Config.Name("AltitudeMultiplier")
			@Config.RangeInt
			public int altitudeMultiplier = 3;
			
			@Config.Comment("Biome Temperature Multiplier - The maximum temperature change in any biome")
			@Config.Name("BiomeMultiplier")
			@Config.RangeInt
			public int biomeMultiplier = 10;
			
			@Config.Comment("Underground Effect - Whether being deep underground reduces some surface temperature effects")
			@Config.Name("UndergroundEffect")
			public boolean undergroundEffect = true;
			
			@Config.Comment("Underground Effect Cutoff - Y Level where surface temperature effects do nothing")
			@Config.Name("UndergroundEffectCutoff")
			@Config.RangeInt(min=0, max=64)
			public int undergroundEffectCutoff = 30;
			
			@Config.Comment("Time Temperature Multiplier - How strongly time affects temperature")
			@Config.Name("TimeMultiplier")
			@Config.RangeInt
			public int timeMultiplier = 3;
			
			@Config.Comment("Time Temperature Day - Whether time changes temperature during the day")
			@Config.Name("TimeTemperatureDay")
			public boolean timeTemperatureDay = true;
			
			@Config.Comment("Time Temperature Night - Whether time changes temperature during the night")
			@Config.Name("TimeTemperatureNight")
			public boolean timeTemperatureNight = true;
			
			@Config.Comment("Time Temperature Shade - Effect of shade on time temperature, only applies when time temperature is hot")
			@Config.Name("TimeTemperatureShade")
			public int timeTemperatureShade = -2;
			
			@Config.Comment("Time Biome Temperature Multiplier - How strongly different biomes effect day/night temperature")
			@Config.Name("TimeBiomeMultiplier")
			@Config.RangeDouble(min=1.0,max=1000000.0)
			public double timeBiomeMultiplier = 1.25d;

			@Config.Comment("Snow Temperature Value - Effect of snowfall on temperature")
			@Config.Name("SnowValue")
			@Config.RangeInt
			public int snowValue = -10;
			
			@Config.Comment("Sprinting Temperature Value - Effect of sprinting on temperature")
			@Config.Name("SprintingValue")
			@Config.RangeInt
			public int sprintingValue = 3;
			
			@Config.Comment("Wet Temperature Value - Effect of being wet on temperature")
			@Config.Name("WetValue")
			@Config.RangeInt
			public int wetValue = -7;
			
			@Config.Comment("Temperature Max Speed - Maximum time in ticks for a player temperature change")
			@Config.Name("TemperatureTickMax")
			@Config.RangeInt(min=20)
			public int temperatureTickMax = 400;
			
			@Config.Comment("Temperature Min Speed - Minimum time in ticks for a player temperature change")
			@Config.Name("TemperatureTickMin")
			@Config.RangeInt(min=20)
			public int temperatureTickMin = 20;
			
			@Config.Comment("TemperatureTickDangerBoost - How much faster in ticks temperature changes happen when escaping dangerous temperatures")
			@Config.Name("TemperatureTickDangerBoost")
			@Config.RangeInt(min=0)
			public int temperatureTickDangerBoost = 60;
			
			@Config.Comment("Enchantment Temperature Change - Effect of temperature enchantments")
			@Config.Name("EnchantmentTemperature")
			@Config.RangeInt
			public int enchantmentTemperature = 1;
			
			@Config.Comment("Heater Temperature Change - Strength of heaters / chillers")
			@Config.Name("HeaterTemperature")
			@Config.RangeInt(min=-1000000, max=1000000)
			public int heaterTemperature = 10;
			
			@Config.Comment("Heater Full Power Range - Distance where a heater / chiller starts to lose strength")
			@Config.Name("HeaterFullPowerRange")
			@Config.RangeDouble(min=0, max=50)
			public double heaterFullPowerRange = 16.0d;
			
			@Config.Comment("Heater Max Range - Distance where a heater / chiller has no effect")
			@Config.Name("HeaterMaxRange")
			@Config.RangeDouble(min=0, max=50)
			public double heaterMaxRange = 32.0d;
			
			@Config.Comment("Blocks Tiles Separate - Whether blocks and tile entities should have temperature calculated separately, for example, if this is set to true heaters and campfires will both add their heat individually")
			@Config.Name("BlocksTilesSeparate")
			public boolean blocksTilesSeparate = true;
			
			@Config.Comment("Stacking Temperature - Whether multiple blocks in a vicinity should combine their effect")
			@Config.Name("StackingTemperature")
			public boolean stackingTemperature = true;
			
			@Config.Comment("Stacking Temperature Limit - How much more extreme block temperature can be from stacking temperature")
			@Config.Name("StackingTemperatureLimit")
			@Config.RangeDouble(min=0, max = 1000000)
			public double stackingTemperatureLimit = 3;
			
			@Config.Comment("Temperature Damage Scaling - Extra damage from hyperthermia and hypothermia over time")
			@Config.Name("TemperatureDamageScaling")
			@Config.RangeDouble(min=0.0)
			public double temperatureDamageScaling = 0.0d;
			
			@Config.Comment("TemperatureDamageDuration - Duration in ticks of hypothermia and hyperthermia")
			@Config.Name("TemperatureDamageDuration")
			@Config.RangeInt(min=0)
			public int temperatureDamageDuration = 400;
			
			
		}
		public class ConfigThirst
		{
			//Not synchronized with clients
			//Shouldn't need to be either, the thirst server sync is aggressive

			@Config.Comment("Global Thirst Exhaustion Multiplier - Multiplies all thirst exhaustion values (1.0 = default, 2.0 = twice as fast, 0.0 = disabled)")
			@Config.Name("ThirstExhaustionMultiplier")
			@Config.RangeDouble(min=0.0)
			public double thirstExhaustionMultiplier = 1.0d;
			
			@Config.Comment("Thirst Exhaustion Limit - How exhausted the player must get before they lose thirst.")
			@Config.Name("ThirstExhaustionLimit")
			@Config.RangeDouble(min=1.0)
			public double thirstExhaustionLimit = 4.0d;
			
			@Config.Comment("Thirsty Strength - Strength of the Thirsty Effect")
			@Config.Name("ThirstyStrength")
			@Config.RangeDouble(min=0.0)
			public double thirstyStrength = 0.025d;
			
			@Config.Comment("Thirst Attacking - How exhausting attacking enemies is")
			@Config.Name("ThirstAttacking")
			@Config.RangeDouble(min=0.0)
			public double thirstAttacking = 0.3d;
			
			@Config.Comment("Thirst Break Block - How exhausting breaking blocks is")
			@Config.Name("ThirstBreakBlock")
			@Config.RangeDouble(min=0.0)
			public double thirstBreakBlock = 0.025d;
			
			@Config.Comment("Thirst Sprint Jump - How exhausting jumping while sprinting is")
			@Config.Name("ThirstSprintJump")
			@Config.RangeDouble(min=0.0)
			public double thirstSprintJump = 0.8d;
			
			@Config.Comment("Thirst Jump - How exhausting jumping without sprinting is")
			@Config.Name("ThirstJump")
			@Config.RangeDouble(min=0.0)
			public double thirstJump = 0.2d;
			
			@Config.Comment("Thirst Base Movement - How exhausting any kind of movement is")
			@Config.Name("ThirstBaseMovement")
			@Config.RangeDouble(min=0.0)
			public double thirstBaseMovement = 0.01d;
			
			@Config.Comment("Thirst Swimming Movement - How exhausting swimming movement is")
			@Config.Name("ThirstSwimmingMovement")
			@Config.RangeDouble(min=0.0)
			public double thirstSwimmingMovement = 0.015d;
			
			@Config.Comment("Thirst Sprinting Movement - How exhausting sprinting movement is")
			@Config.Name("ThirstSprintingMovement")
			@Config.RangeDouble(min=0.0)
			public double thirstSprintingMovement = 0.1d;
			
			@Config.Comment("Thirst Walking Movement - How exhausting walking movement is")
			@Config.Name("ThirstWalkingMovement")
			@Config.RangeDouble(min=0.0)
			public double thirstWalkingMovement = 0.01d;

			@Config.Comment("Whether the player can get parasites from drinking unclean water")
			@Config.Name("ThirstParasites")
			public boolean thirstParasites = false;
			
			@Config.Comment("The chance of parasites from drinking unclean water")
			@Config.Name("ThirstParasitesChance")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstParasitesChance = 0.04d;
			
			@Config.Comment("The duration parasites last")
			@Config.Name("ThirstParasitesDuration")
			@Config.RangeInt(min=1)
			public int thirstParasitesDuration = 1200;
			
			@Config.Comment("How strongly parasites make a player hungry (0.005 is same speed as hunger, 0 to disable")
			@Config.Name("ThirstParasitesHunger")
			@Config.RangeDouble(min=0.0)
			public double thirstParasitesHunger = 0.02d;
			
			@Config.Comment("The chance a player takes damage from parasites (1 is poison speed, 0 to disable)")
			@Config.Name("ThirstParasitesDamage")
			@Config.RangeDouble(min=0.0, max=1.0)
			public double thirstParasitesDamage = 0.2d;
			
			@Config.Comment("Thirst Damage Scaling - Extra damage from dehydration over time")
			@Config.Name("ThirstDamageScaling")
			@Config.RangeDouble(min=0.0)
			public double thirstDamageScaling = 0.0d;
			
			@Config.Comment("Whether drinking salt water (from oceans/large bodies) causes the Thirsty effect. If false, it acts like normal fresh water.")
			@Config.Name("SaltWaterThirst")
			public boolean saltWaterThirst = true;

			
		}
	}
	
	///
	/// Client Options
	///
	
	public static class ConfigClientConfig
	{
		//Debug configuration for working on ingame models
		
		//public double xx = 0.0d;
		//public double yy = 0.0d;
		//public double zz = 0.0d;
		//public double ra = 0.0d;
		//public double rx = 0.0d;
		//public double ry = 0.0d;
		//public double rz = 0.0d;
		//public double px = 0.0d;
		//public double py = 0.0d;
		//public double pz = 0.0d;
		
		@Config.Comment("Thermometer Configuration")
		@Config.Name("Thermometer")
		public final ConfigClientThermometer thermometer = new ConfigClientThermometer();
		
		@Config.Comment("Whether the alternate temperature display is enabled")
		@Config.Name("AlternateTemperature")
		public boolean alternateTemp = true;
		
		@Config.Comment("Whether to draw thirst saturation on the HUD")
		@Config.Name("DrawThirstSaturation")
		public boolean drawThirstSaturation = true;
		
		@Config.Comment("Debug mode for clients")
		@Config.Name("Client DebugMode")
		public boolean clientdebug = false;
		
		@Config.Comment("Debug temperature readout")
		@Config.Name("TemperatureReadout")
		public boolean temperatureReadout = false;
		
		@Config.Comment("Enables the classic temperature icon")
		@Config.Name("ClassicHUDTemperature")
		public boolean classicHUDTemperature = false;
		
		@Config.Comment("Enables the classic thirst bar")
		@Config.Name("ClassicHUDThirst")
		public boolean classicHUDThirst = false;
		
		@Config.Comment("Enables chiller and heater particles")
		@Config.Name("HeaterParticles")
		public boolean heaterParticles = true;
		
		public class ConfigClientThermometer
		{
			@Config.Comment("Whether thermometers display the correct temperature. Only disable this if you are trying to determine what's lagging.")
			@Config.Name("EnableThermometer")
			public boolean enableThermometer = true;
			
			@Config.Comment("Whether thermometers in your inventory will display on your HUD")
			@Config.Name("HUDThermometer")
			public boolean hudThermometer = true;
			
			@Config.Comment("How far left or right the Thermometer HUD is from the default position")
			@Config.Name("XOffset")
			public int hudThermometerX = 0;
			
			@Config.Comment("How far up or down the Thermometer HUD is from the default position")
			@Config.Name("YOffset")
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
				
				//Make sure there's a world running
				if(event.isWorldRunning())
				{
					//Make new message
					MessageConfigLAN message = new MessageConfigLAN();
					
					//Send to server
					PacketHandler.instance.sendToServer(message);
				}
				else
				{
					//Title Screen
					sendLocalServerConfigToAPI();
				}
			}
		}
	}
	
	//TODO proper hash mapping so adding new config is easier, but it's not very important
	//It didn't work the first time I tried, that spurred on the creation of DebugVerify
	
	public static void sendLocalClientConfigToAPI()
	{
		//TODO if it's client side, it doesn't need to be synchronized
		//So it should just be referenced directly instead of using this hash map stuff?...
		
		//Place in Client Config

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
	
	public static void sendLocalServerConfigToAPI() {
	    ConfigSyncHelper.autoSyncServerConfig(server);
	}
	
	private static MessageUpdateConfig getNewConfigMessage() {
	    return new MessageUpdateConfig(ConfigSyncHelper.autoGenerateConfigNBT(server));
	}
	
	public static void sendServerConfigToPlayer(EntityPlayerMP player)
	{
		SimpleDifficulty.logger.info("Sending Configuration to player: "+player.getName());
		PacketHandler.instance.sendTo(getNewConfigMessage(), player);
	}
	
	public static void sendServerConfigToAllPlayers()
	{
		SimpleDifficulty.logger.info("Sending Configuration to all players");
		PacketHandler.instance.sendToAll(getNewConfigMessage());
	}
}
