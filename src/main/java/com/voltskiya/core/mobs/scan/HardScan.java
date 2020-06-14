package com.voltskiya.core.mobs.scan;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.voltskiya.core.mobs.scan.mechanics.SpawningMechanic;
import com.voltskiya.core.mobs.scan.mechanics.MechanicForestFloor;
import com.voltskiya.core.utils.CheapMap;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class HardScan {
    // this is all static because I don't want to need the reference to the object to run scan()

    private static final int SEARCH_DEPTH = 10;
    private static final String MOB_COUNT_FOLDER = "mobCount";
    private static File mainFolder;


    private static Map<Biome, SpawningMechanic> biomeToRules = new HashMap<>();

    static {
        biomeToRules.put(Biome.GIANT_TREE_TAIGA, new MechanicForestFloor());
    }

    private static final Gson gson = new Gson();

    public static void initialize(File folder) {
        mainFolder = folder;
    }

    /**
     * write to the disk how many mobs of each type can spawn in each type of map
     *
     * @param chunks       a grid of c
     * @param chunksLength the length the square grid: chunks
     */
    public static void scan(@NotNull ChunkSnapshot[][] chunks, byte chunksLength) {
        if (chunksLength == 0)
            return; // it will throw an exception later if we don't check this

        // a grid of mob to spawnable blocks of that mob in the given chunk
        // storing every block and a list of mobs to spawn on that block would be too much memory
        // try to keep this map as cheap memory wise as possible
        @SuppressWarnings("unchecked")
        Map<String, Short>[][] chunksToMobArea = new Map[chunksLength][chunksLength];

        // for every chunk in the given grid
        for (byte xIndex = 0; xIndex < chunks.length; xIndex++) {
            for (byte zIndex = 0; zIndex < chunks[xIndex].length; zIndex++) {
                // this is the chunk being analyzed
                ChunkSnapshot chunk = chunks[xIndex][zIndex];

                // for every block in the chunk grid
                for (byte x = 0; x < 16; x++) {
                    for (byte z = 0; z < 16; z++) {

                        // start at the highest y
                        int y = chunk.getHighestBlockYAt(x, z);
                        int timesSolid = 0;

                        // keep looking down y column until we hit a solid block SEARCH_DEPTH times in a row or until we hit the bottom of the world
                        while (y > 0 && timesSolid < SEARCH_DEPTH) {
                            y--;
                            @NotNull Biome biome = chunk.getBiome(x, y, z);
                            @NotNull Material blockType = chunk.getBlockType(x, y, z);
                            if (blockType.isSolid()) {

                                // if the we're at a surface of a section of blocks
                                if (timesSolid++ == 0) {
                                    SpawningMechanic mechanic = biomeToRules.get(biome);
                                    if (mechanic != null)
                                        for (String mobName : mechanic.getSpawnableMobs())
                                            incrementMob(xIndex, zIndex, mobName, chunksToMobArea);
                                }
                            } else if (blockType.isAir()) {
                                timesSolid = 0;
                            }
                        }
                    }
                }
            }
        }
        // we're done filling in the list
        ChunkSnapshot mainChunk = chunks[0][0];
        final String worldName = mainChunk.getWorldName();
        final int x = mainChunk.getX();
        final int z = mainChunk.getZ();
        try {
            save(chunksToMobArea, worldName, x, z);
        } catch (IOException e) {
            System.err.println(String.format("Could not write chunk group %s,%d,%d", worldName, x, z));
        }
    }

    private static void save(Map<String, Short>[][] chunksToMobArea, String worldName, int x, int z) throws IOException {
        JsonArray outerArray = new JsonArray();
        for (Map<String, Short>[] innerArrayChunks : chunksToMobArea) {
            JsonArray innerArray = new JsonArray();
            for (Map<String, Short> innerMapChunks : innerArrayChunks) {
                JsonObject innerMap = new JsonObject();
                if (innerMapChunks != null)
                    for (Map.Entry<String, Short> entryChunks : innerMapChunks.entrySet()) {
                        innerMap.addProperty(entryChunks.getKey(), entryChunks.getValue());
                    }
                innerArray.add(innerMap);
            }
            outerArray.add(innerArray);
        }
        File fileToWrite = new File(mainFolder + File.separator + MOB_COUNT_FOLDER + File.separator + String.format("%s#%d#%d.json", worldName, x, z));
        if (!fileToWrite.exists()) fileToWrite.createNewFile();
        final FileWriter writer = new FileWriter(fileToWrite);
        gson.toJson(outerArray, writer);
        writer.close();
    }


    private static void incrementMob(int xIndex, int zIndex, String mobName, Map<String, Short>[][] chunksToMobArea) {
        Map<String, Short> mapCounter = chunksToMobArea[xIndex][zIndex];
        if (mapCounter == null)
            chunksToMobArea[xIndex][zIndex] = mapCounter = new CheapMap<>();
        mapCounter.put(mobName, (short) (mapCounter.getOrDefault(mobName, (short) 0) + 1));
    }
}