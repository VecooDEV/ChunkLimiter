package com.vecoo.chunklimiter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.storage.player.ChunkPlayerFactory;
import com.vecoo.extralib.chat.UtilChat;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ChunkLimiterCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cl")
                .requires(p -> p.hasPermission(ChunkLimiter.getInstance().getPermission().getPermissionCommand().get("minecraft.command.cl")))
                .executes(e -> execute(e.getSource().getPlayerOrException()))
                .then(Commands.literal("limits")
                        .executes(e -> executeLimits(e.getSource().getPlayerOrException())))
                .then(Commands.literal("reload")
                        .requires(p -> p.hasPermission(ChunkLimiter.getInstance().getPermission().getPermissionCommand().get("minecraft.command.cl.reload")))
                        .executes(e -> executeReload(e.getSource())))
                .then(Commands.literal("help")
                        .executes(e -> executeHelp(e.getSource()))));
    }

    private static int execute(ServerPlayer player) {
        if (ChunkPlayerFactory.hasNotification(player.getUUID())) {
            ChunkPlayerFactory.setNotification(player.getUUID(), false);
            player.sendSystemMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitNotificationDisabled()));
        } else {
            ChunkPlayerFactory.setNotification(player.getUUID(), true);
            player.sendSystemMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitNotificationEnabled()));
        }
        return 1;
    }

    private static int executeLimits(ServerPlayer player) {
        ChunkAccess chunk = player.level().getChunk(player.blockPosition());

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

            player.sendSystemMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitsChunk()
                    .replace("%block%", block)
                    .replace("%maxCount%", String.valueOf(ChunkLimiter.getInstance().getConfig().getBlocksCount().get(block)))
                    .replace("%currentCount%", String.valueOf(currentCount))));
        }
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        ChunkLimiter.getInstance().loadConfig();
        ChunkLimiter.getInstance().loadStorage();

        source.sendSystemMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getConfigReload()));
        return 1;
    }

    private static int executeHelp(CommandSourceStack source) {
        source.sendSystemMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getModHelpPlayer()));
        return 1;
    }
}