package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.compat.SereneSeasonsBridge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public class BlockIceBasic extends BlockIce
{
    private final String waterBlock;

    public BlockIceBasic(String waterBlock)
    {
        this.setSoundType(SoundType.GLASS);
        this.setHardness(0.5F);
        this.setLightOpacity(3);
        this.waterBlock = waterBlock;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        player.addStat(Objects.requireNonNull(StatList.getBlockStats(this)));
        player.addExhaustion(0.005F);

        if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0)
        {
            java.util.List<ItemStack> items = new java.util.ArrayList<>();
            items.add(this.getSilkTouchDrop(state));

            net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
            for (ItemStack is : items)
                spawnAsEntity(worldIn, pos, is);
        }
        else
        {
            if (worldIn.provider.doesWaterVaporize())
            {
                worldIn.setBlockToAir(pos);
                return;
            }

            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            harvesters.set(player);
            this.dropBlockAsItem(worldIn, pos, state, i);
            harvesters.set(null);
            Material material = worldIn.getBlockState(pos.down()).getMaterial();

            if (material.blocksMovement() || material.isLiquid())
            {
                worldIn.setBlockState(pos, getFluidStateSafe());
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.isRemote) return; // Extra security on client threads

        Biome biome = worldIn.getBiome(pos);
        float f = biome.getTemperature(pos);

        if (Loader.isModLoaded("sereneseasons"))
        {
            f = SereneSeasonsBridge.getSeasonTemperature(worldIn, biome, pos);
        }

        if (f > 0.15F && worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - this.getDefaultState().getLightOpacity())
        {
            this.turnIntoWater(worldIn, pos);
        }
    }

    @Override
    protected void turnIntoWater(World worldIn, BlockPos pos)
    {
        if (worldIn.provider.doesWaterVaporize())
        {
            worldIn.setBlockToAir(pos);
        }
        else
        {
            this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
            IBlockState fluidState = getFluidStateSafe();
            worldIn.setBlockState(pos, fluidState);
            worldIn.neighborChanged(pos, fluidState.getBlock(), pos);
        }
    }
    private IBlockState getFluidStateSafe()
    {
        if (SDFluids.fluidBlocks.containsKey(waterBlock))
        {
            Block block = SDFluids.fluidBlocks.get(waterBlock);
            if (block != null)
            {
                return block.getDefaultState();
            }
        }
        return Blocks.WATER.getDefaultState();
    }
}
