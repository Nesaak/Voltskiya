package com.voltskiya.core.mobs.scanning;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static com.voltskiya.core.mobs.MobsCommand.CHUNK_SCAN_INCREMENT;

public class RefactorHardScan {
    private static JavaPlugin plugin;
    private static final Gson gson = new Gson();

    private static File hardScanFolder;
    private static File hardScanRefactoredFolder;

    private static Map<String, List<Integer>> chunkToMobCount = new ConcurrentHashMap<>();
    private static Collection<Integer> singleMobCountEmpty;

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
        singleMobCountEmpty = Collections.nCopies(mobCountPaths.length * CHUNK_SCAN_INCREMENT * CHUNK_SCAN_INCREMENT, 0);
        int i = 0;
        for (; i < mobCountPaths.length; i++) {
            final BufferedReader mobCountReader;
            final File mobCountFile = new File(hardScanFolder, mobCountPaths[i]);
            try {
                mobCountReader = new BufferedReader(new FileReader(mobCountFile));
            } catch (FileNotFoundException e) {
                // this probably never happened
                continue;
            }
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                JsonArray mobCounts = new Gson().fromJson(mobCountReader, JsonArray.class);
                addFileContents(mobCounts, finalI);
                try {
                    mobCountReader.close();
//        todo            mobCountFile.delete(); // get rid of the temporary file
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(finalI + "/" + mobCountPaths.length);
            }, i);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, RefactorHardScan::write, i);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            chunkToMobCount = new ConcurrentHashMap<>();
        }, i + 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                SoftScan.scan();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, i + 2);
    }

    private static void write() {
        final String[] mobPaths = hardScanRefactoredFolder.list();
        if (mobPaths != null) {
            // clean the folder (just in case people changed the name of a mob and there is a lingering file)
            for (String mobPath : mobPaths) {
                File mobFile = new File(hardScanRefactoredFolder, mobPath);
                if (mobFile.exists()) mobFile.delete();
            }
            // write the file for this mob
            for (Map.Entry<String, List<Integer>> mob : chunkToMobCount.entrySet()) {
                File mobFile = new File(hardScanRefactoredFolder, mob.getKey() + ".json");
                try {
                    if (!mobFile.exists()) mobFile.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(mobFile));
                    JsonArray jsonSingleMobCount = new JsonArray();
                    for (Integer i : mob.getValue())
                        jsonSingleMobCount.add(i);
                    gson.toJson(jsonSingleMobCount, writer);
                    writer.close();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, String.format("The file for the hardScanRefactored %s could not be written to correctly", mob.getKey()));
                }
            }
        } else {
            plugin.getLogger().log(Level.SEVERE, String.format("The file %s should be a folder", hardScanFolder.toString()));
        }
    }

    private static void addFileContents(JsonArray mobCountIndividual, int majorIndex) {
        majorIndex *= CHUNK_SCAN_INCREMENT * CHUNK_SCAN_INCREMENT;
        int subIndex = 0;
        for (JsonElement insideArray : mobCountIndividual) {
            for (JsonElement insideMap : insideArray.getAsJsonArray()) {
                for (Map.Entry<String, JsonElement> entry : insideMap.getAsJsonObject().entrySet()) {
                    final int mobCountSingle = entry.getValue().getAsInt();
                    final String mob = entry.getKey();
                    List<Integer> chunkToSingleMobCount = chunkToMobCount.computeIfAbsent(mob, k -> Collections.synchronizedList(new ArrayList<>(singleMobCountEmpty)));
                    chunkToSingleMobCount.set(subIndex + majorIndex, mobCountSingle);
                }
                subIndex++;
            }
        }
    }
}
