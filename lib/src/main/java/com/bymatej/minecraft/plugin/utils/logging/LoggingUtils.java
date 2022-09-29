package com.bymatej.minecraft.plugin.utils.logging;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import static java.util.logging.Level.INFO;

public class LoggingUtils {

    private LoggingUtils() {}

    public static void log(String message) {
        log(INFO, message);
    }

    public static void log(Level level, String message) {
        Bukkit.getLogger().log(level, message);
    }

    public static void log(Level level, String message, Throwable throwable) {
        Bukkit.getLogger().log(level, message, throwable);
    }

}
