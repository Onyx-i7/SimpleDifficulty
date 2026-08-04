package com.charles445.simpledifficulty.register.crafting;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Custom shapeless recipe that purifies water in canteens using charcoal.
 * Preserves the current dose count while changing the water type to PURIFIED.
 */
public class CanteenCharcoalRecipe extends ShapelessRecipe {

    public CanteenCharcoalRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(id, group, result, ingredients);
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingInventory inv) {
        ItemStack output = super.assemble(inv);

        if (!output.isEmpty()) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack ingredient = inv.getItem(i);
                if (!ingredient.isEmpty() && 
                        (ingredient.getItem() == SDItems.canteen.get() || ingredient.getItem() == SDItems.ironCanteen.get())) {
                    if (ingredient.getItem() instanceof IItemCanteen) {
                        IItemCanteen canteen = (IItemCanteen) ingredient.getItem();
                        // Charcoal filter purifies water, so set to PURIFIED
                        canteen.setDoses(output, ThirstEnum.PURIFIED, canteen.getDoses(ingredient));
                    }
                    break;
                }
            }
        }

        return output;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CanteenCharcoalRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = new ResourceLocation(SimpleDifficulty.MODID, "canteen_charcoal");

        @Override
        public CanteenCharcoalRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = net.minecraft.util.JSONUtils.getString(json, "group", "");
            
            com.google.gson.JsonArray ingredientsJson = net.minecraft.util.JSONUtils.getJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (com.google.gson.JsonElement element : ingredientsJson) {
                ingredients.add(Ingredient.fromJson(element));
            }

            ItemStack result = net.minecraft.item.crafting.ShapedRecipe.fromJson(net.minecraft.util.JSONUtils.getJsonObject(json, "result"));

            return new CanteenCharcoalRecipe(recipeId, group, result, ingredients);
        }

        @Override
        public CanteenCharcoalRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readUtf(32767);
            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }
            ItemStack result = buffer.readItem();

            return new CanteenCharcoalRecipe(recipeId, group, result, ingredients);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, CanteenCharcoalRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.getResultItem());
        }
    }
}