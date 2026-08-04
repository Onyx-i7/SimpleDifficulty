package com.charles445.simpledifficulty.api.config;

import net.minecraft.nbt.CompoundNBT;

/**
 * Server-side configuration holder.
 * <p>
 * Example Usage:
 * <pre>
 * boolean serverDebug = ServerConfig.instance.getBoolean(ServerOptions.DEBUG);
 * </pre>
 */
public class ServerConfig extends ConfigBase {
    public static final ServerConfig instance = new ServerConfig();

    /**
     * (Internal use only)
     * Updates the ServerConfig instance with values from a CompoundNBT.
     *
     * @param compound The NBT compound containing configuration values.
     */
    public void updateValues(CompoundNBT compound) {
        for (String key : compound.getAllKeys()) {
            if (values.containsKey(key)) {
                String newValue = compound.getString(key);
                values.put(key, newValue);
            }
        }

        QuickConfig.updateValues();
    }
}