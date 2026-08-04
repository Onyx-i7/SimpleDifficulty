package com.charles445.simpledifficulty.potion;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;

/**
 * Hyperthermia effect - causes overheating damage and nausea at high amplifiers.
 */
public class PotionHyperthermia extends PotionThermia {

    private final ResourceLocation texture;

    public PotionHyperthermia() {
        super(EffectType.HARMFUL, 0xFFC85C);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("hyperthermia");
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

        // Performance Optimization: Prevent network and rendering spam if the player already has an active nausea effect
        EffectInstance activeNausea = player.getEffect(Effects.CONFUSION);
        if (activeNausea != null && activeNausea.getDuration() > 100) {
            return;
        }

        // Math Optimization: Calculate nausea amplifier dynamically based on hyperthermia tier
        // Tier 4-5 -> 0 | Tier 6-7 -> 1 | Tier 8+ -> 2
        int nauseaAmplifier = Math.min((amplifier - 4) / 2, 2);

        player.addEffect(new EffectInstance(Effects.CONFUSION, 405, nauseaAmplifier));
    }
}