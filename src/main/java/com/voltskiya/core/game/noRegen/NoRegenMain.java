package com.voltskiya.core.game.noRegen;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class NoRegenMain {
    public static void enable(JavaPlugin plugin, File dataFolder) {
        new RegenListener(plugin, dataFolder);
        System.out.println("[VoltskiyaApple] [RegenCooldown] enabled");
    }
}
