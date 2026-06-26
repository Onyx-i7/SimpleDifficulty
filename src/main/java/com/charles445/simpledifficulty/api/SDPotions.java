package com.charles445.simpledifficulty.api;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for all SimpleDifficulty potions and potion types.
 * <p>
 * This class holds references to all custom status effects ({@link Potion}) and their
 * corresponding brewing variants ({@link PotionType}) added by the mod. These fields are
 * initialized during the mod's registration phase and should not be modified by addons.
 * </p>
 *
 */
public class SDPotions {

    /**
     * A map of all registered SimpleDifficulty potions (status effects), keyed by their registry name.
     * <p>
     * <b>Do not modify this map directly.</b> It is intended for read-only access.
     * </p>
     */
    public static final Map<String, Potion> potions = new LinkedHashMap<>();

    /**
     * A map of all registered SimpleDifficulty potion types (brewing variants), keyed by their registry name.
     * <p>
     * Potion types define how a potion behaves when brewed (e.g., base duration, extended duration).
     * </p>
     * <p>
     * <b>Do not modify this map directly.</b> It is intended for read-only access.
     * </p>
     */
    public static final Map<String, PotionType> potionTypes = new LinkedHashMap<>();

    // ============================================
    // Negative / Environmental Potions
    // ============================================

    /**
     * The hyperthermia potion effect. Applied when the player's body temperature is dangerously high.
     */
    public static Potion hyperthermia;

    /**
     * The hypothermia potion effect. Applied when the player's body temperature is dangerously low.
     */
    public static Potion hypothermia;

    /**
     * The thirsty potion effect. Applied when the player's thirst level is critically low.
     */
    public static Potion thirsty;

    /**
     * The parasites potion effect. Applied when the player drinks contaminated water.
     * <p>
     * Causes continuous damage or negative effects until cured.
     * </p>
     */
    public static Potion parasites;

    // ============================================
    // Resistance Potions (Buffs)
    // ============================================

    /**
     * The cold resistance potion effect. Reduces the impact of cold temperatures on the player.
     */
    public static Potion cold_resist;

    /**
     * The heat resistance potion effect. Reduces the impact of high temperatures on the player.
     */
    public static Potion heat_resist;

    // ============================================
    // Potion Types (Brewing Variants)
    // ============================================

    /** Base duration brewing type for Cold Resistance. */
    public static PotionType cold_resist_type;

    /** Extended duration brewing type for Cold Resistance. */
    public static PotionType long_cold_resist_type;

    /** Base duration brewing type for Heat Resistance. */
    public static PotionType heat_resist_type;

    /** Extended duration brewing type for Heat Resistance. */
    public static PotionType long_heat_resist_type;
}
