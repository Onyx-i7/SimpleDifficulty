package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.item.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SimpleDifficulty.MODID);

    // Armor Materials
    public static final IArmorMaterial WOOL_ARMOR_MATERIAL = new SimpleArmorMaterial("wool", 2, new int[]{1, 1, 1, 1}, 5, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0f, 0.0f);
    public static final IArmorMaterial ICE_ARMOR_MATERIAL = new SimpleArmorMaterial("ice", 2, new int[]{1, 1, 1, 1}, 5, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0f, 0.0f);

    // Block Items
    public static final RegistryObject<Item> CAMPFIRE_ITEM = ITEMS.register("campfire", () -> new BlockItem(RegisterBlocks.CAMPFIRE.get(), new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> RAIN_COLLECTOR_ITEM = ITEMS.register("rain_collector", () -> new BlockItem(RegisterBlocks.RAIN_COLLECTOR.get(), new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> HEATER_ITEM = ITEMS.register("heater", () -> new BlockItem(RegisterBlocks.HEATER.get(), new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> CHILLER_ITEM = ITEMS.register("chiller", () -> new BlockItem(RegisterBlocks.CHILLER.get(), new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> SPIT_ITEM = ITEMS.register("spit", () -> new BlockItem(RegisterBlocks.SPIT.get(), new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> ICE_PURIFIED_WATER_ITEM = ITEMS.register("purifiedwater_ice", () -> new BlockItem(RegisterBlocks.ICE_PURIFIED_WATER.get(), new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> ICE_SALT_WATER_ITEM = ITEMS.register("saltwater_ice", () -> new BlockItem(RegisterBlocks.ICE_SALT_WATER.get(), new Item.Properties().tab(ModCreativeTab.TAB)));

    // Regular Items
    public static final RegistryObject<Item> PURIFIED_WATER_BOTTLE = ITEMS.register("purified_water_bottle", () -> new ItemPurifiedWaterBottle(new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> SALT_WATER_BOTTLE = ITEMS.register("salt_water_bottle", () -> new ItemSaltWaterBottle(new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> JUICE = ITEMS.register("juice", () -> new ItemJuice(new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> CANTEEN = ITEMS.register("canteen", () -> new ItemCanteen(new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> IRON_CANTEEN = ITEMS.register("iron_canteen", () -> new ItemIronCanteen(new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> CHARCOAL_FILTER = ITEMS.register("charcoal_filter", () -> new Item(new Item.Properties().tab(ModCreativeTab.TAB)));
    
    public static final RegistryObject<Item> ICE_CHUNK = ITEMS.register("ice_chunk", () -> new Item(new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> MAGMA_CHUNK = ITEMS.register("magma_chunk", () -> new Item(new Item.Properties().tab(ModCreativeTab.TAB)));
    
    public static final RegistryObject<Item> THERMOMETER = ITEMS.register("thermometer", () -> new ItemThermometer(new Item.Properties().tab(ModCreativeTab.TAB)));
    
    // Wool Armor
    public static final RegistryObject<Item> WOOL_HELMET = ITEMS.register("wool_helmet", () -> new ItemArmorTemperature(WOOL_ARMOR_MATERIAL, EquipmentSlotType.HEAD, new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> WOOL_CHESTPLATE = ITEMS.register("wool_chestplate", () -> new ItemArmorTemperature(WOOL_ARMOR_MATERIAL, EquipmentSlotType.CHEST, new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> WOOL_LEGGINGS = ITEMS.register("wool_leggings", () -> new ItemArmorTemperature(WOOL_ARMOR_MATERIAL, EquipmentSlotType.LEGS, new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> WOOL_BOOTS = ITEMS.register("wool_boots", () -> new ItemArmorTemperature(WOOL_ARMOR_MATERIAL, EquipmentSlotType.FEET, new Item.Properties().tab(ModCreativeTab.TAB)));
    
    // Ice Armor
    public static final RegistryObject<Item> ICE_HELMET = ITEMS.register("ice_helmet", () -> new ItemArmorTemperature(ICE_ARMOR_MATERIAL, EquipmentSlotType.HEAD, new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> ICE_CHESTPLATE = ITEMS.register("ice_chestplate", () -> new ItemArmorTemperature(ICE_ARMOR_MATERIAL, EquipmentSlotType.CHEST, new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> ICE_LEGGINGS = ITEMS.register("ice_leggings", () -> new ItemArmorTemperature(ICE_ARMOR_MATERIAL, EquipmentSlotType.LEGS, new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> ICE_BOOTS = ITEMS.register("ice_boots", () -> new ItemArmorTemperature(ICE_ARMOR_MATERIAL, EquipmentSlotType.FEET, new Item.Properties().tab(ModCreativeTab.TAB)));

    public static final RegistryObject<Item> FROST_POWDER = ITEMS.register("frost_powder", () -> new Item(new Item.Properties().tab(ModCreativeTab.TAB)));
    public static final RegistryObject<Item> FROST_ROD = ITEMS.register("frost_rod", () -> new Item(new Item.Properties().tab(ModCreativeTab.TAB)));
    
    public static final RegistryObject<Item> DRAGON_CANTEEN = ITEMS.register("dragon_canteen", () -> new ItemDragonCanteen(new Item.Properties().tab(ModCreativeTab.TAB)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    // Custom Armor Material Implementation replacing EnumHelper
    private static class SimpleArmorMaterial implements IArmorMaterial {
        private final String name;
        private final int durabilityMultiplier;
        private final int[] damageReductionAmounts;
        private final int enchantability;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;

        public SimpleArmorMaterial(String name, int durabilityMultiplier, int[] damageReductionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.damageReductionAmounts = damageReductionAmounts;
            this.enchantability = enchantability;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
        }

        @Override public int getDurabilityForSlot(EquipmentSlotType slot) { return new int[]{13, 15, 16, 11}[slot.getIndex()] * this.durabilityMultiplier; }
        @Override public int getDefenseForSlot(EquipmentSlotType slot) { return this.damageReductionAmounts[slot.getIndex()]; }
        @Override public int getEnchantmentValue() { return this.enchantability; }
        @Override public SoundEvent getEquipSound() { return this.equipSound; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; } 
        @Override public String getName() { return SimpleDifficulty.MODID + ":" + this.name; }
        @Override public float getToughness() { return this.toughness; }
        @Override public float getKnockbackResistance() { return this.knockbackResistance; }
    }
}