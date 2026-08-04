package com.charles445.simpledifficulty.api.config.json;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * JSON data class representing temperature effects for consumable items.
 */
public class JsonConsumableTemperature {
    public JsonItemIdentity identity;

    public String group;
    public float temperature;
    public int duration;

    public JsonConsumableTemperature(String group, float temperature, int duration) {
        this(group, temperature, duration, new JsonItemIdentity());
    }

    public JsonConsumableTemperature(String group, float temperature, int duration, String nbt) {
        this(group, temperature, duration, new JsonItemIdentity(nbt));
    }

    public JsonConsumableTemperature(String group, float temperature, int duration, JsonItemIdentity identity) {
        this.temperature = temperature;
        this.duration = duration;
        this.group = group.toLowerCase(Locale.ENGLISH);
        this.identity = identity;
    }

    // ============================================
    // Identity Matching
    // ============================================

    public boolean matches(ItemStack stack) {
        return identity.matches(stack);
    }

    public boolean matches(JsonItemIdentity sentIdentity) {
        return identity.matches(sentIdentity);
    }

    public boolean matches(@Nullable CompoundNBT compound) {
        return identity.matches(compound);
    }
}