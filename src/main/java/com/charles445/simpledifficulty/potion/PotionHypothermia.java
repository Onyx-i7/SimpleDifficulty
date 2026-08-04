package com.charles445.simpledifficulty.potion;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;

/**
 * Hypothermia effect - causes freezing damage, slowness and weakness at high amplifiers.
 */
public class PotionHypothermia extends PotionThermia {

    private final ResourceLocation texture;

    public PotionHypothermia() {
        super(EffectType.HARMFUL, 0x5CEBFF);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("hypothermia");
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void attackPlayer(PlayerEntity player, float damage, int amplifier) {
        if (player == null || amplifier < 4) {
            return;
        }

        // Performance Optimization: Check if effects are already active with significant remaining duration
        EffectInstance activeSlowness = player.getEffect(Effects.MOVEMENT_SLOWDOWN);
        EffectInstance activeWeakness = player.getEffect(Effects.WEAKNESS);

        if (activeSlowness != null && activeSlowness.getDuration() > 100 &&
                activeWeakness != null && activeWeakness.getDuration() > 100) {
            return;
        }

        // Math Optimization: Map dynamic amplifier tiers (4-5 -> 0, 6-7 -> 1, 8+ -> 2)
        int effectAmplifier = Math.min((amplifier - 4) / 2, 2);

        player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 405, effectAmplifier));
        player.addEffect(new EffectInstance(Effects.WEAKNESS, 405, effectAmplifier));
    }
}