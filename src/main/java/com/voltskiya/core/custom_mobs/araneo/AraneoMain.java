package com.voltskiya.core.custom_mobs.araneo;

import org.bukkit.plugin.java.JavaPlugin;

public class AraneoMain {
    public static void enable(JavaPlugin plugin) {
        AraneoAI.initialize(plugin);
        WebProjectile.initialize(plugin);
        new AraneoSpawnListener(plugin);
    }
}
