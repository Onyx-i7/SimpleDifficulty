package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.register.crafting.FixedBrewingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.item.Item;
import net.minecraft.potion.Potions;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registers custom recipes for SimpleDifficulty.
 * Most recipes are handled via JSON files in data/<modid>/recipes/.
 * This class handles programmatic recipe registration for potions and smelting.
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterRecipes {
    public static boolean POTION_RECIPES_AS_VANILLA = true;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Note: Smelting recipes should be in data/simpledifficulty/recipes/ as JSON files
            // Example: purified_water_bottle_from_smelting.json
            
            // Potion recipes (programmatic registration still needed)
            if (POTION_RECIPES_AS_VANILLA) {
                // This allows for CraftTweaker support
                registerPotionMixes();
            } else {
                // Custom brewing recipes (doesn't allow CraftTweaker removal)
                registerCustomBrewingRecipes();
            }
        });
    }

    // private static void registerPotionMixes() {
    //      // TODO: Implement using JSON or reflection
    // }

    private static void registerCustomBrewingRecipes() {
        // Awkward to normal
        registerSameItemPotionRecipes(Potions.AWKWARD, SDItems.ice_chunk.get(), SDPotions.cold_resist_type.get());
        registerSameItemPotionRecipes(Potions.AWKWARD, SDItems.magma_chunk.get(), SDPotions.heat_resist_type.get());

        // Normal to long
        registerSameItemPotionRecipes(SDPotions.cold_resist_type.get(), Items.REDSTONE, SDPotions.long_cold_resist_type.get());
        registerSameItemPotionRecipes(SDPotions.heat_resist_type.get(), Items.REDSTONE, SDPotions.long_heat_resist_type.get());

        // Item to Item conversions
        registerConversionPotionRecipes(SDPotions.cold_resist_type.get());
        registerConversionPotionRecipes(SDPotions.heat_resist_type.get());
        registerConversionPotionRecipes(SDPotions.long_cold_resist_type.get());
        registerConversionPotionRecipes(SDPotions.long_heat_resist_type.get());
    }

    private static void registerConversionPotionRecipes(Potion potionType) {
        registerPotionRecipe(Items.POTION, potionType, Items.DRAGON_BREATH, Items.LINGERING_POTION, potionType);
        registerPotionRecipe(Items.SPLASH_POTION, potionType, Items.DRAGON_BREATH, Items.LINGERING_POTION, potionType);

        registerPotionRecipe(Items.POTION, potionType, Items.GUNPOWDER, Items.SPLASH_POTION, potionType);
        registerPotionRecipe(Items.LINGERING_POTION, potionType, Items.GUNPOWDER, Items.SPLASH_POTION, potionType);
    }

    private static void registerSameItemPotionRecipes(Potion itemInPotionType, Item ingredient, Potion itemOutPotionType) {
        registerPotionRecipe(Items.POTION, itemInPotionType, ingredient, Items.POTION, itemOutPotionType);
        registerPotionRecipe(Items.LINGERING_POTION, itemInPotionType, ingredient, Items.LINGERING_POTION, itemOutPotionType);
        registerPotionRecipe(Items.SPLASH_POTION, itemInPotionType, ingredient, Items.SPLASH_POTION, itemOutPotionType);
    }

    private static void registerPotionRecipe(Item itemIn, Potion itemInPotionType, 
                                          Item ingredient, 
                                          Item itemOut, Potion itemOutPotionType) {
        BrewingRecipeRegistry.addRecipe(
                new FixedBrewingRecipe(
                        toPotion(new ItemStack(itemIn), itemInPotionType),
                        new ItemStack(ingredient),
                        toPotion(new ItemStack(itemOut), itemOutPotionType)
                )
        );
    }

    private static ItemStack toPotion(ItemStack stack, Potion potionType) {
        return PotionUtils.setPotion(stack, potionType);
    }
}