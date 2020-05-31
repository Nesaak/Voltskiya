package com.voltskiya.core.mobs.commands.paint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
}
