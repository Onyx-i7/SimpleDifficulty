package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCanteen extends ItemDrinkBase implements IItemCanteen
{
	public static final String CANTEENTYPE = "CanteenType";
	public static final String DOSES = "Doses";
	
	// Cached NBTTagInt instances for common values to reduce object creation
	private static final NBTTagInt[] CACHED_TYPE_TAGS = new NBTTagInt[ThirstEnum.values().length];
	static
	{
		for(int i = 0; i < ThirstEnum.values().length; i++)
		{
			CACHED_TYPE_TAGS[i] = new NBTTagInt(i);
		}
	}
	
	public ItemCanteen()
	{
		setMaxStackSize(1);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			ItemStack emptyCanteen = new ItemStack(this, 1, 0);
			createTypeTag(emptyCanteen);
			setCanteenEmpty(emptyCanteen);
			
			ItemStack fullCanteen = emptyCanteen.copy();
			setCanteenFull(fullCanteen);
			
			ItemStack purifiedCanteen = fullCanteen.copy();
			setTypeTag(purifiedCanteen, ThirstEnum.PURIFIED.ordinal());

			ItemStack saltCanteen = fullCanteen.copy();
			setTypeTag(saltCanteen, ThirstEnum.SALT.ordinal());
			
			items.add(emptyCanteen);
			items.add(fullCanteen);
			items.add(purifiedCanteen);
			items.add(saltCanteen);
			
		}
	}
	
	@Override
	public String getTranslationKey(ItemStack stack)
	{
		if(isCanteenEmpty(stack))
			return "item."+SimpleDifficulty.MODID+":"+"canteen_empty";
		
		int type = getTypeTag(stack).getInt();
		if(type>=ThirstEnum.values().length)
			return "item."+SimpleDifficulty.MODID+":"+"canteen_broken";
		
		return "item."+SimpleDifficulty.MODID+":"+"canteen_"+ThirstEnum.values()[type].toString();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		
		//Initializes if it hasn't been initialized already
		// Cache the type tag to avoid multiple NBT reads
		NBTTagInt typeTag = getTypeTag(stack);
		int typetag = typeTag.getInt();
		
		//Only attempt refill if item isn't full or if it isn't normal/purified/salt water
		//This prevents full canteens from getting overridden incorrectly
		if(!isCanteenFull(stack) || typetag==ThirstEnum.NORMAL.ordinal())
		{
			ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
			if(traceBlockPos != null)
			{
				ThirstEnum trace = traceBlockPos.thirstEnum;
				
				//Handle different water types correctly
				if(trace==ThirstEnum.PURIFIED)
				{
					//Purified water: consume block, add 1 dose, don't give thirst effect
					if(ServerConfig.instance.getBoolean(ServerOptions.INFINITE_PURIFIED_WATER) || player.world.setBlockToAir(traceBlockPos.pos))
					{
						tryAddDose(stack, ThirstEnum.PURIFIED);
						SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
						player.setActiveHand(hand);
						player.swingArm(hand);
						player.stopActiveHand();
						return new ActionResult(EnumActionResult.SUCCESS, stack);
					}
				}
				else if(trace==ThirstEnum.SALT)
				{
					//Salt water: don't consume block, add 1 dose, will give thirst effect when drunk
					tryAddDose(stack, ThirstEnum.SALT);
					SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
					player.setActiveHand(hand);
					player.swingArm(hand);
					player.stopActiveHand();
					return new ActionResult(EnumActionResult.SUCCESS, stack);
				}
				else if(trace==ThirstEnum.NORMAL)
				{
					//Normal water: don't consume block, add only 1 dose (not fill completely), no thirst effect
					if(ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS))
					{
						//Add only 1 dose instead of filling completely
						if(!isCanteenFull(stack))
						{
							formatCanteen(stack, ThirstEnum.NORMAL);
							setDosesInternal(stack, Math.min(getDoses(stack) + 1, getMaxDoses(stack)));
							SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
							player.setActiveHand(hand);
							player.swingArm(hand);
							player.stopActiveHand();
							return new ActionResult(EnumActionResult.SUCCESS, stack);
						}
					}
				}
				else if(trace==ThirstEnum.RAIN)
				{
					//Rain: convert to normal, add 1 dose
					if(ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN))
					{
						tryAddDose(stack, ThirstEnum.NORMAL);
						SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
						player.setActiveHand(hand);
						player.swingArm(hand);
						player.stopActiveHand();
						return new ActionResult(EnumActionResult.SUCCESS, stack);
					}
				}
				else if(trace==ThirstEnum.CLEAN)
				{
					//Clean water (Betweenlands): don't consume block, add 1 dose, no thirst effect
					tryAddDose(stack, ThirstEnum.CLEAN);
					SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
					player.setActiveHand(hand);
					player.swingArm(hand);
					player.stopActiveHand();
					return new ActionResult(EnumActionResult.SUCCESS, stack);
				}
			}
		}
		if(!isCanteenEmpty(stack))
		{
			IThirstCapability capability = SDCapabilities.getThirstData(player);
			if(capability.isThirsty() || !QuickConfig.isThirstEnabled())
			{
				player.setActiveHand(hand);
				return new ActionResult(EnumActionResult.SUCCESS, stack);
			}
		}
		return new ActionResult(EnumActionResult.FAIL, stack);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		if(world.isRemote || !(entityLiving instanceof EntityPlayer))
			return stack;
		
		if(isCanteenEmpty(stack))
			return stack;
		
		EntityPlayer player = (EntityPlayer)entityLiving;
		ThirstUtil.takeDrink(player, this.getThirstLevel(stack), this.getSaturationLevel(stack), this.getDirtyChance(stack));
		removeDose(stack);
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		//Add durability information
		int max = getMaxDoses(stack);
		
		tooltip.add(I18n.format("item.durability", getDoses(stack), max));
		
		/*
		boolean drawDurability = true;
		
		if(flag.isAdvanced())
		{
			
			if(!isCanteenFull(stack))
			{
				//Advanced tooltips, durability is already shown if the item has damage
				drawDurability = false;
			}
		}
		
		if(drawDurability)
		{
			tooltip.add(I18n.format("item.durability", getMaxDoses(stack) - getDoses(stack), getMaxDoses(stack)));
			//tooltip.add(I18n.format("item.durability", stack.getMaxDamage()-stack.getItemDamage(), stack.getMaxDamage()));
		}
		*/
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
    {
		double max = (double)this.getMaxDoses(stack);
		if(max == 0.0d)
			return 1.0d;
		
		
        return (max - (double)getDoses(stack)) / max;
    }
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return !isCanteenEmpty(stack);
	}

	@Override
	public int getThirstLevel(ItemStack stack)
	{
		ThirstEnum thirstEnum = getThirstEnum(stack);
		return thirstEnum==null ? 0 : thirstEnum.getThirst();
	}

	@Override
	public float getSaturationLevel(ItemStack stack)
	{
		ThirstEnum thirstEnum = getThirstEnum(stack);
		return thirstEnum==null ? 0.0f : thirstEnum.getSaturation();
	}

	@Override
	public float getDirtyChance(ItemStack stack)
	{
		ThirstEnum thirstEnum = getThirstEnum(stack);
		return thirstEnum==null ? 0.0f : thirstEnum.getThirstyChance();
	}
	
	//TODO Nullable is not smart here
	@Nullable
	@Override
	public ThirstEnum getThirstEnum(ItemStack stack)
	{
		int type = getTypeTag(stack).getInt();
		if(type >= ThirstEnum.values().length)
			return null;
		
		return ThirstEnum.values()[type];
	}
	
	protected NBTTagInt getTypeTag(ItemStack stack)
	{
		if(stack.getTagCompound()==null)
		{
			createTypeTag(stack);
			setCanteenEmpty(stack);
		}
		
		NBTBase tag = stack.getTagCompound().getTag(CANTEENTYPE);
		if(tag instanceof NBTTagInt)
		{
			return (NBTTagInt)tag;
		}
		else
		{
			stack.getTagCompound().removeTag(CANTEENTYPE);
			createTypeTag(stack);
			return new NBTTagInt(ThirstEnum.NORMAL.ordinal());
		}
	}
	
	protected void setTypeTag(ItemStack stack, ThirstEnum thirstEnum)
	{
		setTypeTag(stack, thirstEnum.ordinal());
	}
	
	protected void setTypeTag(ItemStack stack, int tag)
	{
		// Use cached tag if available to reduce object creation
		if(tag >= 0 && tag < CACHED_TYPE_TAGS.length)
		{
			stack.setTagInfo(CANTEENTYPE, CACHED_TYPE_TAGS[tag]);
		}
		else
		{
			stack.setTagInfo(CANTEENTYPE, new NBTTagInt(tag));
		}
	}
	
	protected void createTypeTag(ItemStack stack)
	{
		setTypeTag(stack,ThirstEnum.NORMAL.ordinal());
	}
	
	protected NBTTagInt getDosesTag(ItemStack stack)
	{
		if(stack.getTagCompound()==null)
		{
			createDosesTag(stack);
			setCanteenEmpty(stack);
		}
		
		NBTBase tag = stack.getTagCompound().getTag(DOSES);
		if(tag instanceof NBTTagInt)
		{
			return (NBTTagInt)tag;
		}
		else
		{
			stack.getTagCompound().removeTag(DOSES);
			createDosesTag(stack);
			return new NBTTagInt(0);
		}
	}
	
	protected void setDosesTag(ItemStack stack, int doses)
	{
		stack.setTagInfo(DOSES, new NBTTagInt(doses));
	}
	
	protected void createDosesTag(ItemStack stack)
	{
		setDosesTag(stack, 0);
	}
	
	@Override
	public int getDoses(ItemStack stack)
	{
		return getDosesTag(stack).getInt();
	}
	
	@Override
	public int getMaxDoses(ItemStack stack)
	{
		return ServerConfig.instance.getInteger(ServerOptions.CANTEEN_DOSES);
	}
	
	@Override
	public boolean isCanteenFull(ItemStack stack)
	{
		return getDoses(stack) >= getMaxDoses(stack);
	}
	
	@Override
	public boolean isCanteenEmpty(ItemStack stack)
	{
		return getDoses(stack) <= 0;
	}
	
	@Override
	public void setCanteenFull(ItemStack stack)
	{
		setDosesInternal(stack, getMaxDoses(stack));
	}
	
	@Override
	public void setCanteenEmpty(ItemStack stack)
	{
		setDosesInternal(stack, 0);
	}
	
	@Override
	public void removeDose(ItemStack stack)
	{
		if(!isCanteenEmpty(stack))
		{
			//setDosesInternal takes care of invalid results
			setDosesInternal(stack, getDoses(stack)-1);
		}
	}
	
	@Override
	public void setDoses(ItemStack stack, int amount)
	{
		//setDosesInternal takes care of invalid results
		setDosesInternal(stack, amount);
	}
	
	@Override
	public void setDoses(ItemStack stack, ThirstEnum thirstEnum, int amount)
	{
		formatCanteen(stack,thirstEnum);
		
		//setDosesInternal takes care of invalid results
		setDosesInternal(stack, amount);
	}
	
	@Override
	public boolean tryAddDose(ItemStack stack, ThirstEnum thirstEnum)
	{
		int oldDamage = getDoses(stack);
		if(oldDamage < 0)
			oldDamage = 0;
		
		boolean format = formatCanteen(stack,thirstEnum);
		//setDosesInternal takes care of invalid results
		
		//getDoses again, as it has possibly changed since formatCanteen
		
		//Oh also try to fill up the whole thing if it's normal water
		if(thirstEnum == ThirstEnum.NORMAL)
		{
			setDosesInternal(stack, getMaxDoses(stack));
		}
		else
		{
			setDosesInternal(stack, getDoses(stack) + 1);
		}
		
		return format || getDoses(stack) != oldDamage;
	}
	
	protected boolean formatCanteen(ItemStack stack, ThirstEnum thirstEnum)
	{
		if(thirstEnum != getThirstEnum(stack))
		{
			//Set canteen to empty and set new type
			setCanteenEmpty(stack);
			setTypeTag(stack,thirstEnum);
			return true;
		}
		
		//Preload doses
		getDoses(stack);
		
		return false;
	}
	
	protected void setDosesInternal(ItemStack stack, int amount)
	{
		
		if(amount<=0)
		{
			this.setDosesTag(stack, 0);
			return;
		}
		
		int max = this.getMaxDoses(stack);
		
		if(amount > max)
		{
			this.setDosesTag(stack, max);
			return;
		}
		
		this.setDosesTag(stack, amount);
	}
}
