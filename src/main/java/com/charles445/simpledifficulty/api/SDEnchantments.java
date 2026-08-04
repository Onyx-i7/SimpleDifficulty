package com.charles445.simpledifficulty.api;

import com.charles445.simpledifficulty.register.RegisterEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for all SimpleDifficulty enchantments.
 * <p>
 * This class holds references to all enchantments added by the mod.
 * <b>Important:</b> Always call {@code .get()} to retrieve the actual Enchantment instance in-game.
 * </p>
 */
public class SDEnchantments {

    /**
     * Map of all registered SimpleDifficulty enchantments, keyed by their registry name.
     * Useful for iterating over all mod enchantments.
     */
    public static final Map<String, RegistryObject<Enchantment>> enchantments = new LinkedHashMap<String, RegistryObject<Enchantment>>() {{
        put("chilling", RegisterEnchantments.CHILLING);
        put("heating", RegisterEnchantments.HEATING);
    }};

    /**
     * The chilling enchantment. Applied to armor to reduce the wearer's body temperature.
     * <p>
     * Useful for surviving in hot biomes or near heat sources like campfires and heaters.
     * </p>
     */
    public static final RegistryObject<Enchantment> chilling = RegisterEnchantments.CHILLING;

    /**
     * The heating enchantment. Applied to armor to increase the wearer's body temperature.
     * <p>
     * Useful for surviving in cold biomes or near cold sources like chillers.
     * </p>
     */
    public static final RegistryObject<Enchantment> heating = RegisterEnchantments.HEATING;
}