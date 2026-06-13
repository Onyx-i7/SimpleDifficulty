package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class PotionThirsty extends PotionBase {

    private final ResourceLocation texture;
    
    public PotionThirsty() {
        super(true, 0x2B9500);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("thirsty");
    }
    
    @Override
    public ResourceLocation getTexture() {
        return texture;
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        // Performance Optimization: Process effect every 10 ticks (0.5 seconds) instead of every single tick
        // This significantly reduces server tick overhead when multiple players are affected.
        return duration % 10 == 0;
    }
    
    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLivingBaseIn;
            IThirstCapability capability = SDCapabilities.getThirstData(player);
            
            // Safety Check: Prevent NullPointerException during dimension transitions or player detachment
            if (capability == null) {
                return;
            }
            
            // Balance Compensation: Multiply exhaustion by 10 since the tick rate was reduced from 1 to 10
            float baseStrength = (float) ModConfig.server.thirst.thirstyStrength * 10.0F;
            capability.addThirstExhaustion(baseStrength * (1 + amplifier));
        }
    }
}
