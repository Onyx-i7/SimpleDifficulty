package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.potion.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterPotions {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, SimpleDifficulty.MODID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, SimpleDifficulty.MODID);

    public static final RegistryObject<Effect> HYPERTHERMIA = EFFECTS.register("hyperthermia", PotionHyperthermia::new);
    public static final RegistryObject<Effect> HYPOTHERMIA = EFFECTS.register("hypothermia", PotionHypothermia::new);
    public static final RegistryObject<Effect> THIRSTY = EFFECTS.register("thirsty", PotionThirsty::new);
    public static final RegistryObject<Effect> PARASITES = EFFECTS.register("parasites", PotionParasites::new);
    public static final RegistryObject<Effect> COLD_RESIST = EFFECTS.register("cold_resist", PotionResistCold::new);
    public static final RegistryObject<Effect> HEAT_RESIST = EFFECTS.register("heat_resist", PotionResistHeat::new);

    public static final RegistryObject<Potion> COLD_RESIST_TYPE = POTIONS.register("cold_resist_type", () -> new Potion(new EffectInstance(COLD_RESIST.get(), 1200)));
    public static final RegistryObject<Potion> LONG_COLD_RESIST_TYPE = POTIONS.register("long_cold_resist_type", () -> new Potion(new EffectInstance(COLD_RESIST.get(), 2400)));
    public static final RegistryObject<Potion> HEAT_RESIST_TYPE = POTIONS.register("heat_resist_type", () -> new Potion(new EffectInstance(HEAT_RESIST.get(), 1200)));
    public static final RegistryObject<Potion> LONG_HEAT_RESIST_TYPE = POTIONS.register("long_heat_resist_type", () -> new Potion(new EffectInstance(HEAT_RESIST.get(), 2400)));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }
}