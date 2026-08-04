package com.charles445.simpledifficulty.client.gui;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.RenderUtil;
import com.charles445.simpledifficulty.util.WorldUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

/**
 * Renders the temperature HUD overlay on the player's screen.
 */
@OnlyIn(Dist.CLIENT)
public class TemperatureGui {

    private final Minecraft mc = Minecraft.getInstance();
    private final Random rand = new Random();
    private int updateCounter = 0;

    public static final ResourceLocation ICONS = new ResourceLocation("simpledifficulty", "textures/gui/icons.png");
    public static final ResourceLocation TEMPHUD = new ResourceLocation("simpledifficulty", "textures/gui/temphud.png");

    private static final int classicTexturePos_X = 0;
    private static final int classicTexturePos_Y = 32;
    private static final int classicTextureWidth = 16;
    private static final int classicTextureHeight = 16;

    private static final int modernTexturePos_X = 0;
    private static final int modernTexturePos_Y = 0;
    private static final int modernTextureWidth = 16;
    private static final int modernTextureHeight = 16;
    private static final int modernFeelPos_X = 0;
    private static final int modernFeelPos_Y = 16;
    private static final int modernFeelWidth = 32;
    private static final int modernFeelHeight = 32;
    private static final int modernArrowPos_X = 0;
    private static final int modernArrowPos_Y = 144;
    private static final int modernArrowFrames = 14;

    private int oldTemperature = -1;
    private int frameCounterClassic = -1;
    private int frameCounterModern = -1;
    private boolean risingTemperature = false;
    private boolean startAnimation = false;
    private boolean shakeSide = false;

    private static final int texturepos_Y_alt_OVR = 80;
    private static final int texturepos_Y_alt_BG = 96;
    private int alternateTemperature = 0;

    private int worldThermometerTemperature = 0;
    private boolean hasThermometer = false;
    private static final int texturepos_X_therm = 0;
    private static final int texturepos_Y_therm = 192;
    private static final int thermometer_per_row = 8;
    private static final int textureWidthTherm = 16;
    private static final int textureHeightTherm = 16;

