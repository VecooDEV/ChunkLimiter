package com.vecoo.chunklimiter.listener;

import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.storage.player.PlayerFactory;
import com.vecoo.chunklimiter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class ChunkLimiterListener {

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Block block = event.getPlacedBlock().getBlock();
        String blockId = block.getDescriptionId().replaceFirst("block\\.", "").replaceAll("\\.", ":");

        try {
            if (ChunkLimiter.getServerConfig().getBlocksCount().containsKey(blockId)) {
                if (ChunkLimiter.getServerConfig().getBlocksCount().get(blockId) == null) {
                    return;
                }

                int blockCount = 0;
                int blockMax = ChunkLimiter.getServerConfig().getBlocksCount().get(blockId);

                ChunkAccess chunk = event.getLevel().getChunk(event.getPos());

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

                ServerPlayer player = event.getEntity() instanceof ServerPlayer ? (ServerPlayer) event.getEntity() : null;
                if (player != null) {
                    if (blockCount <= blockMax && PlayerFactory.hasNotification(player.getUUID())) {
                        player.sendSystemMessage(Utils.formatMessage(ChunkLimiter.getLocaleConfig().getLimitBlocks()
                                .replace("%current%", String.valueOf(blockCount))
                                .replace("%max%", String.valueOf(blockMax))));
                    } else if (blockCount > blockMax) {
                        player.sendSystemMessage(Utils.formatMessage(ChunkLimiter.getLocaleConfig().getMaxLimitBlocks()));
                    }
                }
            }
        } catch (Exception e) {
            ChunkLimiter.getLogger().error("The blocks in the config are configured incorrectly (config.json).");
        }
    }

    @SubscribeEvent
    public void onTagBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Block block = event.getPlacedBlock().getBlock();

        try {
            for (Map.Entry<String, Integer> entry : ChunkLimiter.getServerConfig().getTagBlocksCount().entrySet()) {
                if (ForgeRegistries.BLOCKS.tags().getTag(TagKey.create(Registries.BLOCK, resourceLocation(entry))).contains(block)) {
                    int blockCount = 0;
                    int blockMax = ChunkLimiter.getServerConfig().getTagBlocksCount().get(resourceLocation(entry).toLanguageKey());

                    ChunkAccess chunk = event.getLevel().getChunk(event.getPos());

                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
                                BlockPos blockPos = new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16);
                                Block blocks = chunk.getBlockState(blockPos).getBlock();

                                if (ForgeRegistries.BLOCKS.tags().getTag(TagKey.create(Registries.BLOCK, resourceLocation(entry))).contains(blocks)) {
                                    blockCount++;
                                    if (blockCount > blockMax) {
                                        event.setCanceled(true);
                                    }
                                }
                            }
                        }
                    }

                    ServerPlayer player = event.getEntity() instanceof ServerPlayer ? (ServerPlayer) event.getEntity() : null;
                    if (player != null) {
                        if (blockCount <= blockMax && PlayerFactory.hasNotification(player.getUUID())) {
                            player.sendSystemMessage(Utils.formatMessage(ChunkLimiter.getLocaleConfig().getLimitBlocks()
                                    .replace("%current%", String.valueOf(blockCount))
                                    .replace("%max%", String.valueOf(blockMax))));
                        } else if (blockCount > blockMax) {
                            player.sendSystemMessage(Utils.formatMessage(ChunkLimiter.getLocaleConfig().getMaxLimitTagBlocks()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            ChunkLimiter.getLogger().error("In the config, block tags are configured incorrectly (config.json).");
        }
    }

    public ResourceLocation resourceLocation(Map.Entry<String, Integer> tagId) {
        String[] parts = tagId.getKey().split("\\.");
        return new ResourceLocation(parts[0], parts[1]);
    }
}