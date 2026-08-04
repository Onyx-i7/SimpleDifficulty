package com.charles445.simpledifficulty.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;

/**
 * Armor item that affects the player's temperature when worn.
 */
public class ItemArmorTemperature extends ArmorItem {

    /**
     * Creates a new temperature-affecting armor item.
     *
     * @param material The armor material.
     * @param equipmentSlot The equipment slot this armor occupies.
     * @param properties The item properties.
     */
    public ItemArmorTemperature(IArmorMaterial material, EquipmentSlotType equipmentSlot, Properties properties) {
        super(material, equipmentSlot, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }
}