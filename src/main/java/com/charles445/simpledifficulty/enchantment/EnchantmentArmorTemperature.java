package com.charles445.simpledifficulty.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArmorTemperature extends Enchantment {

    // Cache slots array to prevent redundant heap allocations at class load time
    private static final EntityEquipmentSlot[] ARMOR_SLOTS = new EntityEquipmentSlot[] {
        EntityEquipmentSlot.CHEST,
        EntityEquipmentSlot.FEET,
        EntityEquipmentSlot.HEAD,
        EntityEquipmentSlot.LEGS
    };

    public EnchantmentArmorTemperature() {
        super(Enchantment.Rarity.COMMON, EnumEnchantmentType.ARMOR, ARMOR_SLOTS);
    }
    
    @Override
    protected boolean canApplyTogether(Enchantment ench) {
        if (ench == null) {
            return false;
        }
        
        // Ensure no two temperature-related armor enchantments can stack together, while respecting super regulations
        return !(ench instanceof EnchantmentArmorTemperature) && super.canApplyTogether(ench);
    }
}
