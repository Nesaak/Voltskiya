package com.voltskiya.core.mobs.paint;

import org.bukkit.ChunkSnapshot;

public class PaintersChunk {
    public int xLoc;
    public int zLoc;
    public PaintersBlock[][] blocks = new PaintersBlock[16][16];

    public PaintersChunk(ChunkSnapshot chunk) {
        this.xLoc = chunk.getX();
        this.zLoc = chunk.getZ();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = chunk.getHighestBlockYAt(x, z) - 1; // -1 because off by one
                if (y == -1) {
                    blocks[x][z] = new PaintersBlock("none", 0, "none");
                } else
                    blocks[x][z] = new PaintersBlock(chunk.getBlockType(x, y, z).name(), y, chunk.getBiome(x, y, z).name());
            }
        }
    }
}
