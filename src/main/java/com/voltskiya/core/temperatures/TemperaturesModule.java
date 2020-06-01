package com.voltskiya.core.temperatures;


import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.temperatures.constants.NavigateArmor;
import com.voltskiya.core.temperatures.constants.NavigateBlocks;
import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import com.voltskiya.core.temperatures.constants.NavigatePotions;
import com.voltskiya.core.temperatures.constants.biomes.NavigateBiomes;
import com.voltskiya.core.temperatures.constants.results.NavigateResults;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class TemperaturesModule extends VoltskiyaModule {
    public void enabled() {
        JavaPlugin plugin = Voltskiya.get();
        final File dataFolder = getDataFolder();
        NavigatePlayers.initialize(plugin, dataFolder);
        NavigateBiomes.initialize(dataFolder);
        NavigateBlocks.initialize(dataFolder);
        NavigateArmor.initialize(dataFolder);
        NavigatePotions.initialize(dataFolder);
        NavigateResults.initialize(dataFolder);
        ActionBar.initialize(plugin);
        IsWetCheck.initialize(plugin);
        new PlayerListener(plugin);
        Voltskiya.get().getCommandManager().registerCommand(new TemperatureCommands(dataFolder));
    }

    @Override
    public String getName() {
        return "Temperatures";
    }
}
