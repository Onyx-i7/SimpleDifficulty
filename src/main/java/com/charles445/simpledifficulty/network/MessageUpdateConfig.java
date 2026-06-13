package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageUpdateConfig implements IMessage {

    // CLIENT side
    
    private NBTTagCompound nbt;
    
    public MessageUpdateConfig() {
        // Necessary to avoid crashes in Forge's reflection instantiation
    }
    
    public MessageUpdateConfig(NBTTagCompound compound) {
        this.nbt = compound;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.nbt = ByteBufUtils.readTag(buf);
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.nbt);
    }
    
    public static class Handler implements IMessageHandler<MessageUpdateConfig, IMessage> {
        
        @Override
        public IMessage onMessage(MessageUpdateConfig message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                // Synchronization delegated to the main rendering thread
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    // Security filter: Prevents crashes if the configuration package arrives corrupted
                    if (message.nbt != null) {
                        ServerConfig.instance.updateValues(message.nbt);
                    }
                });
            }
            return null;
        }
    }
}
