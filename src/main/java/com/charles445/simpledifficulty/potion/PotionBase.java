package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PotionBase extends Potion {

    // Offsets provided in case it becomes a good idea to switch to a sprite sheet format and not individual textures
    protected int xOffset = 0;
    protected int yOffset = 0;
    protected boolean drawHUD = true;
    protected boolean drawInventory = true;
    protected boolean drawInventoryText = true;
    
    public abstract ResourceLocation getTexture();
    
    public PotionBase() {
        super(false, 0xFFFFFF);
    }
    
    public PotionBase(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
    }
    
    protected ResourceLocation formatTexture(String s) {
        return new ResourceLocation(SimpleDifficulty.MODID, "textures/potions/" + s + ".png");
    }
    
    @Override
    public boolean hasStatusIcon() {
        return drawHUD;
    }
    
    public void removePotionCoreEffect(EntityLivingBase entity, final Potion potion) {
        // Potion Core Compatibility Optimization: Avoid allocation churn if checks pass safely
        if (entity.isPotionActive(potion)) {
            PotionEffect activeEffect = entity.getActivePotionEffect(potion);
            if (activeEffect != null && activeEffect.getDuration() > 1) {
                entity.removePotionEffect(potion);
                entity.addPotionEffect(new PotionEffect(potion, 1));
            }
        }
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        // Regeneration base tick rates
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        renderInventoryEffect(x, y, effect, Minecraft.getMinecraft());
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) { 
        if (getTexture() != null && mc != null) {
            mc.getTextureManager().bindTexture(getTexture());
            Gui.drawModalRectWithCustomSizedTexture(x + xOffset + 6, y + yOffset + 7, 0, 0, 18, 18, 18, 18);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
        renderHUDEffect(x, y, effect, Minecraft.getMinecraft(), alpha);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        if (getTexture() != null && mc != null) {
            mc.getTextureManager().bindTexture(getTexture());
            Gui.drawModalRectWithCustomSizedTexture(x + xOffset + 3, y + yOffset + 3, 0, 0, 18, 18, 18, 18);
        }
    }
    
    @Override
    public boolean shouldRenderHUD(PotionEffect effect) {
        return drawHUD;
    }
    
    @Override
    public boolean shouldRender(PotionEffect effect) { 
        return drawInventory; 
    }
    
    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return drawInventoryText;
    }
}
