package com.vecoo.chunklimiter.listener;

import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.storage.player.ChunkPlayerFactory;
import com.vecoo.extralib.chat.UtilChat;
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

        if (player.hasPermissions(ChunkLimiter.getInstance().getPermission().getPermissionCommand().get("chunklimiter.attribute.ignore"))) {
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


//    @SubscribeEvent (Not working, lol)
//    public void onTagBlockPlace(BlockEvent.EntityPlaceEvent event) {
//        Block block = event.getPlacedBlock().getBlock();
//
//        for (Map.Entry<String, Integer> entry : ChunkLimiter.getInstance().getConfig().getTagBlocksCount().entrySet()) {
//            if (BlockTags.bind(entry.getKey()).contains(block)) {
//
//                int blockMax = entry.getValue();
//                int countBlockChunk = UtilWorld.countBlocksInChunk(event.getWorld().getChunk(event.getPos()), entry.getKey());
//
//                if (countBlockChunk > blockMax) {
//                    event.setCanceled(true);
//                }
//
//                ServerPlayerEntity player = event.getEntity() instanceof ServerPlayerEntity ? (ServerPlayerEntity) event.getEntity() : null;
//                if (player != null) {
//                    if (ChunkPlayerFactory.hasNotification(player.getUUID())) {
//                        if (countBlockChunk <= blockMax) {
//                            player.sendMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitTagBlocks()
//                                    .replace("%current%", String.valueOf(countBlockChunk))
//                                    .replace("%max%", String.valueOf(blockMax))), Util.NIL_UUID);
//                        } else {
//                            player.sendMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getMaxLimitTagBlocks()), Util.NIL_UUID);
//                        }
//                    }
//                }
//            }
//        }
//    }
}