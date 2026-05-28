package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.*;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.debug.DebugUtil;
import com.charles445.simpledifficulty.util.WorldUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class TemperatureCapability implements ITemperatureCapability
{
	private int temperature = 12;
	private int ticktimer = 0;
	private int damagecounter = 0;

	private final Map<String, TemporaryModifier> temporaryModifiers = new HashMap<>();

	//Unsaved data
	private int oldtemperature = 0;
	private int updatetimer = 500; //Update immediately first time around
	private int targettemp = 0;
	private int debugtimer = 0;
	private boolean manualDirty = false;
	private int oldmodifiersize = 0;
	private int packetTimer = 0;

	@Override
	public int getTemperatureLevel()
	{
		return temperature;
	}

	@Override
	public int getTemperatureTickTimer()
	{
		return ticktimer;
	}

	@Override
	public int getTemperatureDamageCounter()
	{
		return damagecounter;
	}

	@Override
	public void setTemperatureLevel(int temperature)
	{
		this.temperature = TemperatureUtil.clampTemperature(temperature);
	}

	@Override
	public void setTemperatureTickTimer(int ticktimer)
	{
		this.ticktimer=ticktimer;
	}

	@Override
	public void setTemperatureDamageCounter(int damagecounter)
	{
		this.damagecounter = damagecounter;
	}

	@Override
	public void addTemperatureLevel(int temperature)
	{
		this.setTemperatureLevel(this.getTemperatureLevel() + temperature);
	}

	@Override
	public void addTemperatureTickTimer(int ticktimer)
	{
		this.setTemperatureTickTimer(this.getTemperatureTickTimer() + ticktimer);
	}

	@Override
	public void addTemperatureDamageCounter(int damagecounter)
	{
		this.setTemperatureDamageCounter(this.getTemperatureDamageCounter() + damagecounter);
	}

	@Override
	public void tickUpdate(EntityPlayer player, World world, TickEvent.Phase phase)
	{
		//NOTE Player can be in Creative or Spectator

		if(phase==TickEvent.Phase.START)
		{
			packetTimer++;
			return;
		}

		//DEBUG START
		debugtimer++;
		if(debugtimer>=40 && ServerConfig.instance.getBoolean(ServerOptions.DEBUG))
		{
			debugtimer = 0;
			debugRoutine(player, world);

		}
		//DEBUG END

		//Every 5 ticks
		updatetimer++;
		if(updatetimer>=5)
		{
			updatetimer = 0;

			//Refresh the player's target temperature
			targettemp = TemperatureUtil.getPlayerTargetTemperature(player);
		}

		//Increment tick timer
		addTemperatureTickTimer(1);

		boolean appliedEffect = false;

		if(getTemperatureTickTimer() >= getTemperatureTickLimit())
		{
			setTemperatureTickTimer(0);
			//Clamp target first
			int destinationTemp = TemperatureUtil.clampTemperature(targettemp);
			if(getTemperatureLevel()!=destinationTemp)
			{
				//Temperature is actually different, so it needs to be incremented or decremented
				if(getTemperatureLevel() > destinationTemp)
					addTemperatureLevel(-1);
				else
					addTemperatureLevel(1);

				//DebugUtil.clientMessage(player, "Current Temperature: "+getTemperatureLevel());
			}

			//Effects

			TemperatureEnum tempEnum = getTemperatureEnum();
			if(tempEnum==TemperatureEnum.BURNING)
			{
				if(TemperatureEnum.BURNING.getMiddle() < getTemperatureLevel() && !player.isPotionActive(SDPotions.heat_resist) && !player.isSpectator() && !player.isCreative())
				{
					//Hyperthermia
					applyTemperatureEffect(player, SDPotions.hyperthermia, SDDamageSources.HYPERTHERMIA);
					appliedEffect = true;
				}
			}
			else if(tempEnum==TemperatureEnum.FREEZING)
			{
				if(TemperatureEnum.FREEZING.getMiddle() >= getTemperatureLevel() && !player.isPotionActive(SDPotions.cold_resist) && !player.isSpectator() && !player.isCreative())
				{
					//Hypothermia
					applyTemperatureEffect(player, SDPotions.hypothermia, SDDamageSources.HYPOTHERMIA);
					appliedEffect = true;
				}
			}
		}

		if(!appliedEffect)
		{
			if(this.getTemperatureDamageCounter() != 0)
			{
				//Test whether to reset the temperature damage counter
				boolean hasHypothermia = player.isPotionActive(SDPotions.hypothermia);
				boolean hasHyperthermia = player.isPotionActive(SDPotions.hyperthermia);
				if(!hasHypothermia && !hasHyperthermia)
				{
					//Reset the temperature damage counter
					this.setTemperatureDamageCounter(0);
				}
			}
		}

		//Process temporary modifiers with reduced allocation
		//Decrement duration and remove expired modifiers
		int activeModifierCount = 0;
		
		// Use iterator to avoid ConcurrentModificationException when removing
		Iterator<Map.Entry<String, TemporaryModifier>> iterator = temporaryModifiers.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry<String, TemporaryModifier> entry = iterator.next();
			TemporaryModifier tm = entry.getValue();
			
			if(tm.duration > 0)
			{
				tm.duration--;
				activeModifierCount++;
			}
			else
			{
				// Remove expired modifier
				iterator.remove();
			}
		}
		
		if(oldmodifiersize != activeModifierCount)
		{
			this.manualDirty = true;
		}
		
		oldmodifiersize = activeModifierCount;
	}

	private void debugRoutine(EntityPlayer player, World world)
	{
		//DebugUtil.clientMessage(player, ""+TemperatureUtil.getPlayerTargetTemperature(player));

		DebugUtil.clientMessage(player, "----------------");
		//BlockPos pos = player.getPosition();
		BlockPos pos = WorldUtil.getSidedBlockPos(world, player);

		float cumulative = 0;
		for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values())
		{
			float modsum = 0;
			long nanotime = System.nanoTime();
			modsum += modifier.getWorldInfluence(world, pos);
			modsum += modifier.getPlayerInfluence(player);
			long nanotime2 = System.nanoTime();
			DebugUtil.clientMessage(player, ""+(nanotime2-nanotime)+" : "+modifier.getName()+" - "+modsum);
			cumulative += modsum;
		}
		for(ITemperatureDynamicModifier dynmodifier : TemperatureRegistry.dynamicModifiers.values())
		{
			float oldcumulative = cumulative;
			long nanotime = System.nanoTime();
			cumulative = dynmodifier.applyDynamicWorldInfluence(world, pos, cumulative);
			cumulative = dynmodifier.applyDynamicPlayerInfluence(player, cumulative);
			long nanotime2 = System.nanoTime();
			DebugUtil.clientMessage(player, ""+(nanotime2-nanotime)+" : "+dynmodifier.getName()+" - "+(cumulative-oldcumulative));
		}

		DebugUtil.clientMessage(player, "( "+TemperatureUtil.getPlayerTargetTemperature(player)+ " )");
		DebugUtil.clientMessage(player, "TempTickLimit: "+getTemperatureTickLimit());
	}

	private int getTemperatureTickLimit()
	{
		//Get how quickly to change the player's temperature based on their current temperature and their target temperature

		//TAN clamps the targettemp, this doesn't

		int tickrange = ModConfig.server.temperature.temperatureTickMax - ModConfig.server.temperature.temperatureTickMin;
		//TODO phase this out also this is wrong
		int temprange = TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound();

		//Distance of temperature range
		int currentrange = Math.abs(getTemperatureLevel() - targettemp);

		//Whether player is escaping temperature danger
		boolean escapingDanger = getTemperatureLevel() <= targettemp ? getTemperatureEnum() == TemperatureEnum.FREEZING : getTemperatureEnum() == TemperatureEnum.BURNING;


		return Math.max(ModConfig.server.temperature.temperatureTickMin, ModConfig.server.temperature.temperatureTickMax - ((currentrange * tickrange) / temprange) - (escapingDanger ? ModConfig.server.temperature.temperatureTickDangerBoost : 0));
	}

	private void applyTemperatureEffect(EntityPlayer player, Potion potionIn, DamageSource damageSource)
	{
		int amplifier = 0;
		float existingModifier = 0.0F;

		IAttributeInstance attributeInstance = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);

		for (AttributeModifier modifier : attributeInstance.getModifiersByOperation(0)) {
			// Ignore Spice of Life: Carrot Edition's modifier
			if (!modifier.getName().equals("Health Gained from Trying New Foods"))
			{
				existingModifier += modifier.getAmount();
			}
		}

		if (player.getActivePotionEffect(potionIn) != null)
		{
			int activeAmplifier = Objects.requireNonNull(player.getActivePotionEffect(potionIn)).getAmplifier();

			if (player.getMaxHealth() - (existingModifier + activeAmplifier * 2.0F) - 2.0F > 2.0F
					&& attributeInstance.getModifiersByOperation(1).size() == 0
					&& attributeInstance.getModifiersByOperation(2).size() == 0)
			{
				amplifier = activeAmplifier + 1;
			}
			else
			{
				amplifier = activeAmplifier;
			}
		}

		player.removePotionEffect(potionIn);
		player.addPotionEffect(new PotionEffect(potionIn, ModConfig.server.temperature.temperatureDamageDuration, amplifier));
	}

	@Override
	public boolean isDirty()
	{
		return manualDirty || this.temperature!=this.oldtemperature;
	}

	@Override
	public void setClean()
	{
		this.oldtemperature = this.temperature;
		this.manualDirty = false;
	}

	@Override
	public TemperatureEnum getTemperatureEnum()
	{
		return TemperatureUtil.getTemperatureEnum(getTemperatureLevel());
	}

	@Override
	public ImmutableMap<String, TemporaryModifier> getTemporaryModifiers()
	{
		return ImmutableMap.copyOf(temporaryModifiers);
	}

	@Override
	public void setTemporaryModifier(String name, float temp, int duration)
	{
		//Prevent overriding with nothing or invalid results
		//TODO some manner of determining what overrides what? Not a big deal right now

		if(temp == 0.0f || !Float.isFinite(temp))
			return;

		if(this.temporaryModifiers.containsKey(name))
		{
			//Reset
			this.manualDirty = true;
		}
		this.temporaryModifiers.put(name, new TemporaryModifier(temp, duration));
	}

	@Override
	public void clearTemporaryModifiers()
	{
		this.temporaryModifiers.clear();
	}

	@Override
	public int getPacketTimer()
	{
		return packetTimer;
	}
}
