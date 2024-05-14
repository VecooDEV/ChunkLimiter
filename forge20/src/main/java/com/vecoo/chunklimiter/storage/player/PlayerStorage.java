package com.vecoo.chunklimiter.storage.player;

import com.vecoo.chunklimiter.ChunkLimiter;

import java.util.UUID;

public class PlayerStorage {
    private UUID playerUUID;
    private boolean notification;

    public PlayerStorage(UUID playerUUID, boolean notification) {
        this.playerUUID = playerUUID;
        this.notification = notification;
        ChunkLimiter.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public boolean getNotification() {
        return this.notification;
    }

    public void setNotification(boolean toggle) {
        this.notification = toggle;
    }
}