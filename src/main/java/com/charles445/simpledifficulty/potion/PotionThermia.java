package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class PotionThermia extends PotionBase {    

    public PotionThermia(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityPlayer) {
            World world = entity.getEntityWorld();
            EntityPlayer player = (EntityPlayer) entity;
            
            if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
                ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
                
                // Safety Check: Prevent crashes if capability data is temporarily missing or detached
                if (capability == null) {
                    return;
                }
                
                // Math Optimization: Cleaned up redundant float casting on configuration constants
                float scaling = (float) ModConfig.server.temperature.temperatureDamageScaling;
                float damage = 0.5F + (0.5F * capability.getTemperatureDamageCounter() * scaling);
                
                attackPlayer(player, damage, amplifier);
                capability.addTemperatureDamageCounter(1);
            }
        }
    }
    
    public abstract void attackPlayer(EntityPlayer player, float damage, int amplifier);
}
