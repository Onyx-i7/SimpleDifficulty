package com.charles445.simpledifficulty.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.List;

public class OreDictUtil {
    // LEGACY FIELDS (Deprecated - for addon compatibility)
    // These fields are kept for backward compatibility with addons
    // They reference the live OreDictionary lists, so they will update
    // when new items are registered, but they may cause memory issues
    // in some edge cases. New code should use the getter methods below
    
    @Deprecated
    public static final NonNullList<ItemStack> listAlljuice = OreDictionary.getOres("listAlljuice");
    
    @Deprecated
    public static final NonNullList<ItemStack> listAllsmoothie = OreDictionary.getOres("listAllsmoothie");
    
    @Deprecated
    public static final NonNullList<ItemStack> listAllsoda = OreDictionary.getOres("listAllsoda");
    
    @Deprecated
    public static final NonNullList<ItemStack> logWood = OreDictionary.getOres("logWood");
    
    @Deprecated
    public static final NonNullList<ItemStack> stick = OreDictionary.getOres("stickWood");
    
    // NEW METHODS
    // These methods get the OreDictionary list in real-time,
    // avoiding memory leaks and ensuring data is always current
    
    public static List<ItemStack> getListAlljuice() {
        return OreDictionary.getOres("listAlljuice");
    }
    
    public static List<ItemStack> getListAllsmoothie() {
        return OreDictionary.getOres("listAllsmoothie");
    }
    
    public static List<ItemStack> getListAllsoda() {
        return OreDictionary.getOres("listAllsoda");
    }
    
    public static List<ItemStack> getLogWood() {
        return OreDictionary.getOres("logWood");
    }
    
    public static List<ItemStack> getStick() {
        return OreDictionary.getOres("stickWood");
    }
    
    // UTILITY METHODS
    
    /**
     * Check if an ItemStack matches any item in the given OreDictionary list
     * 
     * @param stackList The OreDictionary list to check against
     * @param stackCheck The ItemStack to check
     * @return true if the stack matches any item in the list
    */
    public static boolean isOre(NonNullList<ItemStack> stackList, ItemStack stackCheck) {
        return OreDictUtil.containsMatch(false, stackList, stackCheck);
    }
    
    /**
     * Check if an ItemStack matches any item in the given OreDictionary name
     * This is the recommended method for new code
     * 
     * @param oreName The OreDictionary name (e.g., "logWood", "stickWood")
     * @param stackCheck The ItemStack to check
     * @return true if the stack matches any item with that OreDictionary name
    */
    public static boolean isOre(String oreName, ItemStack stackCheck) {
        List<ItemStack> oreList = OreDictionary.getOres(oreName);
        return containsMatch(false, oreList, stackCheck);
    }
    
    /**
     * Check if any item in the inputs list matches any item in the targets array
     * 
     * @param strict Whether to use strict matching (metadata sensitive)
     * @param inputs The list of ItemStacks to check
     * @param targets The target ItemStacks to match against
     * @return true if any match is found
     */
    public static boolean containsMatch(boolean strict, List<ItemStack> inputs, @Nonnull ItemStack... targets) {
        for (ItemStack input : inputs) {
            for (ItemStack target : targets) {
                // SWAPPED INPUT AND TARGET
                // Why is it like this
                if (OreDictionary.itemMatches(input, target, strict)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Overload for NonNullList (legacy compatibility)
    */
    public static boolean containsMatch(boolean strict, NonNullList<ItemStack> inputs, @Nonnull ItemStack... targets) {
        return containsMatch(strict, (List<ItemStack>) inputs, targets);
    }
}
