package com.charles445.simpledifficulty.api;

import java.util.ArrayList;
import java.util.List;

/**
 * API class for addon developers to control SimpleDifficulty's integration with their mods.
 * <p>
 * Use this class to disable built-in compatibility or default configurations if your mod
 * implements its own systems for thirst, temperature, or mod integration.
 * </p>
 *
 */
public class SDCompatibility {

    /**
     * Whether the default thirst display should be rendered by SimpleDifficulty.
     * <p>
     * Mods that replace or overlay the thirst display (e.g., custom HUDs) should set this
     * to {@code false} during {@code preInit} or {@code init} to prevent rendering conflicts.
     * </p>
     */
    public static boolean defaultThirstDisplay = true;

    /**
     * List of Mod IDs that have disabled their built-in JSON configuration in SimpleDifficulty.
     * <p>
     * <b>Do not modify this list directly.</b> Use {@link #disableBuiltInModJsonConfiguration(String)} instead.
     * </p>
     *
     * @see #disableBuiltInModJsonConfiguration(String)
     */
    public static final List<String> disabledDefaultJson = new ArrayList<>();

    /**
     * List of Mod IDs that have completely disabled built-in SimpleDifficulty compatibility.
     * <p>
     * <b>Do not modify this list directly.</b> Use {@link #disableBuiltInModCompatibility(String)} instead.
     * </p>
     *
     * @see #disableBuiltInModCompatibility(String)
     */
    public static final List<String> disabledCompletely = new ArrayList<>();

    /**
     * Disables the automatic JSON configuration generation for a specific mod.
     * <p>
     * Call this during {@code preInit} or {@code init} with your mod's Mod ID.
     * SimpleDifficulty will skip creating default JSON settings for your mod,
     * which is useful if you want to define your own defaults or handle configuration internally.
     * </p>
     *
     * @param modid The Mod ID of the mod to exclude from JSON configuration.
     */
    public static void disableBuiltInModJsonConfiguration(String modid) {
        if (modid != null && !modid.isEmpty()) {
            disabledDefaultJson.add(modid);
        }
    }

    /**
     * Completely disables built-in SimpleDifficulty compatibility for a specific mod.
     * <p>
     * Call this during {@code preInit} or {@code init} with your mod's Mod ID.
     * SimpleDifficulty will stop automatically integrating with your mod. This is useful
     * if you want to implement the mod compatibility logic entirely on your own.
     * </p>
     * <p>
     * <b>Note:</b> Calling this will also implicitly disable the built-in JSON configuration
     * for the specified mod.
     * </p>
     *
     * @param modid The Mod ID of the mod to exclude from compatibility.
     */
    public static void disableBuiltInModCompatibility(String modid) {
        if (modid != null && !modid.isEmpty()) {
            disabledDefaultJson.add(modid);
            disabledCompletely.add(modid);
        }
    }
}
