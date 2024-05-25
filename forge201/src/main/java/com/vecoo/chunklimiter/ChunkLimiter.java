package com.vecoo.chunklimiter;

import com.vecoo.chunklimiter.commands.ChunkLimiterCommand;
import com.vecoo.chunklimiter.config.LocaleConfig;
import com.vecoo.chunklimiter.config.PermissionsConfig;
import com.vecoo.chunklimiter.config.ServerConfig;
import com.vecoo.chunklimiter.listener.ChunkLimiterListener;
import com.vecoo.chunklimiter.storage.player.PlayerProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChunkLimiter.MOD_ID)
public class ChunkLimiter {
    public static final String MOD_ID = "chunklimiter";
    private static final Logger LOGGER = LogManager.getLogger("ChunkLimiter");

    private static ChunkLimiter instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;
    private PermissionsConfig permissionsConfig;

    private PlayerProvider playerProvider;

    public ChunkLimiter() {
        instance = this;

        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ChunkLimiterListener());
    }

    public void loadConfig() {
        this.serverConfig = new ServerConfig();
        this.serverConfig.init();
        this.localeConfig = new LocaleConfig();
        this.localeConfig.init();
        this.permissionsConfig = new PermissionsConfig();
        this.permissionsConfig.init();

        this.playerProvider = new PlayerProvider();
        this.playerProvider.init();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ChunkLimiterCommand.register(event.getDispatcher());
    }

    public static ChunkLimiter getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static ServerConfig getServerConfig() {
        return instance.serverConfig;
    }

    public static LocaleConfig getLocaleConfig() {
        return instance.localeConfig;
    }

    public static PermissionsConfig getPermissionsConfig() {
        return instance.permissionsConfig;
    }

    public static PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }
}