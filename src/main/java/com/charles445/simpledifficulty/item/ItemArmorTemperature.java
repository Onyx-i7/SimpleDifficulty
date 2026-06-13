package com.charles445.simpledifficulty.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemArmorTemperature extends ItemArmor {

    public ItemArmorTemperature(ArmorMaterial material, EntityEquipmentSlot equipmentSlot) {
        super(material, 0, equipmentSlot);
    }
    
    @Override
    public boolean hasOverlay(ItemStack stack) {
        return false;
    }
}
