package com.vecoo.chunklimiter.config;

import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.extralib.gson.UtilGson;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ServerConfig {
    private String playerStorage = "/%directory%/storage/ChunkLimiter/players/";
    private HashMap<String, Integer> blocksCount;
    private HashMap<String, Integer> tagBlocksCount;

    public ServerConfig() {
        this.blocksCount = new HashMap<>();
        this.blocksCount.put("minecraft:piston", 4);
        this.tagBlocksCount = new HashMap<>();
        this.tagBlocksCount.put("Not working", 16);
    }

    public String getPlayerStorage() {
        return this.playerStorage;
    }

    public HashMap<String, Integer> getBlocksCount() {
        return this.blocksCount;
    }

    public HashMap<String, Integer> getTagBlocksCount() {
        return this.tagBlocksCount;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ChunkLimiter/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ChunkLimiter/", "config.json", el -> {
                ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

                this.playerStorage = config.getPlayerStorage();
                this.blocksCount = config.getBlocksCount();
                this.tagBlocksCount = config.getTagBlocksCount();
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