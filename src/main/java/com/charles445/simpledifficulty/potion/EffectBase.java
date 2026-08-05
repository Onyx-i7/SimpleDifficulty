package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base class for all custom SimpleDifficulty effects.
 * Handles custom texture rendering for HUD and inventory display.
 */
public abstract class EffectBase extends Effect {

    protected int xOffset = 0;
    protected int yOffset = 0;
    protected boolean drawHUD = true;
    protected boolean drawInventory = true;
    protected boolean drawInventoryText = true;

    public abstract ResourceLocation getTexture();

    public EffectBase(EffectType type, int liquidColor) {
        super(type, liquidColor);
    }

    protected ResourceLocation formatTexture(String s) {
        return new ResourceLocation(SimpleDifficulty.MODID, "textures/effect/" + s + ".png");
    }

    public void removePotionCoreEffect(LivingEntity entity, final Effect effect) {
        if (entity.hasEffect(effect)) {
            EffectInstance activeEffect = entity.getEffect(effect);
            if (activeEffect != null && activeEffect.getDuration() > 1) {
                entity.removeEffect(effect);
                entity.addEffect(new EffectInstance(effect, 1));
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return isReadyVar(duration, amplifier, 50);
    }

    public boolean isReadyVar(int duration, int amplifier, int var) {
        int k = var >> amplifier;
        if (k > 0) {
            return duration % k == 0;
        } else {
            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderInventoryEffect(EffectInstance effect, MatrixStack matrixStack, int x, int y, float z) {
        Minecraft mc = Minecraft.getInstance();
        if (getTexture() != null && mc != null) {
            mc.getTextureManager().bind(getTexture());
            AbstractGui.blit(matrixStack, x + xOffset + 6, y + yOffset + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderHUDEffect(EffectInstance effect, MatrixStack matrixStack, int x, int y, float z, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        if (getTexture() != null && mc != null) {
            mc.getTextureManager().bind(getTexture());
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);
            AbstractGui.blit(matrixStack, x + xOffset + 3, y + yOffset + 3, 0, 0, 18, 18, 18, 18);
        }
    }

    @Override
    public boolean shouldRenderHUD(EffectInstance effect) {
        return drawHUD;
    }

    @Override
    public boolean shouldRender(EffectInstance effect) {
        return drawInventory;
    }

    @Override
    public boolean shouldRenderInvText(EffectInstance effect) {
        return drawInventoryText;
    }
}