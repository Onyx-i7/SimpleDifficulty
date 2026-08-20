package com.charles445.simpledifficulty.compat.mod;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.mrbt0907.weather2.api.WeatherAPI;
import net.mrbt0907.weather2.api.WindReader;
import net.mrbt0907.weather2.api.weather.WeatherEnum;
import net.mrbt0907.weather2.util.Maths.Vec3;
import net.mrbt0907.weather2.weather.storm.WeatherObject;

public class Weather2Compat {

    private static boolean isWeather2Loaded = false;

    public static void init() {
        isWeather2Loaded = Loader.isModLoaded("weather2");
    }

    public static boolean isLoaded() {
        return isWeather2Loaded;
    }

    public static boolean isRainingAt(World world, BlockPos pos) {
        if (world == null || pos == null)
            return false;

        boolean vanillaRain = world.isRainingAt(pos);

        if (isWeather2Loaded) {
            try {
                boolean weather2Rain = WeatherAPI.isPrecipitatingAt(world, pos);
                return weather2Rain || vanillaRain;
            } catch (Exception e) {
                return vanillaRain;
            }
        }
        return vanillaRain;
    }

    public static float getWindSpeedAt(World world, BlockPos pos) {
        if (!isWeather2Loaded || world == null || pos == null)
            return 0.0F;
        try {
            Vec3 weatherVec3 = new Vec3(pos.getX(), pos.getY(), pos.getZ());
            return WindReader.getWindSpeed(world, weatherVec3);
        } catch (Exception e) {
            return 0.0F;
        }
    }

    public static int getThermalIntensityAt(World world, BlockPos pos) {
        if (!isWeather2Loaded || world == null || pos == null)
            return 0;

        try {
            int dim = world.provider.getDimension();
            Vec3 weatherVec3 = new Vec3(pos.getX(), pos.getY(), pos.getZ());
            WeatherObject closestStorm = WeatherAPI.getClosestWeather(dim, weatherVec3, 250.0D, 0, 10);

            if (closestStorm != null) {
                WeatherEnum.Type stormType = closestStorm.type;

                if (stormType == WeatherEnum.Type.SANDSTORM) {
                    return 2;
                }
                if (stormType == WeatherEnum.Type.BLIZZARD) {
                    return -2;
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }
}