package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.tileentity.TileEntityTemperature;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider; // Migrated to a secure interface (still needs testing btw)
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockTemperature extends Block implements ITileEntityProvider {
    public static final PropertyBool ENABLED = PropertyBool.create("enabled");
    
    private final float temperature;
    
    public BlockTemperature(float temperature) {
        super(Material.IRON);
        setHardness(0.5f);
        setDefaultState(blockState.getBaseState().withProperty(ENABLED, false));
        setSoundType(SoundType.METAL);
        this.temperature = temperature;
    }
    
    public float getActiveTemperatureMult() {
        return temperature;
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTemperature();
    }
    
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            boolean enabled = state.getValue(ENABLED);
            boolean powered = world.isBlockPowered(pos);
            
            if (enabled && !powered) {
                turnOff(world, pos, state);
            }
            else if(!enabled && powered) {
                turnOn(world, pos, state);
            }
        }
    }
    
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote) {
            boolean enabled = state.getValue(ENABLED);
            boolean powered = world.isBlockPowered(pos);
        
            if (enabled && !powered) {
                world.scheduleUpdate(pos, this, 4);
            }
            else if (!enabled && powered) {
                turnOn(world, pos, state);
            }
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (state.getValue(ENABLED) && !world.isBlockPowered(pos)) {
                turnOff(world, pos, state);
            }
        }
    }
    
    private void turnOff(final World world, final BlockPos pos, final IBlockState state) {
        // Changed to Flag 3 to prevent Redstone loop closures
        world.setBlockState(pos, state.withProperty(ENABLED, false), 3);
    }
    
    private void turnOn(final World world, final BlockPos pos, final IBlockState state) {
        // Changed to Flag 3 for consistent physical condition updates
        world.setBlockState(pos, state.withProperty(ENABLED, true), 3);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        // Manually purge the TileEntity when the block breaks to prevent memory leaks
        world.removeTileEntity(pos);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
    
    // RENDER
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        super.randomDisplayTick(state, world, pos, rand);
        
        if(state.getValue(ENABLED)) {
            if(rand.nextFloat() <= 0.33f) {
                createRandomParticle(world, pos, rand);
            }
        }
    }
    
    private void createRandomParticle(World world, BlockPos pos, Random rand) {
        double x_a = 0.125d;
        double z_a = 0.125d;
        double x_b = 0.0d;
        double z_b = 0.0d;
        double endoff = 0.875d;
        double thickness = 0.2d;
        
        switch(rand.nextInt(4)) {
            case 0: 
                x_b = endoff;
                z_b = thickness;
                break;
            case 1: 
                x_a = endoff - thickness;
                x_b = endoff;
                z_b = endoff;
                break;
            case 2:
                x_b = endoff;
                z_a = endoff - thickness;
                z_b = endoff;
                break;
            case 3:
                x_b = thickness;
                z_b = endoff;
                break;
            default: break;
        }
        
        double x_r = rand.nextDouble() * (x_b - x_a);
        double z_r = rand.nextDouble() * (z_b - z_a);

        if(ModConfig.client.heaterParticles)
            SimpleDifficulty.proxy.spawnClientParticle(world, temperature >= 0.0f ? "HEATER" : "CHILLER", x_a + x_r + pos.getX(), 0.775d + pos.getY(), z_a + z_r + pos.getZ(), 0.0d, 0.05d, 0.0d);
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("deprecation")
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(ENABLED) ? 15728880 : super.getPackedLightmapCoords(state, source, pos);
    }
    
    // STATE
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ENABLED, meta > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ENABLED) ? 1 : 0;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {ENABLED});
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

    @SideOnly(Side.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 1.0F;
    }
    
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(ENABLED) ? 7 : 0;
    }
}
