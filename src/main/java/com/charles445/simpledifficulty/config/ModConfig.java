package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;

    public static final ClientConfigBuilder CLIENT;
    public static final ServerConfigBuilder SERVER;

    static {
        final Pair<ClientConfigBuilder, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfigBuilder::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();

        final Pair<ServerConfigBuilder, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(ServerConfigBuilder::new);
        SERVER = serverSpecPair.getLeft();
        SERVER_SPEC = serverSpecPair.getRight();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(Type.SERVER, SERVER_SPEC);
    }

    // Automatically syncs config values to the API whenever the config is loaded or changed
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getModId().equals(SimpleDifficulty.MODID)) {
            sendLocalClientConfigToAPI();
            sendLocalServerConfigToAPI();
        }
    }

    public static class ClientConfigBuilder {
        public final ForgeConfigSpec.BooleanValue enableThermometer;
        public final ForgeConfigSpec.BooleanValue hudThermometer;
        public final ForgeConfigSpec.IntValue hudThermometerX;
        public final ForgeConfigSpec.IntValue hudThermometerY;
        public final ForgeConfigSpec.IntValue thirstOffsetX;
        public final ForgeConfigSpec.IntValue thirstOffsetY;
        public final ForgeConfigSpec.BooleanValue alternateTemp;
        public final ForgeConfigSpec.BooleanValue drawThirstSaturation;
        public final ForgeConfigSpec.BooleanValue clientdebug;
        public final ForgeConfigSpec.BooleanValue temperatureReadout;
        public final ForgeConfigSpec.BooleanValue classicHUDTemperature;
        public final ForgeConfigSpec.BooleanValue classicHUDThirst;
        public final ForgeConfigSpec.BooleanValue heaterParticles;

        public ClientConfigBuilder(ForgeConfigSpec.Builder builder) {
            builder.comment("Thermometer Configuration").push("Thermometer");
            enableThermometer = builder.define("EnableThermometer", true);
            hudThermometer = builder.define("HUDThermometer", true);
            hudThermometerX = builder.defineInRange("XOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            hudThermometerY = builder.defineInRange("YOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("HUD Positioning Configuration").push("HUD Positioning");
            thirstOffsetX = builder.defineInRange("Thirst Offset X", -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            thirstOffsetY = builder.defineInRange("Thirst Offset Y", 9, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Client configuration").push("Client");
            alternateTemp = builder.define("AlternateTemperature", true);
            drawThirstSaturation = builder.define("DrawThirstSaturation", true);
            clientdebug = builder.define("Client DebugMode", false);
            temperatureReadout = builder.define("TemperatureReadout", false);
            classicHUDTemperature = builder.define("ClassicHUDTemperature", false);
            classicHUDThirst = builder.define("ClassicHUDThirst", false);
            heaterParticles = builder.define("HeaterParticles", true);
            builder.pop();
        }
    }

    public static class ServerConfigBuilder {
        // General Server Options
        public final ForgeConfigSpec.BooleanValue thirstEnabled;
        public final ForgeConfigSpec.BooleanValue thirstDrinkBlocks;
        public final ForgeConfigSpec.BooleanValue thirstDrinkRain;
        public final ForgeConfigSpec.BooleanValue peacefulDanger;
        public final ForgeConfigSpec.BooleanValue temperatureEnabled;
        public final ForgeConfigSpec.BooleanValue temperatureTEEnabled;
        public final ForgeConfigSpec.IntValue canteenDoses;
        public final ForgeConfigSpec.BooleanValue strictHeaters;
        public final ForgeConfigSpec.IntValue ironCanteenDoses;
        public final ForgeConfigSpec.IntValue dragonCanteenDoses;
        public final ForgeConfigSpec.BooleanValue infinitePurifiedWater;
        public final ForgeConfigSpec.BooleanValue purifiedWaterOpacity;
        public final ForgeConfigSpec.BooleanValue debug;

        // Miscellaneous
        public final ForgeConfigSpec.IntValue campfireDecayChance;
        public final ForgeConfigSpec.IntValue campfireStickIgniteChance;
        public final ForgeConfigSpec.IntValue campfireSpitDelay;
        public final ForgeConfigSpec.IntValue campfireSpitSize;
        public final ForgeConfigSpec.BooleanValue campfireSpitExperience;
        public final ForgeConfigSpec.ConfigValue<? extends String> campfireSpitBlacklist; // Simplified for now
        public final ForgeConfigSpec.BooleanValue campfireSpitBlacklistIsWhitelist;
        public final ForgeConfigSpec.BooleanValue goldenAppleJuiceEffect;
        public final ForgeConfigSpec.BooleanValue iceDropsChunks;
        public final ForgeConfigSpec.BooleanValue magmaDropsChunks;
        public final ForgeConfigSpec.IntValue rainCollectorFillChance;
        public final ForgeConfigSpec.BooleanValue registerEnchantments;
        public final ForgeConfigSpec.IntValue resistancePotionDurationShort;
        public final ForgeConfigSpec.IntValue resistancePotionDurationLong;
        public final ForgeConfigSpec.IntValue routinePacketDelay;

        // Temperature
        public final ForgeConfigSpec.IntValue altitudeMultiplier;
        public final ForgeConfigSpec.IntValue biomeMultiplier;
        public final ForgeConfigSpec.BooleanValue undergroundEffect;
        public final ForgeConfigSpec.IntValue undergroundEffectCutoff;
        public final ForgeConfigSpec.IntValue timeMultiplier;
        public final ForgeConfigSpec.BooleanValue timeTemperatureDay;
        public final ForgeConfigSpec.BooleanValue timeTemperatureNight;
        public final ForgeConfigSpec.IntValue timeTemperatureShade;
        public final ForgeConfigSpec.DoubleValue timeBiomeMultiplier;
        public final ForgeConfigSpec.IntValue snowValue;
        public final ForgeConfigSpec.IntValue sprintingValue;
        public final ForgeConfigSpec.IntValue wetValue;
        public final ForgeConfigSpec.IntValue temperatureTickMax;
        public final ForgeConfigSpec.IntValue temperatureTickMin;
        public final ForgeConfigSpec.IntValue temperatureTickDangerBoost;
        public final ForgeConfigSpec.IntValue enchantmentTemperature;
        public final ForgeConfigSpec.IntValue heaterTemperature;
        public final ForgeConfigSpec.DoubleValue heaterFullPowerRange;
        public final ForgeConfigSpec.DoubleValue heaterMaxRange;
        public final ForgeConfigSpec.BooleanValue blocksTilesSeparate;
        public final ForgeConfigSpec.BooleanValue stackingTemperature;
        public final ForgeConfigSpec.DoubleValue stackingTemperatureLimit;
        public final ForgeConfigSpec.DoubleValue temperatureDamageScaling;
        public final ForgeConfigSpec.IntValue temperatureDamageDuration;

        // Thirst
        public final ForgeConfigSpec.DoubleValue thirstExhaustionMultiplier;
        public final ForgeConfigSpec.DoubleValue thirstExhaustionLimit;
        public final ForgeConfigSpec.DoubleValue thirstyStrength;
        public final ForgeConfigSpec.DoubleValue thirstAttacking;
        public final ForgeConfigSpec.DoubleValue thirstBreakBlock;
        public final ForgeConfigSpec.DoubleValue thirstSprintJump;
        public final ForgeConfigSpec.DoubleValue thirstJump;
        public final ForgeConfigSpec.DoubleValue thirstBaseMovement;
        public final ForgeConfigSpec.DoubleValue thirstSwimmingMovement;
        public final ForgeConfigSpec.DoubleValue thirstSprintingMovement;
        public final ForgeConfigSpec.DoubleValue thirstWalkingMovement;
        public final ForgeConfigSpec.BooleanValue thirstParasites;
        public final ForgeConfigSpec.DoubleValue thirstParasitesChance;
        public final ForgeConfigSpec.IntValue thirstParasitesDuration;
        public final ForgeConfigSpec.DoubleValue thirstParasitesHunger;
        public final ForgeConfigSpec.DoubleValue thirstParasitesDamage;
        public final ForgeConfigSpec.DoubleValue thirstDamageScaling;
        public final ForgeConfigSpec.BooleanValue saltWaterThirst;

        public ServerConfigBuilder(ForgeConfigSpec.Builder builder) {
            builder.push("Server");
            thirstEnabled = builder.define("ThirstEnabled", true);
            thirstDrinkBlocks = builder.define("ThirstDrinkBlocks", true);
            thirstDrinkRain = builder.define("ThirstDrinkRain", true);
            peacefulDanger = builder.define("PeacefulDanger", false);
            temperatureEnabled = builder.define("TemperatureEnabled", true);
            temperatureTEEnabled = builder.define("TemperatureTileEntities", true);
            canteenDoses = builder.defineInRange("CanteenDoses", 3, 1, Integer.MAX_VALUE);
            strictHeaters = builder.define("StrictHeaters", true);
            ironCanteenDoses = builder.defineInRange("IronCanteenDoses", 8, 1, Integer.MAX_VALUE);
            dragonCanteenDoses = builder.defineInRange("DragonCanteenDoses", 30, 1, Integer.MAX_VALUE);
            infinitePurifiedWater = builder.define("Infinite Purified Water", false);
            purifiedWaterOpacity = builder.define("Brighter Purified Water", false);
            debug = builder.define("DebugMode", false);
            builder.pop();

            builder.push("Miscellaneous");
            campfireDecayChance = builder.defineInRange("CampfireDecayChance", 2, 1, Integer.MAX_VALUE);
            campfireStickIgniteChance = builder.defineInRange("CampfireStickIgniteChance", 5, 1, Integer.MAX_VALUE);
            campfireSpitDelay = builder.defineInRange("CampfireSpitDelay", 35, 1, Integer.MAX_VALUE);
            campfireSpitSize = builder.defineInRange("CampfireSpitSize", 3, 1, 10);
            campfireSpitExperience = builder.define("CampfireSpitExperience", true);
            campfireSpitBlacklist = builder.defineList("CampfireSpitBlacklist", java.util.Collections.emptyList(), o -> true);
            campfireSpitBlacklistIsWhitelist = builder.define("CampfireSpitBlacklistIsWhitelist", false);
            goldenAppleJuiceEffect = builder.define("GoldenAppleJuiceEffect", true);
            iceDropsChunks = builder.define("IceDropsChunks", true);
            magmaDropsChunks = builder.define("MagmaDropsChunks", true);
            rainCollectorFillChance = builder.defineInRange("RainCollectorFillChance", 6, 1, Integer.MAX_VALUE);
            registerEnchantments = builder.define("RegisterEnchantments", true);
            resistancePotionDurationShort = builder.defineInRange("ResistancePotionDurationShort", 1200, 1, Integer.MAX_VALUE);
            resistancePotionDurationLong = builder.defineInRange("ResistancePotionDurationLong", 2400, 1, Integer.MAX_VALUE);
            routinePacketDelay = builder.defineInRange("RoutinePacketDelay", 30, 0, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Temperature");
            altitudeMultiplier = builder.defineInRange("AltitudeMultiplier", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            biomeMultiplier = builder.defineInRange("BiomeMultiplier", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            undergroundEffect = builder.define("UndergroundEffect", true);
            undergroundEffectCutoff = builder.defineInRange("UndergroundEffectCutoff", 30, 0, 64);
            timeMultiplier = builder.defineInRange("TimeMultiplier", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            timeTemperatureDay = builder.define("TimeTemperatureDay", true);
            timeTemperatureNight = builder.define("TimeTemperatureNight", true);
            timeTemperatureShade = builder.defineInRange("TimeTemperatureShade", -2, Integer.MIN_VALUE, Integer.MAX_VALUE);
            timeBiomeMultiplier = builder.defineInRange("TimeBiomeMultiplier", 1.25d, 1.0d, 1000000.0d);
            snowValue = builder.defineInRange("SnowValue", -10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            sprintingValue = builder.defineInRange("SprintingValue", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            wetValue = builder.defineInRange("WetValue", -7, Integer.MIN_VALUE, Integer.MAX_VALUE);
            temperatureTickMax = builder.defineInRange("TemperatureTickMax", 400, 20, Integer.MAX_VALUE);
            temperatureTickMin = builder.defineInRange("TemperatureTickMin", 20, 20, Integer.MAX_VALUE);
            temperatureTickDangerBoost = builder.defineInRange("TemperatureTickDangerBoost", 60, 0, Integer.MAX_VALUE);
            enchantmentTemperature = builder.defineInRange("EnchantmentTemperature", 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            heaterTemperature = builder.defineInRange("HeaterTemperature", 10, -1000000, 1000000);
            heaterFullPowerRange = builder.defineInRange("HeaterFullPowerRange", 16.0d, 0.0d, 50.0d);
            heaterMaxRange = builder.defineInRange("HeaterMaxRange", 32.0d, 0.0d, 50.0d);
            blocksTilesSeparate = builder.define("BlocksTilesSeparate", true);
            stackingTemperature = builder.define("StackingTemperature", true);
            stackingTemperatureLimit = builder.defineInRange("StackingTemperatureLimit", 3.0d, 0.0d, 1000000.0d);
            temperatureDamageScaling = builder.defineInRange("TemperatureDamageScaling", 0.0d, 0.0d, Double.MAX_VALUE);
            temperatureDamageDuration = builder.defineInRange("TemperatureDamageDuration", 400, 0, Integer.MAX_VALUE);
            builder.pop();

            builder.push("Thirst");
            thirstExhaustionMultiplier = builder.defineInRange("ThirstExhaustionMultiplier", 1.0d, 0.0d, Double.MAX_VALUE);
            thirstExhaustionLimit = builder.defineInRange("ThirstExhaustionLimit", 4.0d, 1.0d, Double.MAX_VALUE);
            thirstyStrength = builder.defineInRange("ThirstyStrength", 0.025d, 0.0d, Double.MAX_VALUE);
            thirstAttacking = builder.defineInRange("ThirstAttacking", 0.3d, 0.0d, Double.MAX_VALUE);
            thirstBreakBlock = builder.defineInRange("ThirstBreakBlock", 0.025d, 0.0d, Double.MAX_VALUE);
            thirstSprintJump = builder.defineInRange("ThirstSprintJump", 0.8d, 0.0d, Double.MAX_VALUE);
            thirstJump = builder.defineInRange("ThirstJump", 0.2d, 0.0d, Double.MAX_VALUE);
            thirstBaseMovement = builder.defineInRange("ThirstBaseMovement", 0.01d, 0.0d, Double.MAX_VALUE);
            thirstSwimmingMovement = builder.defineInRange("ThirstSwimmingMovement", 0.015d, 0.0d, Double.MAX_VALUE);
            thirstSprintingMovement = builder.defineInRange("ThirstSprintingMovement", 0.1d, 0.0d, Double.MAX_VALUE);
            thirstWalkingMovement = builder.defineInRange("ThirstWalkingMovement", 0.01d, 0.0d, Double.MAX_VALUE);
            thirstParasites = builder.define("ThirstParasites", false);
            thirstParasitesChance = builder.defineInRange("ThirstParasitesChance", 0.04d, 0.0d, 1.0d);
            thirstParasitesDuration = builder.defineInRange("ThirstParasitesDuration", 1200, 1, Integer.MAX_VALUE);
            thirstParasitesHunger = builder.defineInRange("ThirstParasitesHunger", 0.02d, 0.0d, Double.MAX_VALUE);
            thirstParasitesDamage = builder.defineInRange("ThirstParasitesDamage", 0.2d, 0.0d, 1.0d);
            thirstDamageScaling = builder.defineInRange("ThirstDamageScaling", 0.0d, 0.0d, Double.MAX_VALUE);
            saltWaterThirst = builder.define("SaltWaterThirst", true);
            builder.pop();
        }
    }

    public static void sendLocalClientConfigToAPI() {
        ClientConfig.instance.put(ClientOptions.DEBUG, CLIENT.clientdebug.get());
        ClientConfig.instance.put(ClientOptions.DRAW_THIRST_SATURATION, CLIENT.drawThirstSaturation.get());
        ClientConfig.instance.put(ClientOptions.ENABLE_THERMOMETER, CLIENT.enableThermometer.get());
        ClientConfig.instance.put(ClientOptions.ALTERNATE_TEMP, CLIENT.alternateTemp.get());
        ClientConfig.instance.put(ClientOptions.HUD_THERMOMETER, CLIENT.hudThermometer.get());
        ClientConfig.instance.put(ClientOptions.HUD_THERMOMETERX, CLIENT.hudThermometerX.get());
        ClientConfig.instance.put(ClientOptions.HUD_THERMETERY, CLIENT.hudThermometerY.get());
        ClientConfig.instance.put(ClientOptions.TEMPERATURE_READOUT, CLIENT.temperatureReadout.get());
        ClientConfig.instance.put(ClientOptions.CLASSICHUD_TEMPERATURE, CLIENT.classicHUDTemperature.get());
        ClientConfig.instance.put(ClientOptions.CLASSICHUD_THIRST, CLIENT.classicHUDThirst.get());
        ClientConfig.instance.put(ClientOptions.HEATER_PARTICLES, CLIENT.heaterParticles.get());
        ClientConfig.instance.put(ClientOptions.THIRST_HUD_X, CLIENT.thirstOffsetX.get());
        ClientConfig.instance.put(ClientOptions.THIRST_HUD_Y, CLIENT.thirstOffsetY.get());
    }

    public static void sendLocalServerConfigToAPI() {
        ServerConfig.instance.put(ServerOptions.DEBUG, SERVER.debug.get());
        ServerConfig.instance.put(ServerOptions.THIRST_ENABLED, SERVER.thirstEnabled.get());
        ServerConfig.instance.put(ServerOptions.THIRST_DRINK_BLOCKS, SERVER.thirstDrinkBlocks.get());
        ServerConfig.instance.put(ServerOptions.THIRST_DRINK_RAIN, SERVER.thirstDrinkRain.get());
        ServerConfig.instance.put(ServerOptions.PEACEFUL_DANGER, SERVER.peacefulDanger.get());
        ServerConfig.instance.put(ServerOptions.TEMPERATURE_ENABLED, SERVER.temperatureEnabled.get());
        ServerConfig.instance.put(ServerOptions.TEMPERATURE_TE_ENABLED, SERVER.temperatureTEEnabled.get());
        ServerConfig.instance.put(ServerOptions.CANTEEN_DOSES, SERVER.canteenDoses.get());
        ServerConfig.instance.put(ServerOptions.STRICT_HEATERS, SERVER.strictHeaters.get());
        ServerConfig.instance.put(ServerOptions.IRON_CANTEEN_DOSES, SERVER.ironCanteenDoses.get());
        ServerConfig.instance.put(ServerOptions.DRAGON_CANTEEN_DOSES, SERVER.dragonCanteenDoses.get());
        ServerConfig.instance.put(ServerOptions.INFINITE_PURIFIED_WATER, SERVER.infinitePurifiedWater.get());
        ServerConfig.instance.put(ServerOptions.PURIFIED_WATER_OPACITY, SERVER.purifiedWaterOpacity.get());
        ServerConfig.instance.put(ServerOptions.SALT_WATER_THIRST, SERVER.saltWaterThirst.get());
    }
}