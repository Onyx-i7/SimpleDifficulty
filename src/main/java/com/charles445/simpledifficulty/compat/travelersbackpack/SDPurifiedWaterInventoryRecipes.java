package com.charles445.simpledifficulty.compat.travelersbackpack;

import com.charles445.simpledifficulty.api.SDItems;
import com.tiviacz.travelersbackpack.api.inventory.InventoryRecipe;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidRegistry;

public class SDPurifiedWaterInventoryRecipes {
    public static SDPurifiedWaterInventoryRecipes.SDPurifiedWaterInventoryRecipeIn RECIPE_IN;
    public static SDPurifiedWaterInventoryRecipes.SDPurifiedWaterInventoryRecipeOut RECIPE_OUT;

    public static void registerRecipes() {
        RECIPE_IN = new SDPurifiedWaterInventoryRecipes.SDPurifiedWaterInventoryRecipeIn();
        RECIPE_OUT = new SDPurifiedWaterInventoryRecipes.SDPurifiedWaterInventoryRecipeOut();
    }

    public static class SDPurifiedWaterInventoryRecipeIn extends InventoryRecipe {
        public SDPurifiedWaterInventoryRecipeIn() {
            /* Pour into backpack: takes a purified bottle and yields an empty glass bottle */
            super(SDItems.purifiedWaterBottle, FluidRegistry.getFluid("purifiedwater"), 250, Items.GLASS_BOTTLE);
        }

        @Override
        public SoundEvent getSoundEventFill() {
            return SoundEvents.ITEM_BOTTLE_FILL;
        }

        @Override
        public SoundEvent getSoundEventEmpty() {
            return SoundEvents.ITEM_BOTTLE_EMPTY;
        }
    }

    public static class SDPurifiedWaterInventoryRecipeOut extends InventoryRecipe {
        public SDPurifiedWaterInventoryRecipeOut() {
            /* Extract from backpack: takes an empty bottle and returns a full purified bottle */
            super(Items.GLASS_BOTTLE, FluidRegistry.getFluid("purifiedwater"), -250, SDItems.purifiedWaterBottle);
        }

        @Override
        public SoundEvent getSoundEventFill() {
            return SoundEvents.ITEM_BOTTLE_FILL;
        }

        @Override
        public SoundEvent getSoundEventEmpty() {
            return SoundEvents.ITEM_BOTTLE_EMPTY;
        }
    }
}
