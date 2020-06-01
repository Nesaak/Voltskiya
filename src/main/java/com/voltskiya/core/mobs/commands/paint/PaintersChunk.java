package com.voltskiya.core.mobs.commands.paint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.ChunkSnapshot;

public class PaintersChunk {
    public int xLoc;
    public int zLoc;
    public PaintersBlock[][] blocks = new PaintersBlock[16][16];

    public PaintersChunk(JsonObject chunkJsonObject) {
        JsonObject location = chunkJsonObject.getAsJsonObject("location");
        xLoc = location.getAsJsonObject("x").getAsInt();
        zLoc = location.getAsJsonObject("z").getAsInt();
        JsonArray rows = chunkJsonObject.getAsJsonArray("blocks");
        int rowsLength = Math.min(16, rows.size());

        for (int rowIndex = 0; rowIndex < rowsLength; rowIndex++) {
            JsonArray row = rows.get(rowIndex).getAsJsonArray();
            int rowLength = Math.min(16, row.size());
            for (int colIndex = 0; colIndex < rowLength; colIndex++) {
                this.blocks[rowIndex][colIndex] = new PaintersBlock(row.get(colIndex).getAsJsonObject());
            }

        }
    }

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
