package com.vecoo.chunklimiter.storage.player;

import com.vecoo.chunklimiter.ChunkLimiter;

import java.util.UUID;

public class PlayerStorage {
    private final UUID uuid;
    private boolean notification;

    public PlayerStorage(UUID uuid, boolean notification) {
        this.uuid = uuid;
        this.notification = notification;
        ChunkLimiter.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean getNotification() {
        return this.notification;
    }

    public void setNotification(boolean toggle) {
        this.notification = toggle;
        ChunkLimiter.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }
}