package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.SDEnchantments;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ModifierArmor extends ModifierBase
{
	public ModifierArmor()
	{
		super("Armor");
	}
	
	//Armor types, enchantments, and tags
	
	@Override
	public float getPlayerInfluence(EntityPlayer player)
	{
		float value = 0.0f;
		value += checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
		value += checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
		value += checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
		value += checkArmorSlot(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
		return value;
	}
	
	private float checkArmorSlot(ItemStack stack)
	{
		if(stack.isEmpty())
			return 0.0f;
		
		float sum = 0.0f;
		
		//Enchantments
		if(ModConfig.server.miscellaneous.registerEnchantments)
		{
			if(EnchantmentHelper.getEnchantmentLevel(SDEnchantments.chilling, stack) > 0)
			{
				sum -= ModConfig.server.temperature.enchantmentTemperature;
			}
			else if(EnchantmentHelper.getEnchantmentLevel(SDEnchantments.heating, stack) > 0)
			{
				sum += ModConfig.server.temperature.enchantmentTemperature;
			}
		}
		
		//Process JSON
		sum += processStackJSON(stack);
		
		//NBT
		sum += TemperatureUtil.getArmorTemperatureTag(stack);
		
		return sum;
	}
	
	private float processStackJSON(ItemStack stack)
	{
		List<JsonTemperatureIdentity> armorList = JsonConfig.armorTemperatures.get(stack.getItem().getRegistryName().toString());
		
		if(armorList!=null)
		{
			for(JsonTemperatureIdentity jtm : armorList)
			{
				if(jtm==null)
					continue;
				
				if(jtm.matches(stack))
				{
					return jtm.temperature;
				}
			}
		}
		
		return 0.0f;
	}
}
