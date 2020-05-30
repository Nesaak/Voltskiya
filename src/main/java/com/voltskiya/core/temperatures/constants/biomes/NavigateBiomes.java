package com.voltskiya.core.temperatures.constants.biomes;

import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class NavigateBiomes {
    private static final String BIOMES_PATH = "biomeTemperatures.yml";
    private static final String YML_BIOMES = "biomes";
    public static HashMap<Biome, BiomeWithWeatherMap> biomeModifiers;

    public static void initialize(File dataFolder) {
        biomeModifiers = new HashMap<>();
        File file = new File(dataFolder + File.separator + BIOMES_PATH);
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, BIOMES_PATH));
                    return;
                }

            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, BIOMES_PATH));
                return;
            }
        }
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = configOrig.getConfigurationSection(YML_BIOMES);
        if (config == null) {
            config = configOrig.createSection(YML_BIOMES);
            try {
                configOrig.save(file);
            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the %s section in the file \"%s\"", NavigatePlayers.PLUGIN_NAME, YML_BIOMES, BIOMES_PATH));
                return;
            }
        }
        Set<String> keys = config.getKeys(false);
        for (String biome : keys) {
            biomeModifiers.put(Biome.valueOf(biome), new BiomeWithWeatherMap(config.getConfigurationSection(biome)));
        }
    }
}
