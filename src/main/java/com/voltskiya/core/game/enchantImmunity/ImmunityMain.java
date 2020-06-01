package com.voltskiya.core.game.enchantImmunity;

import org.bukkit.plugin.java.JavaPlugin;

public class ImmunityMain {
    public static void enable(JavaPlugin plugin) {
        new ImmunityListener(plugin);
        System.out.println("[VoltskiyaApple] [SmiteImmunity] enabled");
    }
}
