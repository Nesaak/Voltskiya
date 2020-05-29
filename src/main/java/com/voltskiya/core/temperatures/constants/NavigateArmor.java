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

public class NavigateArmor {
    private static final String YML_COLD_ARMOR = "coldArmorModifiers";
    private static final String YML_HEAT_ARMOR = "heatArmorModifiers";
    private static final String ARMOR_PATH = "armorModifiers.yml";
    public static Map<Material, Double> heatArmorToModifier;
    public static Map<Material, Double> coldArmorToModifier;

    public static void initialize(JavaPlugin plugin) {
        heatArmorToModifier = new HashMap<>();
        coldArmorToModifier = new HashMap<>();
        File file = new File(plugin.getDataFolder().toString());
        if (!file.exists()) {
            if (!file.mkdir()) {
                System.err.println(String.format("%s There was an error making the root temperatures folder", NavigatePlayers.PLUGIN_NAME));
                return;
            }
        }

        file = new File(plugin.getDataFolder() + File.separator + ARMOR_PATH);
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, ARMOR_PATH));
                    return;
                }

            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, ARMOR_PATH));
                return;
            }
        }
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);


        ConfigurationSection config = configOrig.getConfigurationSection(YML_HEAT_ARMOR);
        if (config == null) {
            config = configOrig.createSection(YML_HEAT_ARMOR);
            try {
                configOrig.save(file);
            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the %s section in the file \"%s\"", NavigatePlayers.PLUGIN_NAME, YML_HEAT_ARMOR, ARMOR_PATH));
                return;
            }
        }
        Set<String> keys = config.getKeys(false);
        for (String armorType : keys) {
            heatArmorToModifier.put(Material.getMaterial(armorType), config.getDouble(armorType));
        }
        config = configOrig.getConfigurationSection(YML_COLD_ARMOR);
        if (config == null) {
            config = configOrig.createSection(YML_COLD_ARMOR);
            try {
                configOrig.save(file);
            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the %s section in the file \"%s\"", NavigatePlayers.PLUGIN_NAME, YML_COLD_ARMOR, ARMOR_PATH));
                return;
            }
        }
        keys = config.getKeys(false);
        for (String armorType : keys) {
            coldArmorToModifier.put(Material.getMaterial(armorType), config.getDouble(armorType));
        }
    }
}
