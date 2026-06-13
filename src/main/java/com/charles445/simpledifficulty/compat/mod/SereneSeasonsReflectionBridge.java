package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

public class SereneSeasonsReflectionBridge {
    private static boolean initialized = false;
    private static boolean enabled = false;
    private static Method getFloatTemperatureMethod = null;

    public static void init() {
        if (initialized) return;
        initialized = true;

        if (Loader.isModLoaded("sereneseasons")) {
            try {
                Class<?> seasonASMHelperClass = Class.forName("sereneseasons.season.SeasonASMHelper");
                getFloatTemperatureMethod = seasonASMHelperClass.getDeclaredMethod("getFloatTemperature", World.class, Biome.class, BlockPos.class);
                
                enabled = true;
                SimpleDifficulty.logger.info("SereneSeasons Reflection Bridge successfully initialized for dynamic blocks.");
            } 
            catch (Exception e) {
                SimpleDifficulty.logger.error("SereneSeasons Reflection Bridge failed to bind methods! Dynamic blocks will fallback to vanilla temperatures.", e);
                enabled = false;
            }
        }
    }

    public static float getTemperatureSafe(World world, Biome biome, BlockPos pos) {
        if (!enabled || getFloatTemperatureMethod == null) {
            return biome.getTemperature(pos);
        }
        try {
            return (float) getFloatTemperatureMethod.invoke(null, world, biome, pos);
        } 
        catch (Exception e) {
            SimpleDifficulty.logger.error("Error invoking SeasonASMHelper via reflection. Disabling block temperature bridge.", e);
            enabled = false;
            return biome.getTemperature(pos);
        }
    }
}
