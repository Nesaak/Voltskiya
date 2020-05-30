package com.voltskiya.core.game.bedTeleportScroll;

import org.bukkit.plugin.java.JavaPlugin;

public class BedTeleportMain {
    public static void enable(JavaPlugin plugin){
        new BedTeleportListener(plugin);
    }
}
