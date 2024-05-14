package com.vecoo.chunklimiter.util;

import net.minecraft.network.chat.Component;

public class Utils {
    public static Component formatMessage(String message) {
        return Component.literal(message.replace("&", "\u00a7").trim());
    }
}