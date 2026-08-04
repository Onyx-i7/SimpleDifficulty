package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.*;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableTemperatureMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableThirstMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonTemperatureMetadataMigrate;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifierGroupEnum;
import com.charles445.simpledifficulty.compat.JsonCompatDefaults;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.config.json.MaterialTemperature;
import com.charles445.simpledifficulty.register.ExtraItemNames;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal JSON configuration manager.
 * Handles loading, saving, and migration of JSON configuration files.
 */
public class JsonConfigInternal {

    public static MaterialTemperature materialTemperature = new MaterialTemperature();
    public static Map<String, ExtraItem> extraItems = new HashMap<>();
    public static List<String> jsonErrors = new ArrayList<>();
    public static final JsonItemIdentity DEFAULT_ITEM_IDENTITY = new JsonItemIdentity();

    /**
     * Pre-initialization phase.
     * Sets up extra items and loads their configuration.
     *
     * @param jsonDirectory The config directory.
     */
    public static void preInit(File jsonDirectory) {
        SimpleDifficulty.LOGGER.info("Extra Items Initialization");

        makeExtraItem(ExtraItemNames.FROST_ROD, "Frost Rod - For recipes");
        makeExtraItem(ExtraItemNames.FROST_POWDER, "Frost Powder - For recipes");
        makeExtraItem(ExtraItemNames.DRAGON_CANTEEN, "Dragon Canteen - Automatically purifies water")
                .put("capacity", "8");

        extraItems = processJson(JsonFileName.extraItems, extraItems, jsonDirectory, false);
    }

