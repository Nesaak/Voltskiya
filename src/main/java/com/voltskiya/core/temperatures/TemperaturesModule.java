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

public class TemperaturesModule extends VoltskiyaModule {
    public void enabled() {
        JavaPlugin plugin = Voltskiya.get();
        NavigatePlayers.initialize(plugin);
        NavigateBiomes.initialize(plugin);
        NavigateBlocks.initialize(plugin);
        NavigateArmor.initialize(plugin);
        NavigatePotions.initialize(plugin);
        NavigateResults.initialize(plugin);
        ActionBar.initialize(plugin);
        IsWetCheck.initialize(plugin);
        new PlayerListener(plugin);
        Voltskiya.get().getCommandManager().registerCommand(new TemperatureCommands());
    }

    @Override
    public String getName() {
        return "Temperatures";
    }
}
