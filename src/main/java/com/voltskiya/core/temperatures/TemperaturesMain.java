package com.voltskiya.core.temperatures;


import com.voltskiya.core.temperatures.constants.NavigateArmor;
import com.voltskiya.core.temperatures.constants.NavigateBlocks;
import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import com.voltskiya.core.temperatures.constants.NavigatePotions;
import com.voltskiya.core.temperatures.constants.biomes.NavigateBiomes;
import com.voltskiya.core.temperatures.constants.results.NavigateResults;
import org.bukkit.plugin.java.JavaPlugin;

public class TemperaturesMain extends JavaPlugin {
    @Override
    public void onEnable() {
        NavigatePlayers.initialize(this);
        NavigateBiomes.initialize(this);
        NavigateBlocks.initialize(this);
        NavigateArmor.initialize(this);
        NavigatePotions.initialize(this);
        NavigateResults.initialize(this);
        ActionBar.initialize(this);
        IsWetCheck.initialize(this);
        new PlayerListener(this);
        new TemperaturesReload(this);
        new TemperaturesDestroyCommand(this);
        System.out.println(NavigatePlayers.PLUGIN_NAME + " enabled");
    }
}
