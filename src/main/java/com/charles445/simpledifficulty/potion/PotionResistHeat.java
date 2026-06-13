package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionResistHeat extends PotionBase {

    private final ResourceLocation texture;
    
    public PotionResistHeat() {
        super(false, 0xFFCD72);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("resist_heat");
        setBeneficial();
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }
    
    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity == null || SDPotions.hyperthermia == null) {
            return;
        }
        
        // Optimally clears hyperthermia through the base Potion Core compatibility filter
        removePotionCoreEffect(entity, SDPotions.hyperthermia);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        // Performance Optimization: Only run performEffect every 10 ticks (0.5 seconds) 
        // to reduce tick overhead on the server thread.
        return duration % 10 == 0;
    }
}
