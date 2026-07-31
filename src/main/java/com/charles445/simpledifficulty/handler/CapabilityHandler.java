package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.capability.TemperatureProvider;
import com.charles445.simpledifficulty.capability.ThirstProvider;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.network.MessageUpdateTemperature;
import com.charles445.simpledifficulty.network.MessageUpdateThirst;
import com.charles445.simpledifficulty.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class CapabilityHandler
{
	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent event)
	{
		//Find player
		if(event.getObject() instanceof EntityPlayer)
		{
			//EntityPlayer player = (EntityPlayer)event.getObject();
			
			//Attach capabilities
			
			//Attach temperature
			event.addCapability(new ResourceLocation(SimpleDifficulty.MODID, SDCapabilities.TEMPERATURE_IDENTIFIER), new TemperatureProvider(SDCapabilities.TEMPERATURE));
			
			//Attach thirst
			//event.addCapability(new ResourceLocation(SimpleDifficulty.MODID, SimpleDifficultyCapabilities.THIRST_IDENTIFIER), new GenericProvider(SimpleDifficultyCapabilities.THIRST));
			//DebugUtil.messageAll("Capability attached to player");
			event.addCapability(new ResourceLocation(SimpleDifficulty.MODID, SDCapabilities.THIRST_IDENTIFIER), new ThirstProvider(SDCapabilities.THIRST));
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		World world = player.world;
		
		if(world.isRemote)
		{
			
			//Clientside sprinting
			//This is better but you can still hold control
			/*
				if(QuickConfig.isThirstEnabled() && player.isSprinting() && ModCapabilities.getThirstData(player).getThirstLevel()<=6)
				player.setSprinting(false);
			*/		
			return;
		}
		
		//Server Side (assumes EntityPlayerMP from this point on)
		
		int packetTimerThreshold = ModConfig.server.miscellaneous.routinePacketDelay;
		
		//Update Temperature
		if(QuickConfig.isTemperatureEnabled())
		{
			ITemperatureCapability temperatureCapability = SDCapabilities.getTemperatureData(player);
			temperatureCapability.tickUpdate(player, world, event.phase);
			
			if(event.phase == TickEvent.Phase.START && (temperatureCapability.isDirty() || temperatureCapability.getPacketTimer()%packetTimerThreshold == 0))
			{
				temperatureCapability.setClean();
				sendTemperatureUpdate((EntityPlayerMP)player);
			}
		}
		
		//Update Thirst
		if(QuickConfig.isThirstEnabled() && !shouldPlayerSkipThirst(player))
		{
			IThirstCapability thirstCapability = SDCapabilities.getThirstData(player);
			thirstCapability.tickUpdate(player,world,event.phase);
			
			if(event.phase == TickEvent.Phase.START && (thirstCapability.isDirty() || thirstCapability.getPacketTimer()%packetTimerThreshold == 0))
			{
				thirstCapability.setClean();
				sendThirstUpdate((EntityPlayerMP)player);
			}
		}
	}
	
	private boolean shouldPlayerSkipThirst(EntityPlayer player)
	{
		//Skip thirst capability if player is in spectator or creative
		return player.isSpectator() || player.isCreative();
	}
	
	private void sendTemperatureUpdate(EntityPlayerMP player)
	{
		Capability capability = SDCapabilities.TEMPERATURE;
		
		//Make new message with new data
		MessageUpdateTemperature message = new MessageUpdateTemperature(capability.getStorage().writeNBT(capability, SDCapabilities.getTemperatureData(player), null));
		
		//Send to player
		PacketHandler.instance.sendTo(message, player);
	}
	
	private void sendThirstUpdate(EntityPlayerMP player)
	{
		//DebugUtil.messageAll("Player thirst has updated via sendThirstUpdate");
		Capability capability = SDCapabilities.THIRST;
		
		//Make new message with new data
		MessageUpdateThirst message = new MessageUpdateThirst(capability.getStorage().writeNBT(capability, SDCapabilities.getThirstData(player), null));
		
		//Send to player
		PacketHandler.instance.sendTo(message, player);
	}
}
