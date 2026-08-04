package com.charles445.simpledifficulty.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Client command to copy identity JSON (metadata + NBT) to the system clipboard.
 * Usage: /sdcopyidentity <metadata> <nbt>
 */
public class ClientCommandIdentityCopy extends ClientCommandBase {

    @Override
    public String getName() {
        return "sdcopyidentity";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> getCommandBuilder() {
        return LiteralArgumentBuilder.<CommandSource>literal(getName())
                .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("metadata", IntegerArgumentType.integer()))
                    .then(RequiredArgumentBuilder.<CommandSource, String>argument("nbt", StringArgumentType.greedyString()))
                            .executes(this::executeCopy)));
    }

    private int executeCopy(CommandContext<CommandSource> context) {
        int metadata = IntegerArgumentType.getInteger(context, "metadata");
        String nbt = StringArgumentType.getString(context, "nbt");

        if (Minecraft.getInstance().level == null || !Minecraft.getInstance().level.isClientSide) {
            context.getSource().sendSuccess(new StringTextComponent("World was not remote, skipping identity copy execution!"), false);
            return 0;
        }

        if (GraphicsEnvironment.isHeadless()) {
            context.getSource().sendSuccess(new StringTextComponent("Cannot copy identity: Headless environment detected."), false);
            return 0;
        }

        try {
            String nbtResult = nbt.replaceAll("\"", "\\\\\"");

            StringBuilder sbResult = new StringBuilder();
            sbResult.append("      \"identity\": {\n");
            sbResult.append("        \"metadata\": ").append(metadata).append(",\n");
            sbResult.append("        \"nbt\": \"").append(nbtResult).append("\"\n");
            sbResult.append("      },");

            StringSelection selection = new StringSelection(sbResult.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);

            context.getSource().sendSuccess(new StringTextComponent("Copied identity JSON to clipboard!"), false);
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            context.getSource().sendSuccess(new StringTextComponent("An error occurred while copying the identity to the clipboard."), false);
            return 0;
        }
    }
}