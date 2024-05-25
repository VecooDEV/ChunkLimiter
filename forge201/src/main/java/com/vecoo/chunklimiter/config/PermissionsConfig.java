package com.vecoo.chunklimiter.config;

import com.google.gson.Gson;
import com.vecoo.chunklimiter.ChunkLimiter;
import com.vecoo.chunklimiter.util.UtilGson;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PermissionsConfig {
    private HashMap<String, Integer> permissionCommands;

    public PermissionsConfig() {
        this.permissionCommands = new HashMap<>();
        this.permissionCommands.put("minecraft.command.chunklimiter", 0);
        this.permissionCommands.put("minecraft.command.chunklimiter.reload", 2);
    }

    public HashMap<String, Integer> getPermissionCommand() {
        return this.permissionCommands;
    }

    private boolean write() {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync("/config/ChunkLimiter/", "permissions.json", gson.toJson(this));
        return future.join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ChunkLimiter/", "permissions.json", el -> {
                Gson gson = UtilGson.newGson();
                PermissionsConfig config = gson.fromJson(el, PermissionsConfig.class);

                this.permissionCommands.putAll(config.getPermissionCommand());
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ChunkLimiter.getLogger().error("Error in config (permissions.json).");
        }
    }
}