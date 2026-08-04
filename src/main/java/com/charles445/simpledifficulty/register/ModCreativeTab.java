package com.charles445.simpledifficulty.register;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModCreativeTab extends ItemGroup {
    public static final ModCreativeTab TAB = new ModCreativeTab();

    public ModCreativeTab() {
        super("tabSimpleDifficulty");
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RegisterItems.PURIFIED_WATER_BOTTLE.get());
    }
}