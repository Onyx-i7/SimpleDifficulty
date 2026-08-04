package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public abstract class ItemDrinkBase extends Item {

    public abstract int getThirstLevel(ItemStack stack);
    public abstract float getSaturationLevel(ItemStack stack);
    public abstract float getDirtyChance(ItemStack stack);

    public ItemDrinkBase(Properties properties) {
        super(properties.durability(0).stacksTo(8));
    }

    public void runSecondaryEffect(PlayerEntity player, ItemStack stack) {
        // Can be overridden to run a special task
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!QuickConfig.isThirstEnabled()) {
            player.startUsingItem(hand);
            return ActionResult.success(stack);
        }

        IThirstCapability capability = SDCapabilities.getThirstData(player);
        if (capability.isThirsty()) {
            player.startUsingItem(hand);
            return ActionResult.success(stack);
        }

        return ActionResult.fail(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entityLiving) {
        if (world.isClientSide || !(entityLiving instanceof PlayerEntity)) {
            return stack;
        }

        PlayerEntity player = (PlayerEntity) entityLiving;
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
            if (!player.inventory.add(glassBottle)) {
                player.drop(glassBottle, false);
            }
            return stack;
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}