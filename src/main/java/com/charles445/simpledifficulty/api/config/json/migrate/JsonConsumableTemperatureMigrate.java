package com.charles445.simpledifficulty.api.config.json.migrate;

import net.minecraft.item.ItemStack;

import java.util.Locale;

/**
 * Migration class for reading old 1.12.2 JSON configurations that used metadata.
 * Used internally during mod loading to convert legacy configurations.
 */
public class JsonConsumableTemperatureMigrate {
    public int metadata;
    public String group;
    public float temperature;
    public int duration;

    public JsonConsumableTemperatureMigrate(String group, float temperature, int metadata, int duration) {
        this.temperature = temperature;
        this.metadata = metadata;
        this.duration = duration;
        this.group = group.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Checks if this matches the given ItemStack (metadata-based matching for legacy support).
     *
     * @param stack The ItemStack to check.
     * @return true if metadata matches or is wildcard.
     */
    public boolean matches(ItemStack stack) {
        // In 1.16.5, metadata no longer exists, so we treat all items as matching
        // if the metadata was -1 or 32767 (wildcard), otherwise always return true
        // since we can't compare metadata anymore.
        return metadata == -1 || metadata == 32767;
    }

    /**
     * Checks if this matches the given metadata value.
     *
     * @param meta The metadata to check.
     * @return true if metadata matches or is wildcard.
     */
    public boolean matches(int meta) {
        return metadata == -1 || metadata == 32767 || metadata == meta;
    }
}