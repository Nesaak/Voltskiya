package com.voltskiya.core.mobs.scan;

import com.voltskiya.core.mobs.scan.mechanics.SpawningMechanic;
import com.voltskiya.core.mobs.scan.mechanics.MechanicRedWoodsFloor;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HardScan {
    private static List<List<Map<Short, Biome>>> chunksToSpawning = new ArrayList<>();
    private static final Object chunkToSpawningSync = new Object();

    private static Map<Biome, SpawningMechanic> biomeToRules = new HashMap<>();

    static {
        biomeToRules.put(Biome.GIANT_TREE_TAIGA, new MechanicRedWoodsFloor());
    }

    public static void scan(ChunkSnapshot chunk, short lowestChunkX, short lowestChunkZ) {
        final int x = chunk.getX();
        final int z = chunk.getZ();
        int y = chunk.getHighestBlockYAt(x, z);
        @NotNull Biome biome = chunk.getBiome(x, y, z);
        @NotNull Material blockType = chunk.getBlockType(x, y, z);


    }
}
