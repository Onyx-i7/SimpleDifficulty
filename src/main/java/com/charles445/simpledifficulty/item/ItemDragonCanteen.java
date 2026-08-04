package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

/**
 * Dragon canteen item with higher capacity and automatic water purification.
 */
public class ItemDragonCanteen extends ItemCanteen {

    private static final ThirstEnum[] THIRST_VALUES = ThirstEnum.values();

    public ItemDragonCanteen(Properties properties) {
        super(properties);

        addPropertyOverride(new ResourceLocation("contain"), (stack, worldIn, entityIn) -> {
            if (stack.getItem() instanceof IItemCanteen) {
                IItemCanteen canteen = (IItemCanteen) stack.getItem();
                return !canteen.isCanteenEmpty(stack) ? 1.0f : 0.0f;
            }
            return 0.0f;
        });
    }

    @Override
    public int getMaxDoses(ItemStack stack) {
        return ServerConfig.instance.getInteger(ServerOptions.DRAGON_CANTEEN_DOSES);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (isCanteenEmpty(stack)) {
            return "item." + SimpleDifficulty.MODID + ".dragon_canteen_empty";
        }

        int type = getTypeTag(stack).getAsInt();
        if (type >= THIRST_VALUES.length || type < 0) {
            return "item." + SimpleDifficulty.MODID + ".dragon_canteen_broken";
        }

        return "item." + SimpleDifficulty.MODID + ".dragon_canteen_" + THIRST_VALUES[type].toString().toLowerCase();
    }

    @Override
    public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            ItemStack emptyCanteen = new ItemStack(this, 1);
            createTypeTag(emptyCanteen);
            setCanteenEmpty(emptyCanteen);

            ItemStack fullCanteen = emptyCanteen.copy();
            setCanteenFull(fullCanteen);
            setTypeTag(fullCanteen, ThirstEnum.PURIFIED.ordinal());

            items.add(emptyCanteen);
            items.add(fullCanteen);
        }
    }

    protected void customSetTypeTag(ItemStack stack) {
        stack.addTagElement(CANTEENTYPE, IntNBT.valueOf(ThirstEnum.PURIFIED.ordinal()));
    }
}