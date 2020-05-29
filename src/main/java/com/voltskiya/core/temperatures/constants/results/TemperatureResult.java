package com.voltskiya.core.temperatures.constants.results;

import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemperatureResult {
    public final Map<PotionEffectType, PotionInfo> results = new HashMap<>();

    public TemperatureResult(ConfigurationSection config) {
        Set<String> potionTypes = config.getKeys(false);
        for (String potionType : potionTypes) {
            if (potionType.equals("messageCooling") || potionType.equals("messageWarming"))
                continue;
            ConfigurationSection subConfig = config.getConfigurationSection(potionType);
            if (subConfig == null)
                continue;
            PotionEffectType type = PotionEffectType.getByName(potionType);
            if (type == null) {
                System.err.println(NavigatePlayers.PLUGIN_NAME + " could not get result potion effect: " + potionType);
                continue;
            }
            results.put(type, new PotionInfo(subConfig));
        }
    }
}

