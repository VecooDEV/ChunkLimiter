package com.vecoo.chunklimiter;

import com.vecoo.chunklimiter.commands.ChunkLimiterCommand;
import com.vecoo.chunklimiter.config.LocaleConfig;
import com.vecoo.chunklimiter.config.ServerConfig;
import com.vecoo.chunklimiter.listener.BlockListener;
import com.vecoo.chunklimiter.storage.player.PlayerProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ChunkLimiter.MOD_ID)
public class ChunkLimiter {
    public static final String MOD_ID = "chunklimiter";

    private static ChunkLimiter instance;

    public static ServerConfig serverConfig;
    public static LocaleConfig localeConfig;

    private PlayerProvider playerProvider;

    public ChunkLimiter() {
        instance = this;

        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BlockListener());
    }

    public void loadConfig() {
        serverConfig = ServerConfig.Builder.load();
        localeConfig = LocaleConfig.Builder.load();
        playerProvider = new PlayerProvider();
        playerProvider.init();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ChunkLimiterCommand.register(event.getDispatcher());
    }


    public static ChunkLimiter getInstance() {
        return instance;
    }

    public PlayerProvider getPlayerProvider() {
        return this.playerProvider;
    }
}