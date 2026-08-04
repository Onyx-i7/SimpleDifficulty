package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Compatibility handler for right-click interactions with external blocks.
 */
public class CompatRightClick {
    public static IRightClick cauldronHandler;

    static {
        cauldronHandler = new IRightClick() {
            @Override
            public void process(PlayerInteractEvent.RightClickBlock event, World world, BlockPos pos, BlockState state, PlayerEntity player) {
                ItemStack heldItem = player.getMainHandItem();

                if (heldItem.isEmpty() && player.isCrouching()) {
                    if (SDCapabilities.getThirstData(player).isThirsty()) {
                        if (state.getBlock() instanceof CauldronBlock) {
                            int level = state.getValue(CauldronBlock.LEVEL);
                            if (level > 0) {
                                ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                                SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.GENERIC_DRINK);
                            }
                        }
                    }
                } else if (heldItem.getItem() instanceof IItemCanteen) {
                    if (state.getBlock() instanceof CauldronBlock) {
                        int level = state.getValue(CauldronBlock.LEVEL);
                        if (level > 0) {
                            IItemCanteen canteen = (IItemCanteen) heldItem.getItem();
                            if (canteen.tryAddDose(heldItem, ThirstEnum.NORMAL)) {
                                SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.BUCKET_FILL);
                            }
                        }
                    }
                }
            }
        };
    }

    /**
     * Interface for right-click compatibility handlers.
     */
    public interface IRightClick {
        void process(PlayerInteractEvent.RightClickBlock event, World world, BlockPos pos, BlockState state, PlayerEntity player);
    }
}