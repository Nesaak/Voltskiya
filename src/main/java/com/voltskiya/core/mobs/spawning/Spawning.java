package com.voltskiya.core.mobs.spawning;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.mobs.mobs.Mobs;
import com.voltskiya.core.mobs.scanning.CheapLocation;
import com.voltskiya.core.utils.Pair;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class Spawning {

    private static Voltskiya plugin;
    private static File registeredMobsFolder;
    private static File mobLocationsFolder;
    private static final Gson gson = new Gson();

    public static void initialize(Voltskiya pl, File mobLocations, File registeredMobs) {
        plugin = pl;
        registeredMobsFolder = registeredMobs;
        mobLocationsFolder = mobLocations;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                correctSpawnedNumbers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 20 * 10 /*10 arbitrary seconds*/, 20 * 30 * 60);
    }

    public static void correctSpawnedNumbers() throws IOException {
        if (!Bukkit.getOnlinePlayers().isEmpty())
            return; // don't refresh when there are online players
        HashMap<String, Integer> mobCount = new HashMap<>();
        for (String mob : Mobs.mobSet) {
            mobCount.put(mob, 0);
        }
        for (File fileChunkToCount : registeredMobsFolder.listFiles()) {
            // we have a record of this chunk in this world
            BufferedReader read = new BufferedReader(new FileReader(fileChunkToCount));
            JsonArray mobsToLoad = gson.fromJson(read, JsonArray.class);
            for (JsonElement mobToLoadJson : mobsToLoad) {
                SimpleDiskMob mobToLoad = gson.fromJson(mobToLoadJson, SimpleDiskMob.class);
                // add the mob
                mobCount.compute(mobToLoad.name, (k, v) -> {
                    if (v == null) return 1;
                    return v + 1;
                });
            }
        }
        @NotNull Collection<Pair<Integer, Integer>> chunksToNotSpawn = PlayerWatching.getChunksLoaded(Bukkit.getOnlinePlayers());
        for (Map.Entry<String, Integer> entry : mobCount.entrySet()) {
            int shouldBeCount = Mobs.getMobProperCount(entry.getKey());
            entry.setValue(shouldBeCount - entry.getValue());
        }
        // register a bunch of mobs
        for (Map.Entry<String, Integer> mobToNumSpawning : mobCount.entrySet()) {
            if (mobToNumSpawning.getValue() > 0) {
                // we should spawn 'value' number of mobs of type 'key'
                spawnMobs(mobToNumSpawning.getKey(), mobToNumSpawning.getValue(), chunksToNotSpawn);

            }
        }

    }

    private static void spawnMobs(String mobName, Integer numToSpawn, @NotNull Collection<Pair<Integer, Integer>> chunksToNotSpawn) throws IOException {
        File mobLocationChoicesFile = new File(mobLocationsFolder, mobName + ".json");
        if (mobLocationChoicesFile.exists()) {
            JsonArray jsonLocations = gson.fromJson(new BufferedReader(new FileReader(mobLocationChoicesFile)), JsonArray.class);
            List<CheapLocation> locations = new ArrayList<>(jsonLocations.size());
            for (JsonElement jsonLocation : jsonLocations)
                locations.add(gson.fromJson(jsonLocation, CheapLocation.class));
            Collections.shuffle(locations);

            while (numToSpawn != 0 && !locations.isEmpty()) {
                CheapLocation location = locations.remove(0);
                // spawn a mob here
                if (!chunksToNotSpawn.contains(new Pair<>(location.x / 16, location.z / 16))) {
                    File chunkFile = new File(registeredMobsFolder, String.format("%s,%d,%d.json", "world", location.x / 16, location.z / 16));
                    JsonArray mobsInChunk;
                    if (chunkFile.exists()) {
                        final BufferedReader reader = new BufferedReader(new FileReader(chunkFile));
                        mobsInChunk = gson.fromJson(reader, JsonArray.class);
                        reader.close();
                    } else {
                        mobsInChunk = new JsonArray();
                        chunkFile.createNewFile();
                    }
                    SimpleDiskMob newMob = new SimpleDiskMob(mobName, location.x % 16, location.y, location.z % 16);
                    boolean isGoodSpawn = true;
                    for (JsonElement element : mobsInChunk) {
                        SimpleDiskMob mob = gson.fromJson(element, SimpleDiskMob.class);
                        if (mob.equals(newMob)) {
                            isGoodSpawn = false;
                            break;
                        }
                    }
                    if (!isGoodSpawn) {
                        continue;
                    }

                    // save
                    mobsInChunk.add(gson.toJsonTree(newMob));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFile));
                    gson.toJson(mobsInChunk, writer);
                    writer.close();
                } else {
                    continue;
                }
                numToSpawn--;
            }
        } else {
            plugin.getLogger().log(Level.SEVERE, String.format("Could not spawn any mobs of type '%s'", mobName));
        }
    }
}
