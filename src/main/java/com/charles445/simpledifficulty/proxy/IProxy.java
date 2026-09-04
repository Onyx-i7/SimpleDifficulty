package com.charles445.simpledifficulty.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public interface IProxy 
{
	void preInit();
	
	void init();
	
	void postInit();
	
	Side getSide();
	
	@Nullable
    EntityPlayer getClientMinecraftPlayer();
	
	@Nullable
    Boolean isClientConnectedToServer();
	
	void spawnClientParticle(World world, String type, double xPos, double yPos, double zPos, double motionX, double motionY, double motionZ);
}
