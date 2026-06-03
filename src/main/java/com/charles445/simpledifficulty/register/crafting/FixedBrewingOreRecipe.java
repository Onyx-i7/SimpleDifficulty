package com.charles445.simpledifficulty.register.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.AbstractBrewingRecipe;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

/**
 * Fixed brewing recipe that properly handles NBT data for JEI compatibility.
 * This ensures that potion recipes with NBT data are correctly recognized by JEI.
 */
public class FixedBrewingOreRecipe extends AbstractBrewingRecipe<ItemStack>
{
    // This fixes JEI detection issues with potion recipes containing NBT data
    // Standard BrewingRecipe and BrewingOreRecipe don't handle NBT properly
    
    public FixedBrewingOreRecipe(ItemStack input, ItemStack ingredient, ItemStack output)
    {
        super(input, ingredient, output);
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack)
    {
        // Ensure NBT tags match exactly for proper JEI recognition
        return super.isInput(stack) && ItemStack.areItemStackTagsEqual(getInput(), stack);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient)
    {
        return OreDictionary.itemMatches(this.getIngredient(), ingredient, false);
    }
}
