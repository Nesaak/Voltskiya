package com.voltskiya.core.custom_mobs.better_spider_spawn;

import org.bukkit.plugin.java.JavaPlugin;

public class BetterSpiderSpawnMain {
    public static void enable(JavaPlugin plugin) {
        new BetterSpiderSpawn(plugin);
    }
}
