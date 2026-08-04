package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

/**
 * Utility class for safely damaging players based on difficulty and configuration settings.
 */
public class DamageUtil {

    /**
     * Checks if the mod should apply dangerous effects (damage) based on world difficulty.
     *
     * @param world The world context.
     * @return true if the mod should apply damage, false if peaceful and peaceful danger is disabled.
     */
    public static boolean isModDangerous(World world) {
        // Return false only if the world is peaceful and peaceful danger config is false
        return world.getDifficulty() != Difficulty.PEACEFUL || ServerConfig.instance.getBoolean(ServerOptions.PEACEFUL_DANGER);
    }

    /**
     * Checks if the player's health is above the minimum threshold for their difficulty.
     * Prevents killing players on lower difficulties.
     *
     * @param world The world context.
     * @param player The player to check.
     * @return true if the player can safely take damage.
     */
    public static boolean healthAboveDifficulty(World world, PlayerEntity player) {
        Difficulty difficulty = world.getDifficulty();

        if (difficulty == Difficulty.HARD) {
            return true;
        } else if (difficulty == Difficulty.NORMAL && player.getHealth() > 1.0f) {
            return true;
        } else if (difficulty == Difficulty.EASY && player.getHealth() > 10.0f) {
            return true;
        } else if (difficulty == Difficulty.PEACEFUL && player.getHealth() > 10.0f) {
            return true;
        }

        return false;
    }
}