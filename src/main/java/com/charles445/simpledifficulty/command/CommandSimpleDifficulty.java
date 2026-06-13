package com.charles445.simpledifficulty.command;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.JsonFileName;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommandSimpleDifficulty extends CommandBase {

    // Performance Optimization: Cleaned up redundant String array initialization inside Arrays.asList
    private final List<String> tabCompletionsCommands = Arrays.asList(
            "help", "exportJson", "reloadJson", "addArmor", "addBlock",
            "addConsumableTemperature", "addConsumableThirst", "addDimension",
            "addFluid", "addHeldItem", "nbt", "setThirst", "setTemperature"
    );
    
    private final String commandUsage = "/simpledifficulty help";
    
    private final String listOfCommands = 
              "   help <command>\n"
            + "   exportJson\n"
            + "   reloadJson\n"
            + "   addArmor <temperature>\n"
            + "   addBlock <temperature>\n"
            + "   addConsumableTemperature <group> <temperature> <duration>\n"
            + "   addConsumableThirst <amount> <saturation> <thirstyChance>\n"
            + "   addDimension <temperature>\n"
            + "   addFluid <temperature>\n"
            + "   addHeldItem <temperature>\n"
            + "   nbt\n"
            + "   setThirst <thirst> <saturation>\n"
            + "   setTemperature <temperature>";

    private final String warn_notPlayerAdmin = "You do not have permission, or are not a player ingame!";
    private final String warn_invalidArgs = "Invalid Arguments";
    private final String warn_noItem = "Not holding an item!";
    private final String exportJsonReminder = "(Don't forget to exportJson !)";

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sd");
    }
    
    @Override
    public String getName() {
        return "simpledifficulty";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Standard operator permission level for vanilla integration
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return commandUsage;
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, tabCompletionsCommands);
        } else if (args.length == 0) {
            return tabCompletionsCommands;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            help(sender);
            return;
        }
        
        switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "reloadjson": updateJson(server, sender, args); break;
            case "exportjson": exportJson(server, sender, args); break;
            case "addarmor": addArmor(server, sender, args); break;
            case "addblock": addBlock(server, sender, args); break;
            case "addconsumabletemperature": addConsumableTemperature(server, sender, args); break;
            case "addconsumablethirst": addConsumableThirst(server, sender, args); break;
            case "adddimension": addDimension(server, sender, args); break;
            case "addfluid": addFluid(server, sender, args); break;
            case "addhelditem": addHeldItem(server, sender, args); break;
            case "nbt": tagToString(server, sender, args); break;
            case "setthirst": setThirst(server, sender, args); break;
            case "settemperature": setTemperature(server, sender, args); break;
            case "help": helpCommand(server, sender, args); break;
            default: help(sender); break;
        }
    }
    
    private void helpCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 2) {
            message(sender, listOfCommands);
            return;
        }
        
        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "help": message(sender, "If you need more help, you can contact the mod author on CurseForge or GitHub"); return;
            case "exportjson": message(sender, "Exports your in-game JSON changes to the config folder"); return;
            case "reloadjson": message(sender, "Discards any unexported in-game JSON changes.\nReloads the JSON from the config folder"); return;
            case "addarmor": message(sender, "Adds the held armor to the armor JSON\n(changes temperature when worn)\nAdd argument --nbt to include NBT tag\nAdd argument --clear to remove the item from JSON first (ignores metadata and nbt)"); return;
            case "addblock": message(sender, "Adds the held block to the block JSON\n(changes temperature when near the block)\nAdd argument --clear to remove the block from JSON first (ignores metadata and state)"); return;
            case "addconsumabletemperature": message(sender, "Adds the held item to the consumableTemperature JSON\n(modifies temperature over time when consumed)\nAdd argument --nbt to include NBT tag\nAdd argument --clear to remove the item from JSON first (ignores metadata and nbt)"); return;
            case "addconsumablethirst": message(sender, "Adds the held item to the consumableThirst JSON\n(replenishes thirst when consumed)\nAdd argument --nbt to include NBT tag\nAdd argument --clear to remove the item from JSON first (ignores metadata and nbt)"); return;
            case "adddimension": message(sender, "Adds the dimension the player is in to the dimensionTemperature JSON"); return;
            case "addfluid": message(sender, "Adds the held fluid item to the fluid JSON\n(changes temperature when inside the fluid)"); return;
            case "addhelditem": message(sender, "Adds the held item to the heldItems JSON\n(changes player temperature when held in mainhand or offhand)\nAdd argument --nbt to include NBT tag\nAdd argument --clear to remove the item from JSON first (ignores metadata and nbt)"); return;
            case "nbt": message(sender, "Gets an item's NBT tag as a string for config use"); return;
            case "setthirst": message(sender, "Sets the player's thirst"); return;
            case "settemperature": message(sender, "Sets the player's temperature"); return;
            default: message(sender, "/simpledifficulty help <command> \n(Replace <command> with a simpledifficulty command name)"); return;
        }
    }
    
    private void setThirst(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 2) {
                throw new WrongUsageException(warn_invalidArgs + " <thirst> [saturation]");
            }
            try {
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                IThirstCapability capability = SDCapabilities.getThirstData(player);
                if (capability != null) {
                    capability.setThirstLevel(parseInt(args[1]));
                    if (args.length >= 3) {
                        capability.setThirstSaturation((float) parseDouble(args[2]));
                    }
                    message(sender, "Thirst updated successfully.");
                }
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <thirst> [saturation]");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private void setTemperature(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 2) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
            try {
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
                if (capability != null) {
                    capability.setTemperatureLevel(parseInt(args[1]));
                    message(sender, "Temperature updated successfully.");
                }
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private void tagToString(MinecraftServer server, ICommandSender sender, String[] args) {
        if (isAdminPlayer(sender)) {
            EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
            ItemStack stack = player.getHeldItemMainhand();
            
            if (stack.isEmpty()) {
                message(sender, warn_noItem);
                return;
            }
            
            if (stack.hasTagCompound()) {
                NBTTagCompound compound = stack.getTagCompound();
                String compString = compound != null ? compound.toString() : "{}";
                TextComponentString tc = new TextComponentString(compString);
                int metadata = getMetadataFromStack(stack);
                
                Style style = new Style();
                style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sdcopyidentity " + metadata + " " + compString));
                style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to copy identity to clipboard")));
                tc.setStyle(style);
                
                sender.sendMessage(tc);
            } else {
                message(sender, "This item has no NBT tag.");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }

    private int getMetadataFromStack(ItemStack stack) {
        return stack.getHasSubtypes() ? stack.getMetadata() : -1;
    }

    private void addBlock(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 2) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
            
            try {
                float temperature = (float) parseDouble(args[1]);
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                ItemStack stack = player.getHeldItemMainhand();
                
                if (stack.isEmpty()) {
                    message(sender, warn_noItem);
                    return;
                }
                
                Block block = Block.getBlockFromItem(stack.getItem());
                
                if (block == Blocks.AIR) {
                    FluidStack fluidStack = FluidUtil.getFluidContained(stack);
                    if (fluidStack != null) {
                        block = fluidStack.getFluid().getBlock();
                        if (block == Blocks.AIR) {
                            message(sender, "Couldn't find block for fluid!");
                            return;
                        } else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                            block = Blocks.FLOWING_LAVA;
                            JsonConfig.registerBlockTemperature(Blocks.LAVA, temperature);
                        } else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                            block = Blocks.FLOWING_WATER;
                            JsonConfig.registerBlockTemperature(Blocks.WATER, temperature);
                        }
                    } else {
                        message(sender, "Couldn't find block for item!");
                        return;
                    }
                }
                
                if (hasClearArgument(args) && block.getRegistryName() != null) {
                    JsonConfig.blockTemperatures.remove(block.getRegistryName().toString());
                    message(sender, "Removed from JSON");
                }
                
                boolean accepted = JsonConfig.registerBlockTemperature(block, temperature);
                
                if (accepted) {
                    message(sender, "Added block to " + JsonFileName.blockTemperatures + "!\n" + exportJsonReminder);
                } else {
                    message(sender, "Block has properties information in the JSON, use the JSON instead!");
                }
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private void addDimension(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 2) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
            
            try {
                float temperature = (float) parseDouble(args[1]);
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                World world = player.world;
                
                if (world == null) {
                    return;
                }
                
                JsonConfig.registerDimensionTemperature(world.provider.getDimension(), temperature);
                message(sender, "Added dimension to " + JsonFileName.dimensionTemperature + "!\n" + exportJsonReminder);
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private void addFluid(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 2) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
            
            try {
                float temperature = (float) parseDouble(args[1]);
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                ItemStack stack = player.getHeldItemMainhand();
                
                if (stack.isEmpty()) {
                    message(sender, warn_noItem);
                    return;
                }
                
                FluidStack fluidStack = FluidUtil.getFluidContained(stack);
                if (fluidStack == null) {
                    message(sender, "Couldn't find the item's fluid!");
                    return;
                }
                
                JsonConfig.registerFluidTemperature(fluidStack.getFluid().getName(), temperature);
                message(sender, "Added fluid to " + JsonFileName.consumableThirst + "!\n" + exportJsonReminder);
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private void addConsumableThirst(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 4) {
                throw new WrongUsageException(warn_invalidArgs + " <amount> <saturation> <thirstyChance>");
            }
            
            try {
                int amount = parseInt(args[1]);
                float saturation = (float) parseDouble(args[2]);
                float thirstyChance = (float) parseDouble(args[3]);
                
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                ItemStack stack = player.getHeldItemMainhand();
                
                if (stack.isEmpty()) {
                    message(sender, warn_noItem);
                    return;
                }
                
                if (hasClearArgument(args)) {
                    JsonConfig.consumableThirst.remove(getRegistryName(stack));
                    message(sender, "Removed from JSON");
                }
                
                boolean nbtArgument = hasNBTArgument(args);
                if (nbtArgument && stack.hasTagCompound()) {
                    JsonConfig.registerConsumableThirst(getRegistryName(stack), amount, saturation, thirstyChance, getFullIdentity(stack));
                    message(sender, "Added consumable item with nbt to " + JsonFileName.consumableThirst + "!\n" + exportJsonReminder);
                } else {
                    JsonConfig.registerConsumableThirst(stack, amount, saturation, thirstyChance);
                    message(sender, "Added consumable item to " + JsonFileName.consumableThirst + "!\n" + exportJsonReminder);
                }
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <amount> <saturation> <thirstyChance>");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private void addHeldItem(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 2) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
            
            try {
                float temperature = (float) parseDouble(args[1]);
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                ItemStack stack = player.getHeldItemMainhand();
                
                if (stack.isEmpty()) {
                    message(sender, warn_noItem);
                    return;
                }
                
                if (hasClearArgument(args)) {
                    JsonConfig.heldItemTemperatures.remove(getRegistryName(stack));
                    message(sender, "Removed from JSON");
                }
                
                boolean nbtArgument = hasNBTArgument(args);
                if (nbtArgument && stack.hasTagCompound()) {
                    JsonConfig.registerHeldItem(getRegistryName(stack), temperature, getFullIdentity(stack));
                    message(sender, "Added held item with nbt to " + JsonFileName.heldItemTemperatures + "!\n" + exportJsonReminder);
                } else {
                    JsonConfig.registerHeldItem(stack, temperature);
                    message(sender, "Added held item to " + JsonFileName.heldItemTemperatures + "!\n" + exportJsonReminder);
                }
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private void addConsumableTemperature(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 4) {
                throw new WrongUsageException(warn_invalidArgs + " <group> <temperature> <duration>");
            }
            
            try {
                String group = args[1].replaceAll("\"", "");
                float temperature = (float) parseDouble(args[2]);
                int duration = parseInt(args[3]);
                
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                ItemStack stack = player.getHeldItemMainhand();
                
                if (stack.isEmpty()) {
                    message(sender, warn_noItem);
                    return;
                }
                
                if (hasClearArgument(args)) {
                    JsonConfig.consumableTemperature.remove(getRegistryName(stack));
                    message(sender, "Removed from JSON");
                }
                
                boolean nbtArgument = hasNBTArgument(args);
                if (nbtArgument && stack.hasTagCompound()) {
                    JsonConfig.registerConsumableTemperature(group, getRegistryName(stack), temperature, duration, getFullIdentity(stack));
                    message(sender, "Added consumable item with nbt to " + JsonFileName.consumableTemperature + "!\n" + exportJsonReminder);
                } else {
                    JsonConfig.registerConsumableTemperature(group, stack, temperature, duration);
                    message(sender, "Added consumable item to " + JsonFileName.consumableTemperature + "!\n" + exportJsonReminder);
                }
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <group> <temperature> <duration>");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private void addArmor(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (isAdminPlayer(sender)) {
            if (args.length < 2) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
            
            try {
                float temperature = (float) parseDouble(args[1]);
                EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                ItemStack stack = player.getHeldItemMainhand();
                
                if (stack.isEmpty()) {
                    message(sender, warn_noItem);
                    return;
                }
                
                if (hasClearArgument(args)) {
                    JsonConfig.armorTemperatures.remove(getRegistryName(stack));
                    message(sender, "Removed from JSON");
                }
                
                boolean nbtArgument = hasNBTArgument(args);
                if (nbtArgument && stack.hasTagCompound()) {
                    JsonConfig.registerArmorTemperature(getRegistryName(stack), temperature, getFullIdentity(stack));
                    message(sender, "Added armor with nbt to " + JsonFileName.armorTemperatures + "!\n" + exportJsonReminder);
                } else {
                    JsonConfig.registerArmorTemperature(stack, temperature);
                    message(sender, "Added armor to " + JsonFileName.armorTemperatures + "!\n" + exportJsonReminder);
                }
            } catch (NumberFormatException e) {
                throw new WrongUsageException(warn_invalidArgs + " <temperature>");
            }
        } else {
            message(sender, warn_notPlayerAdmin);
        }
    }
    
    private String getRegistryName(ItemStack stack) {
        if (stack.getItem().getRegistryName() == null) {
            return "minecraft:air";
        }
        return stack.getItem().getRegistryName().toString();
    }
    
    private JsonItemIdentity getFullIdentity(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound() != null) {
            return new JsonItemIdentity(getMetadataFromStack(stack), stack.getTagCompound().toString());
        }
        return new JsonItemIdentity(getMetadataFromStack(stack));
    }

    private void exportJson(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (hasPermissionLevel(sender, 4)) {
            message(sender, "Exporting SimpleDifficulty JSON");
            String result = JsonConfigInternal.manuallyExportAll();
            message(sender, result);
        }
    }
    
    private void updateJson(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (hasPermissionLevel(sender, 4)) {
            message(sender, "Reloading SimpleDifficulty JSON");
            JsonConfigInternal.jsonErrors.clear();
            JsonConfigInternal.clearContainers();
            JsonConfigInternal.postInit(SimpleDifficulty.jsonDirectory);
            
            for (String s : JsonConfigInternal.jsonErrors) {
                sender.sendMessage(new TextComponentString(s));
            }
        }
    }

    private boolean isAdminPlayer(ICommandSender sender) {
        return hasPermissionLevel(sender, 2) && sender.getCommandSenderEntity() instanceof EntityPlayer;
    }
    
    private void help(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(this.getUsage(sender)));
    }
    
    private void message(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }
    
    private boolean hasPermissionLevel(ICommandSender sender, int permLevel) {
        return sender.canUseCommand(permLevel, "simpledifficulty");
    }
    
    private boolean hasNBTArgument(String[] input) {
        return hasArgument("--nbt", input);
    }
    
    private boolean hasClearArgument(String[] input) {
        return hasArgument("--clear", input);
    }
    
    private boolean hasArgument(String argument, String[] input) {
        if (input == null) {
            return false;
        }
        for (String s : input) {
            if (s.equals(argument)) {
                return true;
            }
        }
        return false;
    }
}
