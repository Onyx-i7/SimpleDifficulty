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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCanteen extends ItemDrinkBase implements IItemCanteen {

    public static final String CANTEENTYPE = "CanteenType";
    public static final String DOSES = "Doses";

    private static final ThirstEnum[] THIRST_VALUES = ThirstEnum.values();
    private static final IntNBT[] CACHED_TYPE_TAGS = new IntNBT[THIRST_VALUES.length];

    static {
        for (int i = 0; i < THIRST_VALUES.length; i++) {
            CACHED_TYPE_TAGS[i] = IntNBT.valueOf(i);
        }
    }

    public ItemCanteen(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            ItemStack emptyCanteen = new ItemStack(this, 1);
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
    public String getDescriptionId(ItemStack stack) {
        if (isCanteenEmpty(stack)) {
            return "item." + SimpleDifficulty.MODID + ".canteen_empty";
        }

        int type = getTypeTag(stack).getAsInt();
        if (type >= THIRST_VALUES.length) {
            return "item." + SimpleDifficulty.MODID + ".canteen_broken";
        }

        return "item." + SimpleDifficulty.MODID + ".canteen_" + THIRST_VALUES[type].toString().toLowerCase();
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int typetag = getTypeTag(stack).getAsInt();

        if (!isCanteenFull(stack) || typetag == ThirstEnum.NORMAL.ordinal()) {
            ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
            if (traceBlockPos != null) {
                ThirstEnum trace = traceBlockPos.thirstEnum;
                boolean success = false;

                if (trace == ThirstEnum.PURIFIED) {
                    if (ServerConfig.instance.getBoolean(ServerOptions.INFINITE_PURIFIED_WATER)) {
                        tryAddDose(stack, ThirstEnum.PURIFIED);
                        success = true;
                    } else if (player.level.setBlock(traceBlockPos.pos, net.minecraft.block.Blocks.AIR.defaultBlockState(), 3)) {
                        tryAddDose(stack, ThirstEnum.PURIFIED);
                        success = true;
                    }
                } else if (trace == ThirstEnum.SALT) {
                    tryAddDose(stack, ThirstEnum.SALT);
                    success = true;
                } else if (trace == ThirstEnum.NORMAL) {
                    if (ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS) && !isCanteenFull(stack)) {
                        tryAddDose(stack, ThirstEnum.NORMAL);
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
                    SoundUtil.commonPlayPlayerSound(player, SoundEvents.BUCKET_FILL);
                    player.startUsingItem(hand);
                    player.swing(hand);
                    player.stopUsingItem();
                    return ActionResult.success(stack);
                }
            }
        }

        if (!isCanteenEmpty(stack)) {
            IThirstCapability capability = SDCapabilities.getThirstData(player);
            if (capability.isThirsty() || !QuickConfig.isThirstEnabled()) {
                player.startUsingItem(hand);
                return ActionResult.success(stack);
            }
        }

        return ActionResult.fail(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entityLiving) {
        if (world.isClientSide || !(entityLiving instanceof PlayerEntity)) {
            return stack;
        }

        if (isCanteenEmpty(stack)) {
            return stack;
        }

        PlayerEntity player = (PlayerEntity) entityLiving;
        ThirstUtil.takeDrink(player, this.getThirstLevel(stack), this.getSaturationLevel(stack), this.getDirtyChance(stack));
        removeDose(stack);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("item.durability", getDoses(stack), getMaxDoses(stack)));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        double max = (double) this.getMaxDoses(stack);
        return max == 0.0d ? 1.0d : (max - (double) getDoses(stack)) / max;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
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
        int type = getTypeTag(stack).getAsInt();
        if (type >= THIRST_VALUES.length || type < 0) {
            return null;
        }
        return THIRST_VALUES[type];
    }

    protected IntNBT getTypeTag(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null) {
            createTypeTag(stack);
            setCanteenEmpty(stack);
            tagCompound = stack.getTag();
        }

        INBT tag = tagCompound.get(CANTEENTYPE);
        if (tag instanceof IntNBT) {
            return (IntNBT) tag;
        } else {
            tagCompound.remove(CANTEENTYPE);
            createTypeTag(stack);
            return CACHED_TYPE_TAGS[ThirstEnum.NORMAL.ordinal()];
        }
    }

    protected void setTypeTag(ItemStack stack, ThirstEnum thirstEnum) {
        setTypeTag(stack, thirstEnum.ordinal());
    }

    protected void setTypeTag(ItemStack stack, int tag) {
        if (tag >= 0 && tag < CACHED_TYPE_TAGS.length) {
            stack.addTagElement(CANTEENTYPE, CACHED_TYPE_TAGS[tag]);
        } else {
            stack.addTagElement(CANTEENTYPE, IntNBT.valueOf(tag));
        }
    }

    protected void createTypeTag(ItemStack stack) {
        setTypeTag(stack, ThirstEnum.NORMAL.ordinal());
    }

    protected IntNBT getDosesTag(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null) {
            createDosesTag(stack);
            setCanteenEmpty(stack);
            tagCompound = stack.getTag();
        }

        INBT tag = tagCompound.get(DOSES);
        if (tag instanceof IntNBT) {
            return (IntNBT) tag;
        } else {
            tagCompound.remove(DOSES);
            createDosesTag(stack);
            return IntNBT.valueOf(0);
        }
    }

    protected void setDosesTag(ItemStack stack, int doses) {
        stack.addTagElement(DOSES, IntNBT.valueOf(doses));
    }

    protected void createDosesTag(ItemStack stack) {
        setDosesTag(stack, 0);
    }

    @Override
    public int getDoses(ItemStack stack) {
        return getDosesTag(stack).getAsInt();
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
        int oldDoses = getDoses(stack);
        if (oldDoses < 0) {
            oldDoses = 0;
        }

        boolean format = formatCanteen(stack, thirstEnum);

        setDosesInternal(stack, getDoses(stack) + 1);

        return format || getDoses(stack) != oldDoses;
    }

    protected boolean formatCanteen(ItemStack stack, ThirstEnum thirstEnum) {
        if (thirstEnum != getThirstEnum(stack)) {
            int currentDoses = getDoses(stack);
            setTypeTag(stack, thirstEnum);
            setDosesInternal(stack, currentDoses);
            return true;
        }

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