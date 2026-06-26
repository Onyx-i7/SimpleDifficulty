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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockRainCollector extends Block {

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);
    private static final int BASE_TICK_RATE = 240; // 12 seconds baseline interval for slower collection
    
    public BlockRainCollector() {
        super(Material.IRON, MapColor.STONE);
        setDefaultState(blockState.getBaseState().withProperty(LEVEL, 0));
        setHardness(2.0f);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
    }

    private void scheduleDynamicUpdate(World world, BlockPos pos) {
        if (!world.isUpdateScheduled(pos, this)) {
            // Generates a wide window between 12 to 16 seconds to slow down collection significantly
            int dynamicRate = BASE_TICK_RATE + world.rand.nextInt(81);
            world.scheduleUpdate(pos, this, dynamicRate);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            scheduleDynamicUpdate(world, pos);
        }
    }
        
    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (world.isRemote) {
            return;
        }
        tryFillFromWeather(world, pos, state, random);
        scheduleDynamicUpdate(world, pos);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        tryFillFromWeather(world, pos, state, rand);
        scheduleDynamicUpdate(world, pos);
    }

    /**
     * Legacy 3-parameter endpoint wrapper to ensure total compatibility with external handlers (e.g., MiscHandler)
     */
    public void tryFillFromWeather(World world, BlockPos pos, IBlockState state) {
        tryFillFromWeather(world, pos, state, world.rand);
    }

    /**
     * Internal calibrated weather calculation channel utilizing a 25% chance gate per ticker cycle
     */
    public void tryFillFromWeather(World world, BlockPos pos, IBlockState state, Random rand) {
        BlockPos checkPos = pos.up();
        
        if (rand.nextInt(4) == 0 && world.canSeeSky(checkPos) && com.charles445.simpledifficulty.compat.mod.Weather2Compat.isRainingAt(world, checkPos)) {
            int currentLevel = state.getValue(LEVEL);
            if (currentLevel < 3) {
                world.setBlockState(pos, state.withProperty(LEVEL, currentLevel + 1), 3);
                world.updateComparatorOutputLevel(pos, this);
            }
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.isEmpty()) {
            if (player.isSneaking()) {
                int amount = state.getValue(LEVEL);
                if (amount > 0) {
                    if (SDCapabilities.getThirstData(player).isThirsty()) {
                        SoundUtil.commonPlayPlayerSound(player, SoundEvents.ENTITY_GENERIC_DRINK);
                        if (!world.isRemote) {
                            this.setWaterLevel(world, pos, state, player.capabilities.isCreativeMode ? amount : amount - 1);
                            ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                        }
                    }
                }
            }
            return true;
        } else {
            int amount = state.getValue(LEVEL);
            Item item = itemstack.getItem();
            
            if (item == Items.BUCKET) {
                if (amount > 0 && !world.isRemote) {
                    if (!player.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.setHeldItem(hand, ThirstUtil.createNormalWaterBucket());
                        } else if (!player.inventory.addItemStackToInventory(ThirstUtil.createNormalWaterBucket())) {
                            player.dropItem(ThirstUtil.createNormalWaterBucket(), false);
                        }
                    }
                    this.setWaterLevel(world, pos, state, player.capabilities.isCreativeMode ? amount : amount - 1);
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
                }
                return true;
            } else if (item == Items.GLASS_BOTTLE) {
                if (amount > 0 && !world.isRemote) {
                    if (!player.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                    
                    ItemStack waterBottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
                    
                    if (itemstack.isEmpty()) {
                        player.setHeldItem(hand, waterBottle);
                    } else if (!player.inventory.addItemStackToInventory(waterBottle)) {
                        player.dropItem(waterBottle, false);
                    } else if (player instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                    }
                    
                    this.setWaterLevel(world, pos, state, player.capabilities.isCreativeMode ? amount : amount - 1);
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BOTTLE_FILL);
                }
                return true;
            } else if (item == SDItems.canteen) {
                if (amount > 0 && !world.isRemote) {
                    IItemCanteen canteen = (IItemCanteen) item;
                    if (player.capabilities.isCreativeMode) {
                        canteen.tryAddDose(itemstack, ThirstEnum.NORMAL);
                    } else {
                        if (canteen.tryAddDose(itemstack, ThirstEnum.NORMAL)) {
                            this.setWaterLevel(world, pos, state, amount - 1);
                        }
                    }
                    SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
                }
                return true;
            } else {
                return false;
            }
        }
    }
    
    public void setWaterLevel(World world, BlockPos pos, IBlockState state, int level) {
        world.setBlockState(pos, state.withProperty(LEVEL, MathHelper.clamp(level, 0, 3)), 3);
        world.updateComparatorOutputLevel(pos, this);
    }
    
    @Override
    public boolean hasComparatorInputOverride(IBlockState state) { 
        return true; 
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return blockState.getValue(LEVEL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) { 
        return getDefaultState().withProperty(LEVEL, meta); 
    }

    @Override
    public int getMetaFromState(IBlockState state) { 
        return state.getValue(LEVEL); 
    }

    @Override
    protected BlockStateContainer createBlockState() { 
        return new BlockStateContainer(this, new IProperty[] {LEVEL}); 
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) { 
        return false; 
    }

    @Override
    public boolean isFullCube(IBlockState state) { 
        return false; 
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() { 
        return BlockRenderLayer.CUTOUT; 
    }
}
