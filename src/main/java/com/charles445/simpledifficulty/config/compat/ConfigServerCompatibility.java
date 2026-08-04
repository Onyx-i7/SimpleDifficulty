package com.charles445.simpledifficulty.config.compat;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Server-side compatibility configuration builder.
 * Integrates with ForgeConfigSpec for modern configuration management.
 */
public class ConfigServerCompatibility {

    // Toggles
    public final ForgeConfigSpec.BooleanValue armorUnderwear;
    public final ForgeConfigSpec.BooleanValue baubles;
    public final ForgeConfigSpec.BooleanValue betweenlands;
    public final ForgeConfigSpec.BooleanValue harvestFestival;
    public final ForgeConfigSpec.BooleanValue inspirations;
    public final ForgeConfigSpec.BooleanValue oreExcavation;
    public final ForgeConfigSpec.BooleanValue sereneSeasons;

    // Armor Underwear
    public final ForgeConfigSpec.DoubleValue goopakTemperatureModifier;
    public final ForgeConfigSpec.IntValue goopakMaximumActive;
    public final ForgeConfigSpec.DoubleValue ozzyBaseRange;
    public final ForgeConfigSpec.DoubleValue ozzyExtraRange;
    public final ForgeConfigSpec.DoubleValue linerMultiplier;

    // Harvest Festival
    public final ForgeConfigSpec.IntValue seasonWinter;
    public final ForgeConfigSpec.IntValue seasonSpring;
    public final ForgeConfigSpec.IntValue seasonSummer;
    public final ForgeConfigSpec.IntValue seasonAutumn;

    // Serene Seasons
    public final ForgeConfigSpec.IntValue seasonEarlyWinter;
    public final ForgeConfigSpec.IntValue seasonMidWinter;
    public final ForgeConfigSpec.IntValue seasonLateWinter;
    public final ForgeConfigSpec.IntValue seasonEarlySpring;
    public final ForgeConfigSpec.IntValue seasonMidSpring;
    public final ForgeConfigSpec.IntValue seasonLateSpring;
    public final ForgeConfigSpec.IntValue seasonEarlySummer;
    public final ForgeConfigSpec.IntValue seasonMidSummer;
    public final ForgeConfigSpec.IntValue seasonLateSummer;
    public final ForgeConfigSpec.IntValue seasonEarlyAutumn;
    public final ForgeConfigSpec.IntValue seasonMidAutumn;
    public final ForgeConfigSpec.IntValue seasonLateAutumn;

    // Betweenlands
    public final ForgeConfigSpec.IntValue betweenlandsDimensionTemperature;
    public final ForgeConfigSpec.BooleanValue enableCleanWater;
    public final ForgeConfigSpec.DoubleValue swampWaterDirtyChance;

