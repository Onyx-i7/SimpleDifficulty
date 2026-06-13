package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public class ItemThermometer extends Item {

    protected static final Map<Integer, Long> hashAge = new HashMap<>();
    protected static final Map<Integer, Float> hashTemp = new HashMap<>();
    protected static long lastAudit = 0L;
    
    public ItemThermometer() {
        addPropertyOverride(new ResourceLocation("temperature"), (stack, world, pendingEntity) -> {
            boolean hasEntity = pendingEntity != null;
            Entity entity = hasEntity ? pendingEntity : stack.getItemFrame();
            
            if (world == null && entity != null) {
                world = entity.world;
            }
            
            if (world == null || entity == null) {
                return 0.0f;
            }
            
            if (QuickConfig.isTemperatureEnabled() && ModConfig.client.thermometer.enableThermometer) {
                return wobble(world, entity, stack.hashCode());
            }
            
            return 0.0f;
        });
    }

    @SideOnly(Side.CLIENT)
    protected static void audit(long worldTime) {
        if (worldTime - lastAudit >= 200 || worldTime < lastAudit) {
            lastAudit = worldTime;
            
            if (hashTemp.size() != hashAge.size()) {
                SimpleDifficulty.logger.warn("Thermometer audit had mismatched map sizes!");
                hashAge.clear();
                hashTemp.clear();
                return;
            }
            
            // Efficient high-performance removal using Java 8 lambda streams over entrySet
            hashAge.entrySet().removeIf(entry -> {
                boolean shouldRemove = (worldTime - entry.getValue() >= 100L || worldTime < entry.getValue());
                if (shouldRemove) {
                    hashTemp.remove(entry.getKey());
                }
                return shouldRemove;
            });
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static float wobble(World world, Entity entity, int hash) {
        Long age = hashAge.get(hash);
        Float temp = hashTemp.get(hash);
        
        long totalWorldTime = world.getTotalWorldTime();
        ItemThermometer.audit(totalWorldTime);
        
        if (age == null || temp == null) {
            hashAge.remove(hash);
            hashTemp.remove(hash);
            
            long currentWorldTime = world.getTotalWorldTime();
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
    
    @SideOnly(Side.CLIENT)
    private static float calculateTemperature(World world, Entity entity) {
        int tempRange = TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound() + 1;
        return (float) WorldUtil.calculateClientWorldEntityTemperature(world, entity) / (float) tempRange;
    }
}