    /**
     * Post-initialization phase.
     * Registers default configurations and loads all JSON files.
     *
     * @param jsonDirectory The config directory.
     */
    public static void postInit(File jsonDirectory) {
        // Register default armor temperatures
        JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_helmet.get()), 2.0f);
        JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_chestplate.get()), 2.0f);
        JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_leggings.get()), 2.0f);
        JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_boots.get()), 2.0f);

        JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_helmet.get()), -2.0f);
        JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_chestplate.get()), -2.0f);
        JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_leggings.get()), -2.0f);
        JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_boots.get()), -2.0f);

        // Register default block temperatures
        JsonConfig.registerBlockTemperature(SDBlocks.campfire.get(), 7.0f, new JsonPropertyValue("burning", "true"));
        JsonConfig.registerBlockTemperature(SDBlocks.campfire.get(), 0.0f, new JsonPropertyValue("burning", "false"));

        JsonConfig.registerBlockTemperature(Blocks.TORCH, 1.0f);
        JsonConfig.registerBlockTemperature(Blocks.FURNACE, 4.0f); // Note: In 1.16.5, furnace states are handled differently
        JsonConfig.registerBlockTemperature(Blocks.LAVA, 12.5f);
        JsonConfig.registerBlockTemperature(Blocks.MAGMA_BLOCK, 10.0f);

        // Register default consumable temperatures
        JsonConfig.registerConsumableTemperature(TemporaryModifierGroupEnum.FOOD.group(),
                new ItemStack(Items.MUSHROOM_STEW), 1.0f, 1200);

        // Note: Juice items would need special handling in 1.16.5 (no metadata)
        // Consider using NBT tags or separate items for different juice types

        // Register default consumable thirst
        JsonConfig.registerConsumableThirst(new ItemStack(Items.MILK_BUCKET), 4, 1.0f, 0.2f);

        // Register default dimension temperature (Overworld example)
        JsonConfig.registerDimensionTemperature("minecraft:overworld", 0.0f);

        // Register default held item temperatures
        JsonConfig.registerHeldItem(new ItemStack(Blocks.MAGMA_BLOCK), 3.0f);
        JsonConfig.registerHeldItem(new ItemStack(Blocks.TORCH), 1.0f);

        // Load mod compatibility defaults
        JsonCompatDefaults.instance.populate();

        // Process all JSON files
        processAllJson(jsonDirectory);
    }

    /**
     * Clears all JSON configuration containers.
     * WARNING: Do not call before load complete is finished!
     */
    public static void clearContainers() {
        JsonConfig.armorTemperatures.clear();
        JsonConfig.blockTemperatures.clear();
        JsonConfig.consumableTemperature.clear();
        JsonConfig.consumableThirst.clear();
        JsonConfig.dimensionTemperature.clear();
        JsonConfig.fluidTemperatures.clear();
        JsonConfig.heldItemTemperatures.clear();
    }

    /**
     * Processes all JSON configuration files.
     * Handles loading, validation, and migration of legacy formats.
     *
     * @param jsonDirectory The config directory.
     */
    public static void processAllJson(File jsonDirectory) {
        // Armor Temperatures
        String jsonFileName = JsonFileName.armorTemperatures.get();
        Map<String, List<JsonTemperatureIdentity>> jsonArmorTemperatures = null;

        try {
            jsonArmorTemperatures = processUncaughtJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory, true);
        } catch (Exception e) {
            Map<String, JsonTemperature> migrateMap = new HashMap<>();
            migrateMap = processJson(JsonFileName.armorTemperatures_MIGRATE, migrateMap, jsonDirectory, true);

            if (migrateMap != null) {
                boolean migrate = false;
                for (JsonTemperature jt : migrateMap.values()) {
                    if (jt.temperature != 0.0f) {
                        migrate = true;
                        break;
                    }
                }
                if (migrate) {
                    SimpleDifficulty.LOGGER.info("Attempting to migrate {} to new format", jsonFileName);
                    try {
                        for (Map.Entry<String, JsonTemperature> registryEntry : migrateMap.entrySet()) {
                            JsonConfig.registerArmorTemperature(registryEntry.getKey(), registryEntry.getValue().temperature, new JsonItemIdentity());
                        }
                        manuallyWriteToJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory);
                        SimpleDifficulty.LOGGER.info("Migrated {}", jsonFileName);
                    } catch (Exception ex) {
                        logMerge(jsonFileName, ex);
                    }
                } else {
                    jsonArmorTemperatures = processJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory, true);
                }
            }
        }

        if (jsonArmorTemperatures != null) {
            for (Map.Entry<String, List<JsonTemperatureIdentity>> entry : jsonArmorTemperatures.entrySet()) {
                for (JsonTemperatureIdentity jtm : entry.getValue()) {
                    if (jtm.identity != null) jtm.identity.tryPopulateCompound();
                    JsonConfig.registerArmorTemperature(entry.getKey(), jtm.temperature, jtm.identity == null ? DEFAULT_ITEM_IDENTITY : jtm.identity);
                }
            }
            try {
                manuallyWriteToJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory);
            } catch (Exception e) {
                logMerge(jsonFileName, e);
            }
        }

        // Block Temperatures
        jsonFileName = JsonFileName.blockTemperatures.get();
        Map<String, List<JsonPropertyTemperature>> jsonBlockTemperatures = processJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory, true);
        if (jsonBlockTemperatures != null) {
            for (Map.Entry<String, List<JsonPropertyTemperature>> entry : jsonBlockTemperatures.entrySet()) {
                for (JsonPropertyTemperature propTemp : entry.getValue()) {
                    JsonConfig.registerBlockTemperature(entry.getKey(), propTemp.temperature, propTemp.getAsPropertyArray());
                }
            }
            try {
                manuallyWriteToJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory);
            } catch (Exception e) {
                logMerge(jsonFileName, e);
            }
        }

        // Consumable Temperature
        jsonFileName = JsonFileName.consumableTemperature.get();
        Map<String, List<JsonConsumableTemperature>> jsonConsumableTemperature = processJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory, true);
        if (jsonConsumableTemperature != null) {
            boolean migrate = true;
            for (List<JsonConsumableTemperature> mvalues : jsonConsumableTemperature.values()) {
                for (JsonConsumableTemperature value : mvalues) {
                    if (value.identity != null) {
                        migrate = false;
                        break;
                    }
                }
            }

            if (migrate) {
                SimpleDifficulty.LOGGER.info("Attempting to migrate {} to new format", jsonFileName);
                Map<String, List<JsonConsumableTemperatureMigrate>> migrateMap = new HashMap<>();
                try {
                    migrateMap = processUncaughtJson(JsonFileName.consumableTemperature_MIGRATE, migrateMap, jsonDirectory, true);
                    for (Map.Entry<String, List<JsonConsumableTemperatureMigrate>> registryEntry : migrateMap.entrySet()) {
                        for (JsonConsumableTemperatureMigrate jctm : registryEntry.getValue()) {
                            JsonConfig.registerConsumableTemperature(jctm.group, registryEntry.getKey(), jctm.temperature, jctm.duration, new JsonItemIdentity());
                        }
                    }
                    manuallyWriteToJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory);
                    SimpleDifficulty.LOGGER.info("Migrated {}", jsonFileName);
                } catch (Exception e) {
                    logMerge(jsonFileName, e);
                }
            } else {
                for (Map.Entry<String, List<JsonConsumableTemperature>> entry : jsonConsumableTemperature.entrySet()) {
                    for (JsonConsumableTemperature jct : entry.getValue()) {
                        if (jct.identity != null) jct.identity.tryPopulateCompound();
                        JsonConfig.registerConsumableTemperature(jct.group, entry.getKey(), jct.temperature, jct.duration, jct.identity == null ? DEFAULT_ITEM_IDENTITY : jct.identity);
                    }
                }
                try {
                    manuallyWriteToJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory);
                } catch (Exception e) {
                    logMerge(jsonFileName, e);
                }
            }
        }

        // Consumable Thirst
        jsonFileName = JsonFileName.consumableThirst.get();
        Map<String, List<JsonConsumableThirst>> jsonConsumableThirst = processJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory, true);
        if (jsonConsumableThirst != null) {
            boolean migrate = true;
            for (List<JsonConsumableThirst> mvalues : jsonConsumableThirst.values()) {
                for (JsonConsumableThirst value : mvalues) {
                    if (value.identity != null) {
                        migrate = false;
                        break;
                    }
                }
            }

            if (migrate) {
                SimpleDifficulty.LOGGER.info("Attempting to migrate {} to new format", jsonFileName);
                Map<String, List<JsonConsumableThirstMigrate>> migrateMap = new HashMap<>();
                try {
                    migrateMap = processUncaughtJson(JsonFileName.consumableThirst_MIGRATE, migrateMap, jsonDirectory, true);
                    for (Map.Entry<String, List<JsonConsumableThirstMigrate>> registryEntry : migrateMap.entrySet()) {
                        for (JsonConsumableThirstMigrate jctm : registryEntry.getValue()) {
                            JsonConfig.registerConsumableThirst(registryEntry.getKey(), jctm.amount, jctm.saturation, jctm.thirstyChance, new JsonItemIdentity());
                        }
                    }
                    manuallyWriteToJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory);
                    SimpleDifficulty.LOGGER.info("Migrated {}", jsonFileName);
                } catch (Exception e) {
                    logMerge(jsonFileName, e);
                }
            } else {
                for (Map.Entry<String, List<JsonConsumableThirst>> entry : jsonConsumableThirst.entrySet()) {
                    for (JsonConsumableThirst jct : entry.getValue()) {
                        if (jct.identity != null) jct.identity.tryPopulateCompound();
                        JsonConfig.registerConsumableThirst(entry.getKey(), jct.amount, jct.saturation, jct.thirstyChance, jct.identity == null ? DEFAULT_ITEM_IDENTITY : jct.identity);
                    }
                }
                try {
                    manuallyWriteToJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory);
                } catch (Exception e) {
                    logMerge(jsonFileName, e);
                }
            }
        }

        // Dimension Temperatures
        jsonFileName = JsonFileName.dimensionTemperature.get();
        Map<String, JsonTemperature> jsonDimensionTemperature = processJson(JsonFileName.dimensionTemperature, JsonConfig.dimensionTemperature, jsonDirectory, true);
        if (jsonDimensionTemperature != null) {
            for (Map.Entry<String, JsonTemperature> entry : jsonDimensionTemperature.entrySet()) {
                JsonConfig.registerDimensionTemperature(entry.getKey(), entry.getValue().temperature);
            }
            try {
                manuallyWriteToJson(JsonFileName.dimensionTemperature, JsonConfig.dimensionTemperature, jsonDirectory);
            } catch (Exception e) {
                logMerge(jsonFileName, e);
            }
        }

        // Fluid Temperatures
        jsonFileName = JsonFileName.fluidTemperatures.get();
        Map<String, JsonTemperature> jsonFluidTemperatures = processJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory, true);
        if (jsonFluidTemperatures != null) {
            for (Map.Entry<String, JsonTemperature> entry : jsonFluidTemperatures.entrySet()) {
                JsonConfig.registerFluidTemperature(entry.getKey(), entry.getValue().temperature);
            }
            try {
                manuallyWriteToJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory);
            } catch (Exception e) {
                logMerge(jsonFileName, e);
            }
        }

        // Held Item Temperatures
        jsonFileName = JsonFileName.heldItemTemperatures.get();
        Map<String, List<JsonTemperatureIdentity>> jsonHeldItemTemperatures = processJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory, true);
        if (jsonHeldItemTemperatures != null) {
            boolean migrate = true;
            for (List<JsonTemperatureIdentity> mvalues : jsonHeldItemTemperatures.values()) {
                for (JsonTemperatureIdentity value : mvalues) {
                    if (value.identity != null) {
                        migrate = false;
                        break;
                    }
                }
            }

            if (migrate) {
                SimpleDifficulty.LOGGER.info("Attempting to migrate {} to new format", jsonFileName);
                Map<String, List<JsonTemperatureMetadataMigrate>> migrateMap = new HashMap<>();
                try {
                    migrateMap = processUncaughtJson(JsonFileName.heldItemTemperatures_MIGRATE, migrateMap, jsonDirectory, true);
                    for (Map.Entry<String, List<JsonTemperatureMetadataMigrate>> registryEntry : migrateMap.entrySet()) {
                        for (JsonTemperatureMetadataMigrate jctm : registryEntry.getValue()) {
                            JsonConfig.registerHeldItem(registryEntry.getKey(), jctm.temperature, new JsonItemIdentity());
                        }
                    }
                    manuallyWriteToJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory);
                    SimpleDifficulty.LOGGER.info("Migrated {}", jsonFileName);
                } catch (Exception e) {
                    logMerge(jsonFileName, e);
                }
            } else {
                for (Map.Entry<String, List<JsonTemperatureIdentity>> entry : jsonHeldItemTemperatures.entrySet()) {
                    for (JsonTemperatureIdentity jtm : entry.getValue()) {
                        if (jtm.identity != null) jtm.identity.tryPopulateCompound();
                        JsonConfig.registerHeldItem(entry.getKey(), jtm.temperature, jtm.identity == null ? DEFAULT_ITEM_IDENTITY : jtm.identity);
                    }
                }
                try {
                    manuallyWriteToJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory);
                } catch (Exception e) {
                    logMerge(jsonFileName, e);
                }
            }
        }

        // Material Temperature
        materialTemperature = processJson(JsonFileName.materialTemperature, materialTemperature, jsonDirectory, false);
    }

    private static void logMerge(String jsonFileName, Exception e) {
        SimpleDifficulty.LOGGER.error("Error writing merged JSON File: {}", jsonFileName, e);
        jsonErrors.add("config/simpledifficulty/" + jsonFileName + " failed to load!");
    }

    /**
     * Manually exports all JSON configurations to disk.
     *
     * @return Success or failure message.
     */
    public static String manuallyExportAll() {
        File jsonDirectory = SimpleDifficulty.jsonDirectory;
        try {
            manuallyWriteToJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory);
            manuallyWriteToJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory);
            manuallyWriteToJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory);
            manuallyWriteToJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory);
            manuallyWriteToJson(JsonFileName.dimensionTemperature, JsonConfig.dimensionTemperature, jsonDirectory);
            manuallyWriteToJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory);
            manuallyWriteToJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory);
            manuallyWriteToJson(JsonFileName.materialTemperature, materialTemperature, jsonDirectory);
            return "Successfully exported SimpleDifficulty configuration to JSON";
        } catch (Exception e) {
            SimpleDifficulty.LOGGER.error("Export to JSON Failure Details", e);
            return "Export to JSON FAILED! See log for details.";
        }
    }

    @Nullable
    public static <T> T processJson(JsonFileName jfn, final T container, File jsonDirectory, boolean forMerging) {
        try {
            return processUncaughtJson(jfn, container, jsonDirectory, forMerging);
        } catch (Exception e) {
            SimpleDifficulty.LOGGER.error("Error managing JSON File: {}", jfn.get(), e);
            jsonErrors.add("config/simpledifficulty/" + jfn.get() + " failed to load!");
            return forMerging ? null : container;
        }
    }

    @Nullable
    public static <T> T processUncaughtJson(JsonFileName jfn, final T container, File jsonDirectory, boolean forMerging) throws Exception {
        String jsonFileName = jfn.get();
        Type type = JsonTypeToken.get(jfn);

        File jsonFile = new File(jsonDirectory, jsonFileName);
        if (jsonFile.exists()) {
            Gson gson = buildNewGson();
            try (FileReader reader = new FileReader(jsonFile)) {
                return (T) gson.fromJson(reader, type);
            }
        } else {
            Gson gson = buildNewGson();
            FileUtils.write(jsonFile, gson.toJson(container, type), StandardCharsets.UTF_8);
            return forMerging ? null : container;
        }
    }

    private static <T> void manuallyWriteToJson(JsonFileName jfn, final T container, File jsonDirectory) throws Exception {
        String jsonFileName = jfn.get();
        Type type = JsonTypeToken.get(jfn);

        Gson gson = buildNewGson();
        File jsonFile = new File(jsonDirectory, jsonFileName);

        if (jsonFile.exists() && !jsonFile.canWrite()) {
            SimpleDifficulty.LOGGER.warn("{} is set to Read Only! Merged file will not be written.", jfn.toString());
        } else {
            FileUtils.write(jsonFile, gson.toJson(container, type), StandardCharsets.UTF_8);
        }
    }

    private static Gson buildNewGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithModifiers(Modifier.PRIVATE)
                .create();
    }

    private static ExtraItem makeExtraItem(String itemName, String description) {
        ExtraItem extra = new ExtraItem(description, false);
        extraItems.put(itemName, extra);
        return extra;
    }
}