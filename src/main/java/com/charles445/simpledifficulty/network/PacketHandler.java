package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.SimpleChannel;
import net.minecraftforge.fml.network.simpleimpl.SimpleChannel;

import java.util.function.Function;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SimpleDifficulty.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;

        // Client-bound messages
        INSTANCE.registerMessage(id++, MessageUpdateThirst.class, MessageUpdateThirst::encode, MessageUpdateThirst::new, MessageUpdateThirst::handle);
        INSTANCE.registerMessage(id++, MessageUpdateTemperature.class, MessageUpdateTemperature::encode, MessageUpdateTemperature::new, MessageUpdateTemperature::handle);
        INSTANCE.registerMessage(id++, MessageUpdateConfig.class, MessageUpdateConfig::encode, MessageUpdateConfig::new, MessageUpdateConfig::handle);

        // Server-bound messages
        INSTANCE.registerMessage(id++, MessageDrinkWater.class, MessageDrinkWater::encode, MessageDrinkWater::new, MessageDrinkWater::handle);
        INSTANCE.registerMessage(id++, MessageConfigLAN.class, MessageConfigLAN::encode, MessageConfigLAN::new, MessageConfigLAN::handle);
    }
}