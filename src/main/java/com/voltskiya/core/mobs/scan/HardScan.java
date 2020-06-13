package com.voltskiya.core.mobs.scan;

import com.voltskiya.core.mobs.scan.mechanics.SpawningMechanic;
import com.voltskiya.core.mobs.scan.mechanics.MechanicForestFloor;
import com.voltskiya.core.utils.CheapMap;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HardScan {
    // this is all static because I don't want to need the reference to the object to run scan()

    private static final int SEARCH_DEPTH = 10;

    // a grid of mob to spawnable blocks of that mob in the given chunk
    // storing every block and a list of mobs to spawn on that block would be too much memory
    private static List<List<Map<String, Short>>> chunksToBiomePercentage = new ArrayList<>();
    private static final Object chunksToBiomePercentageSync = new Object();

    private static Map<Biome, SpawningMechanic> biomeToRules = new HashMap<>();

    static {
        biomeToRules.put(Biome.GIANT_TREE_TAIGA, new MechanicForestFloor());
    }

    public static void scan(ChunkSnapshot chunk, short lowestChunkX, short lowestChunkZ) {
        final int xIndex = chunk.getX() - lowestChunkX;
        final int zIndex = chunk.getZ() - lowestChunkZ;
        for (byte x = 0; x < 16; x++) {
            for (byte z = 0; z < 16; z++) {
                int y = chunk.getHighestBlockYAt(x, z);
                int timesSolid = 0;
                while (y > 5 && timesSolid < SEARCH_DEPTH) {
                    @NotNull Biome biome = chunk.getBiome(x, y, z);
                    @NotNull Material blockType = chunk.getBlockType(x, --y, z);
                    if (blockType.isSolid()) {
                        timesSolid++;
                        SpawningMechanic mechanic = biomeToRules.get(biome);
                        if (mechanic != null)
                            for (String mobName : mechanic.getSpawnableMobs())
                                incrementMob(xIndex, zIndex, mobName);
                    } else {
                        timesSolid = 0;
                    }
                }
            }
        }
        try {
            System.out.println(Arrays.toString(chunksToBiomePercentage.get(xIndex).get(zIndex).entrySet().toArray()));
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private static void incrementMob(int xIndex, int zIndex, String mobName) {
        synchronized (chunksToBiomePercentageSync) {
            while (xIndex >= chunksToBiomePercentage.size())
                chunksToBiomePercentage.add(new ArrayList<>());
            List<Map<String, Short>> zList = chunksToBiomePercentage.get(xIndex);
            while (zIndex >= zList.size())
                zList.add(new CheapMap<>());
            final Map<String, Short> mapCounter = zList.get(zIndex);
            mapCounter.put(mobName, (short) (mapCounter.getOrDefault(mobName, (short) 0) + 1));
        }
    }
}
