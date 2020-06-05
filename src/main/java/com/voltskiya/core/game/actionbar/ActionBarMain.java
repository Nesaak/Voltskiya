package com.voltskiya.core.game.actionbar;

import org.bukkit.plugin.java.JavaPlugin;

public class ActionBarMain {
    public static void enable(JavaPlugin plugin){
        ActionBar.initialize(plugin);
        new ActionBarRun(plugin);
    }
}
