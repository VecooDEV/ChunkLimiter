package com.vecoo.chunklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.vecoo.chunklimiter.config.LocaleConfig;
import com.vecoo.chunklimiter.storage.player.PlayerFactory;
import com.vecoo.chunklimiter.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ChunkLimiterCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cl").requires(p -> p.hasPermission(2))
                .executes(e -> execute(e.getSource().getPlayerOrException())));
    }

    private static int execute(ServerPlayer player) {
        if (PlayerFactory.hasNotification(player.getUUID())) {
            PlayerFactory.setNotification(player.getUUID(), false);
            player.sendSystemMessage(Utils.formatMessage(LocaleConfig.Builder.load().limitNotificationDisabled));
        } else {
            PlayerFactory.setNotification(player.getUUID(), true);
            player.sendSystemMessage(Utils.formatMessage(LocaleConfig.Builder.load().limitNotificationEnabled));
        }
        return 1;
    }
}