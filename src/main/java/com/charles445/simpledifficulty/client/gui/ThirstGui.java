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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

/**
 * Renders the thirst HUD overlay.
 */
@OnlyIn(Dist.CLIENT)
public class ThirstGui {

    private final Minecraft minecraftInstance = Minecraft.getInstance();
    private final Random rand = new Random();
    private int updateCounter = 0;

    public static final ResourceLocation ICONS = new ResourceLocation("simpledifficulty", "textures/gui/icons.png");
    public static final ResourceLocation THIRSTHUD = new ResourceLocation("simpledifficulty", "textures/gui/thirsthud.png");

    private static final int texturepos_X = 0;
    private static final int texturepos_Y = 0;
    private static final int textureWidth = 9;
    private static final int textureHeight = 9;

    @SubscribeEvent
    public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == ElementType.AIR && QuickConfig.isThirstEnabled() && SDCompatibility.defaultThirstDisplay) {
            ClientPlayerEntity player = minecraftInstance.player;
            if (player == null) return;

            IThirstCapability capability = SDCapabilities.getThirstData(player);
            if (capability == null) return;

            rand.setSeed((long) (updateCounter * 445));

            boolean classic = ModConfig.CLIENT.classicHUDThirst.get();

            if (classic) {
                bind(ICONS);
            } else {
                bind(THIRSTHUD);
            }

            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();
            renderThirst(event.getMatrixStack(), width, height, capability.getThirstLevel(), capability.getThirstSaturation());

            bind(AbstractGui.GUI_ICONS_LOCATION);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!minecraftInstance.isPaused()) {
                updateCounter++;
            }
        }
    }

    private void renderThirst(MatrixStack matrixStack, int width, int height, int thirst, float thirstSaturation) {
        ClientPlayerEntity player = minecraftInstance.player;
        if (player == null) return;

        RenderSystem.enableBlend();

        int left = width / 2 + 82 + ClientConfig.instance.getInteger(ClientOptions.THIRST_HUD_X);
        int top = height - 49 + ClientConfig.instance.getInteger(ClientOptions.THIRST_HUD_Y);

        boolean isThirsty = player.hasEffect(SDPotions.thirsty.get());
        int xOffset = isThirsty ? (textureWidth * 4) : 0;
        int bgXOffset = isThirsty ? (textureWidth * 13) : 0;

        for (int i = 0; i < 10; i++) {
            int halfIcon = i * 2 + 1;
            int x = left - i * 8;
            int y = top;

            if (thirstSaturation <= 0.0F && updateCounter % (thirst * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }

            RenderUtil.drawTexturedModalRect(matrixStack, x, y, texturepos_X + bgXOffset, texturepos_Y, textureWidth, textureHeight);

            if (halfIcon < thirst) {
                RenderUtil.drawTexturedModalRect(matrixStack, x, y, texturepos_X + xOffset + (textureWidth * 4), texturepos_Y, textureWidth, textureHeight);
            } else if (halfIcon == thirst) {
                RenderUtil.drawTexturedModalRect(matrixStack, x, y, texturepos_X + xOffset + (textureWidth * 5), texturepos_Y, textureWidth, textureHeight);
            }
        }

        int thirstSaturationInt = (int) thirstSaturation;
        if (thirstSaturationInt > 0 && ModConfig.CLIENT.drawThirstSaturation.get()) {
            for (int i = 0; i < 10; i++) {
                int halfIcon = i * 2 + 1;
                int x = left - i * 8;
                int y = top;

                if (halfIcon < thirstSaturationInt) {
                    RenderUtil.drawTexturedModalRect(matrixStack, x, y, texturepos_X + (textureWidth * 14), texturepos_Y, textureWidth, textureHeight);
                } else if (halfIcon == thirstSaturationInt) {
                    RenderUtil.drawTexturedModalRect(matrixStack, x, y, texturepos_X + (textureWidth * 15), texturepos_Y, textureWidth, textureHeight);
                }
            }
        }

        RenderSystem.disableBlend();
    }

    private void bind(ResourceLocation resource) {
        minecraftInstance.getTextureManager().bind(resource);
    }
}