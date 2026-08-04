package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifierGroupEnum;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/**
 * Handler for temperature-related events.
 */
public class TemperatureHandler {

    /**
     * Applies temperature effects when a player finishes consuming an item.
     *
     * @param event The item use finish event.
     */
    @SubscribeEvent
    public void onLivingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!QuickConfig.isTemperatureEnabled()) {
            return;
        }

        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            if (player.level.isClientSide) {
                return;
            }

            ItemStack stack = event.getItem();
            ResourceLocation regName = stack.getItem().getRegistryName();

            // Prevent NPE crash if registry name is null
            if (regName == null) {
                return;
            }

            List<JsonConsumableTemperature> consumableList = JsonConfig.consumableTemperature.get(regName.toString());

            if (consumableList != null) {
                for (JsonConsumableTemperature jct : consumableList) {
                    if (jct == null) {
                        continue;
                    }

                    if (jct.matches(stack)) {
                        SDCapabilities.getTemperatureData(player).setTemporaryModifier(jct.group, jct.temperature, jct.duration);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Applies rapid hypothermia changes if the player gets hit by dynamic hail blocks.
     *
     * @param event The living hurt event.
     */
    @SubscribeEvent
    public void onPlayerHurtByWeather(LivingHurtEvent event) {
        if (!QuickConfig.isTemperatureEnabled()) {
            return;
        }

        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            if (player.level.isClientSide) {
                return;
            }

            if ("hail".equals(event.getSource().getMsgId())) {
                SDCapabilities.getTemperatureData(player).setTemporaryModifier(
                        TemporaryModifierGroupEnum.DRINK.group(),
                        ModConfig.SERVER.wetValue.get(),
                        200
                );
            }
        }
    }
}