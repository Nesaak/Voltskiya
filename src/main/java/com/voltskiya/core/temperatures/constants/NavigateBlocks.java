package com.voltskiya.core.temperatures.constants;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NavigateBlocks {
    private static final String YML_BLOCKS = "blockTemps";
    private static final String BLOCKS_PATH = "blockTemperatures.yml";
    public static Map<Material, Double> blockToTemp;

    public static void initialize(File dataFolder) {
        blockToTemp = new HashMap<>();
        File file = new File(dataFolder + File.separator + BLOCKS_PATH);
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, BLOCKS_PATH));
                    return;
                }

            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, BLOCKS_PATH));
                return;
            }
        }
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = configOrig.getConfigurationSection(YML_BLOCKS);
        if (config == null) {
            config = configOrig.createSection(YML_BLOCKS);
            try {
                configOrig.save(file);
            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the %s section in the file \"%s\"", NavigatePlayers.PLUGIN_NAME, YML_BLOCKS, BLOCKS_PATH));
                return;
            }
        }
        Set<String> keys = config.getKeys(false);
        for (String blockType : keys) {
            blockToTemp.put(Material.getMaterial(blockType), config.getDouble(blockType));
        }
    }
}
