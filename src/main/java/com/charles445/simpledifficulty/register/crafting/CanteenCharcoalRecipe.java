package com.charles445.simpledifficulty.register.crafting;

import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.item.ItemCanteen;
import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

public class CanteenCharcoalRecipe extends ShapelessOreRecipe
{
	public CanteenCharcoalRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result)
	{
		 super(group,input,result);
	}
	
	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting invcraft)
	{
		ItemStack output = super.getCraftingResult(invcraft);
		
		if(!output.isEmpty())
		{
			for(int i=0;i<invcraft.getSizeInventory();i++)
			{
				ItemStack ingredient = invcraft.getStackInSlot(i);
				if(!ingredient.isEmpty() && (ingredient.getItem() == SDItems.canteen || ingredient.getItem() == SDItems.ironCanteen))
				{
					IItemCanteen canteen = (ItemCanteen)ingredient.getItem();
					// Charcoal filter purifies water, so set to PURIFIED
					canteen.setDoses(output, ThirstEnum.PURIFIED, canteen.getDoses(ingredient));
					break;
				}
			}
		}
		
		return output;
	}
	 
	public static class Factory implements IRecipeFactory
	{
		//Make a new CanteenCharcoalRecipe via the json information
		
		@Override
		public IRecipe parse(JsonContext context, JsonObject json)
		{
			String group = JsonUtils.getString(json, "group", "");
			
			NonNullList<Ingredient> ingredients = RecipeUtil.getShapelessIngredients(context, json);
			
			ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
			
			return new CanteenCharcoalRecipe(
				   group.isEmpty() ? null : new ResourceLocation(group),
				   ingredients,
				   result);
		}
	}
}
