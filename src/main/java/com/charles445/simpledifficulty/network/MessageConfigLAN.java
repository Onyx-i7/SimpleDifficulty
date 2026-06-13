package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageConfigLAN implements IMessage {

    // Side SERVER

    public MessageConfigLAN() {
        // Necessary for Forge packet instantiation via reflection
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        // No data is shared in the buffer
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // No data is shared in the buffer
    }
    
    public static class Handler implements IMessageHandler<MessageConfigLAN, IMessage> {
        
        @Override
        public IMessage onMessage(MessageConfigLAN message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP sender = ctx.getServerHandler().player;
                
                if (sender != null) {
                    // Safely delegate all processing to the main server thread
                    sender.getServerWorld().addScheduledTask(() -> {
                        // Check if operating on a physical client (Integrated Server / LAN host)
                        if (FMLCommonHandler.instance().getSide().isClient()) {
                            
                            // Security check: Verify the sender is actually the local LAN host owner
                            boolean isHost = FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer() 
                                    && FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner().equals(sender.getName());
                            
                            // Fix: Replaced nonexistent getPermissionLevel() with vanilla OP check
                            if (isHost || sender.getServer().getPlayerList().canSendCommands(sender.getGameProfile())) {
                                ModConfig.sendServerConfigToAllPlayers();
                            } else {
                                SimpleDifficulty.logger.warn("Player {} attempted to force a LAN config update without proper permissions.", sender.getName());
                            }
                        }
                    });
                }
            }
            return null;
        }
    }
}
