package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemJuice extends ItemDrinkBase {

    // Cached values array to prevent memory leaks from continuous values() cloning
    private static final JuiceEnum[] JUICE_VALUES = JuiceEnum.values();

    public ItemJuice() {
        super();
        this.setHasSubtypes(true);
    }
    
    @Override
    public void runSecondaryEffect(EntityPlayer player, ItemStack stack) {
        JuiceEnum type = getEnumForStack(stack);
        if (type == JuiceEnum.GOLDEN_APPLE && ModConfig.server.miscellaneous.goldenAppleJuiceEffect) {
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
        }
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (JuiceEnum juice : JUICE_VALUES) {
                items.add(new ItemStack(this, 1, juice.ordinal()));
            }
        }
    }
    
    @Override
    public String getTranslationKey(ItemStack stack) {
        return "item." + SimpleDifficulty.MODID + ":juice_" + getEnumForStack(stack).toString();
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
    
    protected JuiceEnum getEnumForStack(ItemStack stack) {
        int meta = stack.getMetadata();
        if (meta < 0 || meta >= JUICE_VALUES.length) {
            return JUICE_VALUES[0];
        }
        return JUICE_VALUES[meta];
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        JuiceEnum type = getEnumForStack(stack);
        if (type == JuiceEnum.GOLDEN_APPLE || type == JuiceEnum.GOLDEN_CARROT || type == JuiceEnum.GOLDEN_MELON) {
            return true;
        }
        return super.hasEffect(stack);
    }
    
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
