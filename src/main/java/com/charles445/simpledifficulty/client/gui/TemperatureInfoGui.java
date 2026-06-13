package com.charles445.simpledifficulty.client.gui;

import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;
import java.util.TreeMap;

public class TemperatureInfoGui {

    private final Minecraft mc = Minecraft.getMinecraft();
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
        if (event.getType() == ElementType.TEXT && QuickConfig.isTemperatureEnabled() && ModConfig.client.temperatureReadout && !mc.gameSettings.showDebugInfo) {
            // Safety Check: Avoid crash on initialization or world transits
            if (mc.player == null) {
                return;
            }
            
            // Check permissions safely
            if (mc.player.getPermissionLevel() >= 2 || mc.isSingleplayer() || mc.player.isCreative()) {
                ScaledResolution resolution = event.getResolution();
                displayTemperature(resolution.getScaledWidth(), resolution.getScaledHeight());
            }
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            updateCounter++;
            
            if (updateCounter % 10 == 0 && ModConfig.client.temperatureReadout) {
                updateTemperature();
            }
        }
    }
    
    public void updateTemperature() {
        if (mc.world == null || mc.player == null) {
            return;
        }
        
        World world = mc.world;
        EntityPlayer player = mc.player;
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
        
        // Atomic thread synchronization swap to avoid frame rendering glitches
        synchronized (resultMap) {
            resultMap.clear();
            resultMap.putAll(tempMap);
            resultCumulative = (int) cumulative;
        }
    }
    
    public void displayTemperature(int width, int height) {
        GlStateManager.enableBlend();
        
        int yIncrement = mc.fontRenderer.FONT_HEIGHT;
        int xOffset = 0;
        int yOffset = 0;
        
        // Prevent concurrent adjustments with safe map iteration blocks
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
                
                xOffset = mc.fontRenderer.drawString(name + ": ", xPadding + xOffset, yPadding + yOffset, defaultColor, true);
                mc.fontRenderer.drawString(String.valueOf(value), xPadding + xOffset, yPadding + yOffset, valueColor, true);
                
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
        mc.fontRenderer.drawString("---------", xPadding + xOffset, yPadding + yOffset, defaultColor, true);
        yOffset += yIncrement;

        xOffset = 0;
        xOffset = mc.fontRenderer.drawString("Result: ", xPadding + xOffset, yPadding + yOffset, defaultColor, true);
        mc.fontRenderer.drawString(String.valueOf(resultCumulative), xPadding + xOffset, yPadding + yOffset, valueColor, true);
        yOffset += yIncrement;
        
        GlStateManager.disableBlend();
    }
}
