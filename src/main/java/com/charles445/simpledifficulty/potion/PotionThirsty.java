package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

/**
 * Thirsty effect - increases thirst exhaustion rate, making the player dehydrate faster.
 */
public class PotionThirsty extends EffectBase {

    private final ResourceLocation texture;

    public PotionThirsty() {
        super(EffectType.HARMFUL, 0x2B9500);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("thirsty");
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Performance Optimization: Process effect every 10 ticks (0.5 seconds)
        return duration % 10 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            IThirstCapability capability = SDCapabilities.getThirstData(player);

            // Safety Check: Prevent NullPointerException during dimension transitions
            if (capability == null) {
                return;
            }

            // Balance Compensation: Multiply exhaustion by 10 since tick rate was reduced from 1 to 10
            float baseStrength = (float) (ModConfig.SERVER.thirstyStrength.get() * 10.0D);
            capability.addThirstExhaustion(baseStrength * (1 + amplifier));
        }
    }
}