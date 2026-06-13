package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ItemDragonCanteen extends ItemCanteen {

    public static final String EI_CAPACITY = "capacity";
    
    // Cached values array locally to prevent overhead from continuous values() cloning
    private static final ThirstEnum[] THIRST_VALUES = ThirstEnum.values();
    
    public int capacity = 5;

    public ItemDragonCanteen(ExtraItem extraItem) {
        super();
        
        Integer oCapacity = extraItem.getInteger(EI_CAPACITY);
        if (oCapacity != null) {
            this.capacity = oCapacity.intValue();
        }
        
        // Optimized property override using a clean lambda expression to save memory
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
        return this.capacity;
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
            tryAddDose(emptyCanteen, ThirstEnum.PURIFIED);
            setCanteenEmpty(emptyCanteen);
            
            ItemStack fullCanteen = emptyCanteen.copy();
            tryAddDose(fullCanteen, ThirstEnum.PURIFIED);
            setCanteenFull(fullCanteen);
            
            customSetTypeTag(emptyCanteen);
            customSetTypeTag(fullCanteen);
            
            items.add(emptyCanteen);
            items.add(fullCanteen);
        }
    }
    
    @Override
    public ThirstEnum getThirstEnum(ItemStack stack) {
        // Dragon canteen is safe and always tracks purified water
        return ThirstEnum.PURIFIED;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!isCanteenFull(stack)) {
            ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
            if (traceBlockPos != null) {
                ThirstEnum trace = traceBlockPos.thirstEnum;
                if (trace == ThirstEnum.PURIFIED) {
                    player.world.setBlockToAir(traceBlockPos.pos);
                }
                
                tryAddDose(stack, ThirstEnum.PURIFIED);
                SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
                player.setActiveHand(hand);
                player.swingArm(hand);
                player.stopActiveHand();
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        
        if (!isCanteenEmpty(stack)) {
            IThirstCapability capability = SDCapabilities.getThirstData(player);
            if (capability.isThirsty() || !QuickConfig.isThirstEnabled()) {
                player.setActiveHand(hand);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
    
    protected void customSetTypeTag(ItemStack stack) {
        stack.setTagInfo(CANTEENTYPE, new NBTTagInt(ThirstEnum.PURIFIED.ordinal()));
    }
}
