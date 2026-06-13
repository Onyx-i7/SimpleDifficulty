package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import com.charles445.simpledifficulty.util.internal.ThirstUtilInternal;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageDrinkWater implements IMessage {

    // Server side
    
    public MessageDrinkWater() {
        // Required for Forge reflection initialization
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        // Customer data is neither shared nor trusted to prevent exploits
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // No data is shared during shipping
    }
    
    public static class Handler implements IMessageHandler<MessageDrinkWater, IMessage> {
        
        @Override
        public IMessage onMessage(MessageDrinkWater message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                // Securely delegate player acquisition within the Forge network context
                EntityPlayerMP player = ctx.getServerHandler().player;
                
                if (player != null) {
                    player.getServerWorld().addScheduledTask(() -> {
                        // Security check: Prevents processing if the player is dead or disconnecting
                        if (player.isDead || player.getHealth() <= 0.0f) {
                            return;
                        }
                        
                        ThirstEnumBlockPos traceResult = ThirstUtilInternal.traceWaterToDrink(player);
                        if (traceResult == null) {
                            return;
                        }
                        
                        ThirstEnum result = traceResult.thirstEnum;
                        if (result != null) {
                            ThirstUtil.takeDrink(player, result.getThirst(), result.getSaturation(), result.getThirstyChance());
                            SoundUtil.commonPlayPlayerSound(player, SoundEvents.ENTITY_GENERIC_DRINK);
                        }
                    });
                }
            }
            return null;
        }
    }
}
