package com.charles445.simpledifficulty.api;

import com.charles445.simpledifficulty.register.RegisterItems;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry class for all SimpleDifficulty items and armor materials.
 */
public class SDItems {

    public static final Map<String, RegistryObject<Item>> items = new LinkedHashMap<String, RegistryObject<Item>>() {{
        put("canteen", RegisterItems.CANTEEN);
        put("iron_canteen", RegisterItems.IRON_CANTEEN);
        put("charcoal_filter", RegisterItems.CHARCOAL_FILTER);
        put("juice", RegisterItems.JUICE);
        put("purified_water_bottle", RegisterItems.PURIFIED_WATER_BOTTLE);
        put("salt_water_bottle", RegisterItems.SALT_WATER_BOTTLE);
        put("ice_chunk", RegisterItems.ICE_CHUNK);
        put("magma_chunk", RegisterItems.MAGMA_CHUNK);
        put("thermometer", RegisterItems.THERMOMETER);
        put("wool_helmet", RegisterItems.WOOL_HELMET);
        put("wool_chestplate", RegisterItems.WOOL_CHESTPLATE);
        put("wool_leggings", RegisterItems.WOOL_LEGGINGS);
        put("wool_boots", RegisterItems.WOOL_BOOTS);
        put("ice_helmet", RegisterItems.ICE_HELMET);
        put("ice_chestplate", RegisterItems.ICE_CHESTPLATE);
        put("ice_leggings", RegisterItems.ICE_LEGGINGS);
        put("ice_boots", RegisterItems.ICE_BOOTS);
        put("frost_powder", RegisterItems.FROST_POWDER);
        put("frost_rod", RegisterItems.FROST_ROD);
        put("dragon_canteen", RegisterItems.DRAGON_CANTEEN);
    }};

    // Armor Materials
    public static final IArmorMaterial woolArmorMaterial = RegisterItems.WOOL_ARMOR_MATERIAL;
    public static final IArmorMaterial iceArmorMaterial = RegisterItems.ICE_ARMOR_MATERIAL;

    // Items
    public static final RegistryObject<Item> canteen = RegisterItems.CANTEEN;
    public static final RegistryObject<Item> ironCanteen = RegisterItems.IRON_CANTEEN;
    public static final RegistryObject<Item> charcoalFilter = RegisterItems.CHARCOAL_FILTER;
    public static final RegistryObject<Item> juice = RegisterItems.JUICE;
    public static final RegistryObject<Item> purifiedWaterBottle = RegisterItems.PURIFIED_WATER_BOTTLE;
    public static final RegistryObject<Item> saltWaterBottle = RegisterItems.SALT_WATER_BOTTLE;
    public static final RegistryObject<Item> ice_chunk = RegisterItems.ICE_CHUNK;
    public static final RegistryObject<Item> magma_chunk = RegisterItems.MAGMA_CHUNK;
    public static final RegistryObject<Item> thermometer = RegisterItems.THERMOMETER;

    // Wool Armor
    public static final RegistryObject<Item> wool_helmet = RegisterItems.WOOL_HELMET;
    public static final RegistryObject<Item> wool_chestplate = RegisterItems.WOOL_CHESTPLATE;
    public static final RegistryObject<Item> wool_leggings = RegisterItems.WOOL_LEGGINGS;
    public static final RegistryObject<Item> wool_boots = RegisterItems.WOOL_BOOTS;

    // Ice Armor
    public static final RegistryObject<Item> ice_helmet = RegisterItems.ICE_HELMET;
    public static final RegistryObject<Item> ice_chestplate = RegisterItems.ICE_CHESTPLATE;
    public static final RegistryObject<Item> ice_leggings = RegisterItems.ICE_LEGGINGS;
    public static final RegistryObject<Item> ice_boots = RegisterItems.ICE_BOOTS;

    public static final RegistryObject<Item> frost_powder = RegisterItems.FROST_POWDER;
    public static final RegistryObject<Item> frost_rod = RegisterItems.FROST_ROD;
    public static final RegistryObject<Item> dragonCanteen = RegisterItems.DRAGON_CANTEEN;
}