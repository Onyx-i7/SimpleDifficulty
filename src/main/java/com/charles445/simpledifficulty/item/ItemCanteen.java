package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCanteen extends ItemDrinkBase implements IItemCanteen {

    public static final String CANTEENTYPE = "CanteenType";
    public static final String DOSES = "Doses";
    
    // Cached values array to prevent memory leaks from continuous values() cloning
    private static final ThirstEnum[] THIRST_VALUES = ThirstEnum.values();
    private static final NBTTagInt[] CACHED_TYPE_TAGS = new NBTTagInt[THIRST_VALUES.length];
    
    static {
        for (int i = 0; i < THIRST_VALUES.length; i++) {
            CACHED_TYPE_TAGS[i] = new NBTTagInt(i);
        }
    }
    
    public ItemCanteen() {
        setMaxStackSize(1);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            ItemStack emptyCanteen = new ItemStack(this, 1, 0);
            createTypeTag(emptyCanteen);
            setCanteenEmpty(emptyCanteen);
            
            ItemStack fullCanteen = emptyCanteen.copy();
            setCanteenFull(fullCanteen);
            
            ItemStack purifiedCanteen = fullCanteen.copy();
            setTypeTag(purifiedCanteen, ThirstEnum.PURIFIED.ordinal());

            ItemStack saltCanteen = fullCanteen.copy();
            setTypeTag(saltCanteen, ThirstEnum.SALT.ordinal());
            
            items.add(emptyCanteen);
            items.add(fullCanteen);
            items.add(purifiedCanteen);
            items.add(saltCanteen);
        }
    }
    
    @Override
    public String getTranslationKey(ItemStack stack) {
        if (isCanteenEmpty(stack)) {
            return "item." + SimpleDifficulty.MODID + ":canteen_empty";
        }
        
        int type = getTypeTag(stack).getInt();
        if (type >= THIRST_VALUES.length) {
            return "item." + SimpleDifficulty.MODID + ":canteen_broken";
        }
        
        return "item." + SimpleDifficulty.MODID + ":canteen_" + THIRST_VALUES[type].toString();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        int typetag = getTypeTag(stack).getInt();
        
        if (!isCanteenFull(stack) || typetag == ThirstEnum.NORMAL.ordinal()) {
            ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
            if (traceBlockPos != null) {
                ThirstEnum trace = traceBlockPos.thirstEnum;
                boolean success = false;
                
                if (trace == ThirstEnum.PURIFIED) {
                    if (ServerConfig.instance.getBoolean(ServerOptions.INFINITE_PURIFIED_WATER) || player.world.setBlockToAir(traceBlockPos.pos)) {
                        tryAddDose(stack, ThirstEnum.PURIFIED);
                        success = true;
                    }
                } else if (trace == ThirstEnum.SALT) {
                    tryAddDose(stack, ThirstEnum.SALT);
                    success = true;
                } else if (trace == ThirstEnum.NORMAL) {
                    if (ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS) && !isCanteenFull(stack)) {
                        formatCanteen(stack, ThirstEnum.NORMAL);
                        setDosesInternal(stack, Math.min(getDoses(stack) + 1, getMaxDoses(stack)));
                        success = true;
                    }
                } else if (trace == ThirstEnum.RAIN) {
                    if (ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN)) {
                        tryAddDose(stack, ThirstEnum.NORMAL);
                        success = true;
                    }
                } else if (trace == ThirstEnum.CLEAN) {
                    tryAddDose(stack, ThirstEnum.CLEAN);
                    success = true;
                }
                
                if (success) {
                    SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
                    player.setActiveHand(hand);
                    player.swingArm(hand);
                    player.stopActiveHand();
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
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
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        if (world.isRemote || !(entityLiving instanceof EntityPlayer)) {
            return stack;
        }
        
        if (isCanteenEmpty(stack)) {
            return stack;
        }
        
        EntityPlayer player = (EntityPlayer) entityLiving;
        ThirstUtil.takeDrink(player, this.getThirstLevel(stack), this.getSaturationLevel(stack), this.getDirtyChance(stack));
        removeDose(stack);
        return stack;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format("item.durability", getDoses(stack), getMaxDoses(stack)));
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        double max = (double) this.getMaxDoses(stack);
        return max == 0.0d ? 1.0d : (max - (double) getDoses(stack)) / max;
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return !isCanteenEmpty(stack);
    }

    @Override
    public int getThirstLevel(ItemStack stack) {
        ThirstEnum thirstEnum = getThirstEnum(stack);
        return thirstEnum == null ? 0 : thirstEnum.getThirst();
    }

    @Override
    public float getSaturationLevel(ItemStack stack) {
        ThirstEnum thirstEnum = getThirstEnum(stack);
        return thirstEnum == null ? 0.0f : thirstEnum.getSaturation();
    }

    @Override
    public float getDirtyChance(ItemStack stack) {
        ThirstEnum thirstEnum = getThirstEnum(stack);
        return thirstEnum == null ? 0.0f : thirstEnum.getThirstyChance();
    }
    
    @Nullable
    @Override
    public ThirstEnum getThirstEnum(ItemStack stack) {
        int type = getTypeTag(stack).getInt();
        if (type >= THIRST_VALUES.length || type < 0) {
            return null;
        }
        return THIRST_VALUES[type];
    }
    
    protected NBTTagInt getTypeTag(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            createTypeTag(stack);
            setCanteenEmpty(stack);
            tagCompound = stack.getTagCompound();
        }
        
        NBTBase tag = tagCompound.getTag(CANTEENTYPE);
        if (tag instanceof NBTTagInt) {
            return (NBTTagInt) tag;
        } else {
            tagCompound.removeTag(CANTEENTYPE);
            createTypeTag(stack);
            return CACHED_TYPE_TAGS[ThirstEnum.NORMAL.ordinal()];
        }
    }
    
    protected void setTypeTag(ItemStack stack, ThirstEnum thirstEnum) {
        setTypeTag(stack, thirstEnum.ordinal());
    }
    
    protected void setTypeTag(ItemStack stack, int tag) {
        if (tag >= 0 && tag < CACHED_TYPE_TAGS.length) {
            stack.setTagInfo(CANTEENTYPE, CACHED_TYPE_TAGS[tag]);
        } else {
            stack.setTagInfo(CANTEENTYPE, new NBTTagInt(tag));
        }
    }
    
    protected void createTypeTag(ItemStack stack) {
        setTypeTag(stack, ThirstEnum.NORMAL.ordinal());
    }
    
    protected NBTTagInt getDosesTag(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            createDosesTag(stack);
            setCanteenEmpty(stack);
            tagCompound = stack.getTagCompound();
        }
        
        NBTBase tag = tagCompound.getTag(DOSES);
        if (tag instanceof NBTTagInt) {
            return (NBTTagInt) tag;
        } else {
            tagCompound.removeTag(DOSES);
            createDosesTag(stack);
            return new NBTTagInt(0);
        }
    }
    
    protected void setDosesTag(ItemStack stack, int doses) {
        stack.setTagInfo(DOSES, new NBTTagInt(doses));
    }
    
    protected void createDosesTag(ItemStack stack) {
        setDosesTag(stack, 0);
    }
    
    @Override
    public int getDoses(ItemStack stack) {
        return getDosesTag(stack).getInt();
    }
    
    @Override
    public int getMaxDoses(ItemStack stack) {
        return ServerConfig.instance.getInteger(ServerOptions.CANTEEN_DOSES);
    }
    
    @Override
    public boolean isCanteenFull(ItemStack stack) {
        return getDoses(stack) >= getMaxDoses(stack);
    }
    
    @Override
    public boolean isCanteenEmpty(ItemStack stack) {
        return getDoses(stack) <= 0;
    }
    
    @Override
    public void setCanteenFull(ItemStack stack) {
        setDosesInternal(stack, getMaxDoses(stack));
    }
    
    @Override
    public void setCanteenEmpty(ItemStack stack) {
        setDosesInternal(stack, 0);
    }
    
    @Override
    public void removeDose(ItemStack stack) {
        if (!isCanteenEmpty(stack)) {
            setDosesInternal(stack, getDoses(stack) - 1);
        }
    }
    
    @Override
    public void setDoses(ItemStack stack, int amount) {
        setDosesInternal(stack, amount);
    }
    
    @Override
    public void setDoses(ItemStack stack, ThirstEnum thirstEnum, int amount) {
        formatCanteen(stack, thirstEnum);
        setDosesInternal(stack, amount);
    }
    
    @Override
    public boolean tryAddDose(ItemStack stack, ThirstEnum thirstEnum) {
        int oldDamage = getDoses(stack);
        if (oldDamage < 0) {
            oldDamage = 0;
        }
        
        boolean format = formatCanteen(stack, thirstEnum);
        
        if (thirstEnum == ThirstEnum.NORMAL) {
            setDosesInternal(stack, getMaxDoses(stack));
        } else {
            setDosesInternal(stack, getDoses(stack) + 1);
        }
        
        return format || getDoses(stack) != oldDamage;
    }
    
    protected boolean formatCanteen(ItemStack stack, ThirstEnum thirstEnum) {
        if (thirstEnum != getThirstEnum(stack)) {
            setCanteenEmpty(stack);
            setTypeTag(stack, thirstEnum);
            return true;
        }
        
        getDoses(stack);
        return false;
    }
    
    protected void setDosesInternal(ItemStack stack, int amount) {
        if (amount <= 0) {
            this.setDosesTag(stack, 0);
            return;
        }
        
        int max = this.getMaxDoses(stack);
        if (amount > max) {
            this.setDosesTag(stack, max);
            return;
        }
        
        this.setDosesTag(stack, amount);
    }
}
