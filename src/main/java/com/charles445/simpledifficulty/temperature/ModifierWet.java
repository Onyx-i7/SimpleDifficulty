package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.compat.mod.Weather2Compat;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Temperature modifier when the player is wet (in water, rain, or fluids).
 */
public class ModifierWet extends ModifierBase {
    public ModifierWet() {
        super("Wet");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // Check if it's a fluid block
        FluidState fluidState = state.getFluidState();
        if (!fluidState.isEmpty()) {
            String fluidName = fluidState.getType().getRegistryName() != null ? 
                    fluidState.getType().getRegistryName().toString() : null;
            
            if (fluidName != null) {
                JsonTemperature tempInfo = JsonConfig.fluidTemperatures.get(fluidName);
                if (tempInfo != null) {
                    return tempInfo.temperature;
                }
            }
        }

        // Check if it's water
        if (state.getMaterial() == Material.WATER || fluidState.is(FluidTags.WATER)) {
            return ModConfig.SERVER.wetValue.get();
        }
        // Check Weather2 compatibility for rain
        else if (Weather2Compat.isRainingAt(world, pos)) {
            return ModConfig.SERVER.wetValue.get();
        } else {
            return 0.0f;
        }
    }
}