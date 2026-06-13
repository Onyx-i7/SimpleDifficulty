package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemIronCanteen extends ItemCanteen {

    // Cached values array locally to prevent overhead from continuous values() cloning
    private static final ThirstEnum[] THIRST_VALUES = ThirstEnum.values();

    public ItemIronCanteen() {
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
        return ServerConfig.instance.getInteger(ServerOptions.IRON_CANTEEN_DOSES);
    }
    
    @Override
    public String getTranslationKey(ItemStack stack) {
        if (isCanteenEmpty(stack)) {
            return "item." + SimpleDifficulty.MODID + ":iron_canteen_empty";
        }
        
        int type = getTypeTag(stack).getInt();
        if (type >= THIRST_VALUES.length || type < 0) {
            return "item." + SimpleDifficulty.MODID + ":iron_canteen_broken";
        }
        
        return "item." + SimpleDifficulty.MODID + ":iron_canteen_" + THIRST_VALUES[type].toString();
    }
}
