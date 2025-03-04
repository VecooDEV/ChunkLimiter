package com.vecoo.chunklimiter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.storage.ChunkPlayerFactory;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

public class ChunkLimiterCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("cl")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.cl"))
                .executes(e -> execute(e.getSource().getPlayerOrException()))
                .then(Commands.literal("limits")
                        .executes(e -> executeLimits(e.getSource().getPlayerOrException())))
                .then(Commands.literal("reload")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.cl.reload"))
                        .executes(e -> executeReload(e.getSource())))
                .then(Commands.literal("help")
                        .executes(e -> executeHelp(e.getSource()))));
    }

    private static int execute(ServerPlayerEntity player) {
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
                    .replace("%maxCount%", String.valueOf(ChunkLimiter.getInstance().getConfig().getBlocksCount().get(block)))
                    .replace("%currentCount%", String.valueOf(currentCount))), Util.NIL_UUID);
        }
        return 1;
    }

    private static int executeReload(CommandSource source) {
        ChunkLimiter.getInstance().loadConfig();
        ChunkLimiter.getInstance().loadStorage();

        source.sendSuccess(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getConfigReload()), false);
        return 1;
    }

    private static int executeHelp(CommandSource source) {
        String message = ChunkLimiter.getInstance().getLocale().getHelp();

        if (source.hasPermission(2)) {
            message += ChunkLimiter.getInstance().getLocale().getHelpOp();
        }

        source.sendSuccess(UtilChat.formatMessage(message), false);
        return 1;
    }
}