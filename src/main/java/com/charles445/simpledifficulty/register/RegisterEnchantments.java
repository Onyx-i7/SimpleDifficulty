package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.enchantment.EnchantmentArmorTemperature;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, SimpleDifficulty.MODID);

    public static final RegistryObject<Enchantment> CHILLING = ENCHANTMENTS.register("chilling", EnchantmentArmorTemperature::new);
    public static final RegistryObject<Enchantment> HEATING = ENCHANTMENTS.register("heating", EnchantmentArmorTemperature::new);

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}