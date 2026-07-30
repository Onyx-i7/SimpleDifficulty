package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.api.config.IConfigOption;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Config;

import java.lang.reflect.Field;

public class ConfigSyncHelper {

    /**
     * It automatically reads all configuration variables and adds them to ServerConfig
     */
    public static void autoSyncServerConfig(Object configObject) {
        Class<?> clazz = configObject.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Config.Name nameAnnotation = field.getAnnotation(Config.Name.class);
            if (nameAnnotation != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(configObject);
                    
                    // Find the corresponding configuration option in the ServerOptions enumeration
                    IConfigOption option = findOptionByName(nameAnnotation.value());
                    if (option != null) {
                        ServerConfig.instance.put(option, String.valueOf(value));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (field.getType().isMemberClass()) {
                // If it is an internal class (such as ConfigThirst), apply recursion
                try {
                    field.setAccessible(true);
                    autoSyncServerConfig(field.get(configObject));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Automatically generates the NBTTagCompound for network synchronization
     */
    public static NBTTagCompound autoGenerateConfigNBT(Object configObject) {
        NBTTagCompound compound = new NBTTagCompound();
        buildNBTRecursive(configObject, compound);
        return compound;
    }

    private static void buildNBTRecursive(Object configObject, NBTTagCompound compound) {
        if (configObject == null) return; // Additional Security
        
        Class<?> clazz = configObject.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Config.Name nameAnnotation = field.getAnnotation(Config.Name.class);
            if (nameAnnotation != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(configObject);
                    compound.setString(nameAnnotation.value(), String.valueOf(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (field.getType().isMemberClass()) {
                // If it is an internal class (such as ConfigThirst), apply recursion
                try {
                    field.setAccessible(true);
                    Object innerObject = field.get(configObject);
                    if (innerObject != null) {
                        buildNBTRecursive(innerObject, compound);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Search the ServerOptions enumeration for the constant that matches the given name
     */
    private static IConfigOption findOptionByName(String name) {
        for (ServerOptions option : ServerOptions.values()) {
            if (option.getName().equals(name)) {
                return option;
            }
        }
        return null;
    }
}
