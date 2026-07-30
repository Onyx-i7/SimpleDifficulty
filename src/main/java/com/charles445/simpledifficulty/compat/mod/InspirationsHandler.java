package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.compat.CompatRightClick;
import com.charles445.simpledifficulty.compat.CompatRightClick.IRightClick;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.ReflectUtil;
import com.charles445.simpledifficulty.util.SoundUtil;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InspirationsHandler {
    IRightClick fallbackCauldron;
    
    Class c_BlockEnhancedCauldron;
    Field f_BlockEnhancedCauldron_BOILING;
    PropertyBool o_BlockEnhancedCauldron_BOILING;
    Method m_BlockEnhancedCauldron_getCauldronLevel;
    
    Class c_TileCauldron; 
    Method m_TileCauldron_getState;
    Method m_TileCauldron_setFluidLevel;
    
    Class c_CauldronState;
    Method m_CauldronState_getFluid;
    
    public InspirationsHandler() {
        this.fallbackCauldron = CompatRightClick.cauldronHandler;
        
        try {
            c_BlockEnhancedCauldron = Class.forName("knightminer.inspirations.recipes.block.BlockEnhancedCauldron");
            f_BlockEnhancedCauldron_BOILING = ReflectUtil.findField(c_BlockEnhancedCauldron, "BOILING");
            o_BlockEnhancedCauldron_BOILING = (PropertyBool) f_BlockEnhancedCauldron_BOILING.get(null);
            
            m_BlockEnhancedCauldron_getCauldronLevel = ReflectUtil.findMethod(c_BlockEnhancedCauldron, "getCauldronLevel");

            c_TileCauldron = Class.forName("knightminer.inspirations.recipes.tileentity.TileCauldron");
            m_TileCauldron_getState = ReflectUtil.findMethod(c_TileCauldron, "getState");
            m_TileCauldron_setFluidLevel = ReflectUtil.findMethod(c_TileCauldron, "setFluidLevel");
            
            c_CauldronState = Class.forName("knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe$CauldronState");
            m_CauldronState_getFluid = ReflectUtil.findMethod(c_CauldronState, "getFluid");
            
            CompatRightClick.cauldronHandler = new InspirationsRightClickCauldron();
            
            try {
                CanteenRecipe.createCanteenRecipes();
            } catch(Exception e) {
                SimpleDifficulty.logger.error("Inspirations canteen recipes failed!...", e);
            }
        } catch(Exception e) {
            SimpleDifficulty.logger.error("InspirationsHandler setup failed! Inspirations compatibility is now disabled!", e);
            errorFallback();
        }
    }
    
    public void errorFallback() {
        CompatRightClick.cauldronHandler = this.fallbackCauldron;
    }
    
    public int getCauldronLevel(IBlockState state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return (int) m_BlockEnhancedCauldron_getCauldronLevel.invoke(null, state);
    }
    
    public void setFluidLevel(TileEntity tileCauldron, int fluidLevel) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        m_TileCauldron_setFluidLevel.invoke(tileCauldron, Math.max(0, fluidLevel));
    }
    
    @Nullable
    public Object getCauldronState(TileEntity tileCauldron) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return m_TileCauldron_getState.invoke(tileCauldron);
    }
    
    @Nullable
    public Fluid getCauldronStateFluid(Object cauldronState) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return (Fluid) m_CauldronState_getFluid.invoke(cauldronState);
    }
    
    public class InspirationsRightClickCauldron implements IRightClick {        
        @Override
        public void process(RightClickBlock event, World world, BlockPos pos, IBlockState state, EntityPlayer player) {
			if(!ModConfig.server.compatibility.toggles.inspirations || !c_BlockEnhancedCauldron.isInstance(state.getBlock()))
			{
				fallbackCauldron.process(event, world, pos, state, player);
				return;
			}
			
			ItemStack heldItem = player.getHeldItemMainhand();
			if(heldItem.isEmpty() && player.isSneaking())
			{
				if(SDCapabilities.getThirstData(player).isThirsty())
				{
					//Sneak-right clicking on a cauldron with an empty hand, with a thirsty player
					boolean boiling = state.getValue(o_BlockEnhancedCauldron_BOILING);
					
					try
					{
						int level = getCauldronLevel(state);
						if(level > 0)
						{
							//Store if it's boiling right here
							
							
							TileEntity te = world.getTileEntity(pos);
							if(c_TileCauldron.isInstance(te))
							{
								Object cauldronState = getCauldronState(te);
								if(cauldronState != null)
								{
									Fluid fluid = getCauldronStateFluid(cauldronState);
									if(fluid != null)
									{
										if(fluid == FluidRegistry.WATER)
										{
											if(boiling)
											{
												ThirstUtil.takeDrink(player, ThirstEnum.PURIFIED);
												damageFromBoilingDrink(player);
												setFluidLevel(te, level - 1);
											}
											else
											{
												ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
											}
											SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_GENERIC_DRINK);
										}
										else if(fluid == SDFluids.purifiedWater)
										{
											ThirstUtil.takeDrink(player, ThirstEnum.PURIFIED);
											if(boiling)
											{
												damageFromBoilingDrink(player);
											}
											SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_GENERIC_DRINK);
											//Lower the cauldron level
											setFluidLevel(te, 0);
										}
									}
								}
							}
							else
							{
								if(boiling)
								{
									ThirstUtil.takeDrink(player, ThirstEnum.PURIFIED);
									damageFromBoilingDrink(player);
								}
								else
								{
									ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
								}
								SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_GENERIC_DRINK);
							}
						}
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
					{
						SimpleDifficulty.logger.error("Inspirations compatibility failed while handling a cauldron!", e);
						errorFallback();
						return;
					}
				}
			}
        }
    }
    
    protected void damageFromBoilingDrink(EntityPlayer player) {
        if(!player.isImmuneToFire())
            player.attackEntityFrom(SDDamageSources.INSPIRATIONS_CAULDRON_BURN, 1.0f);
    }
    
    public static class CanteenRecipe implements ICauldronRecipe {
        public static void createCanteenRecipes() {
            CanteenRecipe recipe = new CanteenRecipe();
            
            try {
                Class c_InspirationsRegistry = Class.forName("knightminer.inspirations.library.InspirationsRegistry");
                Class c_ICauldronRecipe = ICauldronRecipe.class; 
                Method m_InspirationsRegistry_addCauldronRecipe = ReflectUtil.findMethod(c_InspirationsRegistry, "addCauldronRecipe", c_ICauldronRecipe);

                m_InspirationsRegistry_addCauldronRecipe.invoke(null, recipe);
            } catch (Exception e) {
                SimpleDifficulty.logger.error("Inspirations compatibility failed while reflecting for cauldron recipes!", e);
            }
        }

        @Override
        public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
            if(!ModConfig.server.compatibility.toggles.inspirations)
				return false;
			
			if(level == 0)
				return false;
			
			if(stack.getItem() instanceof IItemCanteen)
			{
				IItemCanteen canteen = (IItemCanteen)stack.getItem();
				
				Fluid fluid = state.getFluid();
				
				if(fluid == null)
					return false;
				
				if(fluid == FluidRegistry.WATER)
				{
					if(boiling)
					{
						return canteen.tryAddDose(stack, ThirstEnum.PURIFIED);
					}
					else
					{
						return canteen.tryAddDose(stack, ThirstEnum.NORMAL);
					}
				}
			}
			
			return false;
        }

        @Override
        public int getLevel(int level) {
            return level - 1;
        }

        @Override
        public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack getContainer(ItemStack stack) {
            return ItemStack.EMPTY;
        }

        @Override
        public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
            return SoundEvents.ITEM_BUCKET_FILL;
        }
        
        @Override
        public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
            return stack;
        }
    }
}
