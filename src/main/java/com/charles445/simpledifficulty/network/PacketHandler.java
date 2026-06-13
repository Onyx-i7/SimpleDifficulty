package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(SimpleDifficulty.MODID);
    
    public static void init() {
        int id = 0; // Automatic dynamic incremental registration
        
        instance.registerMessage(MessageUpdateThirst.Handler.class, MessageUpdateThirst.class, id++, Side.CLIENT);
        instance.registerMessage(MessageDrinkWater.Handler.class, MessageDrinkWater.class, id++, Side.SERVER);
        instance.registerMessage(MessageConfigLAN.Handler.class, MessageConfigLAN.class, id++, Side.SERVER);
        instance.registerMessage(MessageUpdateConfig.Handler.class, MessageUpdateConfig.class, id++, Side.CLIENT);
        instance.registerMessage(MessageUpdateTemperature.Handler.class, MessageUpdateTemperature.class, id++, Side.CLIENT);
    }
}
