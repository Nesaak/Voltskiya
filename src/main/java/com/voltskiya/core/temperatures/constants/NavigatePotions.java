package com.voltskiya.core.temperatures.constants;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NavigatePotions {
    private static final String YML_POTIONS_HEAT = "potionEffectsHeat";
    private static final String YML_POTIONS_COLD = "potionEffectsCold";
    private static final String POTIONS_PATH = "potionEffects.yml";
    public static Map<PotionEffectType, Double> potionToModifierHeat;
    public static Map<PotionEffectType, Double> potionToModifierCold;

    public static void initialize(JavaPlugin plugin) {
        potionToModifierHeat = new HashMap<>();
        potionToModifierCold = new HashMap<>();
        File file = new File(plugin.getDataFolder().toString());
        if (!file.exists()) {
            if (!file.mkdir()) {
                System.err.println(String.format("%s There was an error making the root temperatures folder", NavigatePlayers.PLUGIN_NAME));
                return;
            }
        }

        file = new File(plugin.getDataFolder() + File.separator + POTIONS_PATH);
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, POTIONS_PATH));
                    return;
                }

            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, POTIONS_PATH));
                return;
            }
        }
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = configOrig.getConfigurationSection(YML_POTIONS_HEAT);
        if (config == null) {
            config = configOrig.createSection(YML_POTIONS_HEAT);
            try {
                configOrig.save(file);
            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the %s section in the file \"%s\"", NavigatePlayers.PLUGIN_NAME, YML_POTIONS_HEAT, POTIONS_PATH));
                return;
            }
        }
        Set<String> keys = config.getKeys(false);
        for (String potionType : keys) {
            potionToModifierHeat.put(PotionEffectType.getByName(potionType), config.getDouble(potionType));
        }
        config = configOrig.getConfigurationSection(YML_POTIONS_COLD);
        if (config == null) {
            config = configOrig.createSection(YML_POTIONS_COLD);
            try {
                configOrig.save(file);
            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the %s section in the file \"%s\"", NavigatePlayers.PLUGIN_NAME, YML_POTIONS_COLD, POTIONS_PATH));
                return;
            }
        }
        keys = config.getKeys(false);
        for (String potionType : keys) {
            potionToModifierCold.put(PotionEffectType.getByName(potionType), config.getDouble(potionType));
        }
    }
}
