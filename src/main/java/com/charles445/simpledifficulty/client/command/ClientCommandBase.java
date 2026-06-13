package com.charles445.simpledifficulty.client.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.IClientCommand;

import java.util.Collections;
import java.util.List;

public abstract class ClientCommandBase implements IClientCommand {

    public abstract int getRequiredPermissionLevel();
    
    @Override
    public abstract String getName();
    
    @Override
    public abstract String getUsage(ICommandSender sender);
    
    @Override
    public abstract void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
    
    @Override
    public List<String> getAliases() {
        // Optimized: Simplified diamond type inference
        return Collections.emptyList();
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        // Optimized: Simplified diamond type inference
        return Collections.emptyList();
    }
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
    }
    
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
    
    @Override
    public int compareTo(ICommand command) {
        // Safety check: Avoid comparison crashes if the incoming command is missing metadata
        if (command == null || command.getName() == null) {
            return 1;
        }
        if (this.getName() == null) {
            return -1;
        }
        return this.getName().compareTo(command.getName());
    }
    
    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }
}
