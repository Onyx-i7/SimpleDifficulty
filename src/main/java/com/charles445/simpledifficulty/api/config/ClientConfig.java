package com.charles445.simpledifficulty.api.config;

/**
 * Client-side configuration holder.
 * <p>
 * Example Usage:
 * <pre>
 * boolean clientDebug = ClientConfig.instance.getBoolean(ClientOptions.DEBUG);
 * </pre>
 */
public class ClientConfig extends ConfigBase {
    public static final ClientConfig instance = new ClientConfig();
}