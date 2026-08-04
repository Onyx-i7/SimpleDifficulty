package com.charles445.simpledifficulty.debug;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.*;
import net.minecraftforge.fml.RegistryObject;

/**
 * Debug verification class to ensure all registry objects are properly initialized.
 * Runs during mod load complete to verify that API references match their registry entries.
 */
public class DebugVerifier {

    /**
     * Runs verification tests on all registered objects.
     */
    public void verify() {
        SimpleDifficulty.LOGGER.debug("Running DebugVerifier");

        // Items
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

        test(SDItems.frost_powder, SDItems.items.get("frost_powder"));
        test(SDItems.frost_rod, SDItems.items.get("frost_rod"));
        test(SDItems.dragonCanteen, SDItems.items.get("dragon_canteen"));

        // Armor Materials (these are direct references, not RegistryObjects)
        // Note: In 1.16.5, armor materials are no longer in a map, they're direct instances

        // Fluids
        if (!SDFluids.fluids.isEmpty()) {
            test(SDFluids.purifiedWater, SDFluids.fluids.values().iterator().next());
        } else {
            printFailure(" empty keyset ", SDFluids.blockPurifiedWater, "fluidBlocks");
        }

        if (!SDFluids.fluids.isEmpty()) {
            test(SDFluids.purifiedWater, SDFluids.fluids.values().iterator().next());
        } else {
            printFailure(" empty keyset ", SDFluids.purifiedWater, "fluids");
        }

        // Blocks
        test(SDBlocks.campfire, SDBlocks.blocks.get("campfire"));
        test(SDBlocks.rainCollector, SDBlocks.blocks.get("rain_collector"));
        test(SDBlocks.heater, SDBlocks.blocks.get("heater"));
        test(SDBlocks.chiller, SDBlocks.blocks.get("chiller"));
        test(SDBlocks.spit, SDBlocks.blocks.get("spit"));

        // Enchantments
        test(SDEnchantments.chilling, SDEnchantments.enchantments.get("chilling"));
        test(SDEnchantments.heating, SDEnchantments.enchantments.get("heating"));

        // Potions (Effects)
        test(SDPotions.hyperthermia, SDPotions.effects.get("hyperthermia"));
        test(SDPotions.hypothermia, SDPotions.effects.get("hypothermia"));
        test(SDPotions.thirsty, SDPotions.effects.get("thirsty"));
        test(SDPotions.parasites, SDPotions.effects.get("parasites"));
        test(SDPotions.cold_resist, SDPotions.effects.get("cold_resist"));
        test(SDPotions.heat_resist, SDPotions.effects.get("heat_resist"));

        // Potion Types
        test(SDPotions.cold_resist_type, SDPotions.cold_resist_type);
        test(SDPotions.long_cold_resist_type, SDPotions.long_cold_resist_type);
        test(SDPotions.heat_resist_type, SDPotions.heat_resist_type);
        test(SDPotions.long_heat_resist_type, SDPotions.long_heat_resist_type);

        SimpleDifficulty.LOGGER.debug("Stopping DebugVerifier");
    }

    /**
     * Tests that two objects are equal and reference the same instance.
     *
     * @param a First object (usually a direct RegistryObject reference).
     * @param b Second object (usually from a map lookup).
     */
    public void test(Object a, Object b) {
        if (a == null || b == null) {
            printFailure(" null ", a, b);
            return;
        }

        // Handle RegistryObject comparison
        Object aResolved = a instanceof RegistryObject ? ((RegistryObject<?>) a).get() : a;
        Object bResolved = b instanceof RegistryObject ? ((RegistryObject<?>) b).get() : b;

        if (aResolved == null || bResolved == null) {
            printFailure(" null after resolution ", aResolved, bResolved);
            return;
        }

        String strA = aResolved.toString();
        String strB = bResolved.toString();

        if (!aResolved.equals(bResolved)) {
            SimpleDifficulty.LOGGER.debug("Test Failure: {}.equals({})", strA, strB);
            printFailure(" .equals ", aResolved, bResolved);
        }

        if (aResolved != bResolved) {
            SimpleDifficulty.LOGGER.debug("Test Failure: {} == {}", strA, strB);
            printFailure(" == ", aResolved, bResolved);
        }
    }

    /**
     * Logs a verification failure.
     *
     * @param s The failure type description.
     * @param a First object.
     * @param b Second object.
     */
    private void printFailure(String s, Object a, Object b) {
        String strA = (a == null) ? "null" : a.toString();
        String strB = (b == null) ? "null" : b.toString();
        SimpleDifficulty.LOGGER.error("Pointer verification failure: {} {} {}", strA, s, strB);
    }
}