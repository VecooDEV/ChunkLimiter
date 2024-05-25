package com.vecoo.chunklimiter.config;

import com.google.gson.Gson;
import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.util.UtilGson;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ServerConfig {
    private HashMap<String, Integer> blocksCount;
    private HashMap<String, Integer> tagBlocksCount;

    public ServerConfig() {
        this.blocksCount = new HashMap<>();
        this.blocksCount.put("minecraft:piston", 4);

        this.tagBlocksCount = new HashMap<>();
        this.tagBlocksCount.put("forge.stone", 16);
    }

    public HashMap<String, Integer> getBlocksCount() {
        return this.blocksCount;
    }

    public HashMap<String, Integer> getTagBlocksCount() {
        return this.tagBlocksCount;
    }

    private boolean write() {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync("/config/ChunkLimiter/", "config.json", gson.toJson(this));
        return future.join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ChunkLimiter/", "config.json", el -> {
                Gson gson = UtilGson.newGson();
                ServerConfig data = gson.fromJson(el, ServerConfig.class);

                this.blocksCount.putAll(data.getBlocksCount());
                this.tagBlocksCount.putAll(data.getTagBlocksCount());
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ChunkLimiter.getLogger().error("Error in config (config.json).");
        }
    }
}