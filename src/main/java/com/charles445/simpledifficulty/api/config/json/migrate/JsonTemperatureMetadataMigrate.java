package com.charles445.simpledifficulty.api.config.json.migrate;

import net.minecraft.item.ItemStack;

/**
 * Migration class for reading old 1.12.2 JSON configurations that used metadata.
 */
public class JsonTemperatureMetadataMigrate {
    public int metadata;
    public float temperature;

    public JsonTemperatureMetadataMigrate(int metadata, float temperature) {
        this.metadata = metadata;
        this.temperature = temperature;
    }

    public boolean matches(ItemStack stack) {
        return metadata == -1 || metadata == 32767;
    }

    public boolean matches(int meta) {
        return metadata == -1 || metadata == 32767 || metadata == meta;
    }
}