package com.voltskiya.core.mobs.scan;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.voltskiya.core.Voltskiya;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class RefactorHardScan {
    private static JavaPlugin plugin;
    private static final Gson gson = new Gson();

    private static File hardScanFolder;
    private static File hardScanRefactoredFolder;

    private static Map<String, Integer> mobCount = new ConcurrentHashMap<>();
    private static Map<String, List<Integer>> chunkToMobCount = new ConcurrentHashMap<>();

    public static void initialize(JavaPlugin pl, File hardScan, File hardScanRefactored) {
        plugin = pl;
        hardScanFolder = hardScan;
        hardScanRefactoredFolder = hardScanRefactored;
    }

    public static void scan() {
        String[] mobCountPaths = hardScanFolder.list();
        if (mobCountPaths == null) {
            plugin.getLogger().log(Level.SEVERE, "Hardscan needs to finish before refactoring Hardscan!");
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
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                JsonArray mobCounts = new Gson().fromJson(mobCountReader, JsonArray.class);
                addFileContents(mobCounts);
                try {
                    mobCountReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(finalI + "/" + mobCountPaths.length);
            }, i);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> System.out.println(Arrays.toString(mobCount.entrySet().toArray())), i);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, RefactorHardScan::write, i);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            mobCount = null;
            chunkToMobCount = null;
        }, i + 1);

    }

    private static void write() {
        final String[] mobPaths = hardScanRefactoredFolder.list();
        if (mobPaths != null) {
            // clean the folder (just in case people changed the name of a mob and there is a lingering file)
            for (String mobPath : mobPaths) {
                File mobFile = new File(hardScanRefactoredFolder, mobPath);
                if (mobFile.exists()) mobFile.delete();
            }
            // write the really empty file for this mob
            for (Map.Entry<String, List<Integer>> mob : chunkToMobCount.entrySet()) {
                File mobFile = new File(hardScanRefactoredFolder, mob.getKey() + ".json");
                try {
                    if (!mobFile.exists()) mobFile.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(mobFile));
                    JsonArray jsonSingleMobCount = new JsonArray();
                    for (Integer i : mob.getValue())
                        jsonSingleMobCount.add(i);
                    writer.write(gson.toJson(jsonSingleMobCount));
                    writer.close();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, String.format("The file for the hardScanRefctored %s could not be written to correctly", mob.getKey()));
                }
            }
        } else {
            plugin.getLogger().log(Level.SEVERE, String.format("The file %s should be a folder", hardScanFolder.toString()));
        }
    }

    private static void addFileContents(JsonArray mobCountIndividual) {
        for (JsonElement insideArray : mobCountIndividual) {
            for (JsonElement insideMap : insideArray.getAsJsonArray()) {
                for (Map.Entry<String, JsonElement> entry : insideMap.getAsJsonObject().entrySet()) {
                    final int mobCountSingle = entry.getValue().getAsInt();
                    final String mob = entry.getKey();
                    mobCount.put(mob, mobCount.getOrDefault(mob, 0) + mobCountSingle);
                    List<Integer> chunkToSingleMobCount = chunkToMobCount.computeIfAbsent(mob, k -> Collections.synchronizedList(new ArrayList<>()));
                    chunkToSingleMobCount.add(mobCountSingle);
                }
            }
        }
    }
}
