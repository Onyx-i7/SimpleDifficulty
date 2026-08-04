package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.capability.TemperatureProvider;
import com.charles445.simpledifficulty.capability.ThirstProvider;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.network.MessageUpdateTemperature;
import com.charles445.simpledifficulty.network.MessageUpdateThirst;
import com.charles445.simpledifficulty.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID)
public class CapabilityHandler {

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(
                    new ResourceLocation(SimpleDifficulty.MODID, SDCapabilities.TEMPERATURE_IDENTIFIER),
                    new TemperatureProvider(SDCapabilities.TEMPERATURE)
            );

            event.addCapability(
                    new ResourceLocation(SimpleDifficulty.MODID, SDCapabilities.THIRST_IDENTIFIER),
                    new ThirstProvider(SDCapabilities.THIRST)
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        World world = player.level;

        if (world.isClientSide) {
            return;
        }

        // Server Side (assumes ServerPlayerEntity from this point on)
        int packetTimerThreshold = ModConfig.SERVER.routinePacketDelay.get();

        // Update Temperature
        if (QuickConfig.isTemperatureEnabled()) {
            ITemperatureCapability temperatureCapability = SDCapabilities.getTemperatureData(player);
            if (temperatureCapability != null) {
                temperatureCapability.tickUpdate(player, world, event.phase);

                if (event.phase == TickEvent.Phase.START && (temperatureCapability.isDirty() || temperatureCapability.getPacketTimer() % packetTimerThreshold == 0)) {
                    temperatureCapability.setClean();
                    sendTemperatureUpdate((ServerPlayerEntity) player);
                }
            }
        }

        // Update Thirst
        if (QuickConfig.isThirstEnabled() && !shouldPlayerSkipThirst(player)) {
            IThirstCapability thirstCapability = SDCapabilities.getThirstData(player);
            if (thirstCapability != null) {
                thirstCapability.tickUpdate(player, world, event.phase);

                if (event.phase == TickEvent.Phase.START && (thirstCapability.isDirty() || thirstCapability.getPacketTimer() % packetTimerThreshold == 0)) {
                    thirstCapability.setClean();
                    sendThirstUpdate((ServerPlayerEntity) player);
                }
            }
        }
    }

    private static boolean shouldPlayerSkipThirst(PlayerEntity player) {
        return player.isSpectator() || player.isCreative();
    }

    private static void sendTemperatureUpdate(ServerPlayerEntity player) {
        Capability<ITemperatureCapability> capability = SDCapabilities.TEMPERATURE;
        CompoundNBT nbt = (CompoundNBT) capability.getStorage().writeNBT(capability, SDCapabilities.getTemperatureData(player), null);
        PacketHandler.INSTANCE.sendTo(new MessageUpdateTemperature(nbt), player.connection.connection, net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT);
    }

    private static void sendThirstUpdate(ServerPlayerEntity player) {
        Capability<IThirstCapability> capability = SDCapabilities.THIRST;
        CompoundNBT nbt = (CompoundNBT) capability.getStorage().writeNBT(capability, SDCapabilities.getThirstData(player), null);
        PacketHandler.INSTANCE.sendTo(new MessageUpdateThirst(nbt), player.connection.connection, net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT);
    }
}