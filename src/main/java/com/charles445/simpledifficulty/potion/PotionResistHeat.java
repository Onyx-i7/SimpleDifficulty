package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDPotions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

/**
 * Heat Resistance effect - clears hyperthermia when applied.
 */
public class PotionResistHeat extends EffectBase {

    private final ResourceLocation texture;

    public PotionResistHeat() {
        super(EffectType.BENEFICIAL, 0xFFCD72);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("resist_heat");
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity == null || SDPotions.hyperthermia == null) {
            return;
        }

        // Optimally clears hyperthermia through the base Potion Core compatibility filter
        removePotionCoreEffect(entity, SDPotions.hyperthermia.get());
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Performance Optimization: Only run applyEffectTick every 10 ticks (0.5 seconds)
        return duration % 10 == 0;
    }
}