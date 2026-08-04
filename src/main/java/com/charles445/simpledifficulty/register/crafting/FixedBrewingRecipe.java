package com.charles445.simpledifficulty.register.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingRecipe;
import javax.annotation.Nonnull;

public class FixedBrewingRecipe extends BrewingRecipe {
    public FixedBrewingRecipe(ItemStack input, ItemStack ingredient, ItemStack output) {
        super(input, ingredient, output);
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack) {
        return super.isInput(stack) && ItemStack.areItemStackTagsEqual(getInput(), stack);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ItemStack.areItemsEqual(getIngredient(), ingredient);
    }
}