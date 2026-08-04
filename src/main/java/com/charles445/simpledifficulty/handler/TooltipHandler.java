package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.text.DecimalFormat;

/**
 * Handler for adding temperature information to item tooltips.
 */
public class TooltipHandler {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Adds temperature modifier information to armor tooltips.
     *
     * @param event The item tooltip event.
     */
    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        float tempTag = TemperatureUtil.getArmorTemperatureTag(event.getItemStack());

        if (tempTag != 0.0f) {
            // Has armor temperature tag
            if (tempTag > 0.0f) {
                event.getToolTip().add(TextFormatting.DARK_RED + " Temperature +" + df.format(tempTag));
            } else {
                event.getToolTip().add(TextFormatting.DARK_BLUE + " Temperature " + df.format(tempTag));
            }
        }
    }
}