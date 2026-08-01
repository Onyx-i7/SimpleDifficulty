package com.charles445.simpledifficulty.client.gui;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
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
    
    private static final int texturepos_X = 0;
    private static final int texturepos_Y = 0;
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

            rand.setSeed((long) (updateCounter * 445));
            
            boolean classic = ModConfig.client.classicHUDThirst;
            
            if (classic) {
                bind(ICONS);
            } else {
                bind(THIRSTHUD);
            }
            
            ScaledResolution resolution = event.getResolution();
            renderThirst(resolution.getScaledWidth(), resolution.getScaledHeight(), capability.getThirstLevel(), capability.getThirstSaturation());
            
            bind(Gui.ICONS);
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!minecraftInstance.isGamePaused()) {
                updateCounter++;
            }
        }
    }
    
    private void renderThirst(int width, int height, int thirst, float thirstSaturation) {
        EntityPlayerSP player = minecraftInstance.player;
        if (player == null) {
            return;
        }

        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        
        // Increment right_height FIRST so Forge's automatic spacing system handles the vertical offset correctly.
        GuiIngameForge.right_height += 10;
        
        // FIX: Base alignment (91) matches vanilla hunger bar. 
        // Config offsets (defaulting to -10 and 9) provide the perfect visual alignment above the hunger bar.
        int left = width / 2 + 91 + ClientConfig.instance.getInteger(ClientOptions.THIRST_HUD_X); 
        int top = height - GuiIngameForge.right_height + ClientConfig.instance.getInteger(ClientOptions.THIRST_HUD_Y); 
        
        boolean isThirsty = player.isPotionActive(SDPotions.thirsty);
        int xOffset = isThirsty ? (textureWidth * 4) : 0;
        int bgXOffset = isThirsty ? (textureWidth * 13) : 0;
        
        for (int i = 0; i < 10; i++) {
            int halfIcon = i * 2 + 1;
            int x = left - i * 8;
            int y = top;
            
            if (thirstSaturation <= 0.0F && updateCounter % (thirst * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }
    
            RenderUtil.drawTexturedModalRect(x, y, texturepos_X + bgXOffset, texturepos_Y, textureWidth, textureHeight);
            
            if (halfIcon < thirst) {
                RenderUtil.drawTexturedModalRect(x, y, texturepos_X + xOffset + (textureWidth * 4), texturepos_Y, textureWidth, textureHeight);
            } else if (halfIcon == thirst) {
                RenderUtil.drawTexturedModalRect(x, y, texturepos_X + xOffset + (textureWidth * 5), texturepos_Y, textureWidth, textureHeight);
            }
        }
        
        int thirstSaturationInt = (int) thirstSaturation;
        if (thirstSaturationInt > 0 && ModConfig.client.drawThirstSaturation) {
            for (int i = 0; i < 10; i++) {
                int halfIcon = i * 2 + 1;
                int x = left - i * 8;
                int y = top;
                
                if (halfIcon < thirstSaturationInt) {
                    RenderUtil.drawTexturedModalRect(x, y, texturepos_X + (textureWidth * 14), texturepos_Y, textureWidth, textureHeight);
                } else if (halfIcon == thirstSaturationInt) {
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
