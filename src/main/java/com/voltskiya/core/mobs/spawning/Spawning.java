package com.voltskiya.core.mobs.spawning;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.mobs.MobsModule;
import com.voltskiya.core.mobs.checks.CheckSpawnable;
import com.voltskiya.core.mobs.mobs.Mobs;
import com.voltskiya.core.mobs.scanning.CheapLocation;
import com.voltskiya.core.mobs.scanning.SpawningEnvironment;
import com.voltskiya.core.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
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
        }, 20 * 1 /*todo 10 arbitrary seconds*/, 20 * 30 * 60);
    }

    public static void correctSpawnedNumbers() throws IOException {
        if (!Bukkit.getOnlinePlayers().isEmpty())
            return; // don't refresh when there are online players
        HashMap<String, Integer> mobCount = new HashMap<>();
        for (String mob : Mobs.getMobSet()) {
            mobCount.put(mob, 0);
        }
        for (File fileChunkToCount : registeredMobsFolder.listFiles()) {
            // we have a record of this chunk in this world
            BufferedReader read = new BufferedReader(new FileReader(fileChunkToCount));
            JsonArray mobsToLoad = gson.fromJson(read, JsonArray.class);
            for (JsonElement mobToLoadJson : mobsToLoad) {
                SimpleDiskMob mobToLoad = gson.fromJson(mobToLoadJson, SimpleDiskMob.class);
                // add the mob
                mobCount.compute(mobToLoad.name, (k, v) -> v == null ? 1 : v + 1);
            }
        }
        @NotNull Collection<Pair<Integer, Integer>> chunksToNotSpawn = PlayerWatching.getChunksLoaded(Bukkit.getOnlinePlayers());
        for (Map.Entry<String, Integer> entry : mobCount.entrySet()) {
            int shouldBeCount = Mobs.getMobProperCount(entry.getKey());
            entry.setValue(shouldBeCount - (int) (entry.getValue() / Mobs.getMobMean(entry.getKey())));
        }
        List<SimpleDiskMob> mobsToSave = new ArrayList<>();
        // register a bunch of mobs
        for (Map.Entry<String, Integer> mobToNumSpawning : mobCount.entrySet()) {
            if (mobToNumSpawning.getValue() > 0) {
                // we should spawn 'value' number of mobs of type 'key'
                mobsToSave.addAll(spawnMobs(mobToNumSpawning.getKey(), mobToNumSpawning.getValue(), chunksToNotSpawn));
            }
        }
        save(mobsToSave);


    }

    private static List<SimpleDiskMob> spawnMobs(String mobName, Integer numToSpawn, @NotNull Collection<Pair<Integer, Integer>> chunksToNotSpawn) throws IOException {
        List<SimpleDiskMob> mobsToSave = new ArrayList<>();

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
                    File chunkFile = new File(registeredMobsFolder, String.format("%s,%d,%d.json", MobsModule.worldToMoniter, location.x / 16, location.z / 16));
                    JsonArray mobsInChunk;
                    if (chunkFile.exists()) {
                        final BufferedReader reader = new BufferedReader(new FileReader(chunkFile));
                        mobsInChunk = gson.fromJson(reader, JsonArray.class);
                        reader.close();
                    } else {
                        mobsInChunk = new JsonArray();
                    }
                    SimpleDiskMob newMob = new SimpleDiskMob(mobName, location.x, location.y, location.z);
                    if (mobsToSave.contains(newMob))
                        continue; // we already have a mob here

                    SimpleDiskMob newChunkMob = new SimpleDiskMob(mobName, location.x % 16, location.y, location.z % 16);
                    boolean isGoodSpawn = true;
                    for (JsonElement element : mobsInChunk) {
                        SimpleDiskMob mob = gson.fromJson(element, SimpleDiskMob.class);
                        if (mob.equals(newChunkMob)) {
                            isGoodSpawn = false;
                            break;
                        }
                    }
                    if (!isGoodSpawn) {
                        continue;
                    }

                    // we have location written in 'newMob'
                    // we should double check that this location is good
                    // we will return a list of mobs that need to be saved a bit later
                    mobsToSave.add(newMob);
                } else {
                    continue;
                }
                numToSpawn--;
            }
        } else {
            plugin.getLogger().log(Level.SEVERE, String.format("Could not spawn any mobs of type '%s'", mobName));
        }
        return mobsToSave;
    }

    private static void save(List<SimpleDiskMob> mobsToSave) {
        // save

        Map<Pair<Integer, Integer>, List<SimpleDiskMob>> chunkToMobsToSave = new HashMap<>();
        for (SimpleDiskMob mobToSave : mobsToSave) {
            int chunkX = mobToSave.x / 16;
            int chunkZ = mobToSave.z / 16;
            Pair<Integer, Integer> chunkCoords = new Pair<>(chunkX, chunkZ);
            List<SimpleDiskMob> mobsInThisChunk = chunkToMobsToSave.computeIfAbsent(chunkCoords, k -> new ArrayList<>());
            mobsInThisChunk.add(mobToSave);
        }

        @NotNull World world = Objects.requireNonNull(Bukkit.getWorld(MobsModule.worldToMoniter));
        int counter = 1;
        // we now have a list of mobs for every chunk that needs to be loaded
        for (Map.Entry<Pair<Integer, Integer>, List<SimpleDiskMob>> chunkCoordMobs : chunkToMobsToSave.entrySet()) {
            final Pair<Integer, Integer> coords = chunkCoordMobs.getKey();
            final List<SimpleDiskMob> chunkMobs = chunkCoordMobs.getValue();

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                List<SimpleDiskMob> mobsToSaveThisChunk = new ArrayList<>(chunkMobs.size());
                for (SimpleDiskMob mobToSpawn : chunkMobs) {
                    int mobSize = 2; // todo make this dynamic
                    int mobsToSpawn = Mobs.getMobCountInstance(mobToSpawn.name);
                    int x = mobToSpawn.x;
                    int y = mobToSpawn.y;
                    int z = mobToSpawn.z;
                    int yCounter = 0;
                    int mobHeight = Math.min(255, mobSize + y);
                    boolean spawnNearby = false;
                    while (yCounter < mobHeight) {
                        if (!world.getBlockAt(x, y + yCounter++, z).getType().isOccluding()) {
                            // this mob can't spawn here
                            spawnNearby = true;
                        }
                    }
                    if (!spawnNearby) {
                        // spawn a mob here
                        mobsToSaveThisChunk.add(mobToSpawn);
                        mobsToSpawn--;
                    }
                    if (mobsToSpawn != 0) {
                        // spawn more mobs nearby
                        List<CheapLocation> spawnableLocations = new ArrayList<>();
                        for (int xi = -5; xi <= 5; xi++) {
                            for (int zi = -5; zi <= 5; zi++) {
                                // start at the top and look down
                                int aboveHeight = 0;
                                int upper = 255 - y;
                                for (int yi = 1; yi < upper; yi++) {
                                    if (world.getBlockAt(x + xi, y + yi, z + zi).getType().isOccluding()) {
                                        break;
                                    } else {
                                        aboveHeight++;
                                    }
                                }
                                for (int yi = 5; yi >= -5; yi--) {
                                    final int currentX = x + xi;
                                    final int currentY = y + yi;
                                    final int currentZ = z + zi;
                                    Block block = world.getBlockAt(currentX, currentY, currentZ);
                                    if (block.getType().isOccluding()) {
                                        if (CheckSpawnable.isSpawnable(mobToSpawn.name, new SpawningEnvironment(block.getBiome(), block.getType(), currentY, aboveHeight))) {
                                            spawnableLocations.add(new CheapLocation(currentX, currentY, currentZ));
                                        }
                                        aboveHeight = 0;
                                    } else {
                                        aboveHeight++;
                                    }
                                }
                            }
                        }
                        if (spawnableLocations.size() < mobsToSpawn) {
                            for (int xi = -10; xi <= 10; xi++) {
                                for (int zi = -10; zi <= 10; zi++) {
                                    // start at the top and look down
                                    int aboveHeight = 1000;
                                    for (int yi = 10; yi >= -10; yi--) {
                                        final int currentX = x + xi;
                                        final int currentY = y + yi;
                                        final int currentZ = z + zi;
                                        Block block = world.getBlockAt(currentX, currentY, currentZ);
                                        if (block.getType().isOccluding()) {
                                            if (!(xi >= -5 && xi <= 5 && zi >= -5 && zi <= 5 && yi == 5)) { // we haven't checked this block
                                                if (CheckSpawnable.isSpawnable(mobToSpawn.name, new SpawningEnvironment(block.getBiome(), block.getType(), currentY, aboveHeight))) {
                                                    spawnableLocations.add(new CheapLocation(currentX, currentY, currentZ));
                                                }
                                            }
                                            aboveHeight = 0;
                                        } else {
                                            aboveHeight++;
                                        }
                                    }
                                }
                            }
                        }
                        if (!spawnableLocations.isEmpty())
                            while (mobsToSpawn != 0) {
                                // shuffle the spawnableLocations and pick some locations to spawn these mobs
                                Collections.shuffle(spawnableLocations);
                                for (CheapLocation spawnableLocation : spawnableLocations) {
                                    mobsToSaveThisChunk.add(new SimpleDiskMob(mobToSpawn.name, spawnableLocation));
                                    if (--mobsToSpawn == 0) break;
                                }
                            }
                    }
                }

                // this saves mobs in chunks they don't live in, but they're close enough that it doesn't matter
                File chunkFile = new File(registeredMobsFolder, String.format("%s,%d,%d.json", MobsModule.worldToMoniter, coords.getKey(), coords.getValue()));
                int chunkSingleX = coords.getKey() * 16;
                int chunkSingleZ = coords.getValue() * 16;
                JsonArray mobsInChunk = new JsonArray();
                for (SimpleDiskMob mob : mobsToSaveThisChunk) {
                    mob = new SimpleDiskMob(mob.name, mob.x - chunkSingleX, mob.y + 1, mob.z - chunkSingleZ);
                    mobsInChunk.add(gson.toJsonTree(mob));
                }
                try {
                    if (!chunkFile.exists()) chunkFile.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFile));
                    gson.toJson(mobsInChunk, writer);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, counter++);
        }

    }

}
