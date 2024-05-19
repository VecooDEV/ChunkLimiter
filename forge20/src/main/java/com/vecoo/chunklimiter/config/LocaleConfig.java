package com.vecoo.chunklimiter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class LocaleConfig {
    public String limitBlocks;
    public String maxLimitBlocks;
    public String limitTagBlocks;
    public String maxLimitTagBlocks;
    public String limitNotificationEnabled;
    public String limitNotificationDisabled;

    public static class Builder {
        public static LocaleConfig load() {
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

            LocaleConfig config = new LocaleConfig();
            File configFile = new File("config/ChunkLimiter/locale.json");
            configFile.getParentFile().mkdirs();

            if (configFile.exists()) {
                try {
                    FileReader fileReader = new FileReader(configFile);
                    config = gson.fromJson(fileReader, LocaleConfig.class);
                    fileReader.close();

                } catch (Exception e) {
                    System.out.println("Error reading config file");
                }
            } else {
                try {
                    config.limitBlocks = "&e(!) The limit of this block per chunk is: %current%/%max%.";
                    config.maxLimitBlocks = "&e(!) You have reached the limit for this block per chunk.";
                    config.limitTagBlocks = "&e(!) The limit of this group of blocks per chunk is: %current%/%max%.";
                    config.maxLimitTagBlocks = "&e(!) You have reached the block group limit per chunk.";
                    config.limitNotificationEnabled = "&e(!) Limit notification enabled";
                    config.limitNotificationDisabled = "&e(!) Limit notification disabled";
                    FileWriter fileWriter = new FileWriter(configFile);
                    gson.toJson(config, fileWriter);
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return config;
        }
    }
}