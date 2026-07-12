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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ThirstHandler {
    // Cached registry name strings to avoid repeated allocations
    private static final String MC_DOMAIN = "minecraft";
    
    private final boolean harvestcraftLoaded = Loader.isModLoaded(ModNames.HARVESTCRAFT);
    
    // Both Sides
    @SubscribeEvent
    public void onLivingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }
        
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            
            if (player.world.isRemote) {
                return;
            }
            
            // Server Side
            ItemStack stack = event.getItem();
            ResourceLocation regName = stack.getItem().getRegistryName();
            
            // Prevent NPE crash if registry name is null (unregistered items)
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
            
            // Use == instead of .equals() for singleton comparison
            if (stack.getItem() == Items.POTIONITEM) {
                PotionType potionType = PotionUtils.getPotionFromItem(stack);
                
                if (potionType.getRegistryName() != null) {
                    String modDomain = potionType.getRegistryName().getNamespace();
                    
                    // Vanilla potions - use cached MC_DOMAIN string
                    if (modDomain.equals(MC_DOMAIN)) {
                        if (potionType == PotionTypes.WATER || potionType == PotionTypes.AWKWARD || 
                            potionType == PotionTypes.MUNDANE || potionType == PotionTypes.THICK) {
                            ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                            return;
                        } else if (potionType != PotionTypes.EMPTY) {
                            ThirstUtil.takeDrink(player, ThirstEnum.POTION);
                            return;
                        }
                    } else if (SDPotions.potionTypes.containsValue(potionType)) {
                        ThirstUtil.takeDrink(player, ThirstEnum.POTION);
                        return;
                    }
                }
            }
            
            // MOD COMPAT
            // For broader use cases like harvestcraft
            
            // 23k with just loaded and disabled completely, every single time
            // The stack getitem getregistryname getNamespace runs ten times faster on anything not mod specific
            // So now it takes 2k every single time, way better
            // I'll be using this system for any further compatibility things like this, probably
            /*
            if (harvestcraftLoaded && regName.getNamespace().equals(ModNames.HARVESTCRAFT) && 
                ModConfig.server.compatibility.toggles.harvestCraft && 
                !SDCompatibility.disabledCompletely.contains(ModNames.HARVESTCRAFT)) {
                if (OreDictUtil.isOre("listAlljuice", stack)) {
                    ThirstUtil.takeDrink(player, ModConfig.server.compatibility.harvestcraft.juiceThirst, 
                        (float) ModConfig.server.compatibility.harvestcraft.juiceSaturation, 
                        (float) ModConfig.server.compatibility.harvestcraft.juiceThirstyChance);
                } else if (OreDictUtil.isOre("listAllsmoothie", stack)) {
                    ThirstUtil.takeDrink(player, ModConfig.server.compatibility.harvestcraft.smoothieThirst, 
                        (float) ModConfig.server.compatibility.harvestcraft.smoothieSaturation, 
                        (float) ModConfig.server.compatibility.harvestcraft.smoothieThirstyChance);
                } else if (OreDictUtil.isOre("listAllsoda", stack)) {
                    ThirstUtil.takeDrink(player, ModConfig.server.compatibility.harvestcraft.sodaThirst, 
                        (float) ModConfig.server.compatibility.harvestcraft.sodaSaturation, 
                        (float) ModConfig.server.compatibility.harvestcraft.sodaThirstyChance);
                }
            }
            */
        }
    }
    
    // Intercept bottle interactions with mod fluid blocks
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickBlockBottle(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote) {
            return; // Only process on server
        }
        
        EntityPlayer player = event.getEntityPlayer();
        ItemStack heldItem = player.getHeldItem(event.getHand());
        
        // Only handle empty bottles
        if (heldItem.isEmpty() || heldItem.getItem() != Items.GLASS_BOTTLE) {
            return;
        }
        
        BlockPos pos = event.getPos();
        IBlockState state = event.getWorld().getBlockState(pos);
        Block block = state.getBlock();
        
        // Check if clicking on mod fluid blocks
        ItemStack resultBottle = null;
        
        if (block == SDFluids.blockPurifiedWater) {
            resultBottle = new ItemStack(SDItems.purifiedWaterBottle);
        } else if (block == SDFluids.blockSaltWater) {
            resultBottle = new ItemStack(SDItems.saltWaterBottle);
        }
        
        // If found a valid bottle result, handle it
        if (resultBottle != null) {
            // Cancel the vanilla interaction
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            
            // Give the player the bottle
            heldItem.shrink(1);
            
            if (heldItem.isEmpty()) {
                player.setHeldItem(event.getHand(), resultBottle);
            } else if (!player.inventory.addItemStackToInventory(resultBottle)) {
                player.dropItem(resultBottle, false);
            }
        }
    }
    
    // Both Sides
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // TODO is a client packet the way to go?
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }
        
        if (event.getWorld().isRemote) {
            // Client Side
            // Only run on main hand (otherwise it runs twice)
            if (event.getHand() == EnumHand.MAIN_HAND) {
                if (clientCheckWater(event.getEntityPlayer())) {
                    clientSendDrinkMessageAndPlaySound(event.getEntityPlayer());
                }
            }
        } else {
            // Server side
            EntityPlayer player = event.getEntityPlayer();
            EnumHand hand = event.getHand();
            if (hand == EnumHand.MAIN_HAND) {
                World world = event.getWorld();
                BlockPos pos = event.getPos();
                IBlockState state = world.getBlockState(pos);
                
                if (state.getBlock() == Blocks.CAULDRON) {
                    CompatRightClick.cauldronHandler.process(event, world, pos, state, player);
                }
            }
        }
    }
    
    // Client Side
    @SubscribeEvent
    public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }
        
        // Only run on main hand (otherwise it runs twice)
        if (event.getHand() == EnumHand.MAIN_HAND) {
            if (clientCheckWater(event.getEntityPlayer())) {
                clientSendDrinkMessageAndPlaySound(event.getEntityPlayer());
            }
        }
    }
    
    private boolean clientCheckWater(EntityPlayer player) {
        if (!player.isSneaking() || !QuickConfig.isThirstEnabled()) {
            return false;
        }
        
        return ThirstUtilInternal.traceWaterToDrink(player) != null;
    }
    
    private void clientSendDrinkMessageAndPlaySound(EntityPlayer player) {
        // Make new message
        MessageDrinkWater message = new MessageDrinkWater();
        
        // Send to server
        PacketHandler.instance.sendToServer(message);
        
        // Play sound and swing arm
        player.swingArm(EnumHand.MAIN_HAND);
        SoundUtil.commonPlayPlayerSound(player, SoundEvents.ENTITY_GENERIC_DRINK);
    }
    
    //
    // Thirst Exhausting Events
    //
    
    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }
        
        World world = event.getEntity().world;
        if (world.isRemote) {
            return;
        }
        
        // Server Side
        EntityPlayer player = event.getEntityPlayer();
        
        if (!shouldSkipThirst(player)) {
            Entity monster = event.getTarget();
            if (monster.canBeAttackedWithItem() && !monster.hitByEntity(player)) {
                addExhaustion(player, (float) ModConfig.server.thirst.thirstAttacking);
            }
        }
    }
    
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }
        
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        
        // Server Side
        EntityPlayer player = event.getPlayer();
        
        if (!shouldSkipThirst(player)) {
            // Check if the player is actually able to harvest the block
            if (event.getState().getBlock().canHarvestBlock(world, event.getPos(), player)) {
                addExhaustion(player, (float) ModConfig.server.thirst.thirstBreakBlock);
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }
        
        World world = event.getEntity().world;
        if (world.isRemote || event.getAmount() == 0.0f) {
            return;
        }
        
        // Server Side
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            
            if (!shouldSkipThirst(player)) {
                // Damage source has special behavior
                addExhaustion(player, event.getSource().getHungerDamage());
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingJump(LivingJumpEvent event) {
        if (!QuickConfig.isThirstEnabled()) {
            return;
        }
        
        World world = event.getEntity().world;
        if (world.isRemote) {
            return;
        }
        
        // Server Side
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            
            if (!shouldSkipThirst(player)) {
                if (player.isSprinting()) {
                    addExhaustion(player, (float) ModConfig.server.thirst.thirstSprintJump);
                } else {
                    addExhaustion(player, (float) ModConfig.server.thirst.thirstJump);
                }
            }
        }
    }
    
    private boolean shouldSkipThirst(EntityPlayer player) {
        return player.isCreative() || player.isSpectator();
    }
    
    private void addExhaustion(EntityPlayer player, float exhaustion) {
        IThirstCapability capability = SDCapabilities.getThirstData(player);
        capability.addThirstExhaustion(exhaustion);
    }
}
