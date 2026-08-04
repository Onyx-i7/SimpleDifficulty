package com.charles445.simpledifficulty.register.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.AbstractBrewingRecipe;

import javax.annotation.Nonnull;

/**
 * Fixed brewing recipe that properly handles NBT data for JEI compatibility.
 * This ensures that potion recipes with NBT data are correctly recognized by JEI.
 * 
 * In 1.16.5, OreDictionary was replaced with Tags, so this class now uses
 * ItemStack matching with NBT comparison instead of OreDictionary.
 */
public class FixedBrewingRecipe extends AbstractBrewingRecipe<ItemStack> {

    public FixedBrewingRecipe(ItemStack input, ItemStack ingredient, ItemStack output) {
        super(input, ingredient, output);
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack) {
        // Ensure NBT tags match exactly for proper JEI recognition
        return super.isInput(stack) && ItemStack.tagMatches(getInput(), stack);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        // In 1.16.5, use ItemStack.matches which checks item, count, and NBT
        return ItemStack.matches(this.getIngredient(), ingredient);
    }
}