package com.voltskiya.core.custom_mobs;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.custom_mobs.araneo.AraneoMain;
import com.voltskiya.core.custom_mobs.better_spider_spawn.BetterSpiderSpawnMain;
import com.voltskiya.core.custom_mobs.thyla.ThylaMain;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CustomMobsModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        File dataFolder = getDataFolder();
        JavaPlugin plugin = Voltskiya.get();

        ThylaMain.enable(plugin, dataFolder);
        BetterSpiderSpawnMain.enable(plugin);
        AraneoMain.enable(plugin);
    }

    @Override
    public String getName() {
        return "CustomMobs";
    }
}
