package com.voltskiya.core.game.rotting;

import com.google.common.collect.ImmutableSet;
import com.voltskiya.core.game.GameTagsNavigate;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RottingMain {
    public static Map<String, Long> rottingChart = new HashMap<>();
    public static Map<String, String> rotIntoChart = new HashMap<>();
    public static NamespacedKey lastCheckedKey;
    public static NamespacedKey rottingCountdownKey;
    public static NamespacedKey cooler;
    public static NamespacedKey vanilla;
    private static JavaPlugin plugin;
    private static File dataFolder;
    public static ImmutableSet<InventoryType> furanceTypes = ImmutableSet.of(
            InventoryType.FURNACE,
            InventoryType.BLAST_FURNACE,
            InventoryType.SMOKER
    );

    public static void enable(JavaPlugin pl, File folder) {
        plugin = pl;
        dataFolder = folder;
        getChart();
        lastCheckedKey = new NamespacedKey(pl, GameTagsNavigate.RottingTagsNavigate.LAST_CHECKED);
        rottingCountdownKey = new NamespacedKey(pl, GameTagsNavigate.RottingTagsNavigate.ROTTING_COUNTDOWN);
        cooler = new NamespacedKey(pl, GameTagsNavigate.RottingTagsNavigate.COOLER);
        vanilla = new NamespacedKey(pl, GameTagsNavigate.RottingTagsNavigate.VANILLA);
        new RottingListener(pl);
        new RottingMerge(pl);
        new RenameListener(pl);
        new RottingSmeltListener(pl);
        new CoolerPlaceListener(pl);

//        new SlotFinderListener(pl);

        System.out.println("[Voltskiya] [Game] [Rotting] enabled");
    }

    private static void getChart() {
        File file = new File(String.format("%s%s%s", dataFolder, File.separator, GameTagsNavigate.RottingTagsNavigate.ROTTING_DIR));
        if (!file.exists())
            if (!file.mkdir()) {
                System.err.println("[Voltskiya] [Game] [Rotting] Could not make the directory for rotting");
                return;
            }
        file = new File(String.format("%s%s%s%s%s%s", dataFolder, File.separator, GameTagsNavigate.RottingTagsNavigate.ROTTING_DIR, File.separator, GameTagsNavigate.RottingTagsNavigate.ROTTING_CHART, ".yml"));
        if (!file.exists())
            try {
                if (!file.createNewFile()) {
                    System.err.println("[Voltskiya] [Game] [Rotting] Could not make the rotting chart file");
                    return;
                }
            } catch (IOException e) {
                System.err.println("[Voltskiya] [Game] [Rotting] Could not make the rotting chart file");
                return;
            }
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = configOrig.getConfigurationSection(GameTagsNavigate.RottingTagsNavigate.YML_CHART);
        if (config == null) {
            config = configOrig.createSection(GameTagsNavigate.RottingTagsNavigate.YML_CHART);
            try {
                configOrig.save(file);
            } catch (IOException ignored) {
            }
        }
        for (String key : config.getKeys(false)) {
            long decayRate = config.getLong(key);
            rottingChart.put(key, decayRate);
        }
        file = new File(String.format("%s%s%s%s%s%s", dataFolder, File.separator, GameTagsNavigate.RottingTagsNavigate.ROTTING_DIR, File.separator, GameTagsNavigate.RottingTagsNavigate.ROT_INTO_CHART, ".yml"));
        if (!file.exists())
            try {
                if (!file.createNewFile()) {
                    System.err.println("[Voltskiya] [Game] [Rotting] Could not make the rot-into chart file");
                    return;
                }
            } catch (IOException e) {
                System.err.println("[Voltskiya] [Game] [Rotting] Could not make the rot-into chart file");
                return;
            }
        configOrig = YamlConfiguration.loadConfiguration(file);
        config = configOrig.getConfigurationSection(GameTagsNavigate.RottingTagsNavigate.YML_CHART);
        if (config == null) {
            config = configOrig.createSection(GameTagsNavigate.RottingTagsNavigate.YML_CHART);
            try {
                configOrig.save(file);
            } catch (IOException ignored) {
            }
        }
        for (String key : config.getKeys(false)) {
            String decayResult = config.getString(key);
            rotIntoChart.put(key, decayResult);
        }
    }
}
