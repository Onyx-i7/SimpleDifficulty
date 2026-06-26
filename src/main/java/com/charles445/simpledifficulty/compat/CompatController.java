package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.compat.mod.SereneSeasonsReflectionBridge;
import com.charles445.simpledifficulty.util.CompatUtil;

import javax.annotation.Nullable;

public class CompatController {
    private static final String compatMod = "com.charles445.simpledifficulty.compat.mod.";
    
    public static void setupCommonPostInit() {
        if (CompatUtil.canUseMod("weather2")) {
            try {
                Class.forName("com.charles445.simpledifficulty.compat.mod.Weather2Compat")
                    .getMethod("init")
                    .invoke(null);
                SimpleDifficulty.logger.info("Weather2 Compatibility Bridge Initialized");
            } catch (Exception e) {
                SimpleDifficulty.logger.error("Failed to initialize Weather2 bridge!", e);
            }
        }

        // Create standard compatibility objects
        Object auwDynamicModifier = newCompatObject(ModNames.AUW, compatMod + "AUWDynamicModifier");
        Object auwModifier = newCompatObject(ModNames.AUW, compatMod + "AUWModifier");
        Object baublesModifier = newCompatObject(ModNames.BAUBLES, compatMod + "BaublesModifier");
        Object betweenlandsHandler = newCompatObject(ModNames.BETWEENLANDS, compatMod + "BetweenlandsHandler");
        Object firstAidCompat = newCompatObject(ModNames.FIRSTAID, compatMod + "FirstAidCompat");
        Object harvestFestivalModifier = newCompatObject(ModNames.HARVESTFESTIVAL, compatMod + "HarvestFestivalModifier");
        Object inspirationsHandler = newCompatObject(ModNames.INSPIRATIONS, compatMod + "InspirationsHandler");
        Object oreExcavationHandler = newCompatObject(ModNames.OREEXCAVATION, compatMod + "OREEXCAVATIONHandler");
        Object sereneSeasonsModifier = newCompatObject(ModNames.SERENESEASONS, compatMod + "SereneSeasonsModifier");
        
        // Load Weather2 safely using the mod's dynamic reflection system
        Object weather2Modifier = newCompatObject("weather2", compatMod + "Weather2Modifier");
        
        if (auwDynamicModifier instanceof ITemperatureDynamicModifier && auwModifier instanceof ITemperatureModifier) {
            SimpleDifficulty.logger.info("Armor Underwear Modifiers Enabled");
            TemperatureRegistry.registerDynamicModifier((ITemperatureDynamicModifier)auwDynamicModifier);
            TemperatureRegistry.registerModifier((ITemperatureModifier)auwModifier);
        }
        
        if (baublesModifier instanceof ITemperatureModifier) {
            SimpleDifficulty.logger.info("Baubles Modifier Enabled");
            TemperatureRegistry.registerModifier((ITemperatureModifier)baublesModifier);
        }
        
        if (betweenlandsHandler != null) {
            SimpleDifficulty.logger.info("The Betweenlands Handler Enabled");
        }
        
        if (harvestFestivalModifier instanceof ITemperatureModifier) {
            SimpleDifficulty.logger.info("Harvest Festival Modifier Enabled");
            TemperatureRegistry.registerModifier((ITemperatureModifier)harvestFestivalModifier);
        }
        
        if (inspirationsHandler != null) {
            SimpleDifficulty.logger.info("Inspirations Handler Enabled");
        }
        
        if (oreExcavationHandler != null) {
            SimpleDifficulty.logger.info("OreExcavation Handler Enabled");
        }
        
        if (sereneSeasonsModifier instanceof ITemperatureModifier) {
            SimpleDifficulty.logger.info("Serene Seasons Modifier Enabled");
            TemperatureRegistry.registerModifier((ITemperatureModifier)sereneSeasonsModifier);
        }
        
        if (weather2Modifier instanceof ITemperatureDynamicModifier) {
            SimpleDifficulty.logger.info("Weather2 Dynamic Modifier Enabled");
            TemperatureRegistry.registerDynamicModifier((ITemperatureDynamicModifier)weather2Modifier);
        }

        SereneSeasonsReflectionBridge.init();
    }
    
    public static void setupClient() {
    }
    
    @Nullable
    public static Object newCompatObject(String modid, String clazzpath) {
        if (CompatUtil.canUseMod(modid)) {
            try {
                Object o = Class.forName(clazzpath).newInstance();
                return o;
            } catch (Exception e) {
                SimpleDifficulty.logger.error("Mod "+modid+" was loaded but object "+clazzpath+" was not accessible!", e);
            }
        }
        return null;
    }
}