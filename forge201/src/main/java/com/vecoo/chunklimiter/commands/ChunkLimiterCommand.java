package com.vecoo.chunklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.storage.player.PlayerFactory;
import com.vecoo.chunklimiter.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ChunkLimiterCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String command : List.of("chunklimiter", "cl")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> p.hasPermission(ChunkLimiter.getPermissionsConfig().getPermissionCommand().get("minecraft.command.chunklimiter")))
                    .executes(e -> execute(e.getSource().getPlayerOrException()))
                    .then(Commands.literal("reload")
                            .requires(p -> p.hasPermission(ChunkLimiter.getPermissionsConfig().getPermissionCommand().get("minecraft.command.chunklimiter.reload")))
                            .executes(e -> executeReload(e.getSource()))));
        }
    }

    private static int execute(ServerPlayer player) {
        if (PlayerFactory.hasNotification(player.getUUID())) {
            PlayerFactory.setNotification(player.getUUID(), false);
            player.sendSystemMessage(Utils.formatMessage(ChunkLimiter.getLocaleConfig().getLimitNotificationDisabled()));
        } else {
            PlayerFactory.setNotification(player.getUUID(), true);
            player.sendSystemMessage(Utils.formatMessage(ChunkLimiter.getLocaleConfig().getLimitNotificationEnabled()));
        }
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        ChunkLimiter.getInstance().loadConfig();
        source.sendSystemMessage(Utils.formatMessage(ChunkLimiter.getLocaleConfig().getConfigReload()));
        return 1;
    }
}