    public ConfigServerCompatibility(ForgeConfigSpec.Builder builder) {
        // Compatibility Toggles
        builder.comment("Built-In Compatibility Toggles - Turn compatibility for mods on or off")
                .push("Compatibility");

        armorUnderwear = builder.comment("Enable Armor Underwear - Built-In Compatibility")
                .define("EnableArmorUnderwear", true);
        baubles = builder.comment("Enable Baubles - Built-In Compatibility")
                .define("EnableBaubles", true);
        betweenlands = builder.comment("Enable The Betweenlands - Built-In Compatibility")
                .define("EnableBetweenlands", true);
        harvestFestival = builder.comment("Enable Harvest Festival - Built-In Compatibility")
                .define("EnableHarvestFestival", true);
        inspirations = builder.comment("Enable Inspirations - Built-In Compatibility")
                .define("EnableInspirations", true);
        oreExcavation = builder.comment("Enable OreExcavation - Built-In Compatibility")
                .define("EnableOreExcavation", true);
        sereneSeasons = builder.comment("Enable Serene Seasons - Built-In Compatibility")
                .define("EnableSereneSeasons", true);

        builder.pop();

        // Armor Underwear
        builder.comment("Armor Underwear Configurations")
                .push("ArmorUnderwear");

        goopakTemperatureModifier = builder.comment("Goopak Temperature Modifier - Effect of a Goopak on temperature")
                .defineInRange("GoopakTemperatureModifier", 2.0d, 0.0d, Double.MAX_VALUE);
        goopakMaximumActive = builder.comment("Goopak Maximum Active - How many Goopaks can stack their effects at once")
                .defineInRange("GoopakMaximumActive", 5, 1, Integer.MAX_VALUE);
        ozzyBaseRange = builder.comment("Ozzy Base Range - The base temperature range of an Ozzy Liner")
                .defineInRange("OzzyBaseRange", 3.0d, 0.0d, Double.MAX_VALUE);
        ozzyExtraRange = builder.comment("Ozzy Extra Range - The added temperature range when upgrading an Ozzy Liner")
                .defineInRange("OzzyExtraRange", 3.0d, 0.0d, Double.MAX_VALUE);
        linerMultiplier = builder.comment("Liner Multiplier - Multiplier for the effect of normal liners")
                .defineInRange("LinerMultiplier", 1.0d, 0.0d, Double.MAX_VALUE);

        builder.pop();

        // Harvest Festival
        builder.comment("Harvest Festival Configurations")
                .push("HarvestFestival");

        seasonWinter = builder.comment("Season Winter - Temperature change during the Harvest Festival season")
                .defineInRange("SeasonWinter", -10, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonSpring = builder.comment("Season Spring - Temperature change during the Harvest Festival season")
                .defineInRange("SeasonSpring", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonSummer = builder.comment("Season Summer - Temperature change during the Harvest Festival season")
                .defineInRange("SeasonSummer", 4, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonAutumn = builder.comment("Season Autumn - Temperature change during the Harvest Festival season")
                .defineInRange("SeasonAutumn", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        builder.pop();

        // Serene Seasons
        builder.comment("Serene Seasons Configurations")
                .push("SereneSeasons");

        seasonEarlyWinter = builder.comment("Season Early Winter - Temperature change")
                .defineInRange("SeasonEarlyWinter", -7, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonMidWinter = builder.comment("Season Mid Winter - Temperature change")
                .defineInRange("SeasonMidWinter", -14, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonLateWinter = builder.comment("Season Late Winter - Temperature change")
                .defineInRange("SeasonLateWinter", -7, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonEarlySpring = builder.comment("Season Early Spring - Temperature change")
                .defineInRange("SeasonEarlySpring", -3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonMidSpring = builder.comment("Season Mid Spring - Temperature change")
                .defineInRange("SeasonMidSpring", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonLateSpring = builder.comment("Season Late Spring - Temperature change")
                .defineInRange("SeasonLateSpring", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonEarlySummer = builder.comment("Season Early Summer - Temperature change")
                .defineInRange("SeasonEarlySummer", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonMidSummer = builder.comment("Season Mid Summer - Temperature change")
                .defineInRange("SeasonMidSummer", 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonLateSummer = builder.comment("Season Late Summer - Temperature change")
                .defineInRange("SeasonLateSummer", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonEarlyAutumn = builder.comment("Season Early Autumn - Temperature change")
                .defineInRange("SeasonEarlyAutumn", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonMidAutumn = builder.comment("Season Mid Autumn - Temperature change")
                .defineInRange("SeasonMidAutumn", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        seasonLateAutumn = builder.comment("Season Late Autumn - Temperature change")
                .defineInRange("SeasonLateAutumn", -3, Integer.MIN_VALUE, Integer.MAX_VALUE);

        builder.pop();

        // Betweenlands
        builder.comment("The Betweenlands Configurations")
                .push("Betweenlands");

        betweenlandsDimensionTemperature = builder.comment("Dimension Temperature - Temperature modifier for The Betweenlands dimension")
                .defineInRange("DimensionTemperature", -3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        enableCleanWater = builder.comment("Enable Clean Water - Allow Clean Water from Betweenlands to be drinkable")
                .define("EnableCleanWater", true);
        swampWaterDirtyChance = builder.comment("Swamp Water Dirty Chance - Chance that drinking swamp water will make you thirsty (0.0-1.0)")
                .defineInRange("SwampWaterDirtyChance", 0.85d, 0.0d, 1.0d);

        builder.pop();
    }
}