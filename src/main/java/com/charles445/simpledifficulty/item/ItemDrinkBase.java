package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public abstract class ItemDrinkBase extends Item {

    public abstract int getThirstLevel(ItemStack stack);
    public abstract float getSaturationLevel(ItemStack stack);
    public abstract float getDirtyChance(ItemStack stack);
    
    public ItemDrinkBase() {
        this.setMaxDamage(0);
        this.setMaxStackSize(8);
    }
    
    public void runSecondaryEffect(EntityPlayer player, ItemStack stack) {
        // Can be overridden to run a special task
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!QuickConfig.isThirstEnabled()) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        
        IThirstCapability capability = SDCapabilities.getThirstData(player);
        if (capability.isThirsty()) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        if (world.isRemote || !(entityLiving instanceof EntityPlayer)) {
            return stack;
        }
        
        EntityPlayer player = (EntityPlayer) entityLiving;
        ResourceLocation registryName = this.getRegistryName();
        boolean override = false;
        
        if (registryName != null) {
            List<JsonConsumableThirst> jctList = JsonConfig.consumableThirst.get(registryName.toString());
            if (jctList != null && !jctList.isEmpty()) {
                for (JsonConsumableThirst jct : jctList) {
                    if (jct != null && jct.matches(stack)) {
                        override = true;
                        break;
                    }
                }
            }
        }

        if (!override) {
            ThirstUtil.takeDrink(player, this.getThirstLevel(stack), this.getSaturationLevel(stack), this.getDirtyChance(stack));
        }
        
        this.runSecondaryEffect(player, stack);
        
        stack.shrink(1);
        ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
        
        if (stack.isEmpty()) {
            return glassBottle;
        } else {
            if (!player.inventory.addItemStackToInventory(glassBottle)) {
                player.dropItem(glassBottle, false);
            }
            return stack;
        }
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
