package com.charles445.simpledifficulty.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public abstract class ClientCommandBase {
    
    public abstract String getName();
    
    public abstract LiteralArgumentBuilder<CommandSource> getCommandBuilder();
    
    /**
     * Registers this command to the dispatcher.
     * Called during client command registration.
     */
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(getCommandBuilder());
    }

    @Override
    public com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.command.CommandSource> getCommandBuilder() {
        return com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.command.CommandSource>literal(getName())
                .then(com.mojang.brigadier.builder.RequiredArgumentBuilder.<net.minecraft.command.CommandSource, String>argument("text", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(this::executeCopy));
    }
}