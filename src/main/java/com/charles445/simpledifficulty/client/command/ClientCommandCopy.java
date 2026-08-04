package com.charles445.simpledifficulty.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Client command to copy a string to the system clipboard.
 * Usage: /sdcopy <text>
 */
public class ClientCommandCopy extends ClientCommandBase {

    @Override
    public String getName() {
        return "sdcopy";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> getCommandBuilder() {
        return LiteralArgumentBuilder.<CommandSource>literal(getName())
                .then(LiteralArgumentBuilder.<CommandSource>argument("text", StringArgumentType.greedyString())
                        .executes(this::executeCopy));
    }

    private int executeCopy(CommandContext<CommandSource> context) {
        String text = StringArgumentType.getString(context, "text");
        
        if (Minecraft.getInstance().level == null || !Minecraft.getInstance().level.isClientSide) {
            context.getSource().sendSuccess(new StringTextComponent("World was not remote, skipping copy execution!"), false);
            return 0;
        }

        if (GraphicsEnvironment.isHeadless()) {
            context.getSource().sendSuccess(new StringTextComponent("Cannot copy to clipboard: Headless environment detected."), false);
            return 0;
        }

        try {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            context.getSource().sendSuccess(new StringTextComponent("Copied to clipboard!"), false);
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            context.getSource().sendSuccess(new StringTextComponent("An error occurred while copying to the clipboard."), false);
            return 0;
        }
    }
}