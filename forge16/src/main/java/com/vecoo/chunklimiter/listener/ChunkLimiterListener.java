package com.vecoo.chunklimiter.listener;

import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.storage.player.ChunkPlayerFactory;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.world.UtilWorld;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkLimiterListener {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

        if (UtilPermission.hasPermission(player, "chunklimiter.attribute.ignore")) {
            return;
        }

        Block block = event.getPlacedBlock().getBlock();
        String blockId = block.getDescriptionId().replaceFirst("block\\.", "").replaceAll("\\.", ":");

        if (ChunkLimiter.getInstance().getConfig().getBlocksCount().get(blockId) == null) {
            return;
        }

        int blockMax = ChunkLimiter.getInstance().getConfig().getBlocksCount().get(blockId);
        int countBlockChunk = UtilWorld.countBlocksInChunk(event.getWorld().getChunk(event.getPos()), block);

        if (countBlockChunk > blockMax) {
            event.setCanceled(true);
        }

        if (ChunkPlayerFactory.hasNotification(player.getUUID())) {
            if (countBlockChunk <= blockMax) {
                player.sendMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getLimitBlocks()
                        .replace("%current%", String.valueOf(countBlockChunk))
                        .replace("%max%", String.valueOf(blockMax))), Util.NIL_UUID);
            } else {
                player.sendMessage(UtilChat.formatMessage(ChunkLimiter.getInstance().getLocale().getMaxLimitBlocks()), Util.NIL_UUID);
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