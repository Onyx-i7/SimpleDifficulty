package com.charles445.simpledifficulty.compat.travelersbackpack;

import com.tiviacz.travelersbackpack.api.integration.ITBPlugin;
import com.tiviacz.travelersbackpack.api.integration.TBPlugin;
import net.minecraftforge.fml.common.Loader;

@TBPlugin
public class SDPlugin implements ITBPlugin {
    @Override
    public String getModName() {
        return "Simple Difficulty";
    }

    @Override
    public boolean canLoad() {
        /* Check if the backpack mod is present in the current runtime environment */
        return Loader.isModLoaded("travelersbackpack");
    }

    @Override
    public void preInit() {}

    @Override
    public void init() {
        /* Fire cross-mod registry handles during the initialization lifecycle phase */
        SDPurifiedWaterEffect.registerEffect();
        SDPurifiedWaterInventoryRecipes.registerRecipes();
    }

    @Override
    public void postInit() {}
}
