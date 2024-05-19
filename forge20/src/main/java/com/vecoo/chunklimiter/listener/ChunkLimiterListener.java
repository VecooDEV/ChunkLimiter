package com.vecoo.chunklimiter.listener;

import com.vecoo.chunklimiter.config.LocaleConfig;
import com.vecoo.chunklimiter.config.ServerConfig;
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

        if (ServerConfig.Builder.load().blocksCount.containsKey(block.getDescriptionId())) {
            if (ServerConfig.Builder.load().blocksCount.get(block.getDescriptionId()) == null) {
                return;
            }

            int blockCount = 0;
            int blockMax = ServerConfig.Builder.load().blocksCount.get(block.getDescriptionId());

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
                    player.sendSystemMessage(Utils.formatMessage(LocaleConfig.Builder.load().limitBlocks
                            .replace("%current%", String.valueOf(blockCount))
                            .replace("%max%", String.valueOf(blockMax))));
                } else if (blockCount > blockMax) {
                    player.sendSystemMessage(Utils.formatMessage(LocaleConfig.Builder.load().maxLimitBlocks));
                }
            }
        }
    }

    @SubscribeEvent
    public void onTagBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Block block = event.getPlacedBlock().getBlock();

        for (Map.Entry<String, Integer> entry : ServerConfig.Builder.load().tagBlocksCount.entrySet()) {
            if (ForgeRegistries.BLOCKS.tags().getTag(TagKey.create(Registries.BLOCK, resourceLocation(entry))) == null) {
                continue;
            }

            if (ForgeRegistries.BLOCKS.tags().getTag(TagKey.create(Registries.BLOCK, resourceLocation(entry))).contains(block)) {
                int blockCount = 0;
                int blockMax = ServerConfig.Builder.load().tagBlocksCount.get(resourceLocation(entry).toLanguageKey());

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
                        player.sendSystemMessage(Utils.formatMessage(LocaleConfig.Builder.load().limitTagBlocks
                                .replace("%current%", String.valueOf(blockCount))
                                .replace("%max%", String.valueOf(blockMax))));
                    } else if (blockCount > blockMax) {
                        player.sendSystemMessage(Utils.formatMessage(LocaleConfig.Builder.load().maxLimitTagBlocks));
                    }
                }
            }
        }
    }

    public ResourceLocation resourceLocation(Map.Entry<String, Integer> f) {
        String[] parts = f.getKey().split("\\.");
        return new ResourceLocation(parts[0], parts[1]);
    }
}