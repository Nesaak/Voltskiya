package com.voltskiya.core.temperatures.constants.results;

import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NavigateResults {
    private static final String YML_RESULTS = "results";
    private static final String RESULTS_YML = "results.yml";
    public static Map<Integer, TemperatureResult> tempToResult;

    public static void initialize(JavaPlugin plugin) {
        tempToResult = new HashMap<>();
        File file = new File(plugin.getDataFolder().toString());
        if (!file.exists()) {
            if (!file.mkdir()) {
                System.err.println(String.format("%s There was an error making the root temperatures folder", NavigatePlayers.PLUGIN_NAME));
                return;
            }
        }

        file = new File(plugin.getDataFolder() + File.separator + RESULTS_YML);
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, RESULTS_YML));
                    return;
                }

            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, RESULTS_YML));
                return;
            }
        }
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = configOrig.getConfigurationSection(YML_RESULTS);
        if (config == null) {
            config = configOrig.createSection(YML_RESULTS);
            try {
                configOrig.save(file);
            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the %s section in the file \"%s\"", NavigatePlayers.PLUGIN_NAME, YML_RESULTS, RESULTS_YML));
                return;
            }
        }
        Set<String> keys = config.getKeys(false);
        for (String temp : keys) {
            ConfigurationSection subConfig = config.getConfigurationSection(temp);
            if (subConfig == null)
                continue;
            try {
                tempToResult.put(Integer.valueOf(temp), new TemperatureResult(subConfig));
            } catch (NumberFormatException ignored) {
            }

        }
    }
}