package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Temperature modifier when the player is sprinting.
 * Sprinting generates body heat.
 */
public class ModifierSprint extends ModifierBase {
    public ModifierSprint() {
        super("Sprint");
    }

    @Override
    public float getPlayerInfluence(PlayerEntity player) {
        if (player.isSprinting())
            return ModConfig.SERVER.sprintingValue.get();
        else
            return 0.0f;
    }
}