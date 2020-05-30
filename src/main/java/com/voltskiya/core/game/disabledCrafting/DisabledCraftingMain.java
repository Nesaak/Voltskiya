package com.voltskiya.core.game.disabledCrafting;

import org.bukkit.plugin.java.JavaPlugin;

public class DisabledCraftingMain {
    public static void enable(JavaPlugin plugin) {
        new DisabledCraftingListener(plugin);
    }
}
