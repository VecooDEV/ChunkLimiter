package com.vecoo.chunklimiter.listener;

import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.storage.ChunkPlayerFactory;
import com.vecoo.chunklimiter.util.PermissionNodes;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.world.UtilWorld;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkLimiterListener {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (UtilPermission.hasPermission(player, PermissionNodes.CHUNKLIMITER_ATTRIBUTE_IGNORE)) {
            return;
        }

        Block block = event.getPlacedBlock().getBlock();
        String blockId = block.getDescriptionId().replaceFirst("block\\.", "").replaceAll("\\.", ":");

        if (ChunkLimiter.getInstance().getConfig().getBlocksCount().get(blockId) == null) {
            return;
        }

        int blockMax = ChunkLimiter.getInstance().getConfig().getBlocksCount().get(blockId);
        int countBlockChunk = UtilWorld.countBlocksInChunk(event.getLevel().getChunk(event.getPos()), block);

        if (countBlockChunk > blockMax) {
            event.setCanceled(true);
        }

        if (ChunkPlayerFactory.hasNotification(player.getUUID())) {
            if (countBlockChunk <= blockMax) {
                player.sendSystemMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitBlocks()
                        .replace("%current%", String.valueOf(countBlockChunk))
                        .replace("%max%", String.valueOf(blockMax))));
            } else {
                player.sendSystemMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getMaxLimitBlocks()));
            }
        }
    }
}