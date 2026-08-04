package com.charles445.simpledifficulty.register.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;

/**
 * Utility class for recipe parsing.
 * In 1.16.5, most recipe parsing is handled by Forge's built-in systems.
 * This class provides helper methods for custom recipe types.
 */
public class RecipeUtil {

    /**
     * Parses shapeless recipe ingredients from JSON.
     *
     * @param json The JSON object containing the ingredients array.
     * @return A NonNullList of Ingredients.
     * @throws JsonSyntaxException if the ingredients array is empty or invalid.
     */
    public static NonNullList<Ingredient> getShapelessIngredients(JsonObject json) {
        NonNullList<Ingredient> ingList = NonNullList.create();
        JsonArray ingredientsJson = JSONUtils.getAsJsonArray(json, "ingredients");
        
        for (JsonElement ele : ingredientsJson) {
            ingList.add(Ingredient.fromJson(ele));
        }

        if (ingList.isEmpty()) {
            throw new JsonSyntaxException("No ingredients for shapeless recipe");
        }

        return ingList;
    }

    /**
     * Parses a single ingredient from JSON.
     *
     * @param json The JSON object or array representing the ingredient.
     * @return The parsed Ingredient.
     */
    public static Ingredient getIngredient(JsonElement json) {
        return Ingredient.fromJson(json);
    }
}