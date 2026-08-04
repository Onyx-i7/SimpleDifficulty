package com.charles445.simpledifficulty.debug;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Debug utility class for logging, timing, and messaging during development.
 */
public class DebugUtil {

    private static long elapsedTotal = 0L;
    private static long elapsedInst = 0L;
    private static long elapsed = 0L;
    private static long snapshot = 0L;
    private static long snapshotTime = 0L;
    private static long count = 0L;

    private static long nanoCache = 0L;

    /**
     * Sends a debug message to all online players if server debug mode is enabled.
     *
     * @param s The message to send.
     */
    public static void messageAll(String s) {
        if (ServerConfig.instance.getBoolean(ServerOptions.DEBUG)) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null && server.getPlayerList() != null) {
                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                    player.sendMessage(new StringTextComponent(s), player.getUUID());
                }
            }
        }
    }

    /**
     * Sends a debug message to a specific player if debug mode is enabled for their side.
     *
     * @param player The player to send the message to.
     * @param s The message to send.
     */
    public static void clientMessage(PlayerEntity player, String s) {
        if (player == null) {
            return;
        }

        if (player.level.isClientSide) {
            // Client side
            if (!ClientConfig.instance.getBoolean(ClientOptions.DEBUG)) {
                return;
            }
        } else {
            // Server side
            if (!ServerConfig.instance.getBoolean(ServerOptions.DEBUG)) {
                return;
            }
        }

        player.sendMessage(new StringTextComponent(s), player.getUUID());
    }

    /**
     * Starts a performance timer.
     */
    public static void startTimer() {
        elapsed = System.nanoTime();
    }

    /**
     * Stops the performance timer and logs the elapsed time.
     *
     * @param client If true, sends the result to the client player; otherwise sends to all players.
     */
    public static void stopTimer(boolean client) {
        nanoCache = System.nanoTime();
        elapsedInst = nanoCache - elapsed;
        elapsedTotal += elapsedInst;
        count++;

        if (count <= 0) {
            return;
        }

        if (snapshotTime < nanoCache) {
            long currentDiff = elapsedTotal - snapshot;
            long average = currentDiff / count;
            String debugMessage = String.format("%d : %d (%d)", currentDiff, count, average);

            if (client) {
                PlayerEntity player = Minecraft.getInstance().player;
                if (player != null) {
                    DebugUtil.clientMessage(player, debugMessage);
                }
            } else {
                DebugUtil.messageAll(debugMessage);
            }

            snapshot = elapsedTotal;
            count = 0;
            snapshotTime = System.nanoTime() + 1000000000L;
        }
    }
}