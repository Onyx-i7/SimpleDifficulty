package com.charles445.simpledifficulty.api.config.json;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

/**
 * JSON data class representing a temperature value associated with a specific item identity.
 * <p>
 * In 1.16.5, items no longer use metadata for variants. Identity is now determined by
 * the item's registry name and optionally its NBT compound tag.
 * </p>
 */
public class JsonTemperatureIdentity {
    public JsonItemIdentity identity;
    public float temperature;

    public JsonTemperatureIdentity(float temperature) {
        this(temperature, new JsonItemIdentity());
    }

    public JsonTemperatureIdentity(float temperature, String nbt) {
        this(temperature, new JsonItemIdentity(nbt));
    }

    public JsonTemperatureIdentity(float temperature, JsonItemIdentity identity) {
        this.temperature = temperature;
        this.identity = identity;
    }

    // ============================================
    // Identity Matching
    // ============================================

    /**
     * Checks if this temperature identity matches the given ItemStack.
     *
     * @param stack The ItemStack to check.
     * @return true if the identity matches.
     */
    public boolean matches(ItemStack stack) {
        return identity.matches(stack);
    }

    /**
     * Checks if this temperature identity matches another identity.
     *
     * @param sentIdentity The identity to compare against.
     * @return true if the identities match.
     */
    public boolean matches(JsonItemIdentity sentIdentity) {
        return identity.matches(sentIdentity);
    }

    /**
     * Checks if this temperature identity matches an ItemStack with specific NBT.
     *
     * @param compound The NBT compound to check against.
     * @return true if the identity matches.
     */
    public boolean matches(@Nullable CompoundNBT compound) {
        return identity.matches(compound);
    }
}