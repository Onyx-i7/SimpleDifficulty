package com.charles445.simpledifficulty.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;

public class ClientCommandIdentityCopy extends ClientCommandBase {

    @Override
    public String getName() {
        return "sdcopyidentity";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> getCommandBuilder() {
        return LiteralArgumentBuilder.<CommandSource>literal(getName())
                .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("metadata", IntegerArgumentType.integer())
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("nbt", StringArgumentType.greedyString())
                                .executes(this::executeCopy)));
    }

    private int executeCopy(CommandContext<CommandSource> context) {
        int metadata = IntegerArgumentType.getInteger(context, "metadata");
        String nbt = StringArgumentType.getString(context, "nbt");
        String identity = metadata + "," + nbt;
        
        Minecraft.getInstance().keyboardHandler.setClipboard(identity);
        context.getSource().sendSuccess(new StringTextComponent("Identity copied: " + identity), false);
        return Command.SINGLE_SUCCESS;
    }
}