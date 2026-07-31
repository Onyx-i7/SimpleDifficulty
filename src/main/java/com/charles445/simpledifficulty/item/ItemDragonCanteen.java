package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ItemDragonCanteen extends ItemCanteen {

    private static final ThirstEnum[] THIRST_VALUES = ThirstEnum.values();

    public ItemDragonCanteen() {
        super();
        
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
    public String getTranslationKey(ItemStack stack) {
        if (isCanteenEmpty(stack)) {
            return "item." + SimpleDifficulty.MODID + ":dragon_canteen_empty";
        }
        
        int type = getTypeTag(stack).getInt();
        if (type >= THIRST_VALUES.length || type < 0) {
            return "item." + SimpleDifficulty.MODID + ":dragon_canteen_broken";
        }
        
        return "item." + SimpleDifficulty.MODID + ":dragon_canteen_" + THIRST_VALUES[type].toString();
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            ItemStack emptyCanteen = new ItemStack(this, 1, 0);
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
        stack.setTagInfo(CANTEENTYPE, new NBTTagInt(ThirstEnum.PURIFIED.ordinal()));
    }
}
