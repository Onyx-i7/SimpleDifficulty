package com.charles445.simpledifficulty.compat.mod;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// To modify this file, consult the Weather2 API. At the time of writing this comment, it is used for this code: https://github.com/Mrbt0907/Weather2-Remastered/tree/1.12.2-2.9/src/main/java/net/mrbt0907/weather2/api

public class Weather2Compat {

    private static boolean isWeather2Loaded = false;
    private static Method methodIsPrecipitatingAt;
    private static Method methodGetWindSpeed;
    private static Method methodGetClosestWeather;
    private static java.lang.reflect.Constructor<?> vec3Constructor;
    
    // Advanced reflection variables to inspect storms based on WeatherEnum
    private static Method methodGetStormType; 
    private static Field fieldStormStage;
    private static Object enumTypeSandstorm;
    private static Object enumTypeBlizzard;

    public static void init() {
        try {
            Class<?> clazzLoader = Class.forName("net.minecraft.fml.common.Loader");
            Method methodIsModLoaded = clazzLoader.getMethod("isModLoaded", String.class);
            boolean modLoaded = (boolean) methodIsModLoaded.invoke(null, "weather2");

            if (modLoaded) {
                Class<?> clazzWeatherAPI = Class.forName("net.mrbt0907.weather2.api.WeatherAPI");
                methodIsPrecipitatingAt = clazzWeatherAPI.getMethod("isPrecipitatingAt", World.class, BlockPos.class);

                Class<?> clazzVec3 = Class.forName("net.mrbt0907.weather2.util.Maths.Vec3");
                vec3Constructor = clazzVec3.getConstructor(double.class, double.class, double.class);

                Class<?> clazzWeatherEnumTypeArray = Class.forName("[Lnet.mrbt0907.weather2.api.weather.WeatherEnum$Type;");
                methodGetClosestWeather = clazzWeatherAPI.getMethod("getClosestWeather", 
                    int.class, clazzVec3, double.class, int.class, int.class, clazzWeatherEnumTypeArray);

                Class<?> clazzWindReader = Class.forName("net.mrbt0907.weather2.api.WindReader");
                methodGetWindSpeed = clazzWindReader.getMethod("getWindSpeed", World.class, clazzVec3);

                Class<?> clazzWeatherObject = Class.forName("net.mrbt0907.weather2.weather.storm.WeatherObject");
                
                try {
                    methodGetStormType = clazzWeatherObject.getMethod("getType");
                } catch (NoSuchMethodException e) {
                    methodGetStormType = null;
                }

                try {
                    fieldStormStage = clazzWeatherObject.getField("stage");
                } catch (NoSuchFieldException e) {
                    fieldStormStage = null;
                }

                Class<?> clazzWeatherEnumType = Class.forName("net.mrbt0907.weather2.api.weather.WeatherEnum$Type");
                for (Object enumConstant : clazzWeatherEnumType.getEnumConstants()) {
                    String name = enumConstant.toString();
                    if (name.equalsIgnoreCase("SANDSTORM")) {
                        enumTypeSandstorm = enumConstant;
                    } else if (name.equalsIgnoreCase("BLIZZARD")) {
                        enumTypeBlizzard = enumConstant;
                    }
                }

                isWeather2Loaded = true;
            }
        } catch (Exception e) {
            isWeather2Loaded = false;
        }
    }
    
    public static boolean isLoaded() {
        return isWeather2Loaded;
    }

    /**
     * Checks if it's currently raining or precipitating at the given block position.
     * Unifies Weather2 checks and Vanilla rain checks robustly.
     */
    public static boolean isRainingAt(World world, BlockPos pos) {
        if (world == null || pos == null) return false;
        
        // Fix: Use the standard world.isRainingAt(pos) without over-restricting it 
        // with world.isRaining(), as some mod states decouple the global rain boolean.
        boolean vanillaRain = world.isRainingAt(pos);
        
        if (isWeather2Loaded) {
            try {
                // Check Weather2 API precipitation
                boolean weather2Rain = (boolean) methodIsPrecipitatingAt.invoke(null, world, pos);
                return weather2Rain || vanillaRain;
            } catch (Exception e) {
                return vanillaRain;
            }
        }
        return vanillaRain;
    }

    public static float getWindSpeedAt(World world, BlockPos pos) {
        if (!isWeather2Loaded || world == null || pos == null) return 0.0F;
        try {
            Object weatherVec3 = vec3Constructor.newInstance(pos.getX(), pos.getY(), pos.getZ());
            return (float) methodGetWindSpeed.invoke(null, world, weatherVec3);
        } catch (Exception e) {
            return 0.0F;
        }
    }

    public static int getThermalIntensityAt(World world, BlockPos pos) {
        if (!isWeather2Loaded || world == null || pos == null) return 0;

        try {
            int dim = world.provider.getDimension();
            Object weatherVec3 = vec3Constructor.newInstance(pos.getX(), pos.getY(), pos.getZ());
            Object closestStorm = methodGetClosestWeather.invoke(null, dim, weatherVec3, 250.0D, 0, 10, null);
            
            if (closestStorm != null) {
                Object stormType = null;
                
                if (methodGetStormType != null) {
                    stormType = methodGetStormType.invoke(closestStorm);
                } else {
                    try {
                        Field fieldType = closestStorm.getClass().getField("type");
                        stormType = fieldType.get(closestStorm);
                    } catch (Exception ex) { }
                }

                if (stormType != null) {
                    int stage = (fieldStormStage != null) ? fieldStormStage.getInt(closestStorm) : 1;
                    int severity = Math.max(1, stage);

                    if (stormType.equals(enumTypeSandstorm)) {
                        return 2 * severity;
                    }
                    
                    if (stormType.equals(enumTypeBlizzard)) {
                        return -2 * severity;
                    }
                }
            }
        } catch (Exception e) {
            // Stability fallback
        }
        return 0;
    }
}
