package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Iron canteen item with increased capacity compared to the standard canteen.
 */
public class ItemIronCanteen extends ItemCanteen {

    private static final ThirstEnum[] THIRST_VALUES = ThirstEnum.values();

    public ItemIronCanteen(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public int getMaxDoses(ItemStack stack) {
        return ServerConfig.instance.getInteger(ServerOptions.IRON_CANTEEN_DOSES);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (isCanteenEmpty(stack)) {
            return "item." + SimpleDifficulty.MODID + ".iron_canteen_empty";
        }

        int type = getTypeTag(stack).getAsInt();
        if (type >= THIRST_VALUES.length || type < 0) {
            return "item." + SimpleDifficulty.MODID + ".iron_canteen_broken";
        }

        return "item." + SimpleDifficulty.MODID + ".iron_canteen_" + THIRST_VALUES[type].toString().toLowerCase();
    }
}