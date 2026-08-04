package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;
import com.charles445.simpledifficulty.util.CompatUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.List;

import static com.charles445.simpledifficulty.compat.ModNames.*;

/**
 * Default JSON compatibility configurations for supported mods.
 * Registers temperature and thirst values for items/blocks from other mods.
 */
public class JsonCompatDefaults {
    public static final JsonCompatDefaults instance = new JsonCompatDefaults();

    /**
     * Populates all default configurations for loaded mods.
     */
    public void populate() {
        populateBaubles();
        populateBiomesOPlenty();
        populateHarvestCraft();
        populateLycanitesMobs();
        populatePyrotech();
        populateRealisticTorches();
        populateRustic();
        populateSimpleCampfire();
        populateTinkersConstruct();
    }

    /**
     * Populates configuration for a specific mod.
     *
     * @param modid The mod ID to populate.
     * @return true if successful.
     */
    public boolean populate(String modid) {
        return switch (modid) {
            case BAUBLES -> populateBaubles();
            case BIOMESOPLENTY -> populateBiomesOPlenty();
            case HARVESTCRAFT -> populateHarvestCraft();
            case LYCANITESMOBS -> populateLycanitesMobs();
            case PYROTECH -> populatePyrotech();
            case REALISTICTORCHES -> populateRealisticTorches();
            case RUSTIC -> populateRustic();
            case SIMPLECAMPFIRE -> populateSimpleCampfire();
            case TINKERSCONSTRUCT -> populateTinkersConstruct();
            default -> false;
        };
    }

    private boolean populateBaubles() {
        if (!canUseModJsonDefaults(BAUBLES)) return false;
        JsonConfig.registerArmorTemperature("baubles:ring", -0.25f);
        return true;
    }

    private boolean populateBiomesOPlenty() {
        if (!canUseModJsonDefaults(BIOMESOPLENTY)) return false;
        addFluidTemperature("hot_spring_water", 3.0f);
        return true;
    }

    private boolean populateHarvestCraft() {
        if (!canUseModJsonDefaults(HARVESTCRAFT)) return false;

        // Drinks
        addDrink("harvestcraft:teaitem", 6, 5.0f);
        addDrink("harvestcraft:coffeeitem", 6, 5.0f);
        addDrink("harvestcraft:hotchocolateitem", 6, 5.0f);
        addDrink("harvestcraft:lemonaideitem", 9, 7.0f);
        addDrink("harvestcraft:raspberryicedteaitem", 9, 7.0f);
        addDrink("harvestcraft:chaiteaitem", 9, 7.0f);
        addDrink("harvestcraft:espressoitem", 12, 9.0f);
        addDrink("harvestcraft:coffeeconlecheitem", 18, 15.0f);
        addDrink("harvestcraft:coconutmilkitem", 3, 3.0f);
        addDrink("harvestcraft:chocolatemilkitem", 6, 5.0f);
        addDrink("harvestcraft:fruitpunchitem", 6, 5.0f);
        addDrink("harvestcraft:pinacoladaitem", 6, 5.0f);
        addDrink("harvestcraft:eggnogitem", 15, 12.0f);
        addDrink("harvestcraft:soymilkitem", 3, 3.0f);
        addDrink("harvestcraft:applecideritem", 6, 5.0f);
        addDrink("harvestcraft:energydrinkitem", 15, 12.0f);
        addDrink("harvestcraft:greenteaitem", 6, 5.0f);
        addDrink("harvestcraft:earlgreyteaitem", 6, 5.0f);
        addDrink("harvestcraft:bubbleteaitem", 12, 9.0f);
        addDrink("harvestcraft:rosepetalteaitem", 6, 5.0f);
        addDrink("harvestcraft:cherryslushieitem", 9, 7.0f);
        addDrink("harvestcraft:lycheeteaitem", 9, 7.0f);
        addDrink("harvestcraft:dandelionteaitem", 9, 7.0f);
        addDrink("harvestcraft:raspberrymilkshakeitem", 15, 12.0f);
        addDrink("harvestcraft:pumpkinspicelatteitem", 20, 19.0f);
        addDrink("harvestcraft:rootbeerfloatitem", 18, 15.0f);
        addDrink("harvestcraft:hotcocoaitem", 15, 12.0f);
        addDrink("harvestcraft:strawberrymilkshakeitem", 9, 7.0f);
        addDrink("harvestcraft:chocolatemilkshakeitem", 9, 7.0f);
        addDrink("harvestcraft:bananamilkshakeitem", 9, 7.0f);
        addDrink("harvestcraft:gooseberrymilkshakeitem", 9, 7.0f);
        addDrink("harvestcraft:durianmilkshakeitem", 9, 7.0f);
        addDrink("harvestcraft:cookiesandmilkitem", 8, 7.0f);
        addDrink("harvestcraft:sundayhighteaitem", 12, 10.0f);
        addDrink("harvestcraft:delightedmealitem", 14, 13.0f);
        addDrink("harvestcraft:weekendpicnicitem", 20, 20.0f);
        addDrink("harvestcraft:theatreboxitem", 16, 15.0f);
        addDrink("harvestcraft:friedfeastitem", 20, 20.0f);
        addDrink("harvestcraft:bbqplatteritem", 20, 20.0f);

        // Candles
        for (int i = 1; i <= 16; i++) {
            addHeldItemTemperature("harvestcraft:candledeco" + i, 1.0f);
            addBlockTemperature("harvestcraft:candledeco" + i, 1.0f);
        }

        return true;
    }

