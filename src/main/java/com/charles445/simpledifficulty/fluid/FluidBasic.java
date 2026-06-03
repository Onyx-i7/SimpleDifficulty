package com.charles445.simpledifficulty.fluid;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDFluids;
import git.jbredwards.fluidlogged_api.api.fluid.ICompatibleFluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

/**
 * Basic fluid implementation with Fluidlogged API compatibility.
 * Automatically registers the fluid and its corresponding bucket.
 */
@Optional.Interface(iface = "git.jbredwards.fluidlogged_api.api.fluid.ICompatibleFluid", modid = "fluidlogged_api")
public class FluidBasic extends Fluid implements ICompatibleFluid
{
    public FluidBasic(String fluidName, String still, String flowing)
    {
        super(fluidName,
            new ResourceLocation(SimpleDifficulty.MODID, "fluids/"+still),
            new ResourceLocation(SimpleDifficulty.MODID, "fluids/"+flowing)
        );
        
        // Add to generic fluid map
        SDFluids.fluids.put(fluidName, this);
        
        // Register fluid and add bucket for self
        // Note: Bucket localization must be handled in lang files using format:
        // item.fluidName.bucket=Localized Name
        FluidRegistry.registerFluid(SDFluids.fluids.get(fluidName));
        FluidRegistry.addBucketForFluid(SDFluids.fluids.get(fluidName));
    }

    @Nonnull
    @Override
    @Optional.Method(modid = "fluidlogged_api")
    public Fluid getParentFluid()
    {
        return FluidRegistry.WATER;
    }
}
