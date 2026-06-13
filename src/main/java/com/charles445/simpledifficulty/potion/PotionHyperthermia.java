package com.charles445.simpledifficulty.potion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionHyperthermia extends PotionThermia {

    private final ResourceLocation texture;
    
    public PotionHyperthermia() {
        super(true, 0xFFC85C);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("hyperthermia");
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

        // Performance Optimization: Prevent network and rendering spam if the player already has an active nausea effect
        PotionEffect activeNausea = player.getActivePotionEffect(MobEffects.NAUSEA);
        if (activeNausea != null && activeNausea.getDuration() > 100) {
            return;
        }

        // Math Optimization: Calculate nausea amplifier dynamically based on hyperthermia tier
        // Tier 4-5 -> 0 | Tier 6-7 -> 1 | Tier 8+ -> 2
        int nauseaAmplifier = Math.min((amplifier - 4) / 2, 2);
        
        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 405, nauseaAmplifier));
    }
}