    private boolean populateLycanitesMobs() {
        if (!canUseModJsonDefaults(LYCANITESMOBS)) return false;

        addBlockTemperature("lycanitesmobs:purelava", 12.5f);
        addBlockTemperature("lycanitesmobs:moglava", 12.5f);
        addBlockTemperature("lycanitesmobs:ooze", -12.5f);
        addBlockTemperature("lycanitesmobs:rabbitooze", -12.5f);
        addBlockTemperature("lycanitesmobs:frostfire", -5.0f);
        addBlockTemperature("lycanitesmobs:icefire", -8.0f);

        return true;
    }

    private boolean populatePyrotech() {
        if (!canUseModJsonDefaults(PYROTECH)) return false;

        addBlockTemperature("pyrotech:torch_fiber", 3.0f, new JsonPropertyValue("type", "LIT"));
        addBlockTemperature("pyrotech:torch_stone", 3.0f, new JsonPropertyValue("type", "LIT"));
        addBlockTemperature("pyrotech:campfire", 7.0f, new JsonPropertyValue("variant", "LIT"));
        addBlockTemperature("pyrotech:kiln_pit", 6.0f, new JsonPropertyValue("variant", "ACTIVE"));
        addBlockTemperature("pyrotech:bloomery", 6.0f, new JsonPropertyValue("type", "BottomLit"));
        addBlockTemperature("pyrotech:wither_forge", 7.0f, new JsonPropertyValue("type", "BottomLit"));
        addBlockTemperature("pyrotech:pile_slag", 4.0f, new JsonPropertyValue("molten", "true"));

        float pyrotechStone = 5.0f;
        float pyroTechBrick = 4.0f;

        addBlockTemperature("pyrotech:stone_kiln", pyrotechStone, new JsonPropertyValue("type", "BOTTOM_LIT"));
        addBlockTemperature("pyrotech:stone_oven", pyrotechStone, new JsonPropertyValue("type", "BOTTOM_LIT"));
        addBlockTemperature("pyrotech:stone_sawmill", pyrotechStone, new JsonPropertyValue("type", "BOTTOM_LIT"));
        addBlockTemperature("pyrotech:stone_crucible", pyrotechStone, new JsonPropertyValue("type", "BOTTOM_LIT"));
        addBlockTemperature("pyrotech:brick_kiln", pyroTechBrick, new JsonPropertyValue("type", "BOTTOM_LIT"));
        addBlockTemperature("pyrotech:brick_oven", pyroTechBrick, new JsonPropertyValue("type", "BOTTOM_LIT"));
        addBlockTemperature("pyrotech:brick_sawmill", pyroTechBrick, new JsonPropertyValue("type", "BOTTOM_LIT"));
        addBlockTemperature("pyrotech:brick_crucible", pyroTechBrick, new JsonPropertyValue("type", "BOTTOM_LIT"));

        return true;
    }

