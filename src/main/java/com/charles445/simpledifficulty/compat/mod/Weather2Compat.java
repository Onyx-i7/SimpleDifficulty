package com.charles445.simpledifficulty.compat.mod;

import java.lang.reflect.Method;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// To modify this file, consult the Weather2 API. At the time of writing this comment, it is used for this code: https://github.com/Mrbt0907/Weather2-Remastered/tree/1.12.2-2.9/src/main/java/net/mrbt0907/weather2/api

public class Weather2Compat {

    private static boolean isWeather2Loaded = false;
    private static Method methodIsPrecipitatingAt;
    private static Method methodGetWindSpeed;
    private static java.lang.reflect.Constructor<?> vec3Constructor;

    public static void init() {
        try {
            Class<?> clazzLoader = Class.forName("net.minecraft.fml.common.Loader");
            Method methodIsModLoaded = clazzLoader.getMethod("isModLoaded", String.class);
            boolean modLoaded = (boolean) methodIsModLoaded.invoke(null, "weather2");

            if (modLoaded) {
                Class<?> clazzWeatherAPI = Class.forName("net.mrbt0907.weather2.api.WeatherAPI");
                methodIsPrecipitatingAt = clazzWeatherAPI.getMethod("isPrecipitatingAt", World.class, BlockPos.class);

                Class<?> clazzWindReader = Class.forName("net.mrbt0907.weather2.api.WindReader");
                Class<?> clazzVec3 = Class.forName("net.mrbt0907.weather2.util.Maths.Vec3");
                methodGetWindSpeed = clazzWindReader.getMethod("getWindSpeed", World.class, clazzVec3);
                
                vec3Constructor = clazzVec3.getConstructor(double.class, double.class, double.class);

                isWeather2Loaded = true;
            }
        } catch (Exception e) {
            isWeather2Loaded = false;
        }
    }
    
    public static boolean isRainingAt(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }

        if (isWeather2Loaded) {
            try {
                return (boolean) methodIsPrecipitatingAt.invoke(null, world, pos);
            } catch (Exception e) {
                return world.isRaining() && world.isRainingAt(pos);
            }
        }
        return world.isRaining() && world.isRainingAt(pos);
    }

    public static float getWindSpeedAt(World world, BlockPos pos) {
        if (!isWeather2Loaded || world == null || pos == null) {
            return 0.0F;
        }
        
        try {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();

            Object weatherVec3 = vec3Constructor.newInstance(x, y, z);
            return (float) methodGetWindSpeed.invoke(null, world, weatherVec3);
        } catch (Exception e) {
            return 0.0F;
        }
    }
}
