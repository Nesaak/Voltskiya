package com.voltskiya.core.game.respawn;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RespawnMain {
    public static void enable(JavaPlugin plugin, File dataFolder) {
        new RespawnListener(plugin, dataFolder);
        System.out.println("[VoltskiyaApple] [RespawnRegion] enabled");
    }
}
