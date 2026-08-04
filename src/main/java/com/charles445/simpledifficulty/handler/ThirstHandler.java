package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.compat.CompatRightClick;
import com.charles445.simpledifficulty.compat.ModNames;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.network.MessageDrinkWater;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.util.SoundUtil;
import com.charles445.simpledifficulty.util.internal.ThirstUtilInternal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.List;

/**
 * Handler for thirst-related events.
 */
public class ThirstHandler {
    private static final String MC_DOMAIN = "minecraft";

    private final boolean harvestcraftLoaded = ModList.get().isLoaded(ModNames.HARVESTCRAFT);

    /**
     * Applies thirst effects when a player finishes consuming an item.
     *
     * @param event The item use finish event.
     */
    @SubscribeEvent
    public void onLivingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }

        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            if (player.level.isClientSide) {
                return;
            }

            // Server Side
            ItemStack stack = event.getItem();
            ResourceLocation regName = stack.getItem().getRegistryName();

            // Prevent NPE crash if registry name is null
            if (regName == null) {
                return;
            }

            // JSON
            List<JsonConsumableThirst> consumableList = JsonConfig.consumableThirst.get(regName.toString());
            if (consumableList != null) {
                for (JsonConsumableThirst jct : consumableList) {
                    if (jct == null) {
                        continue;
                    }
                    if (jct.matches(stack)) {
                        ThirstUtil.takeDrink(player, jct.amount, jct.saturation, jct.thirstyChance);
                        return;
                    }
                }
            }

            // Vanilla Potions
            if (stack.getItem() == Items.POTION) {
                Potion potionType = PotionUtils.getPotion(stack);

                if (potionType.getRegistryName() != null) {
                    String modDomain = potionType.getRegistryName().getNamespace();

                    // Vanilla potions
                    if (modDomain.equals(MC_DOMAIN)) {
                        if (potionType == Potions.WATER || potionType == Potions.AWKWARD ||
                                potionType == Potions.MUNDANE || potionType == Potions.THICK) {
                            ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                            return;
                        } else if (potionType != Potions.EMPTY) {
                            ThirstUtil.takeDrink(player, ThirstEnum.POTION);
                            return;
                        }
                    } else if (SDPotions.potionTypes.containsValue(potionType)) {
                        ThirstUtil.takeDrink(player, ThirstEnum.POTION);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Intercepts bottle interactions with mod fluid blocks.
     *
     * @param event The right click block event.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickBlockBottle(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isClientSide) {
            return; // Only process on server
        }

        PlayerEntity player = event.getPlayer();
        ItemStack heldItem = player.getItemInHand(event.getHand());

        // Only handle empty bottles
        if (heldItem.isEmpty() || heldItem.getItem() != Items.GLASS_BOTTLE) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = event.getWorld().getBlockState(pos);
        Block block = state.getBlock();

        // Check if clicking on mod fluid blocks
        ItemStack resultBottle = null;

        Block purifiedWaterBlock = SDFluids.blockPurifiedWater.get();
        Block saltWaterBlock = SDFluids.blockSaltWater.get();

        if (block == purifiedWaterBlock) {
            resultBottle = new ItemStack(SDItems.purifiedWaterBottle.get());
        } else if (block == saltWaterBlock) {
            resultBottle = new ItemStack(SDItems.saltWaterBottle.get());
        }

        // If found a valid bottle result, handle it
        if (resultBottle != null) {
            // Cancel the vanilla interaction
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.SUCCESS);

            // Give the player the bottle
            heldItem.shrink(1);

            if (heldItem.isEmpty()) {
                player.setItemInHand(event.getHand(), resultBottle);
            } else if (!player.getInventory().add(resultBottle)) {
                player.drop(resultBottle, false);
            }
        }
    }

    /**
     * Handles right-click block events for drinking from cauldrons.
     *
     * @param event The right click block event.
     */
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }

        if (event.getWorld().isClientSide) {
            // Client Side
            if (event.getHand() == Hand.MAIN_HAND) {
                if (clientCheckWater(event.getPlayer())) {
                    clientSendDrinkMessageAndPlaySound(event.getPlayer());
                }
            }
        } else {
            // Server side
            PlayerEntity player = event.getPlayer();
            Hand hand = event.getHand();
            if (hand == Hand.MAIN_HAND) {
                World world = event.getWorld();
                BlockPos pos = event.getPos();
                BlockState state = world.getBlockState(pos);

                if (state.getBlock() == Blocks.CAULDRON) {
                    CompatRightClick.cauldronHandler.process(event, world, pos, state, player);
                }
            }
        }
    }

    /**
     * Handles right-click empty events for drinking from water sources.
     *
     * @param event The right click empty event.
     */
    @SubscribeEvent
    public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }

        if (event.getHand() == Hand.MAIN_HAND) {
            if (clientCheckWater(event.getPlayer())) {
                clientSendDrinkMessageAndPlaySound(event.getPlayer());
            }
        }
    }

    private boolean clientCheckWater(PlayerEntity player) {
        if (!player.isCrouching() || !QuickConfig.isThirstEnabled()) {
            return false;
        }

        return ThirstUtilInternal.traceWaterToDrink(player) != null;
    }

    private void clientSendDrinkMessageAndPlaySound(PlayerEntity player) {
        MessageDrinkWater message = new MessageDrinkWater();
        PacketHandler.INSTANCE.sendToServer(message);

        player.swing(Hand.MAIN_HAND);
        SoundUtil.commonPlayPlayerSound(player, SoundEvents.GENERIC_DRINK);
    }

    //
    // Thirst Exhausting Events
    //

    /**
     * Adds exhaustion when a player attacks an entity.
     *
     * @param event The attack entity event.
     */
    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (!QuickConfig.isThirstEnabled()) return;

        World world = event.getEntity().level;
        if (world.isClientSide) return;

        // Server Side
        PlayerEntity player = event.getPlayer();

        if (!shouldSkipThirst(player)) {
            Entity monster = event.getTarget();
            if (monster.isAttackable()) {
                float exhaustion = (float) (ModConfig.SERVER.thirstAttacking.get() * QuickConfig.getThirstExhaustionMultiplier());
                addExhaustion(player, exhaustion);
            }
        }
    }

    /**
     * Adds exhaustion when a player breaks a block.
     *
     * @param event The block break event.
     */
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!QuickConfig.isThirstEnabled()) return;

        World world = event.getWorld();
        if (world.isClientSide) return;

        PlayerEntity player = event.getPlayer();

        if (!shouldSkipThirst(player)) {
            if (event.getState().getBlock().canHarvestBlock(event.getState(), world, event.getPos(), player)) {
                float exhaustion = (float) (ModConfig.SERVER.thirstBreakBlock.get() * QuickConfig.getThirstExhaustionMultiplier());
                addExhaustion(player, exhaustion);
            }
        }
    }

    /**
     * Adds exhaustion when a player takes damage.
     *
     * @param event The living hurt event.
     */
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!QuickConfig.isThirstEnabled()) return;

        World world = event.getEntity().level;
        if (world.isClientSide || event.getAmount() == 0.0f) return;

        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            if (!shouldSkipThirst(player)) {
                // Note: In 1.16.5, DamageSource doesn't have getHungerDamage()
                // We'll use a fixed value based on damage amount
                float exhaustion = (float) (event.getAmount() * 0.1f * QuickConfig.getThirstExhaustionMultiplier());
                addExhaustion(player, exhaustion);
            }
        }
    }

    /**
     * Adds exhaustion when a player jumps.
     *
     * @param event The living jump event.
     */
    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!QuickConfig.isThirstEnabled()) return;

        World world = event.getEntity().level;
        if (world.isClientSide) return;

        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            if (!shouldSkipThirst(player)) {
                double multiplier = QuickConfig.getThirstExhaustionMultiplier();
                float exhaustion = player.isSprinting()
                        ? (float) (ModConfig.SERVER.thirstSprintJump.get() * multiplier)
                        : (float) (ModConfig.SERVER.thirstJump.get() * multiplier);

                addExhaustion(player, exhaustion);
            }
        }
    }

    private boolean shouldSkipThirst(PlayerEntity player) {
        return player.isCreative() || player.isSpectator();
    }

    private void addExhaustion(PlayerEntity player, float exhaustion) {
        if (exhaustion <= 0.0f) return;

        IThirstCapability capability = SDCapabilities.getThirstData(player);
        if (capability != null) {
            capability.addThirstExhaustion(exhaustion);
        }
    }
}