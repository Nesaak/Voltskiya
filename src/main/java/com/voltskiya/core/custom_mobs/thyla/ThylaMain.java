package com.voltskiya.core.custom_mobs.thyla;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ThylaMain {
    public static void enable(JavaPlugin plugin, File dataFolder) {
        ThylaAI.initialize(plugin, dataFolder);
        new ThylaSpawnOrHitListener(plugin);
    }
}
