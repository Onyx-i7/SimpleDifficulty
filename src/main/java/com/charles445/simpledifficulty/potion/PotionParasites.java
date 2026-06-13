package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PotionParasites extends PotionBase {

    private final ResourceLocation texture;

    public PotionParasites() {
        // Corrected bad effect assignment (true instead of false, as parasites are definitely harmful)
        super(true, 0xFFE1B7);
        this.xOffset = 0;
        this.yOffset = 0;
        this.texture = formatTexture("parasites");
    }
    
    @Override
    public ResourceLocation getTexture() {
        return texture;
    }
    
    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityPlayer) {
            World world = entity.getEntityWorld();
            EntityPlayer player = (EntityPlayer) entity;
            
            double hunger = ModConfig.server.thirst.thirstParasitesHunger;
            
            // Hunger exhaustion application
            if (hunger > 0.0D) {
                player.addExhaustion((float) (hunger * (amplifier + 1)));
            }
            
            if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
                double poison = ModConfig.server.thirst.thirstParasitesDamage;
            
                if (poison > 0.0D) {
                    // Performance & Thread Safety Fix: Retreive duration directly from the entity 
                    // instead of using a global shared variable instance which causes multiplayer desyncs.
                    PotionEffect activeEffect = player.getActivePotionEffect(this);
                    int currentDuration = activeEffect != null ? activeEffect.getDuration() : 0;

                    if (isReadyVar(currentDuration, amplifier, 25) && player.getRNG().nextDouble() < poison) {
                        player.attackEntityFrom(SDDamageSources.PARASITES, 1.0F);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        // Optimized: Always returns true because checking and filtering logic is now safely processed 
        // inside performEffect per-player instance.
        return true;
    }
}
