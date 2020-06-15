package com.voltskiya.core.mobs.scan;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SoftScan {
    private static JavaPlugin plugin;

    private static File hardScanFolder;
    private static File mobLocationsFolder;

    private static Map<String, Integer> mobCount = new ConcurrentHashMap<>();

    public static void initialize(JavaPlugin pl, File hardScan, File mobLocations) {
        plugin = pl;
        hardScanFolder = hardScan;
        mobLocationsFolder = mobLocations;
    }

    public static void scan() {
        String[] mobCountPaths = hardScanFolder.list();
        if (mobCountPaths == null) {
            plugin.getLogger().log(Level.SEVERE, "Hardscan needs to finish before a softscan");
            return;
        }
        int i = 0;
        for (; i < mobCountPaths.length; i++) {
            BufferedReader mobCountReader;
            try {
                mobCountReader = new BufferedReader(new FileReader(new File(hardScanFolder, mobCountPaths[i])));
            } catch (FileNotFoundException e) {
                // this probably never happened
                continue;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                JsonArray mobCounts = new Gson().fromJson(mobCountReader, JsonArray.class);
                addFileContents(mobCounts);
                try {
                    mobCountReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, i);

            System.out.println(i + "/" + mobCountPaths.length);
        }
        findLocations();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> System.out.println(Arrays.toString(mobCount.entrySet().toArray())), i);
    }

    private static void findLocations() {
        ArrayList<Integer> locationsIndex = new ArrayList<>();
    }

    private static void addFileContents(JsonArray mobCountIndividual) {
        for (JsonElement insideArray : mobCountIndividual) {
            for (JsonElement insideMap : insideArray.getAsJsonArray()) {
                for (Map.Entry<String, JsonElement> entry : insideMap.getAsJsonObject().entrySet()) {
                    mobCount.put(entry.getKey(), mobCount.getOrDefault(entry.getKey(), 0) + entry.getValue().getAsInt());
                }
            }
        }
    }


}
