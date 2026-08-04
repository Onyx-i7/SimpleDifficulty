package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Parasites effect - causes exhaustion and periodic poison-like damage.
 */
public class PotionParasites extends EffectBase {

    private final ResourceLocation texture;

    public PotionParasites() {
        super(EffectType.HARMFUL, 0xFFE1B7);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("parasites");
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            World world = entity.level;
            PlayerEntity player = (PlayerEntity) entity;

            double hunger = ModConfig.SERVER.thirstParasitesHunger.get();

            // Hunger exhaustion application
            if (hunger > 0.0D) {
                player.getFoodData().addExhaustion((float) (hunger * (amplifier + 1)));
            }

            if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
                double poison = ModConfig.SERVER.thirstParasitesDamage.get();

                if (poison > 0.0D) {
                    // Thread Safety Fix: Retrieve duration directly from the entity
                    EffectInstance activeEffect = player.getEffect(this);
                    int currentDuration = activeEffect != null ? activeEffect.getDuration() : 0;

                    if (isReadyVar(currentDuration, amplifier, 25) && player.getRandom().nextDouble() < poison) {
                        player.hurt(SDDamageSources.PARASITES, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Always returns true because checking is safely processed inside applyEffectTick
        return true;
    }
}