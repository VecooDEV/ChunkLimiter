package com.vecoo.chunklimiter;

import com.vecoo.chunklimiter.command.ChunkLimiterCommand;
import com.vecoo.chunklimiter.config.LocaleConfig;
import com.vecoo.chunklimiter.config.ServerConfig;
import com.vecoo.chunklimiter.listener.ChunkLimiterListener;
import com.vecoo.chunklimiter.storage.player.PlayerProvider;
import com.vecoo.chunklimiter.util.PermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod(ChunkLimiter.MOD_ID)
public class ChunkLimiter {
    public static final String MOD_ID = "chunklimiter";
    private static final Logger LOGGER = LogManager.getLogger("ChunkLimiter");

    private static ChunkLimiter instance;

    private ServerConfig config;
    private LocaleConfig locale;

    private PlayerProvider playerProvider;

    private MinecraftServer server;

    public ChunkLimiter() {
        instance = this;

        this.loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ChunkLimiterListener());
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.permissionList.add(PermissionNodes.CHUNKLIMITER_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.CHUNKLIMITER_RELOAD_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.CHUNKLIMITER_ATTRIBUTE_IGNORE);

        event.addNodes(new ArrayList<>(PermissionNodes.permissionList));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ChunkLimiterCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        this.server = event.getServer();
        this.loadStorage();
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.locale = new LocaleConfig();
            this.locale.init();
        } catch (Exception e) {
            LOGGER.error("[ChunkLimiter] Error load config.");
        }
    }

    public void loadStorage() {
        try {
            this.playerProvider = new PlayerProvider("/%directory%/storage/ChunkLimiter/players/", this.server);
            this.playerProvider.init();
        } catch (Exception e) {
            LOGGER.error("[ChunkLimiter] Error load storage.");
        }
    }

    public static ChunkLimiter getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public ServerConfig getConfig() {
        return instance.config;
    }

    public LocaleConfig getLocale() {
        return instance.locale;
    }

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}