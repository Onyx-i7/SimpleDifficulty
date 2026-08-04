package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.SDEnchantments;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Temperature modifier based on worn armor pieces.
 * Considers enchantments, JSON configuration, and NBT temperature tags.
 */
public class ModifierArmor extends ModifierBase {
    public ModifierArmor() {
        super("Armor");
    }

    @Override
    public float getPlayerInfluence(PlayerEntity player) {
        float value = 0.0f;
        value += checkArmorSlot(player.getItemBySlot(EquipmentSlotType.HEAD));
        value += checkArmorSlot(player.getItemBySlot(EquipmentSlotType.CHEST));
        value += checkArmorSlot(player.getItemBySlot(EquipmentSlotType.LEGS));
        value += checkArmorSlot(player.getItemBySlot(EquipmentSlotType.FEET));
        return value;
    }

    private float checkArmorSlot(ItemStack stack) {
        if (stack.isEmpty())
            return 0.0f;

        float sum = 0.0f;

        // Enchantments
        if (ModConfig.SERVER.registerEnchantments.get()) {
            if (EnchantmentHelper.getItemEnchantmentLevel(SDEnchantments.chilling.get(), stack) > 0) {
                sum -= ModConfig.SERVER.enchantmentTemperature.get();
            } else if (EnchantmentHelper.getItemEnchantmentLevel(SDEnchantments.heating.get(), stack) > 0) {
                sum += ModConfig.SERVER.enchantmentTemperature.get();
            }
        }

        // Process JSON
        sum += processStackJSON(stack);

        // NBT temperature tag
        sum += TemperatureUtil.getArmorTemperatureTag(stack);

        return sum;
    }

    private float processStackJSON(ItemStack stack) {
        if (stack.getItem().getRegistryName() == null) return 0.0f;
        
        List<JsonTemperatureIdentity> armorList = JsonConfig.armorTemperatures.get(stack.getItem().getRegistryName().toString());

        if (armorList != null) {
            for (JsonTemperatureIdentity jtm : armorList) {
                if (jtm == null)
                    continue;

                if (jtm.matches(stack)) {
                    return jtm.temperature;
                }
            }
        }

        return 0.0f;
    }
}