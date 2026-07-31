package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.enchantment.EnchantmentArmorTemperature;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import static com.charles445.simpledifficulty.api.SDEnchantments.*;

public class RegisterEnchantments
{
	@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID)
	public static class Registrar
	{
		@SubscribeEvent
		public static void registerEnchantments(RegistryEvent.Register<Enchantment> event)
		{
			IForgeRegistry<Enchantment> registry = event.getRegistry();
			
			//TODO name with mod initials? In case other mods want to use these names
			chilling = registerAs("chilling", new EnchantmentArmorTemperature(), registry);
			heating = registerAs("heating", new EnchantmentArmorTemperature(), registry);
		}
		
		private static Enchantment registerAs(String name, final Enchantment newEnchantment, IForgeRegistry<Enchantment> registry)
		{
			newEnchantment.setName(name);
			newEnchantment.setRegistryName(SimpleDifficulty.MODID,name);
			
			if(ModConfig.server.miscellaneous.registerEnchantments)
				registry.register(newEnchantment);
			
			//Add to map
			enchantments.put(name, newEnchantment);
			
			return newEnchantment;
		}
	}
}
