package com.voltskiya.core.mobs.scan;

import com.google.gson.*;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.mobs.scan.mechanics.SpawningMechanic;
import com.voltskiya.core.utils.Pair;
import com.voltskiya.core.utils.Sorting;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

import static com.voltskiya.core.mobs.MobsCommand.CHUNK_SCAN_INCREMENT;
import static com.voltskiya.core.mobs.scan.HardScan.SEARCH_DEPTH;
import static com.voltskiya.core.mobs.scan.HardScan.biomeToRules;

public class SoftScan {
    private static final float MOB_PERCENTAGE = 0.001f;
    public static final JsonPrimitive JSON_PRIMITIVE_ZERO = new JsonPrimitive(0);
    private static File mobLocationsTempFolder;
    private static File mobLocationsFolder;
    private static File mobCountsFolder;

    private static JavaPlugin plugin;

    private static final Gson gson = new Gson();
    private static final Random random = new Random();

    public static void initialize(Voltskiya pl, File mobLocations, File mobCounts, File mobLocationsTemp) {
        plugin = pl;
        mobLocationsFolder = mobLocations;
        mobCountsFolder = mobCounts;
        mobLocationsTempFolder = mobLocationsTemp;
    }

    public static void scan() throws IOException {
        String[] mobLocationPaths = mobLocationsFolder.list();
        if (mobLocationPaths != null) {
            // clean the folder (just in case people changed the name of a mob and there is a lingering file)
            for (String mobLocationPath : mobLocationPaths) {
                File mobLocationFile = new File(mobLocationsFolder, mobLocationPath);
                if (mobLocationFile.exists()) mobLocationFile.delete();
            }

            String[] mobCountsPaths = mobCountsFolder.list();
            if (mobCountsPaths == null) {
                plugin.getLogger().log(Level.SEVERE, String.format("The file %s should be a folder", mobCountsFolder.toString()));
                return;
            }
            List<Indexes> mobToIndices = new ArrayList<>();
            for (String mobCountsPath : mobCountsPaths) {
                JsonArray mobCountsArray = gson.fromJson(new BufferedReader(new FileReader(new File(mobCountsFolder, mobCountsPath))), JsonArray.class);

                // I don't think I need a long, unless the map gets bigger and a mob is allowed to spawn on every air space
                // choose locations from the first spawnable location to the last
                int totalMobCount = 0;
                int[] chunkMobCount = new int[mobCountsArray.size()];
                int i = 0;
                for (JsonElement mobCount : mobCountsArray) {
                    final int mobCountInt = mobCount.getAsInt();
                    totalMobCount += mobCountInt;
                    chunkMobCount[i++] = mobCountInt;
                }
                int mobLocationsSize = (int) (MOB_PERCENTAGE * totalMobCount);
                int[] mobLocationChoices = new int[mobLocationsSize];
                for (i = 0; i < mobLocationsSize; i++)
                    mobLocationChoices[i] = random.nextInt(totalMobCount); // choose a random number between the first spawnable location and the last
                Arrays.sort(mobLocationChoices);
                mobToIndices.add(new Indexes(mobCountsPath.substring(0, mobCountsPath.length() - 5), mobLocationChoices, chunkMobCount));
                // we have the list of location indices
            }
            // rescan everything and find the locations
            findLocations(mobToIndices);

        } else {
            plugin.getLogger().log(Level.SEVERE, String.format("The file %s should be a folder", mobLocationsFolder.toString()));
        }
    }

