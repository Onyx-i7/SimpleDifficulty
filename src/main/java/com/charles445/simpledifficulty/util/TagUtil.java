package com.charles445.simpledifficulty.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for working with item tags (replacement for OreDictionary in 1.16.5).
 * Provides helper methods to check if items belong to specific tags.
 */
public class TagUtil {

    // Legacy field names kept for addon compatibility (deprecated)
    @Deprecated
    public static final List<ItemStack> listAlljuice = getItemsFromTag("forge:juices");

    @Deprecated
    public static final List<ItemStack> listAllsmoothie = getItemsFromTag("forge:smoothies");

    @Deprecated
    public static final List<ItemStack> listAllsoda = getItemsFromTag("forge:sodas");

    @Deprecated
    public static final List<ItemStack> logWood = getItemsFromTag("forge:logs");

    @Deprecated
    public static final List<ItemStack> stick = getItemsFromTag("forge:rods/wooden");

    // NEW METHODS using Tag system

    /**
     * Gets all items that belong to a specific tag.
     *
     * @param tagName The tag name (e.g., "forge:logs", "forge:rods/wooden").
     * @return A list of ItemStacks representing all items with that tag.
     */
    public static List<ItemStack> getItemsFromTag(String tagName) {
        ITag<Item> tag = ItemTags.getAllTags().getTag(new ResourceLocation(tagName));
        if (tag == null) {
            return java.util.Collections.emptyList();
        }
        return tag.getValues().stream()
                .map(ItemStack::new)
                .collect(Collectors.toList());
    }

    /**
     * Gets all items that belong to a specific tag (real-time lookup).
     *
     * @param tagName The tag name.
     * @return A list of ItemStacks.
     */
    public static List<ItemStack> getListAlljuice() {
        return getItemsFromTag("forge:juices");
    }

    public static List<ItemStack> getListAllsmoothie() {
        return getItemsFromTag("forge:smoothies");
    }

    public static List<ItemStack> getListAllsoda() {
        return getItemsFromTag("forge:sodas");
    }

    public static List<ItemStack> getLogWood() {
        return getItemsFromTag("forge:logs");
    }

    public static List<ItemStack> getStick() {
        return getItemsFromTag("forge:rods/wooden");
    }

    /**
     * Checks if an ItemStack belongs to a specific tag.
     *
     * @param tagName The tag name to check (e.g., "forge:logs", "forge:rods/wooden").
     * @param stack The ItemStack to check.
     * @return true if the item belongs to the tag.
     */
    public static boolean isInTag(String tagName, ItemStack stack) {
        if (stack.isEmpty()) return false;
        ITag<Item> tag = ItemTags.getAllTags().getTag(new ResourceLocation(tagName));
        return tag != null && tag.contains(stack.getItem());
    }

    /**
     * Legacy method for backward compatibility.
     * Checks if an ItemStack matches any item in the given tag list.
     *
     * @param tagName The tag name.
     * @param stackCheck The ItemStack to check.
     * @return true if the stack matches.
     */
    @Deprecated
    public static boolean isOre(String tagName, ItemStack stackCheck) {
        return isInTag(tagName, stackCheck);
    }

    /**
     * Checks if an item is in the given tag.
     *
     * @param tag The tag to check.
     * @param stack The ItemStack to check.
     * @return true if the item is in the tag.
     */
    public static boolean isInTag(ITag<Item> tag, ItemStack stack) {
        return tag != null && !stack.isEmpty() && tag.contains(stack.getItem());
    }
}