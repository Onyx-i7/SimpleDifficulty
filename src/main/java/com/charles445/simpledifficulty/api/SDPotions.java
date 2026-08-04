package com.charles445.simpledifficulty.api;

import com.charles445.simpledifficulty.register.RegisterPotions;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for SimpleDifficulty potions.
 * <p>
 * <b>Important 1.16.5 Naming Convention:</b>
 * <ul>
 *   <li>{@link Effect} - Represents the actual status effect (what used to be called "Potion" in 1.12.2).</li>
 *   <li>{@link Potion} - Represents the brewing recipe/potion type (what used to be called "PotionType" in 1.12.2).</li>
 * </ul>
 * </p>
 */
public class SDPotions {

    public static final Map<String, RegistryObject<Effect>> effects = new LinkedHashMap<String, RegistryObject<Effect>>() {{
        put("hyperthermia", RegisterPotions.HYPERTHERMIA);
        put("hypothermia", RegisterPotions.HYPOTHERMIA);
        put("thirsty", RegisterPotions.THIRSTY);
        put("parasites", RegisterPotions.PARASITES);
        put("cold_resist", RegisterPotions.COLD_RESIST);
        put("heat_resist", RegisterPotions.HEAT_RESIST);
    }};

    // Status Effects (formerly Potion in 1.12)
    public static final RegistryObject<Effect> hyperthermia = RegisterPotions.HYPERTHERMIA;
    public static final RegistryObject<Effect> hypothermia = RegisterPotions.HYPOTHERMIA;
    public static final RegistryObject<Effect> thirsty = RegisterPotions.THIRSTY;
    public static final RegistryObject<Effect> parasites = RegisterPotions.PARASITES;
    public static final RegistryObject<Effect> cold_resist = RegisterPotions.COLD_RESIST;
    public static final RegistryObject<Effect> heat_resist = RegisterPotions.HEAT_RESIST;

    // Brewing Potion Types (formerly PotionType in 1.12)
    public static final RegistryObject<Potion> cold_resist_type = RegisterPotions.COLD_RESIST_TYPE;
    public static final RegistryObject<Potion> long_cold_resist_type = RegisterPotions.LONG_COLD_RESIST_TYPE;
    public static final RegistryObject<Potion> heat_resist_type = RegisterPotions.HEAT_RESIST_TYPE;
    public static final RegistryObject<Potion> long_heat_resist_type = RegisterPotions.LONG_HEAT_RESIST_TYPE;
}