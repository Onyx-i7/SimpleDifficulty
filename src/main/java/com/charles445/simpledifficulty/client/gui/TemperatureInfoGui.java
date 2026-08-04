package com.charles445.simpledifficulty.client.gui;

import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.TreeMap;

/**
 * Renders detailed temperature information on the screen for debugging/admin purposes.
 */
@OnlyIn(Dist.CLIENT)
public class TemperatureInfoGui {

    private final Minecraft mc = Minecraft.getInstance();
    private int updateCounter = 0;

    public int xPadding = 2;
    public int yPadding = 2;

    public int transparency = 0xDD000000;
    public int defaultColor = 0xFFFFFF | transparency;
    public int coldColor = 0x7777FF | transparency;
    public int hotColor = 0xFF7777 | transparency;

    public final Map<String, Float> resultMap = new TreeMap<>();
    public int resultCumulative = 0;

    @SubscribeEvent
    public void onPostRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == ElementType.TEXT && QuickConfig.isTemperatureEnabled() && ModConfig.CLIENT.temperatureReadout.get() && !mc.options.renderDebug) {
            if (mc.player == null) return;

            if (mc.player.hasPermissions(2) || mc.isLocalServer() || mc.player.isCreative()) {
                int width = event.getWindow().getGuiScaledWidth();
                int height = event.getWindow().getGuiScaledHeight();
                displayTemperature(event.getMatrixStack(), width, height);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            updateCounter++;

            if (updateCounter % 10 == 0 && ModConfig.CLIENT.temperatureReadout.get()) {
                updateTemperature();
            }
        }
    }

    public void updateTemperature() {
        if (mc.level == null || mc.player == null) return;

        World world = mc.level;
        PlayerEntity player = mc.player;
        BlockPos pos = WorldUtil.getSidedBlockPos(world, player);

        float cumulative = 0f;
        Map<String, Float> tempMap = new TreeMap<>();

        for (ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
            float result = modifier.getWorldInfluence(world, pos);
            result += modifier.getPlayerInfluence(player);
            tempMap.put(modifier.getName(), result);
            cumulative += result;
        }

        for (ITemperatureDynamicModifier modifier : TemperatureRegistry.dynamicModifiers.values()) {
            float oldCumulative = cumulative;
            cumulative = modifier.applyDynamicWorldInfluence(world, pos, cumulative);
            cumulative = modifier.applyDynamicPlayerInfluence(player, cumulative);
            tempMap.put(modifier.getName(), cumulative - oldCumulative);
        }

        synchronized (resultMap) {
            resultMap.clear();
            resultMap.putAll(tempMap);
            resultCumulative = (int) cumulative;
        }
    }

    public void displayTemperature(MatrixStack matrixStack, int width, int height) {
        RenderSystem.enableBlend();

        int yIncrement = mc.font.lineHeight;
        int xOffset = 0;
        int yOffset = 0;

        synchronized (resultMap) {
            for (Map.Entry<String, Float> entry : resultMap.entrySet()) {
                xOffset = 0;

                String name = entry.getKey();
                Float value = entry.getValue();

                int valueColor = defaultColor;
                if (value > 0.0f) {
                    valueColor = hotColor;
                } else if (value < 0.0f) {
                    valueColor = coldColor;
                }

                xOffset = mc.font.draw(matrixStack, name + ": ", xPadding + xOffset, yPadding + yOffset, defaultColor);
                mc.font.draw(matrixStack, String.valueOf(value), xPadding + xOffset, yPadding + yOffset, valueColor);

                yOffset += yIncrement;
            }
        }

        int valueColor = defaultColor;
        switch (TemperatureUtil.getTemperatureEnum(resultCumulative)) {
            case BURNING:
            case HOT:
                valueColor = hotColor;
                break;
            case COLD:
            case FREEZING:
                valueColor = coldColor;
                break;
            case NORMAL:
            default:
                break;
        }

        xOffset = 0;
        mc.font.draw(matrixStack, "---------", xPadding + xOffset, yPadding + yOffset, defaultColor);
        yOffset += yIncrement;

        xOffset = 0;
        xOffset = mc.font.draw(matrixStack, "Result: ", xPadding + xOffset, yPadding + yOffset, defaultColor);
        mc.font.draw(matrixStack, String.valueOf(resultCumulative), xPadding + xOffset, yPadding + yOffset, valueColor);
        yOffset += yIncrement;

        RenderSystem.disableBlend();
    }
}