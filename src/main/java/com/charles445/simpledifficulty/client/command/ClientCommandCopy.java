package com.charles445.simpledifficulty.client.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ClientCommandCopy extends ClientCommandBase {

    @Override
    public String getName() {
        return "sdcopy";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sdcopy <string>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args == null || args.length == 0) {
            sender.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
            return;
        }

        // Check if the world is remote (client-side execution check)
        if (!sender.getEntityWorld().isRemote) {
            sender.sendMessage(new TextComponentString("World was not remote, skipping copy execution!"));
            return;
        }

        // Performance and Stability Fix: Guard against Headless environments to avoid AWT Toolkit crashes
        if (GraphicsEnvironment.isHeadless()) {
            sender.sendMessage(new TextComponentString("Cannot copy to clipboard: Headless environment detected."));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        
        try {
            String result = sb.toString().trim();
            StringSelection selection = new StringSelection(result);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            
            sender.sendMessage(new TextComponentString("Copied to clipboard!"));
        } catch (Exception e) {
            sender.sendMessage(new TextComponentString("An error occurred while copying to the clipboard."));
        }
    }
}
