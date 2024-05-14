package com.vecoo.chunklimiter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashMap;

public class ServerConfig {
    public HashMap<String, Integer> blocksCount;

    public static class Builder {
        public static ServerConfig load() {
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

            ServerConfig config = new ServerConfig();
            File configFile = new File("config/ChunkLimiter/config.json");
            configFile.getParentFile().mkdirs();

            if (configFile.exists()) {
                try {
                    FileReader fileReader = new FileReader(configFile);
                    config = gson.fromJson(fileReader, ServerConfig.class);
                    fileReader.close();

                } catch (Exception e) {
                    System.out.println("Error reading config file");
                }
            } else {
                try {
                    config.blocksCount = new HashMap<>();
                    config.blocksCount.put("block.minecraft.diamond_block", 8);
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

    public void saveConfig() {
        File configFile = new File("config/ChunkLimiter/config.json");

        try (FileWriter writer = new FileWriter(configFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(this);
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}