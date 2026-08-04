package com.charles445.simpledifficulty.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Utility class for playing sounds in the world.
 */
public class SoundUtil {

    /**
     * Plays a sound for a player. Must be run on both sides to work as intended.
     *
     * @param player The player to play the sound for.
     * @param sound The sound event to play.
     */
    public static void commonPlayPlayerSound(PlayerEntity player, SoundEvent sound) {
        player.playSound(sound, 0.5f, 1.0f);
    }

    /**
     * Plays a block sound on the server side.
     *
     * @param world The world context.
     * @param pos The position to play the sound at.
     * @param sound The sound event to play.
     */
    public static void serverPlayBlockSound(World world, BlockPos pos, SoundEvent sound) {
        if (!world.isClientSide) {
            float volume = 0.75f;

            if (sound == SoundEvents.GENERIC_DRINK) {
                volume = 0.5f;
            }

            world.playSound(null, pos, sound, SoundCategory.BLOCKS, volume, 1.0f);
        }
    }

    /**
     * Plays a block sound on the server side with custom volume.
     *
     * @param world The world context.
     * @param pos The position to play the sound at.
     * @param sound The sound event to play.
     * @param volume The volume level.
     */
    public static void serverPlayBlockSound(World world, BlockPos pos, SoundEvent sound, float volume) {
        if (!world.isClientSide) {
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, volume, 1.0f);
        }
    }

    /**
     * Plays a block sound on the server side with custom volume and pitch.
     *
     * @param world The world context.
     * @param pos The position to play the sound at.
     * @param sound The sound event to play.
     * @param volume The volume level.
     * @param pitch The pitch level.
     */
    public static void serverPlayBlockSound(World world, BlockPos pos, SoundEvent sound, float volume, float pitch) {
        if (!world.isClientSide) {
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, volume, pitch);
        }
    }
}