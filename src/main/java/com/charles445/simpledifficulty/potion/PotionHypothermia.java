package com.charles445.simpledifficulty.potion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionHypothermia extends PotionThermia {

    private final ResourceLocation texture;
    
    public PotionHypothermia() {
        super(true, 0x5CEBFF);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("hypothermia");
    }
    
    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void attackPlayer(EntityPlayer player, float damage, int amplifier) {
        if (player == null || amplifier < 4) {
            return;
        }

        // Performance Optimization: Check if effects are already active with significant remaining duration to block network spam
        PotionEffect activeSlowness = player.getActivePotionEffect(MobEffects.SLOWNESS);
        PotionEffect activeWeakness = player.getActivePotionEffect(MobEffects.WEAKNESS);
        
        if (activeSlowness != null && activeSlowness.getDuration() > 100 &&
            activeWeakness != null && activeWeakness.getDuration() > 100) {
            return;
        }

        // Math Optimization: Map dynamic amplifier tiers (4-5 -> 0, 6-7 -> 1, 8+ -> 2)
        int effectAmplifier = Math.min((amplifier - 4) / 2, 2);

        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 405, effectAmplifier));
        player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 405, effectAmplifier));
    }
}
