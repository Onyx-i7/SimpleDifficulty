package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.item.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;

import static com.charles445.simpledifficulty.api.SDItems.*;

public class RegisterItems
{
	@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID)
	public static class Registrar
	{
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event)
		{
			SimpleDifficulty.logger.info("Registering Items");
			
			//Register armor materials first
			//EnumHelper.addArmorMaterial(name, textureName, durability, reductionAmounts, enchantability, soundOnEquip, toughness)
			// LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F),
			woolArmorMaterial = EnumHelper.addArmorMaterial("SDWOOL", "simpledifficulty:wool", 2, new int[]{1,1,1,1}, 5, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0f);
			woolArmorMaterial.repairMaterial = new ItemStack(Blocks.WOOL);
			SDItems.armorMaterials.put("woolArmorMaterial", woolArmorMaterial);
			
			iceArmorMaterial = EnumHelper.addArmorMaterial("SDICE", "simpleDifficulty:ice", 2, new int[]{1,1,1,1}, 5, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0f);
			iceArmorMaterial.repairMaterial = new ItemStack(SDItems.ice_chunk);
			SDItems.armorMaterials.put("iceArmorMaterial", iceArmorMaterial);
			
			//Item registration
			IForgeRegistry<Item> registry = event.getRegistry();
			
			purifiedWaterBottle = registerAs("purified_water_bottle", new ItemPurifiedWaterBottle(), registry);
			saltWaterBottle = registerAs("salt_water_bottle", new ItemSaltWaterBottle(), registry);
			juice = registerAs("juice", new ItemJuice(), registry);
			canteen = registerAs("canteen", new ItemCanteen(), registry);
			ironCanteen = registerAs("iron_canteen", new ItemIronCanteen(), registry);
			charcoalFilter = registerAs("charcoal_filter", new Item(), registry);
			
			ice_chunk = registerAs("ice_chunk", new Item(), registry);
			magma_chunk = registerAs("magma_chunk", new Item(), registry);
			
			thermometer = registerAs("thermometer", new ItemThermometer(), registry);
			
			wool_helmet = registerAs("wool_helmet", new ItemArmorTemperature(woolArmorMaterial, EntityEquipmentSlot.HEAD), registry);
			wool_chestplate = registerAs("wool_chestplate", new ItemArmorTemperature(woolArmorMaterial, EntityEquipmentSlot.CHEST), registry);
			wool_leggings = registerAs("wool_leggings", new ItemArmorTemperature(woolArmorMaterial, EntityEquipmentSlot.LEGS), registry);
			wool_boots = registerAs("wool_boots", new ItemArmorTemperature(woolArmorMaterial, EntityEquipmentSlot.FEET), registry);
			
			ice_helmet = registerAs("ice_helmet", new ItemArmorTemperature(iceArmorMaterial, EntityEquipmentSlot.HEAD), registry);
			ice_chestplate = registerAs("ice_chestplate", new ItemArmorTemperature(iceArmorMaterial, EntityEquipmentSlot.CHEST), registry);
			ice_leggings = registerAs("ice_leggings", new ItemArmorTemperature(iceArmorMaterial, EntityEquipmentSlot.LEGS), registry);
			ice_boots = registerAs("ice_boots", new ItemArmorTemperature(iceArmorMaterial, EntityEquipmentSlot.FEET), registry);
			
			//Extra Items
			
			for(Map.Entry<String, ExtraItem> entry : JsonConfigInternal.extraItems.entrySet())
			{
				String name = entry.getKey();
				ExtraItem extraItem = entry.getValue();
				
				if(extraItem.enabled)
				{
					switch(name)
					{	
						default: 
							SimpleDifficulty.logger.error("Tried to register invalid extra item: "+name);
							break;
					}
				}
			}
		}
		
		private static Item registerAs(String name, final Item newItem, IForgeRegistry<Item> registry)
		{
			//Register
			newItem.setRegistryName(SimpleDifficulty.MODID, name);
			newItem.setTranslationKey(newItem.getRegistryName().toString());
			newItem.setCreativeTab(ModCreativeTab.instance);
			registry.register(newItem);
			
			//Add to generic item map with settings
			SDItems.items.put(name, newItem);

			return newItem;
		}
	}
}
