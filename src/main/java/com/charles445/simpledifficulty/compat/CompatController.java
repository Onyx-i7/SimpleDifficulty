package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.compat.mod.SereneSeasonsReflectionBridge;
import com.charles445.simpledifficulty.util.CompatUtil;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

/**
 * Controller for mod compatibility initialization.
 * Handles loading and registration of temperature modifiers from other mods.
 */
public class CompatController {
    private static final String COMPAT_MOD_PACKAGE = "com.charles445.simpledifficulty.compat.mod.";

    /**
     * Sets up common compatibility after mod initialization.
     */
    public static void setupCommonPostInit() {
        // Weather2 compatibility
        if (CompatUtil.canUseMod(ModNames.WEATHER2)) {
            try {
                Class.forName("com.charles445.simpledifficulty.compat.mod.Weather2Compat")
                        .getMethod("init")
                        .invoke(null);
                SimpleDifficulty.LOGGER.info("Weather2 Compatibility Bridge Initialized");
            } catch (Exception e) {
                SimpleDifficulty.LOGGER.error("Failed to initialize Weather2 bridge!", e);
            }
        }

        // Create standard compatibility objects
        Object auwDynamicModifier = newCompatObject(ModNames.AUW, COMPAT_MOD_PACKAGE + "AUWDynamicModifier");
        Object auwModifier = newCompatObject(ModNames.AUW, COMPAT_MOD_PACKAGE + "AUWModifier");
        Object baublesModifier = newCompatObject(ModNames.BAUBLES, COMPAT_MOD_PACKAGE + "BaublesModifier");
        Object betweenlandsHandler = newCompatObject(ModNames.BETWEENLANDS, COMPAT_MOD_PACKAGE + "BetweenlandsHandler");
        Object firstAidCompat = newCompatObject(ModNames.FIRSTAID, COMPAT_MOD_PACKAGE + "FirstAidCompat");
        Object harvestFestivalModifier = newCompatObject(ModNames.HARVESTFESTIVAL, COMPAT_MOD_PACKAGE + "HarvestFestivalModifier");
        Object inspirationsHandler = newCompatObject(ModNames.INSPIRATIONS, COMPAT_MOD_PACKAGE + "InspirationsHandler");
        Object oreExcavationHandler = newCompatObject(ModNames.OREEXCAVATION, COMPAT_MOD_PACKAGE + "OreExcavationHandler");
        Object sereneSeasonsModifier = newCompatObject(ModNames.SERENESEASONS, COMPAT_MOD_PACKAGE + "SereneSeasonsModifier");
        Object weather2Modifier = newCompatObject(ModNames.WEATHER2, COMPAT_MOD_PACKAGE + "Weather2Modifier");

        // Register modifiers
        if (auwDynamicModifier instanceof ITemperatureDynamicModifier && auwModifier instanceof ITemperatureModifier) {
            SimpleDifficulty.LOGGER.info("Armor Underwear Modifiers Enabled");
            TemperatureRegistry.registerDynamicModifier((ITemperatureDynamicModifier) auwDynamicModifier);
            TemperatureRegistry.registerModifier((ITemperatureModifier) auwModifier);
        }

        if (baublesModifier instanceof ITemperatureModifier) {
            SimpleDifficulty.LOGGER.info("Baubles Modifier Enabled");
            TemperatureRegistry.registerModifier((ITemperatureModifier) baublesModifier);
        }

        if (betweenlandsHandler != null) {
            SimpleDifficulty.LOGGER.info("The Betweenlands Handler Enabled");
        }

        if (harvestFestivalModifier instanceof ITemperatureModifier) {
            SimpleDifficulty.LOGGER.info("Harvest Festival Modifier Enabled");
            TemperatureRegistry.registerModifier((ITemperatureModifier) harvestFestivalModifier);
        }

        if (inspirationsHandler != null) {
            SimpleDifficulty.LOGGER.info("Inspirations Handler Enabled");
        }

        if (oreExcavationHandler != null) {
            SimpleDifficulty.LOGGER.info("OreExcavation Handler Enabled");
        }

        if (sereneSeasonsModifier instanceof ITemperatureModifier) {
            SimpleDifficulty.LOGGER.info("Serene Seasons Modifier Enabled");
            TemperatureRegistry.registerModifier((ITemperatureModifier) sereneSeasonsModifier);
        }

        if (weather2Modifier instanceof ITemperatureDynamicModifier) {
            SimpleDifficulty.LOGGER.info("Weather2 Dynamic Modifier Enabled");
            TemperatureRegistry.registerDynamicModifier((ITemperatureDynamicModifier) weather2Modifier);
        }

        SereneSeasonsReflectionBridge.init();
    }

    /**
     * Sets up client-side compatibility.
     */
    public static void setupClient() {
        // Client-side compatibility setup
    }

    /**
     * Creates a new compatibility object instance using reflection.
     *
     * @param modid The mod ID to check.
     * @param clazzpath The fully qualified class name.
     * @return The instance, or null if failed.
     */
    @Nullable
    public static Object newCompatObject(String modid, String clazzpath) {
        if (CompatUtil.canUseMod(modid)) {
            try {
                return Class.forName(clazzpath).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                SimpleDifficulty.LOGGER.error("Mod {} was loaded but object {} was not accessible!", modid, clazzpath, e);
            }
        }
        return null;
    }
}