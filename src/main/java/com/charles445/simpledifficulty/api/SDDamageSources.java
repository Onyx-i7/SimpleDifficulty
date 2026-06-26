package com.charles445.simpledifficulty.api;

import net.minecraft.util.DamageSource;

/**
 * Registry class for all custom damage sources added by SimpleDifficulty.
 * <p>
 * These damage sources are used when players take damage from environmental hazards
 * like dehydration, extreme temperatures, or parasites. Addons can use these to detect
 * specific causes of death or apply custom logic (e.g., preventing resurrection, 
 * modifying death messages, or triggering achievements).
 * </p>
 *
 */
public class SDDamageSources {

    /**
     * Damage source for dehydration (thirst reaching zero).
     * <p>
     * Properties:
     * <ul>
     *   <li>Bypasses armor (cannot be mitigated by armor)</li>
     *   <li>Absolute damage (not affected by resistance or protection enchantments)</li>
     * </ul>
     * </p>
     */
    public static final DamageSource DEHYDRATION = (new DamageSource("dehydration")).setDamageBypassesArmor().setDamageIsAbsolute();

    /**
     * Damage source for hyperthermia (body temperature too high).
     * <p>
     * Properties:
     * <ul>
     *   <li>Bypasses armor (cannot be mitigated by armor)</li>
     * </ul>
     * </p>
     */
    public static final DamageSource HYPERTHERMIA = (new DamageSource("hyperthermia")).setDamageBypassesArmor();

    /**
     * Damage source for hypothermia (body temperature too low).
     * <p>
     * Properties:
     * <ul>
     *   <li>Bypasses armor (cannot be mitigated by armor)</li>
     * </ul>
     * </p>
     */
    public static final DamageSource HYPOTHERMIA = (new DamageSource("hypothermia")).setDamageBypassesArmor();

    /**
     * Damage source for parasites (contaminated water or food).
     * <p>
     * Properties:
     * <ul>
     *   <li>Bypasses armor (cannot be mitigated by armor)</li>
     *   <li>Magic damage (treated as magical, can be blocked by certain effects)</li>
     * </ul>
     * </p>
     */
    public static final DamageSource PARASITES = (new DamageSource("parasites")).setDamageBypassesArmor().setMagicDamage();

    /**
     * Damage source for Inspirations mod compatibility (burning from hot cauldron).
     * <p>
     * Properties:
     * <ul>
     *   <li>Bypasses armor (cannot be mitigated by armor)</li>
     *   <li>Fire damage (treated as fire, can be mitigated by Fire Resistance)</li>
     * </ul>
     * </p>
     */
    public static final DamageSource INSPIRATIONS_CAULDRON_BURN = (new DamageSource("inspirationscauldronburn")).setDamageBypassesArmor().setFireDamage();
}
