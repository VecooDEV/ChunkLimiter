package com.vecoo.chunklimiter.config;

import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.extralib.gson.UtilGson;

import java.util.concurrent.CompletableFuture;

public class LocaleConfig {
    private String configReload = "&e(!) Configs have been reloaded.";
    private String limitBlocks = "&e(!) The limit of this block per chunk is: %current%/%max%.";
    private String maxLimitBlocks = "&e(!) You have reached the limit for this block per chunk.";
    private String limitTagBlocks = "&e(!) The limit of this group of blocks per chunk is: %current%/%max%.";
    private String maxLimitTagBlocks = "&e(!) You have reached the block group limit per chunk.";
    private String limitsChunk = "&e- %block% - %currentCount%/%maxCount%";
    private String limitNotificationEnabled = "&e(!) Limit notification enabled.";
    private String limitNotificationDisabled = "&e(!) Limit notification disabled.";
    private String modHelpPlayer = "&eInformation the ChunkLimiter:\n" +
            "- /cl - Enable or disable notifications about chunk limits.\n" +
            "- /cl reload - Reload all configurations.\n" +
            "- /cl help - Information the mod";

    public String getConfigReload() {
        return this.configReload;
    }

    public String getLimitBlocks() {
        return this.limitBlocks;
    }

    public String getLimitNotificationDisabled() {
        return this.limitNotificationDisabled;
    }

    public String getLimitNotificationEnabled() {
        return this.limitNotificationEnabled;
    }

    public String getLimitTagBlocks() {
        return this.limitTagBlocks;
    }

    public String getMaxLimitBlocks() {
        return this.maxLimitBlocks;
    }

    public String getMaxLimitTagBlocks() {
        return this.maxLimitTagBlocks;
    }

    public String getModHelpPlayer() {
        return this.modHelpPlayer;
    }

    public String getLimitsChunk() {
        return this.limitsChunk;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ChunkLimiter/", "locale.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ChunkLimiter/", "locale.json", el -> {
                LocaleConfig config = UtilGson.newGson().fromJson(el, LocaleConfig.class);

                this.modHelpPlayer = config.getModHelpPlayer();
                this.maxLimitTagBlocks = config.getMaxLimitTagBlocks();
                this.configReload = config.getConfigReload();
                this.limitBlocks = config.getLimitBlocks();
                this.limitNotificationDisabled = config.getLimitNotificationDisabled();
                this.limitNotificationEnabled = config.getLimitNotificationEnabled();
                this.limitTagBlocks = config.getLimitTagBlocks();
                this.maxLimitTagBlocks = config.getMaxLimitTagBlocks();
                this.maxLimitBlocks = config.getMaxLimitBlocks();
                this.limitsChunk = config.getLimitsChunk();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ChunkLimiter.getLogger().error("[ChunkLimiter] Error in locale config.");
            write();
        }
    }
}