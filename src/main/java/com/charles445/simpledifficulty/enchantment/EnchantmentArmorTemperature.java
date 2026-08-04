package com.charles445.simpledifficulty.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

/**
 * Enchantment that modifies the temperature effect of armor.
 * Can be either heating or cooling depending on the registry name.
 */
public class EnchantmentArmorTemperature extends Enchantment {

    // Cache slots array to prevent redundant heap allocations
    private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[] {
            EquipmentSlotType.CHEST,
            EquipmentSlotType.FEET,
            EquipmentSlotType.HEAD,
            EquipmentSlotType.LEGS
    };

    public EnchantmentArmorTemperature() {
        super(Rarity.COMMON, EnchantmentType.ARMOR, ARMOR_SLOTS);
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        if (ench == null) {
            return false;
        }

        // Ensure no two temperature-related armor enchantments can stack together
        return !(ench instanceof EnchantmentArmorTemperature) && super.checkCompatibility(ench);
    }
}