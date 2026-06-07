package com.charles445.simpledifficulty.compat.mod;

import java.lang.reflect.Method;

// IMPORTANT: I barely understand how I made this work but for those who want to modify it here is the Weather2 Remastered repo that was used to develop this code: "https://github.com/Mrbt0907/Weather2-Remastered/tree/1.12.2-2.9/src/main/java/net/mrbt0907/weather2/api" and this gave a lot of problems almost that I don't recommend editing it
public class Weather2Compat {

    private static boolean isWeather2Loaded = false;
    
    // Reflection Targets
    private static Method methodIsPrecipitatingAt;
    private static Method methodGetWindSpeed;
    private static Method methodIsWorldRaining;
    private static Method methodIsRainingAtPos;

    // Initializes reflection mappings. Call this from the main mod initialization phase to set up compatibility. Catches all exceptions to prevent crashes if Weather2 is absent
    public static void init() {
        try {
            Class<?> clazzWorld = Class.forName("net.minecraft.world.World");
            Class<?> clazzBlockPos = Class.forName("net.minecraft.util.math.BlockPos");
            Class<?> clazzLoader = Class.forName("net.minecraft.fml.common.Loader");

            // Map vanilla fallbacks
            methodIsWorldRaining = clazzWorld.getMethod("isRaining");
            methodIsRainingAtPos = clazzWorld.getMethod("isRainingAt", clazzBlockPos);

            // Dynamic mod detection
            Method methodIsModLoaded = clazzLoader.getMethod("isModLoaded", String.class);
            boolean modLoaded = (boolean) methodIsModLoaded.invoke(null, "weather2");

            if (modLoaded) {
                // Map Weather2 endpoints
                Class<?> clazzWeatherAPI = Class.forName("net.mrbt0907.weather2.api.WeatherAPI");
                methodIsPrecipitatingAt = clazzWeatherAPI.getMethod("isPrecipitatingAt", clazzWorld, clazzBlockPos);

                Class<?> clazzWindReader = Class.forName("net.mrbt0907.weather2.api.WindReader");
                Class<?> clazzVec3 = Class.forName("net.mrbt0907.weather2.util.Maths.Vec3");
                methodGetWindSpeed = clazzWindReader.getMethod("getWindSpeed", clazzWorld, clazzVec3);

                isWeather2Loaded = true;
            }
        } catch (Exception e) {
            isWeather2Loaded = false;
        }
    }

    // Dual-channel precipitation check. First checks vanilla rain, then Weather2 if available
    public static boolean isRainingAt(Object world, Object pos) {
        if (world == null || pos == null) return false;
        
        try {
            // Channel 1: Native Vanilla Rain
            if ((boolean) methodIsWorldRaining.invoke(world) && (boolean) methodIsRainingAtPos.invoke(world, pos)) {
                return true;
            }

            // Channel 2: Weather2 Check
            if (isWeather2Loaded) {
                return (boolean) methodIsPrecipitatingAt.invoke(null, world, pos);
            }
        } catch (Exception e) {
            try { return (boolean) methodIsRainingAtPos.invoke(world, pos); } catch (Exception ex) { return false; }
        }
        return false;
    }

    // Reads local dynamic wind speed from Weather2. Returns 0.0f if inactive.
    public static float getWindSpeedAt(Object world, Object pos) {
        if (!isWeather2Loaded || world == null || pos == null) return 0.0F;
        try {
            Class<?> clazzVec3 = Class.forName("net.mrbt0907.weather2.util.Maths.Vec3");
            int x = (int) pos.getClass().getMethod("getX").invoke(pos);
            int y = (int) pos.getClass().getMethod("getY").invoke(pos);
            int z = (int) pos.getClass().getMethod("getZ").invoke(pos);

            // Weather2 uses its own Vec3 wrapper for vectors
            Object weatherVec3 = clazzVec3.getConstructor(double.class, double.class, double.class)
                    .newInstance((double) x, (double) y, (double) z);

            return (float) methodGetWindSpeed.invoke(null, world, weatherVec3);
        } catch (Exception e) {
            return 0.0F;
        }
    }
}