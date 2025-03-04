package com.vecoo.chunklimiter.config;

import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.extralib.gson.UtilGson;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ServerConfig {
    private HashMap<String, Integer> blocksCount;

    public ServerConfig() {
        this.blocksCount = new HashMap<>();
        this.blocksCount.put("minecraft:piston", 4);
    }

    public HashMap<String, Integer> getBlocksCount() {
        return this.blocksCount;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ChunkLimiter/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ChunkLimiter/", "config.json", el -> {
                ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

                this.blocksCount = config.getBlocksCount();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ChunkLimiter.getLogger().error("[ChunkLimiter] Error in config.");
            write();
        }
    }
}