    private static void findLocations(List<Indexes> mobToIndices) throws IOException {
        // this is an index to determine where we currently are going through the indexes for each mob
        World world = Bukkit.getWorld("world"); // todo change this to use a constant determined in a yml for settings
        @NotNull WorldBorder border = world.getWorldBorder();
        @NotNull Location borderCenter = border.getCenter();
        double size = border.getSize();
        short lowerX = (short) ((borderCenter.getX() - size) / 16);
        short lowerZ = (short) ((borderCenter.getZ() - size) / 16);
        short higherX = (short) ((borderCenter.getX() + size) / 16);
        short higherZ = (short) ((borderCenter.getZ() + size) / 16);
        int currentChunkTotalIndex = 0;
        // increment through each chunk grouping (we do this to keep the ordering correct)
        // this should be BigO(N^2) instead of BigO(N^4)
        for (short x = lowerX; x < higherX; x += CHUNK_SCAN_INCREMENT) {
            for (short z = lowerZ; z < higherZ; z += CHUNK_SCAN_INCREMENT) {
                short currentChunkSubIndex = 0;
                JsonObject chunksToMob = new JsonObject();
                for (byte xi = 0; xi < CHUNK_SCAN_INCREMENT; xi++) {
                    for (byte zi = 0; zi < CHUNK_SCAN_INCREMENT; zi++) {
                        // I use an iterator here for removing stuff from the list while iterating over it
                        Iterator<Indexes> mobToIndicesIterator = mobToIndices.iterator();
                        List<Pair<String, Short>> mobToSingleChunks = new ArrayList<>(1);
                        while (mobToIndicesIterator.hasNext()) {
                            Indexes mob = mobToIndicesIterator.next();
                            short mobsToSpawnHere = mob.getNextChunk(currentChunkTotalIndex);
                            if (mobsToSpawnHere == -1)
                                mobToIndicesIterator.remove();
                            else {
                                // we need to load the chunk
                                System.out.println(mobsToSpawnHere);
                                mobToSingleChunks.add(new Pair<>(mob.mobPath, mobsToSpawnHere));
                            }
                        }

                        for (Pair<String, Short> mobToSingleChunk : mobToSingleChunks) {
                            JsonArray element;
                            final String mobToSingleChunkName = mobToSingleChunk.getKey();
                            if (chunksToMob.has(mobToSingleChunkName))
                                element = chunksToMob.get(mobToSingleChunkName).getAsJsonArray();
                            else {
                                chunksToMob.add(mobToSingleChunkName, element = new JsonArray());
                                for (int i = 0; i < CHUNK_SCAN_INCREMENT * CHUNK_SCAN_INCREMENT; i++)
                                    element.add(JSON_PRIMITIVE_ZERO);
                            }
                            element.set(currentChunkSubIndex, new JsonPrimitive(mobToSingleChunk.getValue()));
                        }
                        currentChunkTotalIndex++;
                        currentChunkSubIndex++;
                    }
                }
                File fileToWrite = new File(mobLocationsTempFolder, String.format("%s#%d#%d.json", "world", x, z));
                if (!fileToWrite.exists()) fileToWrite.createNewFile();
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileToWrite));
                gson.toJson(chunksToMob, fileWriter);
                fileWriter.close();
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            findLocationsNextChunkGroup(lowerX, lowerZ, higherX, higherZ, lowerX, lowerZ);
        }, 1); // give it a tick
    }

    private static void findLocationsNextChunkGroup(short lowerX, short lowerZ, short higherX, short higherZ, short currentX, short currentZ) {
        File chunkGroupFile = new File(mobLocationsTempFolder, String.format("%s#%d#%d.json", "world", currentX, currentZ));
        JsonObject chunkGroupContents;
        try {
            chunkGroupContents = gson.fromJson(new BufferedReader(new FileReader(chunkGroupFile)), JsonObject.class);
        } catch (FileNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("%s#%d#%d.json was not found when calculating locations", "world", currentX, currentZ));
            scheduleNextRead(lowerX, lowerZ, higherX, higherZ, currentX, currentZ, 0);
            return;
        }
        // chunkToMobLocationCountAll is an array (of size CHUNK_SCAN_INCREMENT * CHUNK_SCAN_INCREMENT)
        // that contains a list of varying length of the mob type to how many mob locations should be in a given chunk
        @SuppressWarnings("unchecked")
        List<Pair<String, Short>>[] chunkToMobLocationCountAll = new ArrayList[CHUNK_SCAN_INCREMENT * CHUNK_SCAN_INCREMENT];
        for (Map.Entry<String, JsonElement> chunkGroupSingleMob : chunkGroupContents.entrySet()) {
            JsonArray chunkGroupMobList = chunkGroupSingleMob.getValue().getAsJsonArray();
            // these should have size of CHUNK_SCAN_INCREMENT * CHUNK_SCAN_INCREMENT
            int size = chunkGroupMobList.size();
            for (int i = 0; i < size; i++) {
                List<Pair<String, Short>> mobsToLocationCount = chunkToMobLocationCountAll[i];
                if (mobsToLocationCount == null)
                    chunkToMobLocationCountAll[i] = mobsToLocationCount = new ArrayList<>();
                mobsToLocationCount.add(new Pair<>(chunkGroupSingleMob.getKey(), chunkGroupMobList.get(i).getAsShort()));
            }
        }
        short currentChunk = 0;
        short delayCounter = 0;
        int xi = 0, zi = 0;
        World world = Bukkit.getWorld("world");

        for (List<Pair<String, Short>> chunkToMobsLocationCount : chunkToMobLocationCountAll) {
            // chunkToMobsLocationCount is either null or a list with the size of 'CHUNK_SCAN_INCREMENT * CHUNK_SCAN_INCREMENT'
            if (chunkToMobsLocationCount != null) {
                final Map<String, Wow> mobToStuff = new HashMap<>();
                for (Pair<String, Short> chunkToMobLocationCount : chunkToMobsLocationCount) {
                    mobToStuff.put(chunkToMobLocationCount.getKey(), new Wow(chunkToMobLocationCount.getValue()));
                }
                int finalXIndex = currentX + xi;
                int finalZIndex = currentZ + zi;
                final Map<String, List<Location>> mobToFinalLocations = new HashMap<>();
                int finalXi = xi;
                int finalZi = zi;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    dealWithChunkGroup(world, chunkToMobsLocationCount, mobToStuff, finalXIndex, finalZIndex, mobToFinalLocations, finalXi, finalZi);
                }, delayCounter++);
            }
            currentChunk++;
            xi++;
            if (xi == CHUNK_SCAN_INCREMENT) {
                xi = 0;
                zi++;
            }
        }
        scheduleNextRead(lowerX, lowerZ, higherX, higherZ, currentX, currentZ, delayCounter);

    }

    private static void dealWithChunkGroup(World world, List<Pair<String, Short>> chunkToMobsLocationCount, Map<String, Wow> mobToStuff, int finalXIndex, int finalZIndex, Map<String, List<Location>> mobToFinalLocations, int finalXi, int finalZi) {
        ChunkSnapshot chunkToScan = world.getChunkAt(finalXIndex, finalZIndex).getChunkSnapshot(true, true, false);
        // for every block in the chunk grid
        for (byte x = 0; x < 16; x++) {
            for (byte z = 0; z < 16; z++) {

                // start at the highest y
                int y = chunkToScan.getHighestBlockYAt(x, z);
                short timesSolid = 0;
                short emptySpace = 10000; // there is a lot of spcae above

                // keep looking down y column until we hit a solid block SEARCH_DEPTH times in a row or until we hit the bottom of the world
                while (y > 0 && timesSolid < SEARCH_DEPTH) {
                    y--;
                    @NotNull Biome biome = chunkToScan.getBiome(x, y, z);
                    @NotNull Material blockType = chunkToScan.getBlockType(x, y, z);
                    if (blockType.isSolid()) {

                        // if the we're at a surface of a section of blocks
                        if (timesSolid++ == 0) {
                            SpawningMechanic mechanic = biomeToRules.get(biome);
                            if (mechanic != null) {
                                SpawningEnvironment environment = new SpawningEnvironment(biome, blockType, y, emptySpace);
                                for (String mobName : mechanic.getSpawnableMobs()) {
                                    if (mechanic.isSpawnable(mobName, environment)) {
                                        Wow stuff = mobToStuff.get(mobName);
                                        if (stuff != null)
                                            // increment the spawnable locations for this mob
                                            stuff.incrementSpawableInThisChunk();
                                        // else there is not going to be any mobs to put in this chunk anyways
                                        // because we already calculated how many are going to be in this chunk
                                    }
                                }
                            }

                        }
                    } else if (blockType.isAir()) {
                        timesSolid = 0;
                        emptySpace++;
                    } else {
                        emptySpace = 0;
                    }
                }
            }
        }

        // get the indexes of the final locations
        for (Map.Entry<String, Wow> singleMobToStuff : mobToStuff.entrySet()) {
            singleMobToStuff.getValue().fillFinalLocationIndexes();
        }

        // scan it a third and final time
        // for every block in the chunk grid
        for (byte x = 0; x < 16; x++) {
            for (byte z = 0; z < 16; z++) {

                // start at the highest y
                int y = chunkToScan.getHighestBlockYAt(x, z);
                short timesSolid = 0;
                short emptySpace = 10000; // there is a lot of space above

                // keep looking down y column until we hit a solid block SEARCH_DEPTH times in a row or until we hit the bottom of the world
                while (y > 0 && timesSolid < SEARCH_DEPTH) {
                    y--;
                    @NotNull Biome biome = chunkToScan.getBiome(x, y, z);
                    @NotNull Material blockType = chunkToScan.getBlockType(x, y, z);
                    if (blockType.isSolid()) {

                        // if the we're at a surface of a section of blocks
                        if (timesSolid++ == 0) {
                            SpawningMechanic mechanic = biomeToRules.get(biome);
                            if (mechanic != null) {
                                SpawningEnvironment environment = new SpawningEnvironment(biome, blockType, y, emptySpace);
                                for (String mobName : mechanic.getSpawnableMobs()) {
                                    if (mechanic.isSpawnable(mobName, environment)) {
                                        final Wow singleMobToStuff = mobToStuff.get(mobName);
                                        final short finalLocationIndexesIndex = singleMobToStuff.finalLocationIndexesIndex;
                                        final short currentSpawnableLocationIndex = singleMobToStuff.currentSpawnableLocationIndex;
                                        if (finalLocationIndexesIndex == singleMobToStuff.totalCountNeeded)
                                            // ignore things if I've already finished with the mob
                                            continue;
                                        short finalLocation = singleMobToStuff.mobToFinalLocationIndexes[finalLocationIndexesIndex];
                                        // if we should increment the index and save this location
                                        if (finalLocation == currentSpawnableLocationIndex) {
                                            // save this location
                                            List<Location> finalLocations = mobToFinalLocations.computeIfAbsent(mobName, a -> new ArrayList<>());
                                            finalLocations.add(new Location(world, x + finalXi, y, z + finalZi));
                                            singleMobToStuff.incrementFinalLocationIndexesIndex();
                                        }
                                        // increment mobToCurrentIndex's value (the current spawnable location we're scanning atm
                                        singleMobToStuff.incrementSpawnableLocationIndex();
                                    }
                                }
                            }

                        }
                    } else if (blockType.isAir()) {
                        timesSolid = 0;
                        emptySpace++;
                    } else {
                        emptySpace = 0;
                    }
                }
            }
        }
    }

    private static void scheduleNextRead(short lowerX, short lowerZ, short higherX, short higherZ, short currentX, short currentZ, int ticksToSchedule) {
        final short nextX, nextZ;
        currentZ += CHUNK_SCAN_INCREMENT;
        if (currentZ >= higherZ) {
            nextZ = lowerZ;
            nextX = (short) (currentX + CHUNK_SCAN_INCREMENT);
            if (nextX >= higherX)
                return;
        } else {
            nextX = currentX; // this was already incremented
            nextZ = currentZ;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> findLocationsNextChunkGroup(lowerX, lowerZ, higherX, higherZ, nextX, nextZ), ticksToSchedule);
    }

    private static class Indexes {
        public final String mobPath;
        private final int[] mobLocationChoices;
        private final int[] chunkMobCount;
        private int currentIndex = 0;
        private int currentMobCount = 0;
        private short mobsToSpawnThisChunk = 0;

        public Indexes(String mobCountsPath, int[] mobLocationChoices, int[] chunkMobCount) {
            this.mobPath = mobCountsPath;
            this.mobLocationChoices = mobLocationChoices;
            this.chunkMobCount = chunkMobCount;
        }

        /**
         * gets how many mobs of this type were in this chunk and need to be chosen as possible
         * spawn locations for this mob
         * We do not explicitly say where in the chunk it needs to spawn because it is possible
         * that the land changes from players from soft scan to soft scan
         *
         * @param currentChunk the current chunk index that is being addressed
         * @return -1 if there needn't be any more mobs of this type to spawn
         * or how many mobs of this type should be chosen as spawn locations
         */
        public short getNextChunk(int currentChunk) {
            mobsToSpawnThisChunk = 0;
            if (currentIndex >= mobLocationChoices.length)
                return -1;
            currentMobCount += chunkMobCount[currentChunk];
            while (currentIndex < mobLocationChoices.length) {
                // if the index we chose earlier is in this chunk
                if (mobLocationChoices[currentIndex] < currentMobCount) {
                    // we need to spawn another mob
                    mobsToSpawnThisChunk++;
                    currentIndex++;
                } else {
                    // we should not spawn any more mobs in this chunk
                    break;
                }
            }
            return mobsToSpawnThisChunk;
        }
    }

    private static class Wow {
        // mob to how many locations needed for this mob for this chunk
        public short totalCountNeeded;
        // mob to how many real spawnable locations there are in this chunk
        public short spawnableInThisChunk = 0;
        // the current index in the finalLocationIndexes
        public short finalLocationIndexesIndex = 0;
        // the current spawnable location
        public short currentSpawnableLocationIndex = 0;

        // the indexes of the final locations to mark where mobs spawn
        public short[] mobToFinalLocationIndexes;

        private Wow(short totalCountNeeded) {
            this.totalCountNeeded = totalCountNeeded;

            // mobCount is how many of these mobs we should put
            mobToFinalLocationIndexes = new short[totalCountNeeded];
        }

        public void incrementSpawableInThisChunk() {
            spawnableInThisChunk++;
        }

        public void incrementFinalLocationIndexesIndex() {
            finalLocationIndexesIndex++;
        }

        public void incrementSpawnableLocationIndex() {
            currentSpawnableLocationIndex++;
        }

        public void fillFinalLocationIndexes() {
            if (spawnableInThisChunk == 0) return;
            final int finalLocationIndexesLength = mobToFinalLocationIndexes.length;
            for (short i = 0; i < finalLocationIndexesLength; i++) {
                mobToFinalLocationIndexes[i] = (short) random.nextInt(spawnableInThisChunk);
            }
            // We don't want a mob to have 2 spawn locations on the same block
            for (short i = 1; i < finalLocationIndexesLength; i++) {
                // the next if statement will probably rarely happen, but if it does happen, we shouldn't just
                // randomly pick another number and hope it works
                if (mobToFinalLocationIndexes[i] == mobToFinalLocationIndexes[i - 1]) {
                    // fix the value at i-1 very slowly just to ensure that it is done rather than guessing again
                    final List<Short> tempLocationIndexes = new ArrayList<>(spawnableInThisChunk);
                    for (short j = 0, k = 0; j < finalLocationIndexesLength; j++) {
                        if (k == finalLocationIndexesLength) {
                            // add the rest of the things to the shuffling list
                            do {
                                tempLocationIndexes.add(j++);
                            } while (j < finalLocationIndexesLength);
                        }
                        if (mobToFinalLocationIndexes[k] > j) {
                            // we need to catch up and add more to this temp list
                            tempLocationIndexes.add(j);
                        } else if (mobToFinalLocationIndexes[k] != j) { //( mobToFinalLocationIndexes < j)
                            // increment k and decrement j to retry with the next tempLocationIndices
                            k++;
                            j--;
                        }// else (mobToFinalLocationIndexes[k] == j) and we don't include this number in our shuffling list

                    }
                    Collections.shuffle(tempLocationIndexes);
                    short l = 0;
                    for (; i < finalLocationIndexesLength; i++) {
                        if (mobToFinalLocationIndexes[i] == mobToFinalLocationIndexes[i - 1]) {
                            mobToFinalLocationIndexes[i - 1] = tempLocationIndexes.get(l++);
                        }
                    }
                    Sorting.insertionSort(mobToFinalLocationIndexes); // it's good because it's mostly if not completely sorted already
                }
            }
        }


    }
}

