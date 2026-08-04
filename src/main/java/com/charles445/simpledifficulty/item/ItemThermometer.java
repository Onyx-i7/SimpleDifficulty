package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thermometer item that displays the current temperature.
 */
public class ItemThermometer extends Item {

    protected static final Map<Integer, Long> hashAge = new ConcurrentHashMap<>();
    protected static final Map<Integer, Float> hashTemp = new ConcurrentHashMap<>();
    protected static long lastAudit = 0L;

    public ItemThermometer(Properties properties) {
        super(properties);
    }

    protected static void audit(long worldTime) {
        if (worldTime - lastAudit >= 200 || worldTime < lastAudit) {
            lastAudit = worldTime;

            if (hashTemp.size() != hashAge.size()) {
                SimpleDifficulty.LOGGER.warn("Thermometer audit had mismatched map sizes!");
                hashAge.clear();
                hashTemp.clear();
                return;
            }

            hashAge.entrySet().removeIf(entry -> {
                boolean shouldRemove = (worldTime - entry.getValue() >= 100L || worldTime < entry.getValue());
                if (shouldRemove) {
                    hashTemp.remove(entry.getKey());
                }
                return shouldRemove;
            });
        }
    }

    private static float wobble(World world, Entity entity, int hash) {
        Long age = hashAge.get(hash);
        Float temp = hashTemp.get(hash);

        long totalWorldTime = world.getGameTime();
        ItemThermometer.audit(totalWorldTime);

        if (age == null || temp == null) {
            hashAge.remove(hash);
            hashTemp.remove(hash);

            long currentWorldTime = world.getGameTime();
            hashAge.put(hash, currentWorldTime);
            float newTemp = calculateTemperature(world, entity);
            hashTemp.put(hash, newTemp);
            return newTemp;
        }

        if (totalWorldTime - age >= 10L + (hash & 7)) {
            float newTemp = calculateTemperature(world, entity);
            hashTemp.put(hash, newTemp);
            hashAge.put(hash, totalWorldTime);
            return newTemp;
        }

        return temp;
    }

    private static float calculateTemperature(World world, Entity entity) {
        int tempRange = TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound() + 1;
        return (float) WorldUtil.calculateClientWorldEntityTemperature(world, entity) / (float) tempRange;
    }
}