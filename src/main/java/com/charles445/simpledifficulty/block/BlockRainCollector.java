package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

public class BlockRainCollector extends Block
{
    /*
     * This is essentially a faster acting cauldron
     * As such, most of it can be found in BlockCauldron
     * * It would be nice to just extend BlockCauldron but that's harder to manage
     */
    
    //BlockCauldron.LEVEL
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);
    
    private static Method weather2IsRainingMethod = null;
    private static Object weather2ManagerInstance = null;
    
    static {
        try {
            if (Loader.isModLoaded("weather2")) {
                String[] classNames = {
                    "corosus.weather2.weathersystem.WeatherManager",
                    "corosus.weather2.weathersystem.WeatherManagerBase",
                    "corosus.weather2.util.WorldHelper",
                    "corosus.weather2.util.WeatherUtil",
                    "corosus.weather2.WorldHelper"
                };
                
                for (String className : classNames) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        
                        if (weather2ManagerInstance == null) {
                            for (Field f : clazz.getFields()) {
                                if (f.getName().equalsIgnoreCase("instance") || f.getName().equalsIgnoreCase("INSTANCE")) {
                                    weather2ManagerInstance = f.get(null);
                                    break;
                                }
                            }
                        }
                        
                        for (Method m : clazz.getMethods()) {
                            String name = m.getName().toLowerCase();
                            if ((name.contains("rain") || name.contains("precip") || name.contains("weather")) && m.getReturnType() == boolean.class) {
                                Class<?>[] params = m.getParameterTypes();
                                if (params.length == 1 && params[0] == BlockPos.class) {
                                    weather2IsRainingMethod = m;
                                    break;
                                } else if (params.length == 2 && params[0] == World.class && params[1] == BlockPos.class) {
                                    weather2IsRainingMethod = m;
                                    break;
                                }
                            }
                        }
                        
                        if (weather2IsRainingMethod != null) {
                            break;
                        }
                    } catch (ClassNotFoundException ignored) {}
                }
            }
        } catch (Exception e) {
            // Failed to initialize Weather2 compatibility
        }
    }
    
    //BlockCauldron()
    public BlockRainCollector()
    {
        super(Material.IRON, MapColor.STONE);
        setDefaultState(blockState.getBaseState().withProperty(LEVEL, 0));
        setHardness(2.0f);
        setSoundType(SoundType.METAL);

        //Serene Seasons Compatibility
        setTickRandomly(true);
    }
        
    //Serene Seasons and Weather2 Remastered Compatibility
    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        boolean isRaining = false;
        if (weather2IsRainingMethod != null) {
            try {
                Object target = java.lang.reflect.Modifier.isStatic(weather2IsRainingMethod.getModifiers()) ? null : weather2ManagerInstance;
                if (target != null || java.lang.reflect.Modifier.isStatic(weather2IsRainingMethod.getModifiers())) {
                    if (weather2IsRainingMethod.getParameterTypes().length == 1) {
                        isRaining = (Boolean) weather2IsRainingMethod.invoke(target, pos.up());
                    } else {
                        isRaining = (Boolean) weather2IsRainingMethod.invoke(target, world, pos.up());
                    }
                } else {
                    isRaining = world.isRaining();
                }
            } catch (Exception e) {
                isRaining = world.isRaining();
            }
        } else {
            isRaining = world.isRaining();
        }

        if (world.rand.nextInt(Math.max(1, ModConfig.server.miscellaneous.rainCollectorFillChance)) == 0 && isRaining && world.canSeeSky(pos.up()))
        {
            float f = world.getBiome(pos).getTemperature(pos);

            if (world.getBiomeProvider().getTemperatureAtHeight(f, pos.getY()) >= 0.15F)
            {
                IBlockState iblockstate = world.getBlockState(pos);

                if (iblockstate.getValue(LEVEL) < 3)
                {
                    // OPTIMIZATION: Switched to flag 3 to update redstone/comparators cleanly during automatic rain filling
                    world.setBlockState(pos, iblockstate.cycleProperty(LEVEL), 3);
                }
            }
        }
    }
    
    //BlockCauldron.onBlockActivated
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.isEmpty())
        {
            if(player.isSneaking())
            {
                int amount = state.getValue(LEVEL);
                if(amount > 0)
                {
                    if(SDCapabilities.getThirstData(player).isThirsty())
                    {
                        SoundUtil.commonPlayPlayerSound(player, SoundEvents.ENTITY_GENERIC_DRINK);
                        
                        if(!world.isRemote)
                        {
                            // Server Side
                            this.setWaterLevel(world, pos, state, player.capabilities.isCreativeMode ? amount : amount - 1);
                            ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                        }
                    }
                }
            }
            
            return true;
        }
        else
        {
            int amount = state.getValue(LEVEL);
            Item item = itemstack.getItem();
            
            if (item == Items.BUCKET)
            {
                if (amount > 0 && !world.isRemote)
                {
                    if (!player.capabilities.isCreativeMode)
                    {
                        itemstack.shrink(1);

                        if (itemstack.isEmpty())
                        {
                            player.setHeldItem(hand, ThirstUtil.createNormalWaterBucket());
                        }
                        else if (!player.inventory.addItemStackToInventory(ThirstUtil.createNormalWaterBucket()))
                        {
                            player.dropItem(ThirstUtil.createNormalWaterBucket(), false);
                        }
                    }
                    this.setWaterLevel(world, pos, state, player.capabilities.isCreativeMode ? amount : amount - 1);
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
                }
                
                return true;
            }
            else if (item == Items.GLASS_BOTTLE)
            {
                if(amount > 0 && !world.isRemote)
                {
                    // FIX: Allowed Creative Mode players to fill glass bottles, matching vanilla behaviors
                    if(!player.capabilities.isCreativeMode)
                    {
                        itemstack.shrink(1);
                    }
                    
                    ItemStack waterBottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
                    
                    if (itemstack.isEmpty())
                    {
                        player.setHeldItem(hand, waterBottle);
                    }
                    else if (!player.inventory.addItemStackToInventory(waterBottle))
                    {
                        player.dropItem(waterBottle, false);
                    }
                    else if (player instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                    }
                    
                    this.setWaterLevel(world, pos, state, player.capabilities.isCreativeMode ? amount : amount - 1);
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BOTTLE_FILL);
                }
                
                return true;
            }
            else if (item == SDItems.canteen)
            {
                if(amount > 0 && !world.isRemote)
                {
                    // FIX: Allowed Creative Mode players to test and fill doses into canteens seamlessly without draining water
                    IItemCanteen canteen = (IItemCanteen)item;
                    
                    if (player.capabilities.isCreativeMode)
                    {
                        canteen.tryAddDose(itemstack, ThirstEnum.NORMAL);
                    }
                    else
                    {
                        if(canteen.tryAddDose(itemstack, ThirstEnum.NORMAL))
                        {
                            this.setWaterLevel(world, pos, state, amount - 1);
                        }
                    }
                    
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
                }
                
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    //BlockCauldron.setWaterLevel
    public void setWaterLevel(World world, BlockPos pos, IBlockState state, int level)
    {
        // OPTIMIZATION: Flag 3 forces immediate block rerender AND updates neighbor triggers for reliable Redstone comparator integration
        world.setBlockState(pos, state.withProperty(LEVEL, MathHelper.clamp(level, 0, 3)), 3);
        world.updateComparatorOutputLevel(pos, this);
    }
    
    // COMPARATOR
    
    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return blockState.getValue(LEVEL);
    }

    // STATE
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LEVEL, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LEVEL);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {LEVEL});
    }
    
    // RENDER
    
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}
