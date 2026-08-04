package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.world.World;

/**
 * Abstract base class for temperature-related damage effects (Hyperthermia and Hypothermia).
 * Subclasses implement the specific attack behavior for each temperature extreme.
 */
public abstract class PotionThermia extends EffectBase {

    public PotionThermia(EffectType type, int liquidColor) {
        super(type, liquidColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            World world = entity.level;
            PlayerEntity player = (PlayerEntity) entity;

            if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
                ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);

                // Safety Check: Prevent crashes if capability data is temporarily missing
                if (capability == null) {
                    return;
                }

                float scaling = ModConfig.SERVER.temperatureDamageScaling.get().floatValue();
                float damage = 0.5F + (0.5F * capability.getTemperatureDamageCounter() * scaling);

                attackPlayer(player, damage, amplifier);
                capability.addTemperatureDamageCounter(1);
            }
        }
    }

    /**
     * Applies the specific attack behavior for this temperature effect.
     *
     * @param player The player to attack.
     * @param damage The calculated damage amount.
     * @param amplifier The effect amplifier.
     */
    public abstract void attackPlayer(PlayerEntity player, float damage, int amplifier);
}