    @SubscribeEvent
    public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == ElementType.ALL && QuickConfig.isTemperatureEnabled() && mc.gameMode != null && mc.gameMode.hasExperience()) {
            rand.setSeed((long) (updateCounter * 445));

            boolean classic = ModConfig.CLIENT.classicHUDTemperature.get();

            ClientPlayerEntity player = mc.player;
            if (player == null) return;

            ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
            if (capability == null) return;

            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();

            RenderSystem.enableBlend();

            MatrixStack matrixStack = event.getMatrixStack();

            if (classic) {
                bind(ICONS);
                renderClassicTemperatureIcon(matrixStack, width, height, capability.getTemperatureLevel());
            } else {
                bind(TEMPHUD);
                renderTemperatureIcon(matrixStack, width, height, capability.getTemperatureLevel());
            }

            RenderSystem.disableBlend();
            bind(AbstractGui.GUI_ICONS_LOCATION);
        }
    }

    private void renderTemperatureIcon(MatrixStack matrixStack, int width, int height, int temperature) {
        TemperatureEnum tempEnum = TemperatureUtil.getTemperatureEnum(temperature);

        int bgXOffset = getTempHudCoreBGX(temperature);
        int x = width / 2 - 8;
        int y = height - 54;
        int xOffset = 0;

        int shakeFrequency = getShakeFrequency(tempEnum, temperature);

        if (shakeFrequency > 0) {
            if (updateCounter % shakeFrequency == 0) {
                int shakeCheck = updateCounter / shakeFrequency;
                shakeSide = (shakeCheck % 2 == 0);
            }
            xOffset = shakeSide ? 1 : -1;
        }

        if (ModConfig.CLIENT.alternateTemp.get()) {
            RenderUtil.drawTexturedModalRect(matrixStack, x - 8 + xOffset, y - 8, modernFeelPos_X + this.getTempHudFeelBGX(alternateTemperature), modernFeelPos_Y + this.getTempHudFeelBGY(alternateTemperature), modernFeelWidth, modernFeelHeight);
            RenderUtil.drawTexturedModalRect(matrixStack, x + xOffset, y, modernTexturePos_X + bgXOffset, modernTexturePos_Y, modernTextureWidth, modernTextureHeight);
        } else {
            RenderUtil.drawTexturedModalRect(matrixStack, x + xOffset, y, modernTexturePos_X + bgXOffset, modernTexturePos_Y, modernTextureWidth, modernTextureHeight);
        }

        renderTemperatureChangeAnimation(matrixStack, false, x, y, temperature);
        renderThermometer(matrixStack, x, y);
    }

    private void renderClassicTemperatureIcon(MatrixStack matrixStack, int width, int height, int temperature) {
        TemperatureEnum tempEnum = TemperatureUtil.getTemperatureEnum(temperature);
        int bgXOffset = classicTextureWidth * tempEnum.ordinal();
        int x = width / 2 - 8;
        int y = height - 54;
        int xOffset = 0;

        int shakeFrequency = getShakeFrequency(tempEnum, temperature);

        if (shakeFrequency > 0) {
            if (updateCounter % shakeFrequency == 0) {
                int shakeCheck = updateCounter / shakeFrequency;
                shakeSide = (shakeCheck % 2 == 0);
            }
            xOffset = shakeSide ? 1 : -1;
        }

        if (ModConfig.CLIENT.alternateTemp.get()) {
            int outsideOffset = classicTextureWidth * TemperatureUtil.getTemperatureEnum(alternateTemperature).ordinal();
            RenderUtil.drawTexturedModalRect(matrixStack, x + xOffset, y, classicTexturePos_X + outsideOffset, texturepos_Y_alt_BG, classicTextureWidth, classicTextureHeight);
            RenderUtil.drawTexturedModalRect(matrixStack, x + xOffset, y, classicTexturePos_X + bgXOffset, texturepos_Y_alt_OVR, classicTextureWidth, classicTextureHeight);
        } else {
            RenderUtil.drawTexturedModalRect(matrixStack, x + xOffset, y, classicTexturePos_X + bgXOffset, classicTexturePos_Y, classicTextureWidth, classicTextureHeight);
        }

        renderTemperatureChangeAnimation(matrixStack, true, x, y, temperature);
        renderThermometer(matrixStack, x, y);
    }

    private void renderTemperatureChangeAnimation(MatrixStack matrixStack, boolean classic, int x, int y, int temperature) {
        if (oldTemperature == -1) {
            oldTemperature = temperature;
        }

        if (oldTemperature != temperature) {
            risingTemperature = oldTemperature < temperature;
            oldTemperature = temperature;
            startAnimation = true;
        }

        if (classic) {
            if (frameCounterClassic >= 0) {
                int ovrXOffset = classicTextureWidth * frameCounterClassic;
                int ovrYOffset = (risingTemperature ? 1 : 2) * classicTextureHeight;
                RenderUtil.drawTexturedModalRect(matrixStack, x, y, classicTexturePos_X + ovrXOffset, classicTexturePos_Y + ovrYOffset, classicTextureWidth, classicTextureHeight);
            }
        } else {
            if (frameCounterModern >= 0) {
                int ovrXOffset = (modernArrowFrames - frameCounterModern) * modernTextureWidth;
                int ovrYOffset = (risingTemperature ? 1 : 0) * modernTextureHeight;
                RenderUtil.drawTexturedModalRect(matrixStack, x, y, modernArrowPos_X + ovrXOffset, modernArrowPos_Y + ovrYOffset, modernTextureWidth, modernTextureHeight);
            }
        }
    }

    private void renderThermometer(MatrixStack matrixStack, int x, int y) {
        if (hasThermometer && ModConfig.CLIENT.hudThermometer.get() && ModConfig.CLIENT.enableThermometer.get()) {
            int therm_position = worldThermometerTemperature - TemperatureEnum.FREEZING.getLowerBound();
            int therm_x = (therm_position % thermometer_per_row) * textureWidthTherm + texturepos_X_therm;
            int therm_y = (therm_position / thermometer_per_row) * textureHeightTherm + texturepos_Y_therm;

            int therm_xOffset = ModConfig.CLIENT.hudThermometerX.get();
            int therm_yOffset = ModConfig.CLIENT.hudThermometerY.get();

            RenderUtil.drawTexturedModalRect(matrixStack, x + therm_xOffset, y - 18 + therm_yOffset, therm_x, therm_y, textureWidthTherm, textureHeightTherm);
        }
    }

    private int getTempHudFeelBGX(int temperature) {
        if (temperature < 6 || temperature >= 20) return 0;
        return ((temperature / 2) - 2) * modernFeelWidth;
    }

    private int getTempHudFeelBGY(int temperature) {
        if (temperature >= 20) return modernFeelHeight;
        return 0;
    }

    private int getTempHudCoreBGX(int temperature) {
        int bgx = 0;
        boolean animated = false;

        if (temperature < 6) {
            bgx = 0;
            animated = true;
        } else if (temperature >= 20) {
            bgx = 12 * modernTextureWidth;
            animated = true;
        } else {
            bgx = ((temperature / 2) + 2) * modernTextureWidth;
        }

        if (animated) {
            if (temperature < 12) {
                bgx += (this.updateCounter % 5) * modernTextureWidth;
            } else {
                bgx += (this.updateCounter % 4) * modernTextureWidth;
            }
        }
        return bgx;
    }

    private int getShakeFrequency(TemperatureEnum tempEnum, int temperature) {
        if (tempEnum == TemperatureEnum.FREEZING) {
            if (temperature == TemperatureEnum.FREEZING.getUpperBound()) {
                return 0;
            } else if (temperature > TemperatureEnum.FREEZING.getMiddle() + 1) {
                return 2;
            } else {
                return 1;
            }
        } else if (tempEnum == TemperatureEnum.BURNING) {
            if (temperature == TemperatureEnum.BURNING.getLowerBound()) {
                return 0;
            } else if (temperature >= TemperatureEnum.BURNING.getMiddle()) {
                return 1;
            } else {
                return 2;
            }
        }
        return 0;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!mc.isPaused()) {
                updateCounter++;

                if (frameCounterClassic >= 0) frameCounterClassic--;
                if (frameCounterModern >= 0) frameCounterModern--;

                if (startAnimation) {
                    frameCounterClassic = 11;
                    frameCounterModern = modernArrowFrames;
                    startAnimation = false;
                }

                if (updateCounter % 15 == 12 && QuickConfig.isTemperatureEnabled()) {
                    if (mc.player != null) {
                        PlayerEntity player = mc.player;
                        World world = player.level;

                        if (ModConfig.CLIENT.alternateTemp.get()) {
                            alternateTemperature = TemperatureUtil.clampTemperature(TemperatureUtil.getPlayerTargetTemperature(player));
                        }

                        if (ModConfig.CLIENT.hudThermometer.get() && ModConfig.CLIENT.enableThermometer.get()) {
                            worldThermometerTemperature = TemperatureUtil.clampTemperature((int) WorldUtil.calculateClientWorldEntityTemperature(world, player));
                            hasThermometer = player.inventory.contains(new ItemStack(SDItems.thermometer.get()));
                        }
                    }
                }
            }
        }
    }

    private void bind(ResourceLocation resource) {
        mc.getTextureManager().bind(resource);
    }
}