package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Temperature modifier based on items held in main hand and offhand.
 */
public class ModifierHeldItems extends ModifierBase {
    public ModifierHeldItems() {
        super("HeldItems");
    }

    @Override
    public float getPlayerInfluence(PlayerEntity player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        float sum = 0.0f;

        if (!mainHand.isEmpty()) {
            sum += processStack(mainHand);
        }

        if (!offHand.isEmpty()) {
            sum += processStack(offHand);
        }

        return sum;
    }

    private float processStack(ItemStack stack) {
        if (stack.getItem().getRegistryName() == null) return 0.0f;
        
        List<JsonTemperatureIdentity> heldItemList = JsonConfig.heldItemTemperatures.get(stack.getItem().getRegistryName().toString());

        if (heldItemList != null) {
            for (JsonTemperatureIdentity jtm : heldItemList) {
                if (jtm == null)
                    continue;

                if (jtm.matches(stack)) {
                    return jtm.temperature;
                }
            }
        }

        return 0.0f;
    }
}