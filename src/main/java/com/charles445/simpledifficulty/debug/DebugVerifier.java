package com.charles445.simpledifficulty.debug;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.*;

public class DebugVerifier {

    public void verify() {
        SimpleDifficulty.logger.debug("Running DebugVerifier");
        
        test(SDItems.canteen, SDItems.items.get("canteen"));
        test(SDItems.charcoalFilter, SDItems.items.get("charcoal_filter"));
        test(SDItems.juice, SDItems.items.get("juice"));
        test(SDItems.purifiedWaterBottle, SDItems.items.get("purified_water_bottle"));
        
        test(SDItems.ice_chunk, SDItems.items.get("ice_chunk"));
        test(SDItems.magma_chunk, SDItems.items.get("magma_chunk"));
        test(SDItems.thermometer, SDItems.items.get("thermometer"));
        
        test(SDItems.wool_helmet, SDItems.items.get("wool_helmet"));
        test(SDItems.wool_chestplate, SDItems.items.get("wool_chestplate"));
        test(SDItems.wool_leggings, SDItems.items.get("wool_leggings"));
        test(SDItems.wool_boots, SDItems.items.get("wool_boots"));
        
        test(SDItems.ice_helmet, SDItems.items.get("ice_helmet"));
        test(SDItems.ice_chestplate, SDItems.items.get("ice_chestplate"));
        test(SDItems.ice_leggings, SDItems.items.get("ice_leggings"));
        test(SDItems.ice_boots, SDItems.items.get("ice_boots"));
        
        test(SDItems.woolArmorMaterial, SDItems.armorMaterials.get("woolArmorMaterial"));
        test(SDItems.iceArmorMaterial, SDItems.armorMaterials.get("iceArmorMaterial"));
        
        // Failsafe checks for Fluids keyset to avoid NoSuchElementException if empty
        if (!SDFluids.fluidBlocks.isEmpty()) {
            test(SDFluids.blockPurifiedWater, SDFluids.fluidBlocks.get(SDFluids.fluidBlocks.keySet().iterator().next()));
        } else {
            printFailure(" empty keyset ", SDFluids.blockPurifiedWater, "fluidBlocks");
        }

        if (!SDFluids.fluids.isEmpty()) {
            test(SDFluids.purifiedWater, SDFluids.fluids.get(SDFluids.fluids.keySet().iterator().next()));
        } else {
            printFailure(" empty keyset ", SDFluids.purifiedWater, "fluids");
        }
        
        test(SDBlocks.campfire, SDBlocks.blocks.get("campfire"));
        test(SDBlocks.rainCollector, SDBlocks.blocks.get("rain_collector"));
        test(SDBlocks.heater, SDBlocks.blocks.get("heater"));
        test(SDBlocks.chiller, SDBlocks.blocks.get("chiller"));
        test(SDBlocks.spit, SDBlocks.blocks.get("spit"));
        
        test(SDEnchantments.chilling, SDEnchantments.enchantments.get("chilling"));
        test(SDEnchantments.heating, SDEnchantments.enchantments.get("heating"));
        
        test(SDPotions.hyperthermia, SDPotions.potions.get("hyperthermia"));
        test(SDPotions.hypothermia, SDPotions.potions.get("hypothermia"));
        test(SDPotions.thirsty, SDPotions.potions.get("thirsty"));
        test(SDPotions.parasites, SDPotions.potions.get("parasites"));
        test(SDPotions.cold_resist, SDPotions.potions.get("cold_resist"));
        test(SDPotions.heat_resist, SDPotions.potions.get("heat_resist"));
        
        test(SDPotions.cold_resist_type, SDPotions.potionTypes.get("cold_resist_type"));
        test(SDPotions.long_cold_resist_type, SDPotions.potionTypes.get("long_cold_resist_type"));
        test(SDPotions.heat_resist_type, SDPotions.potionTypes.get("heat_resist_type"));
        test(SDPotions.long_heat_resist_type, SDPotions.potionTypes.get("long_heat_resist_type"));
        
        SimpleDifficulty.logger.debug("Stopping DebugVerifier");
    }
    
    public void test(Object a, Object b) {
        if (a == null || b == null) {
            printFailure(" null ", a, b);
            return;
        }
        
        String strA = a.toString();
        String strB = b.toString();
        
        if (!a.equals(b)) {
            SimpleDifficulty.logger.debug("Test Failure: " + strA + ".equals(" + strB + ")");
            printFailure(" .equals ", a, b);
        }
        
        if (a != b) {
            SimpleDifficulty.logger.debug("Test Failure: " + strA + " == " + strB);
            printFailure(" == ", a, b);
        }
    }
    
    private void printFailure(String s, Object a, Object b) {
        String strA = (a == null) ? "null" : a.toString();
        String strB = (b == null) ? "null" : b.toString();
        SimpleDifficulty.logger.error("Pointer verification failure: " + strA + s + strB);
    }
}
