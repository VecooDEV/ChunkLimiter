package com.vecoo.chunklimiter.storage.player;

import com.vecoo.chunklimiter.ChunkLimiter;

import java.util.UUID;

public class ChunkPlayerFactory {
    public static boolean hasNotification(UUID playerUUID) {
        return ChunkLimiter.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getNotification();
    }

    public static void setNotification(UUID playerUUID, boolean toggle) {
        ChunkLimiter.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).setNotification(toggle);
    }
}