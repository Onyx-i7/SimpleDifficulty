package com.charles445.simpledifficulty.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy extends CommonProxy {
    // Participation Award
    
    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public EntityPlayer getClientMinecraftPlayer() {
        return null;
    }

    @Override
    public Boolean isClientConnectedToServer() {
        return null;
    }
}
