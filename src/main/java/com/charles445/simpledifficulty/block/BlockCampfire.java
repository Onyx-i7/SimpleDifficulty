package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.compat.mod.Weather2Compat;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.OreDictUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockCampfire extends Block implements IBlockStateIgnore
{
    private static final int AGE_MIN = 0;
    private static final int AGE_MAX = 7;
    private static final int LOG_REFUEL = 3;
    private static final int RAIN_CHECK_RATE = 20;
    
    public static final PropertyInteger AGE = PropertyInteger.create("age", AGE_MIN, AGE_MAX);
    public static final PropertyBool BURNING = PropertyBool.create("burning");

    private static final IProperty[] ignoredProperties = new IProperty[]{BURNING};
    private static final AxisAlignedBB HITBOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D);
    
    private boolean isRainingAt(World world, BlockPos pos)
    {
        BlockPos checkPos = pos.up();
        boolean canSeeSky = world.canSeeSky(checkPos);
        
        // Si no ve el cielo, no puede lloverle
        if (!canSeeSky) {
            return false;
        }
        
        boolean isRaining = Weather2Compat.isRainingAt(world, checkPos);
        
        // LOG DE DIAGNÓSTICO
        System.out.println("[CampfireDebug] ¿Está lloviendo en " + checkPos + "? Respuesta: " + isRaining + " (Vanilla Raining: " + world.isRaining() + ")");
        
        return isRaining;
    }
    
    private void scheduleRainCheck(World world, BlockPos pos)
    {
        world.scheduleUpdate(pos, this, RAIN_CHECK_RATE);
    }
    
    public BlockCampfire()
    {
        super(Material.CIRCUITS, MapColor.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(AGE, AGE_MIN).withProperty(BURNING, false));
        setHardness(0.5f);
        setSoundType(SoundType.WOOD);
        setTickRandomly(true);
    }
    
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote && state.getValue(BURNING)) {
            scheduleRainCheck(world, pos);
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        final ItemStack heldItemStack = player.getHeldItem(hand);
        if(heldItemStack.isEmpty())
            return true;
        
        final Item heldItem = heldItemStack.getItem();
        if(Block.getBlockFromItem(heldItem).equals(SDBlocks.spit))
            return false;
        
        boolean isRainingAtPos = isRainingAt(world, pos);
        
        if(world.isRemote)
        {
            if(heldItem == Items.FLINT_AND_STEEL)
            {
                int age = state.getValue(AGE);
                boolean burning = state.getValue(BURNING);
                if(!burning && age < AGE_MAX && !isRainingAtPos)
                {
                    world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
                }
            }
            return true;
        }
        
        int age = state.getValue(AGE);
        boolean burning = state.getValue(BURNING);
        
        if(OreDictUtil.isOre(OreDictUtil.logWood, heldItemStack))
        {
            if(age > AGE_MIN)
            {
                if(!player.capabilities.isCreativeMode)
                    heldItemStack.shrink(1);
                int refuelAmount = (LOG_REFUEL + (age == AGE_MAX ? 1 : 0));
                world.setBlockState(pos, state.withProperty(AGE, Math.max(AGE_MIN, age - refuelAmount)), 3);
                
                if (burning) {
                    scheduleRainCheck(world, pos);
                }
            }
            return true;
        }
        else if(!burning && age < AGE_MAX && !isRainingAtPos)
        {
            boolean ignited = false;
            
            if(OreDictUtil.isOre(OreDictUtil.stick, heldItemStack) || heldItem == Items.STICK)
            {
                if(!player.capabilities.isCreativeMode)
                    heldItemStack.shrink(1);
                if(world.rand.nextInt(ModConfig.server.miscellaneous.campfireStickIgniteChance) == 0)
                {
                    world.setBlockState(pos, state.withProperty(BURNING, true), 3);
                    ignited = true;
                }
            }
            else if(heldItem == Items.FLINT_AND_STEEL)
            {
                world.setBlockState(pos, state.withProperty(BURNING, true), 3);
                heldItemStack.damageItem(1, player);
                ignited = true;
            }
            
            if (ignited) {
                System.out.println("[CampfireDebug] Fogata encendida exitosamente en " + pos);
                scheduleRainCheck(world, pos);
            }
            return true;
        }
        return true;
    }
    
    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (world.isRemote) return;
        
        int age = state.getValue(AGE);
        boolean burning = state.getValue(BURNING);
        
        if (burning)
        {
            if (isRainingAt(world, pos))
            {
                System.out.println("[CampfireDebug] Apagada por lluvia en randomTick.");
                extinguishCampfire(world, pos, state);
                return;
            }

            if (rand.nextInt(ModConfig.server.miscellaneous.campfireDecayChance) == 0)
            {
                age++;
                if (age >= AGE_MAX)
                {
                    System.out.println("[CampfireDebug] Fogata consumida por completo (Age max alcanzada).");
                    world.setBlockState(pos, state.withProperty(AGE, AGE_MAX).withProperty(BURNING, false), 3);
                    effectExtinguish(world, pos);
                }
                else
                {
                    world.setBlockState(pos, state.withProperty(AGE, age), 3);
                }
            }
        }
    }
    
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!world.isRemote && state.getValue(BURNING))
        {
            if (isRainingAt(world, pos))
            {
                System.out.println("[CampfireDebug] Apagada por lluvia en neighborChanged.");
                extinguishCampfire(world, pos, state);
            }
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (world.isRemote) return;
        if (!state.getValue(BURNING)) return;
        
        System.out.println("[CampfireDebug] Ejecutando updateTick de rutina en " + pos);
        
        if (isRainingAt(world, pos))
        {
            System.out.println("[CampfireDebug] Apagada por lluvia en updateTick.");
            extinguishCampfire(world, pos, state);
            return;
        }
        
        scheduleRainCheck(world, pos);
    }
    
    public void extinguishCampfire(World world, BlockPos pos, IBlockState state) {
        if (state.getValue(BURNING)) {
            // Fix v0.7.9: If an external event tries to turn it off, check if it's really raining
            // If it is NOT raining, ignore the external order and keep the fire going
            if (!isRainingAt(world, pos)) {
                return; 
            }

            // If it is indeed raining, proceed to turn it off as usual
            world.setBlockState(pos, state.withProperty(BURNING, false), 3);
            effectExtinguish(world, pos);
        }
    }
    
    @Override
    public int tickRate(World world) { return RAIN_CHECK_RATE; }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        boolean burning = (meta & 1) != 0;
        int age = meta >> 1;
        return this.getDefaultState().withProperty(AGE, age).withProperty(BURNING, burning);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(AGE) << 1) | (state.getValue(BURNING) ? 1 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, new IProperty[] {AGE, BURNING}); }   
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return HITBOX; }
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) { return Block.NULL_AABB; }
    
    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        if (!world.isRemote && state.getValue(BURNING) && entity instanceof EntityLivingBase) { entity.setFire(1); }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) { return BlockFaceShape.UNDEFINED; }
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) { return state.getValue(BURNING) ? 15 : 0; }
    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) { return 0; }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        super.randomDisplayTick(state, world, pos, rand);
        if (state.getValue(BURNING))
        {
            int age = state.getValue(AGE);
            float strength = 1.0f - ((float)age / (float)(AGE_MAX - AGE_MIN));
            if (rand.nextFloat() < strength)
            {
                int loop = rand.nextInt(6) + 1;
                for (int i = 0; i < loop; i++) { createFlameParticle(world, pos, rand); }
            }
            if (rand.nextInt(30) == 0)
            {
                world.playSound(0.5d + pos.getX(), 0.5d + pos.getY(), 0.5d + pos.getZ(), SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
            }
        }
    }
    
    private void effectExtinguish(World world, BlockPos pos) {
        SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH);
        if (!world.isRemote) {
            for (int i = 0; i < 4; i++) {
                double xOffset = pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 0.4;
                double yOffset = pos.getY() + 0.4 + world.rand.nextDouble() * 0.3;
                double zOffset = pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 0.4;
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xOffset, yOffset, zOffset, 0.0, 0.05, 0.0);
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xOffset, yOffset, zOffset, 0.0, 0.03, 0.0);
            }
        }
    }  
    
    private void createFlameParticle(World world, BlockPos pos, Random rand)
    {
        double yOffset = rand.nextDouble() * 0.35d + 0.35d;
        double offAdj = (0.7d - yOffset) * 2.28571428d;
        double xOffset = (rand.nextDouble() - 0.5d) * offAdj + 0.5d;
        double zOffset = (rand.nextDouble() - 0.5d) * offAdj + 0.5d;
        world.spawnParticle(EnumParticleTypes.FLAME, xOffset + pos.getX(), yOffset + pos.getY(), zOffset + pos.getZ(), 0.0d, (rand.nextDouble() * 0.015d) + 0.005d, 0.0d);
    }
    
    @Override
    public boolean isFullCube(IBlockState state) { return false; }
    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }
    @Override
    public IProperty[] getIgnoredProperties() { return ignoredProperties; }
}