    private boolean populateRealisticTorches() {
        if (!canUseModJsonDefaults(REALISTICTORCHES)) return false;

        addHeldItemTemperature("realistictorches:torch_lit", 1.0f);
        addBlockTemperature("realistictorches:torch_lit", 1.0f);

        return true;
    }

    private boolean populateRustic() {
        if (!canUseModJsonDefaults(RUSTIC)) return false;

        addHeldItemTemperature("rustic:candle", 1.0f);
        addBlockTemperature("rustic:candle", 1.0f);
        addHeldItemTemperature("rustic:candle_gold", 1.0f);
        addBlockTemperature("rustic:candle_gold", 1.0f);

        int boozeThirst = 5;
        float boozeSaturation = 5.0f;
        float boozeSickness = 0.25f;
        int juiceThirst = 6;
        float juiceSaturation = 5.0f;

        addDrink("rustic:elixir", 4, 0.4f);
        addDrink("rustic:fluid_bottle", 2, 0.1f, 0.9f, rusticFluid("oliveoil"));
        addDrink("rustic:fluid_bottle", 2, 8.0f, rusticFluid("ironberryjuice"));
        addDrink("rustic:fluid_bottle", juiceThirst, juiceSaturation, rusticFluid("wildberryjuice"));
        addDrink("rustic:fluid_bottle", juiceThirst, juiceSaturation, rusticFluid("grapejuice"));
        addDrink("rustic:fluid_bottle", juiceThirst, juiceSaturation, rusticFluid("applejuice"));
        addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, rusticFluid("alewort"));
        addDrink("rustic:fluid_bottle", 4, 3.0f, rusticFluid("honey"));
        addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, rusticFluid("ale"));
        addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, rusticFluid("cider"));
        addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, rusticFluid("ironwine"));
        addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, rusticFluid("mead"));
        addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, rusticFluid("wildberrywine"));
        addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, rusticFluid("wine"));

        return true;
    }

    private String rusticFluid(String name) {
        return "{Fluid:{FluidName:\"" + name + "\"}}";
    }

    private boolean populateSimpleCampfire() {
        if (!canUseModJsonDefaults(SIMPLECAMPFIRE)) return false;

        addBlockTemperature("campfire:campfire", 7.0f);
        return true;
    }

    private boolean populateTinkersConstruct() {
        if (!canUseModJsonDefaults(TINKERSCONSTRUCT)) return false;

        float moltenTemp = 12.5f;

        addBlockTemperature("tconstruct:molten_alubrass", moltenTemp);
        addBlockTemperature("tconstruct:molten_aluminum", moltenTemp);
        addBlockTemperature("tconstruct:molten_ardite", moltenTemp);
        addBlockTemperature("tconstruct:molten_brass", moltenTemp);
        addBlockTemperature("tconstruct:molten_bronze", moltenTemp);
        addBlockTemperature("tconstruct:molten_clay", moltenTemp);
        addBlockTemperature("tconstruct:molten_cobalt", moltenTemp);
        addBlockTemperature("tconstruct:molten_copper", moltenTemp);
        addBlockTemperature("tconstruct:molten_dirt", moltenTemp);
        addBlockTemperature("tconstruct:molten_electrum", moltenTemp);
        addBlockTemperature("tconstruct:molten_emerald", moltenTemp);
        addBlockTemperature("tconstruct:molten_glass", moltenTemp);
        addBlockTemperature("tconstruct:molten_gold", moltenTemp);
        addBlockTemperature("tconstruct:molten_iron", moltenTemp);
        addBlockTemperature("tconstruct:molten_knightslime", moltenTemp);
        addBlockTemperature("tconstruct:molten_lead", moltenTemp);
        addBlockTemperature("tconstruct:molten_manyullyn", moltenTemp);
        addBlockTemperature("tconstruct:molten_nickel", moltenTemp);
        addBlockTemperature("tconstruct:molten_obsidian", moltenTemp);
        addBlockTemperature("tconstruct:molten_pigiron", moltenTemp);
        addBlockTemperature("tconstruct:molten_silver", moltenTemp);
        addBlockTemperature("tconstruct:molten_steel", moltenTemp);
        addBlockTemperature("tconstruct:molten_stone", moltenTemp);
        addBlockTemperature("tconstruct:molten_tin", moltenTemp);
        addBlockTemperature("tconstruct:molten_zinc", moltenTemp);

        float lavawood = 5.0f;
        float firewood = 7.0f;

        addBlockTemperature("tconstruct:firewood", lavawood, new JsonPropertyValue("type", "LAVAWOOD"));
        addBlockTemperature("tconstruct:firewood", firewood, new JsonPropertyValue("type", "FIREWOOD"));
        addBlockTemperature("tconstruct:firewood_stairs", firewood);
        addBlockTemperature("tconstruct:lavawood_stairs", lavawood);
        addBlockTemperature("tconstruct:firewood_slab", lavawood, new JsonPropertyValue("type", "LAVAWOOD"));
        addBlockTemperature("tconstruct:firewood_slab", firewood, new JsonPropertyValue("type", "FIREWOOD"));

        float heldWood = 2.0f;

        addHeldItemTemperature("tconstruct:firewood", heldWood);
        addHeldItemTemperature("tconstruct:firewood_stairs", heldWood);
        addHeldItemTemperature("tconstruct:lavawood_stairs", heldWood);
        addHeldItemTemperature("tconstruct:firewood_slab", heldWood);
        addHeldItemTemperature("tconstruct:stone_torch", 1.0f);

        return true;
    }

    // API Methods

    private void addBlockTemperature(String registryName, float temperature, JsonPropertyValue... properties) {
        JsonConfig.registerBlockTemperature(registryName, temperature, properties);
    }

    private void addDrink(String registryName, int amount, float saturation) {
        addDrink(registryName, amount, saturation, 0.0f);
    }

    private void addDrink(String registryName, int amount, float saturation, float thirstyChance) {
        JsonConfig.registerConsumableThirst(registryName, amount, saturation, thirstyChance, new JsonItemIdentity());
    }

    private void addDrink(String registryName, int amount, float saturation, String nbt) {
        JsonConfig.registerConsumableThirst(registryName, amount, saturation, 0.0f, new JsonItemIdentity(nbt));
    }

    private void addDrink(String registryName, int amount, float saturation, float thirstyChance, String nbt) {
        JsonConfig.registerConsumableThirst(registryName, amount, saturation, thirstyChance, new JsonItemIdentity(nbt));
    }

    private void addFluidTemperature(String fluidName, float temperature) {
        JsonConfig.registerFluidTemperature(fluidName, temperature);
    }

    private void addHeldItemTemperature(String registryName, float temperature) {
        JsonConfig.registerHeldItem(registryName, temperature, new JsonItemIdentity());
    }

    private boolean canUseModJsonDefaults(String modid) {
        return canUseModJsonDefaults(modid, true);
    }

    private boolean canUseModJsonDefaults(String modid, boolean modBool) {
        return modBool && CompatUtil.canUseMod(modid) && !SDCompatibility.disabledDefaultJson.contains(modid);
    }
}