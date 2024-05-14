package com.vecoo.chunklimiter.listener;

import com.vecoo.chunklimiter.config.LocaleConfig;
import com.vecoo.chunklimiter.config.ServerConfig;
import com.vecoo.chunklimiter.storage.player.PlayerFactory;
import com.vecoo.chunklimiter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockListener {

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (ServerConfig.Builder.load().blocksCount.containsKey(event.getPlacedBlock().getBlock().getDescriptionId())) {
            Block block = event.getPlacedBlock().getBlock();
            ChunkAccess chunk = event.getLevel().getChunk(event.getPos());
            int blockMax = ServerConfig.Builder.load().blocksCount.get(block.getDescriptionId());
            int blockCount = 0;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
                        BlockPos blockPos = new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16);
                        Block blocks = chunk.getBlockState(blockPos).getBlock();

                        if (blocks.equals(block)) {
                            blockCount++;
                            if (blockCount > blockMax) {
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
            if (event.getEntity() instanceof ServerPlayer) {
                if (blockCount <= blockMax && PlayerFactory.hasNotification(event.getEntity().getUUID())) {
                    event.getEntity().sendSystemMessage(Utils.formatMessage(LocaleConfig.Builder.load().limitBlocks
                            .replace("%current%", String.valueOf(blockCount))
                            .replace("%max%", String.valueOf(blockMax))));
                } else if (blockCount > blockMax) {
                    event.getEntity().sendSystemMessage(Utils.formatMessage(LocaleConfig.Builder.load().maxLimitBlocks));
                }
            }
        }
    }
}