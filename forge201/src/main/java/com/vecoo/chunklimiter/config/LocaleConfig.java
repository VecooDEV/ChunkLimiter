package com.vecoo.chunklimiter.config;

import com.google.gson.Gson;
import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.util.UtilGson;

import java.util.concurrent.CompletableFuture;

public class LocaleConfig {
    private String configReload;
    private String limitBlocks;
    private String maxLimitBlocks;
    private String limitTagBlocks;
    private String maxLimitTagBlocks;
    private String limitNotificationEnabled;
    private String limitNotificationDisabled;

    public LocaleConfig() {
        this.configReload = "\u00A7e(!) Configs have been reloaded.";
        this.limitBlocks = "\u00A7e(!) The limit of this block per chunk is: %current%/%max%.";
        this.maxLimitBlocks = "\u00A7e(!) You have reached the limit for this block per chunk.";
        this.limitTagBlocks = "\u00A7e(!) The limit of this group of blocks per chunk is: %current%/%max%.";
        this.maxLimitTagBlocks = "\u00A7e(!) You have reached the block group limit per chunk.";
        this.limitNotificationEnabled = "\u00A7e(!) Limit notification enabled";
        this.limitNotificationDisabled = "\u00A7e(!) Limit notification disabled";
    }

    public String getConfigReload() {
        return this.configReload;
    }

    public String getLimitBlocks() {
        return this.limitBlocks;
    }

    public String getMaxLimitBlocks() {
        return this.maxLimitBlocks;
    }

    public String getLimitTagBlocks() {
        return this.limitTagBlocks;
    }

    public String getMaxLimitTagBlocks() {
        return this.maxLimitTagBlocks;
    }

    public String getLimitNotificationEnabled() {
        return this.limitNotificationEnabled;
    }

    public String getLimitNotificationDisabled() {
        return this.limitNotificationDisabled;
    }

    private boolean write() {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync("/config/ChunkLimiter/", "locale.json", gson.toJson(this));
        return future.join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ChunkLimiter/", "locale.json", el -> {
                Gson gson = UtilGson.newGson();
                LocaleConfig config = gson.fromJson(el, LocaleConfig.class);

                this.configReload = config.getConfigReload();
                this.limitBlocks = config.getLimitBlocks();
                this.maxLimitBlocks = config.getMaxLimitBlocks();
                this.limitTagBlocks = config.getLimitTagBlocks();
                this.maxLimitTagBlocks = config.getMaxLimitTagBlocks();
                this.limitNotificationEnabled = config.getLimitNotificationEnabled();
                this.limitNotificationDisabled = config.getLimitNotificationDisabled();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ChunkLimiter.getLogger().error("Error in config (locale.json).");
        }
    }
}