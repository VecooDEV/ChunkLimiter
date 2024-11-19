package com.vecoo.chunklimiter.config;

import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.extralib.gson.UtilGson;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PermissionConfig {
    private HashMap<String, Integer> permissionCommands;

    public PermissionConfig() {
        this.permissionCommands = new HashMap<>();
        this.permissionCommands.put("minecraft.command.chunklimiter", 0);
        this.permissionCommands.put("minecraft.command.chunklimiter.ignore", 2);
        this.permissionCommands.put("minecraft.command.chunklimiter.reload", 2);
    }

    public HashMap<String, Integer> getPermissionCommand() {
        return this.permissionCommands;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ChunkLimiter/", "permission.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ChunkLimiter/", "permission.json",
                    el -> this.permissionCommands = UtilGson.newGson().fromJson(el, PermissionConfig.class).getPermissionCommand());
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ChunkLimiter.getLogger().error("[ChunkLimiter] Error in permission config.");
            write();
        }
    }
}