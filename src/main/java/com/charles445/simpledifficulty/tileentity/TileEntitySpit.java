package com.charles445.simpledifficulty.tileentity;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.block.BlockCampfire;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntitySpit extends TileEntity implements ITickable {

    private static final String NBT_INT_PROGRESS = "progress";
    private static final String NBT_DOUBLE_EXPERIENCE = "experience";
    private static final String NBT_TAG_ITEMS = "items";
    
    public ItemHandler items;
    private int progress = 0;
    private double experience = 0.0d;
    private int timer = 0;
    
    public TileEntitySpit() {
        items = new ItemHandler(ModConfig.server.miscellaneous.campfireSpitSize);
    }
    
    @Override
    public void update() {
        // All the cooking logic and timings occur exclusively on the server
        if (this.world == null || this.world.isRemote) {
            return;
        }
        
        this.timer++;
        if (this.timer >= 20) {
            this.timer = 0;
            secondUpdate();
        }
    }
    
    private void secondUpdate() {
        if (shouldCook()) {
            progress++;
            if (progress >= ModConfig.server.miscellaneous.campfireSpitDelay) {
                cookFood();
                progress = 0;
            }
        } else {
            if (progress > 0) {
                progress = 0; // Clean reset if they turn off the campfire halfway through cooking
            }
        }
    }
    
    private void cookFood() {
        boolean changed = false;
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stack = items.getStackInSlot(i);
            
            if (isCookable(stack)) {
                ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack).copy();
                
                if (ModConfig.server.miscellaneous.campfireSpitExperience) {
                    experience += FurnaceRecipes.instance().getSmeltingExperience(result);
                }
                
                items.setStackInSlot(i, result);
                changed = true;
            }
        }
        if (changed) {
            this.markDirty();
            updateClients();
        }
    }
    
    private boolean playWorldSound(World world, BlockPos pos, boolean deposit) {
        SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, 0.4f, 0.9f);
        return true;
    }
    
    public void handleRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return;

        ItemStack heldItemStack = player.getHeldItem(hand);
        boolean rawWithdraw = heldItemStack.isEmpty();
        boolean playedSound = false;
        boolean found = false;
        boolean withdrewToHand = false;
        
        // Try removing cooked food first
        for (int i = 0; i < items.getSlots(); i++) {
            if (isCooked(items.getStackInSlot(i))) {
                withdrewToHand = withdrawFromSlot(player, hand, i);
                dumpExperience(world, pos);
                if (!playedSound) {
                    playedSound = playWorldSound(world, pos, false);
                }
                found = true;
                break;
            }
        }
        
        // Place raw food on the grill
        if (!withdrewToHand && isCookable(heldItemStack)) {
            String heldItemName = heldItemStack.getItem().getRegistryName().toString();
            boolean isBlacklisted = false;
            String[] spitBlacklist = ModConfig.server.miscellaneous.campfireSpitBlacklist;
            
            for (String s : spitBlacklist) {
                if (s.equals(heldItemName)) {
                    isBlacklisted = true;
                    break;
                }
            }
            
            if (isBlacklisted == ModConfig.server.miscellaneous.campfireSpitBlacklistIsWhitelist) {
                for (int i = 0; i < items.getSlots(); i++) {
                    if (items.getStackInSlot(i).isEmpty()) {
                        items.insertItem(i, new ItemStack(heldItemStack.getItem(), 1, heldItemStack.getItemDamage()), false);
                        heldItemStack.shrink(1);
                        progress = 0;
                        
                        if (!playedSound) {
                            playedSound = playWorldSound(world, pos, true);
                        }
                        found = true;
                        break;
                    }
                }
            }
        }
        
        // Bending down with an empty hand removes raw food prematurely
        if (!found && rawWithdraw && player.isSneaking()) {
            for (int i = 0; i < items.getSlots(); i++) {
                if (!items.getStackInSlot(i).isEmpty()) {
                    withdrawFromSlot(player, hand, i);
                    if (!playedSound) {
                        playedSound = playWorldSound(world, pos, false);
                    }
                    break;
                }
            }
        }
    }
    
    private boolean withdrawFromSlot(EntityPlayer player, EnumHand hand, int slot) {
        ItemStack stack = items.extractItem(slot, 1, false);
        if (stack.isEmpty()) return false;

        if (player.getHeldItem(hand).isEmpty()) {
            player.setHeldItem(hand, stack);
            return true;
        } else if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
            return false;
        } else {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
            }
            return false;
        }
    }
    
    private boolean isCookable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
        return !result.isEmpty() && result.getItem() instanceof ItemFood;
    }
    
    private boolean isCooked(ItemStack stack) {
        return !stack.isEmpty() && !isCookable(stack);
    }
    
    private boolean shouldCook() {
        boolean hasItem = false;
        for (int i = 0; i < items.getSlots(); i++) {
            if (isCookable(items.getStackInSlot(i))) {
                hasItem = true;
                break;
            }
        }
        
        if (!hasItem) return false;
    
        IBlockState state = world.getBlockState(pos.down());
        if (state.getBlock() == SDBlocks.campfire) {
            return state.getValue(BlockCampfire.BURNING);
        }
        return false;
    }
    
    public void dumpItems(World world, BlockPos pos) {
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack itemstack = items.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                items.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        this.markDirty();
        updateClients();
    }
    
    public void dumpExperience(World world, BlockPos pos) {
        int convExp = (int) experience;
        experience -= convExp;
        
        while (convExp > 0) {
            int val = EntityXPOrb.getXPSplit(convExp);
            convExp -= val;
            world.spawnEntity(new EntityXPOrb(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, val));
        }
        this.markDirty();
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        progress = compound.getInteger(NBT_INT_PROGRESS);
        items.deserializeNBT(compound.getCompoundTag(NBT_TAG_ITEMS));
        experience = compound.getDouble(NBT_DOUBLE_EXPERIENCE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(NBT_INT_PROGRESS, progress);
        compound.setTag(NBT_TAG_ITEMS, items.serializeNBT());
        compound.setDouble(NBT_DOUBLE_EXPERIENCE, experience);
        return compound;
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (this.world != null && this.world.isRemote) {
            // Force the client renderer to redraw the block to update the floating items
            IBlockState state = world.getBlockState(pos);
            this.world.notifyBlockUpdate(pos, state, state, 2);
        }
    }
    
    public void updateClients() {
        if (this.world != null && !this.world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 2);
        }
    }
    
    public class ItemHandler extends ItemStackHandler {
        public ItemHandler(int slots) {
            super(slots);
        }
        
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            TileEntitySpit.this.markDirty();
            TileEntitySpit.this.updateClients();
        }
        
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }
}
