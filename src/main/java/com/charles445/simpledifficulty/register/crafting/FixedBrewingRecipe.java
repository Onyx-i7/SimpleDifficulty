package com.charles445.simpledifficulty.register.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

public class FixedBrewingRecipe extends BrewingRecipe {
    public FixedBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        super(input, ingredient, output);
    }

    @Override
    public boolean isInput(ItemStack stack) {
        return super.isInput(stack) && ItemStack.tagMatches(getInput().getItems()[0], stack);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return this.getIngredient().test(ingredient);
    }
}