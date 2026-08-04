package com.charles445.simpledifficulty.register.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;

/**
 * Fixed brewing recipe that properly handles NBT data for JEI compatibility.
 */
public class FixedBrewingRecipe extends BrewingRecipe {

    public FixedBrewingRecipe(ItemStack input, ItemStack ingredient, ItemStack output) {
        super(input, ingredient, output);
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack) {
        return super.isInput(stack) && ItemStack.tagMatches(getInput(), stack);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ItemStack.matches(this.getIngredient(), ingredient);
    }
}