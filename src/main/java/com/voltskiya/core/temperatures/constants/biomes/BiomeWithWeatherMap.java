package com.voltskiya.core.temperatures.constants.biomes;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class BiomeWithWeatherMap {
    private static final String WEATHER = "weather";
    private static final String NO_WEATHER = "clear";
    public final HashMap<Boolean, BiomesMap> biomeModifiers;

    public BiomeWithWeatherMap(ConfigurationSection config) {
        biomeModifiers = new HashMap<>();
        if (config == null)
            return;
        biomeModifiers.put(true, new BiomesMap(config.getConfigurationSection(WEATHER)));
        biomeModifiers.put(false, new BiomesMap(config.getConfigurationSection(NO_WEATHER)));
    }
}
