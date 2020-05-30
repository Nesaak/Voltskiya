package com.voltskiya.core.custom_mobs;

import com.voltskiya.core.custom_mobs.araneo.AraneoMain;
import com.voltskiya.core.custom_mobs.better_spider_spawn.BetterSpiderSpwnMain;
import com.voltskiya.core.custom_mobs.thyla.ThylaMain;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomMobsMain extends JavaPlugin {
    public static final String PLUGIN_NAME = "[VoltskiyaMobs]";

    @Override
    public void onEnable() {
        ThylaMain.enable(this);
        BetterSpiderSpwnMain.enable(this);
        AraneoMain.enable(this);
        System.out.println(PLUGIN_NAME + " enabled");
    }
}
