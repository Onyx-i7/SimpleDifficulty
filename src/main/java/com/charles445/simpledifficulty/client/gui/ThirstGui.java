package com.charles445.simpledifficulty.client.gui;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class ThirstGui {

    private final Minecraft minecraftInstance = Minecraft.getMinecraft();
    private final Random rand = new Random();
    private int updateCounter = 0;
    
    public static final ResourceLocation ICONS = new ResourceLocation("simpledifficulty:textures/gui/icons.png");
    public static final ResourceLocation THIRSTHUD = new ResourceLocation("simpledifficulty:textures/gui/thirsthud.png");
    
    // Position on the icons sheet
    private static final int texturepos_X = 0;
    private static final int texturepos_Y = 0;
    // Dimensions of the icon
    private static final int textureWidth = 9;
    private static final int textureHeight = 9;
    
    @SubscribeEvent
    public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == ElementType.AIR && QuickConfig.isThirstEnabled() && SDCompatibility.defaultThirstDisplay) {
            EntityPlayerSP player = minecraftInstance.player;
            if (player == null) {
                return;
            }

            IThirstCapability capability = SDCapabilities.getThirstData(player);
            if (capability == null) {
                return;
            }

            // Set the seed to avoid shaking during pausing
            rand.setSeed((long) (updateCounter * 445));
            
            boolean classic = ModConfig.client.classicHUDThirst;
            
            // Bind to custom icons image
            if (classic) {
                bind(ICONS);
            } else {
                bind(THIRSTHUD);
            }
            
            // Render thirst at the scaled resolution
            ScaledResolution resolution = event.getResolution();
            renderThirst(resolution.getScaledWidth(), resolution.getScaledHeight(), capability.getThirstLevel(), capability.getThirstSaturation());
            
            // Rebind to old icons image
            bind(Gui.ICONS);
            
            // Bump up the rendering height so air bubbles draw above thirst
            // TODO does this break any mods?
            GuiIngameForge.right_height += 10;
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Make sure game isn't paused as the GUI shouldn't be changing
            if (!minecraftInstance.isGamePaused()) {
                updateCounter++;
            }
        }
    }
    
    // Similar behavior to net.minecraftforge.client.GuiIngameForge.renderFood
    private void renderThirst(int width, int height, int thirst, float thirstSaturation) {
        EntityPlayerSP player = minecraftInstance.player;
        if (player == null) {
            return;
        }

        // thirst is 0 - 20
        GlStateManager.enableBlend();
        
        // Many mods set this and forget to set it back.
        // Setting it back pre-emptively because this has been reported with two mods.
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        
        int left = width / 2 + 82; // Same x offset as the hunger bar
        int top = height - GuiIngameForge.right_height;
        
        // Performance fix: Cache potion status before processing the loops
        boolean isThirsty = player.isPotionActive(SDPotions.thirsty);
        int xOffset = isThirsty ? (textureWidth * 4) : 0;
        int bgXOffset = isThirsty ? (textureWidth * 13) : 0;
        
        // Draw the 10 thirst bubbles
        for (int i = 0; i < 10; i++) {
            int halfIcon = i * 2 + 1;
            int x = left - i * 8;
            int y = top;
            
            // Shake based on saturation and thirst level
            if (thirstSaturation <= 0.0F && updateCounter % (thirst * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }
    
            // Background
            RenderUtil.drawTexturedModalRect(x, y, texturepos_X + bgXOffset, texturepos_Y, textureWidth, textureHeight);
            
            // Foreground
            if (halfIcon < thirst) { // Full
                RenderUtil.drawTexturedModalRect(x, y, texturepos_X + xOffset + (textureWidth * 4), texturepos_Y, textureWidth, textureHeight);
            } else if (halfIcon == thirst) { // Half
                RenderUtil.drawTexturedModalRect(x, y, texturepos_X + xOffset + (textureWidth * 5), texturepos_Y, textureWidth, textureHeight);
            }
        }
        
        // Draw the 10 saturation bubbles
        // Because AppleSkin is awesome and everybody knows it
        int thirstSaturationInt = (int) thirstSaturation;
        if (thirstSaturationInt > 0 && ModConfig.client.drawThirstSaturation) {
            for (int i = 0; i < 10; i++) {
                int halfIcon = i * 2 + 1;
                int x = left - i * 8;
                int y = top;
                
                // Foreground
                if (halfIcon < thirstSaturationInt) { // Full
                    RenderUtil.drawTexturedModalRect(x, y, texturepos_X + (textureWidth * 14), texturepos_Y, textureWidth, textureHeight);
                } else if (halfIcon == thirstSaturationInt) { // Half
                    RenderUtil.drawTexturedModalRect(x, y, texturepos_X + (textureWidth * 15), texturepos_Y, textureWidth, textureHeight);
                }
            }
        }
        
        GlStateManager.disableBlend();
    }
    
    private void bind(ResourceLocation resource) {
        minecraftInstance.getTextureManager().bindTexture(resource);
    }
}
