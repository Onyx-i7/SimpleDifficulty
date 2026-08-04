package com.charles445.simpledifficulty.tileentity;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.block.BlockCampfire;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.SoundUtil;
import com.charles445.simpledifficulty.register.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * TileEntity for the Spit block, which cooks food items placed on it
 * when positioned above a burning campfire.
 */
public class TileEntitySpit extends TileEntity implements ITickableTileEntity {

    private static final String NBT_INT_PROGRESS = "progress";
    private static final String NBT_DOUBLE_EXPERIENCE = "experience";
    private static final String NBT_TAG_ITEMS = "items";

    public ItemHandler items;
    private int progress = 0;
    private double experience = 0.0d;
    private int timer = 0;

    public TileEntitySpit() {
        super(RegisterBlocks.SPIT_TILE_ENTITY.get());
        items = new ItemHandler(ModConfig.SERVER.campfireSpitSize.get());
    }

    public TileEntitySpit(TileEntityType<?> type) {
        super(type);
        items = new ItemHandler(ModConfig.SERVER.campfireSpitSize.get());
    }

    @Override
    public void tick() {
        // All cooking logic and timings occur exclusively on the server
        if (this.level == null || this.level.isClientSide) {
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
            if (progress >= ModConfig.SERVER.campfireSpitDelay.get()) {
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
                ItemStack result = getCookingResult(stack);
                if (result != null && !result.isEmpty()) {
                    ItemStack resultCopy = result.copy();

                    if (ModConfig.SERVER.campfireSpitExperience.get()) {
                        experience += getCookingExperience(resultCopy);
                    }

                    items.setStackInSlot(i, resultCopy);
                    changed = true;
                }
            }
        }
        if (changed) {
            this.setChanged();
            updateClients();
        }
    }

    @Nullable
    private ItemStack getCookingResult(ItemStack stack) {
        if (this.level == null) return ItemStack.EMPTY;
        return this.level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new net.minecraft.inventory.Inventory(stack), this.level)
            .map(recipe -> recipe.assemble(new net.minecraft.inventory.Inventory(stack)))
            .orElse(ItemStack.EMPTY);
    }

    private float getCookingExperience(ItemStack stack) {
        if (this.level == null) return 0.0f;
        return this.level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new net.minecraft.inventory.Inventory(stack), this.level)
            .map(recipe -> recipe.getExperience())
            .orElse(0.0f);
    }

    private boolean playWorldSound(World world, BlockPos pos, boolean deposit) {
        SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.PLAYER_ATTACK_NODAMAGE, 0.4f, 0.9f);
        return true;
    }

    public void handleRightClick(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) return;

        ItemStack heldItemStack = player.getItemInHand(hand);
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
            java.util.List<?> spitBlacklist = ModConfig.SERVER.campfireSpitBlacklist.get();

            for (String s : spitBlacklist) {
                if (s.equals(heldItemName)) {
                    isBlacklisted = true;
                    break;
                }
            }

            java.util.List<? extends String> spitBlacklist = ModConfig.SERVER.campfireSpitBlacklist.get(); {
                for (int i = 0; i < items.getSlots(); i++) {
                    if (items.getStackInSlot(i).isEmpty()) {
                        items.insertItem(i, new ItemStack(heldItemStack.getItem(), 1), false);
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
        if (!found && rawWithdraw && player.isCrouching()) {
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

    private boolean withdrawFromSlot(PlayerEntity player, Hand hand, int slot) {
        ItemStack stack = items.extractItem(slot, 1, false);
        if (stack.isEmpty()) return false;

        if (player.getItemInHand(hand).isEmpty()) {
            player.setItemInHand(hand, stack);
            return true;
        } else if (!player.inventory.add(stack)) {
            player.drop(stack, false);
            return false;
        } else {
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) player).refreshContainer(player.containerMenu);
            }
            return false;
        }
    }

    private boolean isCookable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (this.level == null) return false;
        ItemStack result = getCookingResult(stack);
        return !result.isEmpty() && result.getItem().isEdible();
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

        BlockState state = level.getBlockState(worldPosition.below());
        if (state.getBlock() == SDBlocks.campfire.get()) {
            return state.getValue(BlockCampfire.BURNING);
        }
        return false;
    }

    public void dumpItems(World world, BlockPos pos) {
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack itemstack = items.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                items.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        this.setChanged();
        updateClients();
    }

    public void dumpExperience(World world, BlockPos pos) {
        int convExp = (int) experience;
        experience -= convExp;

        while (convExp > 0) {
            int val = ExperienceOrbEntity.getExperienceValue(convExp);
            convExp -= val;
            world.addFreshEntity(new ExperienceOrbEntity(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, val));
        }
        this.setChanged();
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        progress = compound.getInt(NBT_INT_PROGRESS);
        items.deserializeNBT(compound.getCompound(NBT_TAG_ITEMS));
        experience = compound.getDouble(NBT_DOUBLE_EXPERIENCE);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.putInt(NBT_INT_PROGRESS, progress);
        compound.put(NBT_TAG_ITEMS, items.serializeNBT());
        compound.putDouble(NBT_DOUBLE_EXPERIENCE, experience);
        return compound;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();
        if (tag != null) {
            BlockState state = this.level != null ? this.level.getBlockState(worldPosition) : null;
            if (state != null) {
                load(state, tag);
            }
            if (this.level != null && this.level.isClientSide) {
                // Force the client renderer to redraw the block to update the floating items
                this.level.sendBlockUpdated(worldPosition, state, state, 2);
            }
        }
    }

    public void updateClients() {
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 2);
        }
    }

    public class ItemHandler extends ItemStackHandler {
        public ItemHandler(int slots) {
            super(slots);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            TileEntitySpit.this.setChanged();
            TileEntitySpit.this.updateClients();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }
}