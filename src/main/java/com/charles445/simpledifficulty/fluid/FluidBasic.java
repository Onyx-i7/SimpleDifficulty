package com.charles445.simpledifficulty.fluid;

import com.charles445.simpledifficulty.SimpleDifficulty;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public abstract class FluidBasic extends ForgeFlowingFluid {

    public FluidBasic(Properties properties) {
        super(properties);
    }

    @Override
    public Item getBucket() {
        return null; // Buckets handled separately or via FluidAttributes
    }

    public static FluidAttributes.Builder createAttributes(String still, String flowing) {
        return FluidAttributes.builder(
                new ResourceLocation(SimpleDifficulty.MODID, "fluids/" + still),
                new ResourceLocation(SimpleDifficulty.MODID, "fluids/" + flowing)
        ).density(1000).viscosity(1000);
    }

    public static class Source extends FluidBasic {
        public Source(Supplier<ForgeFlowingFluid.Properties> properties) {
            super(properties.get());
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends FluidBasic {
        public Flowing(Supplier<ForgeFlowingFluid.Properties> properties) {
            super(properties.get());
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(net.minecraftforge.fluids.ForgeFlowingFluid.LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, net.minecraft.world.IBlockReader world, 
            net.minecraft.util.math.BlockPos pos, net.minecraft.fluid.Fluid fluidIn, 
            net.minecraft.util.Direction direction) {
        return false;
    }

    @Override
    public net.minecraft.util.math.vector.Vector3d getFlow(net.minecraft.world.IBlockReader world, 
            net.minecraft.util.math.BlockPos pos, FluidState state) {
        return net.minecraft.util.math.vector.Vector3d.ZERO;
    }

    @Override
    protected int getSlopeFindDistance(net.minecraft.world.IWorldReader world) {
        return 4;
    }

    @Override
    protected int getDropOff(net.minecraft.world.IWorldReader world) {
        return 1;
    }

    @Override
    public int getTickDelay(net.minecraft.world.IWorldReader world) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0f;
    }
}