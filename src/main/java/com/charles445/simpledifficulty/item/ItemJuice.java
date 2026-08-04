package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;

/**
 * Juice item with different types stored via NBT tags.
 * In 1.16.5, metadata was removed, so use NBT to store juice type.
 */
public class ItemJuice extends ItemDrinkBase {

    private static final String JUICE_TYPE_TAG = "JuiceType";
    private static final JuiceEnum[] JUICE_VALUES = JuiceEnum.values();

    public ItemJuice(Properties properties) {
        super(properties.stacksTo(8)); 
    }

    @Override
    public void runSecondaryEffect(PlayerEntity player, ItemStack stack) {
        JuiceEnum type = getEnumForStack(stack);
        if (type == JuiceEnum.GOLDEN_APPLE && ModConfig.SERVER.goldenAppleJuiceEffect.get()) {
            player.addEffect(new EffectInstance(Effects.REGENERATION, 100, 1));
            player.addEffect(new EffectInstance(Effects.ABSORPTION, 2400, 0));
        }
    }

    @Override
    public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (JuiceEnum juice : JUICE_VALUES) {
                ItemStack stack = new ItemStack(this, 1);
                setJuiceType(stack, juice);
                items.add(stack);
            }
        }
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return "item." + SimpleDifficulty.MODID + ".juice_" + getEnumForStack(stack).toString().toLowerCase();
    }

    @Override
    public int getThirstLevel(ItemStack stack) {
        return getEnumForStack(stack).getThirstLevel();
    }

    @Override
    public float getSaturationLevel(ItemStack stack) {
        return getEnumForStack(stack).getSaturation();
    }

    @Override
    public float getDirtyChance(ItemStack stack) {
        return getEnumForStack(stack).getDirtyChance();
    }

    /**
     * Gets the juice type enum for the given stack.
     *
     * @param stack The item stack.
     * @return The juice type enum.
     */
    protected JuiceEnum getEnumForStack(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null || !tag.contains(JUICE_TYPE_TAG)) {
            return JUICE_VALUES[0];
        }

        String typeName = tag.getString(JUICE_TYPE_TAG);
        for (JuiceEnum juice : JUICE_VALUES) {
            if (juice.getName().equals(typeName)) {
                return juice;
            }
        }
        return JUICE_VALUES[0];
    }

    /**
     * Sets the juice type for the given stack.
     *
     * @param stack The item stack.
     * @param juice The juice type to set.
     */
    protected void setJuiceType(ItemStack stack, JuiceEnum juice) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(JUICE_TYPE_TAG, juice.getName());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        JuiceEnum type = getEnumForStack(stack);
        if (type == JuiceEnum.GOLDEN_APPLE || type == JuiceEnum.GOLDEN_CARROT || type == JuiceEnum.GOLDEN_MELON) {
            return true;
        }
        return super.hasEffect(stack);
    }

    /**
     * Enum representing different juice types with their properties.
     */
    public enum JuiceEnum {
        APPLE("apple", 8, 6.4f),
        BEETROOT("beetroot", 10, 8.0f),
        CACTUS("cactus", 9, 2.7f),
        CARROT("carrot", 8, 4.8f),
        CHORUS_FRUIT("chorus_fruit", 12, 7.2f),
        GOLDEN_APPLE("golden_apple", 20, 20.0f),
        GOLDEN_CARROT("golden_carrot", 14, 14.0f),
        GOLDEN_MELON("golden_melon", 16, 16.0f),
        MELON("melon", 8, 4.0f),
        PUMPKIN("pumpkin", 7, 4.9f);

        private final String name;
        private final int thirst;
        private final float saturation;

        JuiceEnum(String name, int thirst, float saturation) {
            this.name = name;
            this.thirst = thirst;
            this.saturation = saturation;
        }

        public String getName() {
            return name;
        }

        public int getThirstLevel() {
            return thirst;
        }

        public float getSaturation() {
            return saturation;
        }

        public float getDirtyChance() {
            return 0.0f;
        }

        @Override
        public String toString() {
            return this.getName();
        }
    }
}