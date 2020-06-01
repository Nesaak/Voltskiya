package com.voltskiya.core.temperatures.constants.biomes;

import com.voltskiya.core.temperatures.constants.TimeOfDay;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Set;

public class BiomesMap {
    public final HashMap<TimeOfDay, Double> biomeModifiers;

    public BiomesMap(ConfigurationSection config) {
        biomeModifiers = new HashMap<>();
        if (config == null)
            return;
        Set<String> keys = config.getKeys(false);
        for (String time : keys) {
            biomeModifiers.put(TimeOfDay.valueOf(time), config.getDouble(time));
        }
    }
}
