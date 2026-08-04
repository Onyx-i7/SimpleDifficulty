package com.charles445.simpledifficulty.command;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.JsonFileName;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Locale;

/**
 * Main command handler for SimpleDifficulty using Brigadier.
 * Provides utilities for managing temperature, thirst, and JSON configurations.
 */
public class CommandSimpleDifficulty {

    private static final String EXPORT_JSON_REMINDER = "(Don't forget to exportJson!)";

    /**
     * Registers all SimpleDifficulty commands to the dispatcher.
     *
     * @param dispatcher The command dispatcher to register with.
     */
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> root = Commands.literal("simpledifficulty")
                .requires(source -> source.hasPermission(2))
                .executes(context -> help(context))
                .then(Commands.literal("help")
                        .executes(context -> help(context))
                        .then(Commands.argument("command", StringArgumentType.word())
                                .executes(context -> helpCommand(context, StringArgumentType.getString(context, "command")))))
                .then(Commands.literal("reloadJson")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> updateJson(context)))
                .then(Commands.literal("exportJson")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> exportJson(context)))
                .then(Commands.literal("addArmor")
                        .then(Commands.argument("temperature", DoubleArgumentType.doubleArg())
                                .executes(context -> addArmor(context, DoubleArgumentType.getDouble(context, "temperature")))
                                .then(Commands.literal("--nbt").executes(context -> addArmorNBT(context, DoubleArgumentType.getDouble(context, "temperature"))))
                                .then(Commands.literal("--clear").executes(context -> addArmorClear(context)))))
                .then(Commands.literal("addBlock")
                        .then(Commands.argument("temperature", DoubleArgumentType.doubleArg())
                                .executes(context -> addBlock(context, DoubleArgumentType.getDouble(context, "temperature")))
                                .then(Commands.literal("--clear").executes(context -> addBlockClear(context)))))
                .then(Commands.literal("addConsumableTemperature")
                        .then(Commands.argument("group", StringArgumentType.word())
                                .then(Commands.argument("temperature", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                                .executes(context -> addConsumableTemperature(context, 
                                                        StringArgumentType.getString(context, "group"),
                                                        DoubleArgumentType.getDouble(context, "temperature"),
                                                        IntegerArgumentType.getInteger(context, "duration")))
                                                .then(Commands.literal("--nbt").executes(context -> addConsumableTemperatureNBT(context,
                                                        StringArgumentType.getString(context, "group"),
                                                        DoubleArgumentType.getDouble(context, "temperature"),
                                                        IntegerArgumentType.getInteger(context, "duration"))))
                                                .then(Commands.literal("--clear").executes(context -> addConsumableTemperatureClear(context)))))))
                .then(Commands.literal("addConsumableThirst")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .then(Commands.argument("saturation", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("thirstyChance", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                .executes(context -> addConsumableThirst(context,
                                                        IntegerArgumentType.getInteger(context, "amount"),
                                                        (float) DoubleArgumentType.getDouble(context, "saturation"),
                                                        (float) DoubleArgumentType.getDouble(context, "thirstyChance")))
                                                .then(Commands.literal("--nbt").executes(context -> addConsumableThirstNBT(context,
                                                        IntegerArgumentType.getInteger(context, "amount"),
                                                        (float) DoubleArgumentType.getDouble(context, "saturation"),
                                                        (float) DoubleArgumentType.getDouble(context, "thirstyChance"))))
                                                .then(Commands.literal("--clear").executes(context -> addConsumableThirstClear(context)))))))
                .then(Commands.literal("addDimension")
                        .then(Commands.argument("temperature", DoubleArgumentType.doubleArg())
                                .executes(context -> addDimension(context, DoubleArgumentType.getDouble(context, "temperature")))))
                .then(Commands.literal("addFluid")
                        .then(Commands.argument("temperature", DoubleArgumentType.doubleArg())
                                .executes(context -> addFluid(context, DoubleArgumentType.getDouble(context, "temperature")))))
                .then(Commands.literal("addHeldItem")
                        .then(Commands.argument("temperature", DoubleArgumentType.doubleArg())
                                .executes(context -> addHeldItem(context, DoubleArgumentType.getDouble(context, "temperature")))
                                .then(Commands.literal("--nbt").executes(context -> addHeldItemNBT(context, DoubleArgumentType.getDouble(context, "temperature"))))
                                .then(Commands.literal("--clear").executes(context -> addHeldItemClear(context)))))
                .then(Commands.literal("nbt")
                        .executes(context -> tagToString(context)))
                .then(Commands.literal("setThirst")
                        .then(Commands.argument("thirst", IntegerArgumentType.integer(0, 20))
                                .executes(context -> setThirst(context, IntegerArgumentType.getInteger(context, "thirst"), 5.0f))
                                .then(Commands.argument("saturation", DoubleArgumentType.doubleArg(0.0))
                                        .executes(context -> setThirst(context, IntegerArgumentType.getInteger(context, "thirst"), (float) DoubleArgumentType.getDouble(context, "saturation"))))))
                .then(Commands.literal("setTemperature")
                        .then(Commands.argument("temperature", IntegerArgumentType.integer())
                                .executes(context -> setTemperature(context, IntegerArgumentType.getInteger(context, "temperature")))));

        dispatcher.register(root);
        dispatcher.register(Commands.literal("sd").redirect(dispatcher.getRoot().getChild("simpledifficulty")));
    }

    private static int help(CommandContext<CommandSource> context) {
        String listOfCommands = 
                "/simpledifficulty help\n" +
                "/simpledifficulty exportJson\n" +
                "/simpledifficulty reloadJson\n" +
                "/simpledifficulty addArmor <temperature> [--nbt|--clear]\n" +
                "/simpledifficulty addBlock <temperature> [--clear]\n" +
                "/simpledifficulty addConsumableTemperature <group> <temperature> <duration> [--nbt|--clear]\n" +
                "/simpledifficulty addConsumableThirst <amount> <saturation> <thirstyChance> [--nbt|--clear]\n" +
                "/simpledifficulty addDimension <temperature>\n" +
                "/simpledifficulty addFluid <temperature>\n" +
                "/simpledifficulty addHeldItem <temperature> [--nbt|--clear]\n" +
                "/simpledifficulty nbt\n" +
                "/simpledifficulty setThirst <thirst> [saturation]\n" +
                "/simpledifficulty setTemperature <temperature>";
        
        context.getSource().sendSuccess(new StringTextComponent(listOfCommands), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int helpCommand(CommandContext<CommandSource> context, String commandName) {
        String helpText;
        String lowerCaseName = commandName.toLowerCase(Locale.ENGLISH);
        
        switch(lowerCaseName) {
            case "exportjson":
                helpText = "Exports your in-game JSON changes to the config folder";
                break;
            case "reloadjson":
                helpText = "Discards any unexported in-game JSON changes and reloads from config";
                break;
            case "addarmor":
                helpText = "Adds the held armor to the armor JSON (changes temperature when worn)";
                break;
            case "addblock":
                helpText = "Adds the held block to the block JSON (changes temperature when near)";
                break;
            case "addconsumabletemperature":
                helpText = "Adds the held item to consumableTemperature JSON";
                break;
            case "addconsumablethirst":
                helpText = "Adds the held item to consumableThirst JSON";
                break;
            case "adddimension":
                helpText = "Adds the current dimension to dimensionTemperature JSON";
                break;
            case "addfluid":
                helpText = "Adds the held fluid item to fluid JSON";
                break;
            case "addhelditem":
                helpText = "Adds the held item to heldItems JSON";
                break;
            case "nbt":
                helpText = "Gets an item's NBT tag as a string for config use";
                break;
            case "setthirst":
                helpText = "Sets the player's thirst level";
                break;
            case "settemperature":
                helpText = "Sets the player's temperature level";
                break;
            default:
                helpText = "Unknown command. Use /simpledifficulty help";
                break;
        }
        
        context.getSource().sendSuccess(new StringTextComponent(helpText), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setThirst(CommandContext<CommandSource> context, int thirst, float saturation) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            IThirstCapability capability = SDCapabilities.getThirstData(player);
            if (capability != null) {
                capability.setThirstLevel(thirst);
                capability.setThirstSaturation(saturation);
                context.getSource().sendSuccess(new StringTextComponent("Thirst updated successfully."), true);
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int setTemperature(CommandContext<CommandSource> context, int temperature) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
            if (capability != null) {
                capability.setTemperatureLevel(temperature);
                context.getSource().sendSuccess(new StringTextComponent("Temperature updated successfully."), true);
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int tagToString(CommandContext<CommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            if (stack.hasTag()) {
                CompoundNBT compound = stack.getTag();
                String compString = compound != null ? compound.toString() : "{}";
                IFormattableTextComponent tc = new StringTextComponent(compString);

                Style style = Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sdcopyidentity " + compString))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to copy identity to clipboard")));
                tc.setStyle(style);

                context.getSource().sendSuccess(tc, false);
            } else {
                context.getSource().sendFailure(new StringTextComponent("This item has no NBT tag."));
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addBlock(CommandContext<CommandSource> context, double temperature) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            Block block = Block.byItem(stack.getItem());
            if (block == Blocks.AIR) {
                context.getSource().sendFailure(new StringTextComponent("Couldn't find block for item!"));
                return 0;
            }

            boolean accepted = JsonConfig.registerBlockTemperature(block, (float) temperature);
            if (accepted) {
                context.getSource().sendSuccess(new StringTextComponent("Added block to " + JsonFileName.blockTemperatures + "!\n" + EXPORT_JSON_REMINDER), true);
            } else {
                context.getSource().sendFailure(new StringTextComponent("Block has properties information in the JSON, use the JSON instead!"));
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addBlockClear(CommandContext<CommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            Block block = Block.byItem(stack.getItem());
            if (block.getRegistryName() != null) {
                JsonConfig.blockTemperatures.remove(block.getRegistryName().toString());
                context.getSource().sendSuccess(new StringTextComponent("Removed from JSON"), true);
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addDimension(CommandContext<CommandSource> context, double temperature) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            World world = player.level;

            JsonConfig.registerDimensionTemperature(world.dimension().location().toString(), (float) temperature);
            context.getSource().sendSuccess(new StringTextComponent("Added dimension to " + JsonFileName.dimensionTemperature + "!\n" + EXPORT_JSON_REMINDER), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addFluid(CommandContext<CommandSource> context, double temperature) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(null);
            if (fluidStack == null) {
                context.getSource().sendFailure(new StringTextComponent("Couldn't find the item's fluid!"));
                return 0;
            }

            JsonConfig.registerFluidTemperature(fluidStack.getFluid().getRegistryName().toString(), (float) temperature);
            context.getSource().sendSuccess(new StringTextComponent("Added fluid to " + JsonFileName.fluidTemperatures + "!\n" + EXPORT_JSON_REMINDER), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addConsumableThirst(CommandContext<CommandSource> context, int amount, float saturation, float thirstyChance) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            JsonConfig.registerConsumableThirst(stack, amount, saturation, thirstyChance);
            context.getSource().sendSuccess(new StringTextComponent("Added consumable item to " + JsonFileName.consumableThirst + "!\n" + EXPORT_JSON_REMINDER), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addConsumableThirstNBT(CommandContext<CommandSource> context, int amount, float saturation, float thirstyChance) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            if (stack.hasTag()) {
                JsonConfig.registerConsumableThirst(getRegistryName(stack), amount, saturation, thirstyChance, getFullIdentity(stack));
                context.getSource().sendSuccess(new StringTextComponent("Added consumable item with NBT to " + JsonFileName.consumableThirst + "!\n" + EXPORT_JSON_REMINDER), true);
            } else {
                context.getSource().sendFailure(new StringTextComponent("Item has no NBT tag!"));
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addConsumableThirstClear(CommandContext<CommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            JsonConfig.consumableThirst.remove(getRegistryName(stack));
            context.getSource().sendSuccess(new StringTextComponent("Removed from JSON"), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addHeldItem(CommandContext<CommandSource> context, double temperature) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            JsonConfig.registerHeldItem(stack, (float) temperature);
            context.getSource().sendSuccess(new StringTextComponent("Added held item to " + JsonFileName.heldItemTemperatures + "!\n" + EXPORT_JSON_REMINDER), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addHeldItemNBT(CommandContext<CommandSource> context, double temperature) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            if (stack.hasTag()) {
                JsonConfig.registerHeldItem(getRegistryName(stack), (float) temperature, getFullIdentity(stack));
                context.getSource().sendSuccess(new StringTextComponent("Added held item with NBT to " + JsonFileName.heldItemTemperatures + "!\n" + EXPORT_JSON_REMINDER), true);
            } else {
                context.getSource().sendFailure(new StringTextComponent("Item has no NBT tag!"));
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addHeldItemClear(CommandContext<CommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            JsonConfig.heldItemTemperatures.remove(getRegistryName(stack));
            context.getSource().sendSuccess(new StringTextComponent("Removed from JSON"), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addConsumableTemperature(CommandContext<CommandSource> context, String group, double temperature, int duration) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            JsonConfig.registerConsumableTemperature(group, stack, (float) temperature, duration);
            context.getSource().sendSuccess(new StringTextComponent("Added consumable item to " + JsonFileName.consumableTemperature + "!\n" + EXPORT_JSON_REMINDER), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addConsumableTemperatureNBT(CommandContext<CommandSource> context, String group, double temperature, int duration) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            if (stack.hasTag()) {
                JsonConfig.registerConsumableTemperature(group, getRegistryName(stack), (float) temperature, duration, getFullIdentity(stack));
                context.getSource().sendSuccess(new StringTextComponent("Added consumable item with NBT to " + JsonFileName.consumableTemperature + "!\n" + EXPORT_JSON_REMINDER), true);
            } else {
                context.getSource().sendFailure(new StringTextComponent("Item has no NBT tag!"));
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addConsumableTemperatureClear(CommandContext<CommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            JsonConfig.consumableTemperature.remove(getRegistryName(stack));
            context.getSource().sendSuccess(new StringTextComponent("Removed from JSON"), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addArmor(CommandContext<CommandSource> context, double temperature) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            JsonConfig.registerArmorTemperature(stack, (float) temperature);
            context.getSource().sendSuccess(new StringTextComponent("Added armor to " + JsonFileName.armorTemperatures + "!\n" + EXPORT_JSON_REMINDER), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addArmorNBT(CommandContext<CommandSource> context, double temperature) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            if (stack.hasTag()) {
                JsonConfig.registerArmorTemperature(getRegistryName(stack), (float) temperature, getFullIdentity(stack));
                context.getSource().sendSuccess(new StringTextComponent("Added armor with NBT to " + JsonFileName.armorTemperatures + "!\n" + EXPORT_JSON_REMINDER), true);
            } else {
                context.getSource().sendFailure(new StringTextComponent("Item has no NBT tag!"));
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addArmorClear(CommandContext<CommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty()) {
                context.getSource().sendFailure(new StringTextComponent("Not holding an item!"));
                return 0;
            }

            JsonConfig.armorTemperatures.remove(getRegistryName(stack));
            context.getSource().sendSuccess(new StringTextComponent("Removed from JSON"), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new StringTextComponent("You must be a player to use this command."));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int exportJson(CommandContext<CommandSource> context) {
        context.getSource().sendSuccess(new StringTextComponent("Exporting SimpleDifficulty JSON"), true);
        String result = JsonConfigInternal.manuallyExportAll();
        context.getSource().sendSuccess(new StringTextComponent(result), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int updateJson(CommandContext<CommandSource> context) {
        context.getSource().sendSuccess(new StringTextComponent("Reloading SimpleDifficulty JSON"), true);
        JsonConfigInternal.jsonErrors.clear();
        JsonConfigInternal.clearContainers();
        JsonConfigInternal.postInit(SimpleDifficulty.jsonDirectory);

        for (String s : JsonConfigInternal.jsonErrors) {
            context.getSource().sendFailure(new StringTextComponent(s));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static String getRegistryName(ItemStack stack) {
        if (stack.getItem().getRegistryName() == null) {
            return "minecraft:air";
        }
        return stack.getItem().getRegistryName().toString();
    }

    private static JsonItemIdentity getFullIdentity(ItemStack stack) {
        if (stack.hasTag() && stack.getTag() != null) {
            return new JsonItemIdentity(stack.getTag().toString());
        }
        return new JsonItemIdentity();
    }
}