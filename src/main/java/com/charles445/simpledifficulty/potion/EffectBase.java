package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.potion.EffectType;
import net.minecraft.client.gui.screen.Screen;

/**
 * Base class for all custom SimpleDifficulty effects.
 * Handles custom texture rendering for HUD and inventory display.
 */
public abstract class EffectBase extends Effect {

    // Offsets for texture positioning (useful if migrating to sprite sheets later)
    protected int xOffset = 0;
    protected int yOffset = 0;
    protected boolean drawHUD = true;
    protected boolean drawInventory = true;
    protected boolean drawInventoryText = true;

    /**
     * Gets the custom texture location for this effect.
     *
     * @return The texture ResourceLocation.
     */
    public abstract ResourceLocation getTexture();

    public EffectBase(EffectType type, int liquidColor) {
        super(type, liquidColor);
    }

    protected ResourceLocation formatTexture(String s) {
        return new ResourceLocation(SimpleDifficulty.MODID, "textures/potions/" + s + ".png");
    }

    /**
     * Removes a potion effect while maintaining compatibility with potion core systems.
     *
     * @param entity The entity to remove the effect from.
     * @param effect The effect to remove.
     */
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
        // Default: Regeneration-style tick rate
        return isReadyVar(duration, amplifier, 50);
    }

    /**
     * Helper method for variable tick rate calculations.
     *
     * @param duration The current duration.
     * @param amplifier The effect amplifier.
     * @param var The base tick rate variable.
     * @return true if the effect should tick this frame.
     */
    public boolean isReadyVar(int duration, int amplifier, int var) {
        int k = var >> amplifier;
        if (k > 0) {
            return duration % k == 0;
        } else {
            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderInventoryEffect(EffectInstance effect, int x, int y, float z, MatrixStack matrixStack, Screen gui) {
        Minecraft mc = Minecraft.getInstance();
        if (getTexture() != null && mc != null) {
            mc.getTextureManager().bind(getTexture());
            AbstractGui.blit(matrixStack, x + xOffset + 6, y + yOffset + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHUDEffect(MatrixStack matrixStack, int x, int y, float z, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        if (getTexture() != null && mc != null) {
            mc.getTextureManager().bind(getTexture());
            RenderSystem.color(1.0f, 1.0f, 1.0f, alpha);
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