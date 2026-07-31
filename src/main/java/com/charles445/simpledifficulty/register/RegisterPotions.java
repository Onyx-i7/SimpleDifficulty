package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.potion.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import static com.charles445.simpledifficulty.api.SDPotions.*;

public class RegisterPotions
{
	@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID)
	public static class Registrar
	{
		@SubscribeEvent
		public static void register(final RegistryEvent.Register<Potion> event)
		{
			final IForgeRegistry<Potion> registry = event.getRegistry();
			
			hyperthermia = registerAs("hyperthermia", new PotionHyperthermia(), registry, SharedMonsterAttributes.MAX_HEALTH, "8452B93A-62A1-4FD5-8A00-350FE3D1CFFF", -2.0D, 0);
			hypothermia = registerAs("hypothermia", new PotionHypothermia(), registry, SharedMonsterAttributes.MAX_HEALTH, "17FC672A-FBEF-4A20-8B7C-D8307C7E0258", -2.0D, 0);
			thirsty = registerAs("thirsty", new PotionThirsty(), registry);
			parasites = registerAs("parasites", new PotionParasites(), registry);
			
			cold_resist = registerAs("cold_resist", new PotionResistCold(), registry);
			heat_resist = registerAs("heat_resist", new PotionResistHeat(), registry);
		}
		
		@SubscribeEvent
		public static void registerTypes(final RegistryEvent.Register<PotionType> event)
		{
			final IForgeRegistry<PotionType> registry = event.getRegistry();
			
			cold_resist_type = registerTypeAs("cold_resist_type", SDPotions.cold_resist, ModConfig.server.miscellaneous.resistancePotionDurationShort, registry);
			long_cold_resist_type = registerTypeAs("long_cold_resist_type", SDPotions.cold_resist, ModConfig.server.miscellaneous.resistancePotionDurationLong, registry);
			
			heat_resist_type = registerTypeAs("heat_resist_type", SDPotions.heat_resist, ModConfig.server.miscellaneous.resistancePotionDurationShort, registry);
			long_heat_resist_type = registerTypeAs("long_heat_resist_type", SDPotions.heat_resist, ModConfig.server.miscellaneous.resistancePotionDurationLong, registry);
			
		}
		
		private static PotionType registerTypeAs(String name, final Potion potion, int duration, IForgeRegistry<PotionType> registry)
		{
			final PotionType potionType = new PotionType(new PotionEffect(potion,duration));
			potionType.setRegistryName(SimpleDifficulty.MODID, name);
			registry.register(potionType);
			
			SDPotions.potionTypes.put(name, potionType);
			
			return potionType;
		}
		
		private static Potion registerAs(String name, final Potion potion, IForgeRegistry<Potion> registry)
		{
			potion.setRegistryName(SimpleDifficulty.MODID,name);
			//TODO consider including modid to effect name
			potion.setPotionName("effect."+name);
			registry.register(potion);
			
			//Add to generic potion map with settings
			SDPotions.potions.put(name, potion);
			
			return potion;
		}

		private static Potion registerAs(String name, final Potion potion, IForgeRegistry<Potion> registry, IAttribute attribute, String uniqueId, double ammount, int operation)
		{
			potion.setRegistryName(SimpleDifficulty.MODID,name);
			//TODO consider including modid to effect name
			potion.setPotionName("effect."+name);
			registry.register(potion.registerPotionAttributeModifier(attribute, uniqueId, ammount, operation));

			//Add to generic potion map with settings
			SDPotions.potions.put(name, potion);

			return potion;
		}
	}
}
