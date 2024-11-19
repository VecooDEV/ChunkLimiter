package com.vecoo.chunklimiter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.storage.player.ChunkPlayerFactory;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermissions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import java.util.Arrays;

public class ChunkLimiterCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        for (String command : Arrays.asList("chunklimiter", "cl")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> UtilPermissions.hasPermission(p, "minecraft.command.chunklimiter", ChunkLimiter.getInstance().getPermission().getPermissionCommand()))
                    .executes(e -> execute(e.getSource().getPlayerOrException()))
                    .then(Commands.literal("limits")
                            .executes(e -> executeLimits(e.getSource().getPlayerOrException())))
                    .then(Commands.literal("reload")
                            .requires(p -> UtilPermissions.hasPermission(p, "minecraft.command.chunklimiter.reload", ChunkLimiter.getInstance().getPermission().getPermissionCommand()))
                            .executes(e -> executeReload(e.getSource())))
                    .then(Commands.literal("help")
                            .executes(e -> executeHelp(e.getSource()))));
        }
    }

    private static int execute(ServerPlayerEntity player) {
        if (!UtilPermissions.hasPermission(player, "minecraft.command.chunklimiter", ChunkLimiter.getInstance().getPermission().getPermissionCommand())) {
            player.sendMessage(UtilChat.formatMessage(ExtraLib.getInstance().getLocale().getPlayerNotPermission()), Util.NIL_UUID);
            return 0;
        }

        if (ChunkPlayerFactory.hasNotification(player.getUUID())) {
            ChunkPlayerFactory.setNotification(player.getUUID(), false);
            player.sendMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitNotificationDisabled()), Util.NIL_UUID);
        } else {
            ChunkPlayerFactory.setNotification(player.getUUID(), true);
            player.sendMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitNotificationEnabled()), Util.NIL_UUID);
        }
        return 1;
    }

    private static int executeLimits(ServerPlayerEntity player) {
        if (!UtilPermissions.hasPermission(player, "minecraft.command.chunklimiter", ChunkLimiter.getInstance().getPermission().getPermissionCommand())) {
            player.sendMessage(UtilChat.formatMessage(ExtraLib.getInstance().getLocale().getPlayerNotPermission()), Util.NIL_UUID);
            return 0;
        }

        IChunk chunk = player.getLevel().getChunk(player.blockPosition());

        for (String block : ChunkLimiter.getInstance().getConfig().getBlocksCount().keySet()) {
            int currentCount = 0;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < chunk.getMaxBuildHeight(); y++) {
                        BlockPos blockPos = new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16);
                        String blockId = chunk.getBlockState(blockPos).getBlock().getDescriptionId().replaceFirst("block\\.", "").replaceAll("\\.", ":");

                        if (blockId.equals(block)) {
                            currentCount++;
                        }
                    }
                }
            }

            player.sendMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitsChunk()
                    .replace("%block%", block)
                    .replace("%maxCount%", String.valueOf( ChunkLimiter.getInstance().getConfig().getBlocksCount().get(block)))
                    .replace("%currentCount%", String.valueOf(currentCount))), Util.NIL_UUID);
        }
        return 1;
    }

    private static int executeReload(CommandSource source) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.chunklimiter.reload", ChunkLimiter.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(ExtraLib.getInstance().getLocale().getPlayerNotPermission()), false);
            return 0;
        }

        ChunkLimiter.getInstance().loadConfig();
        ChunkLimiter.getInstance().loadStorage();

        source.sendSuccess(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getConfigReload()), false);
        return 1;
    }

    private static int executeHelp(CommandSource source) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.chunklimiter", ChunkLimiter.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(ExtraLib.getInstance().getLocale().getPlayerNotPermission()), false);
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getModHelpPlayer()), false);
        return 1;
    }
}