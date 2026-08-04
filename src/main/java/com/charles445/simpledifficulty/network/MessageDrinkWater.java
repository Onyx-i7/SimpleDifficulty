package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import com.charles445.simpledifficulty.util.internal.ThirstUtilInternal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server-bound packet sent when a client player attempts to drink from a water source.
 * The server validates the water source before applying thirst restoration.
 */
public class MessageDrinkWater {

    /**
     * Empty constructor required for packet reflection.
     */
    public MessageDrinkWater() {
    }

    /**
     * Decodes the packet from the network buffer.
     *
     * @param buf The packet buffer.
     */
    public MessageDrinkWater(PacketBuffer buf) {
        // No data to read
    }

    /**
     * Encodes the packet into the network buffer.
     *
     * @param message The message to encode.
     * @param buf The packet buffer.
     */
    public static void encode(MessageDrinkWater message, PacketBuffer buf) {
        // No data to write
    }

    /**
     * Handles the packet on the server side.
     *
     * @param message The message to handle.
     * @param contextSupplier The network context supplier.
     */
    public static void handle(MessageDrinkWater message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.enqueueWork(() -> {
                ServerPlayerEntity player = context.getSender();
                if (player != null) {
                    // Security check: Prevent processing if the player is dead
                    if (!player.isAlive()) {
                        return;
                    }

                    ThirstEnumBlockPos traceResult = ThirstUtilInternal.traceWaterToDrink(player);
                    if (traceResult == null) {
                        return;
                    }

                    ThirstEnum result = traceResult.thirstEnum;
                    if (result != null) {
                        ThirstUtil.takeDrink(player, result.getThirst(), result.getSaturation(), result.getThirstyChance());
                        SoundUtil.commonPlayPlayerSound(player, SoundEvents.GENERIC_DRINK);
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}