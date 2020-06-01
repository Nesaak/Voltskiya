package com.voltskiya.core.mobs.commands.paint;

import com.google.gson.JsonObject;

public class PaintersBlock {
    public final String blockType;
    public final int elevation;
    public final String biome;

    public PaintersBlock(JsonObject jsonObject) {
        this.blockType = jsonObject.get("blockType").getAsString();
        this.elevation = jsonObject.get("elevation").getAsInt();
        this.biome = jsonObject.get("biome").getAsString();
    }

    public PaintersBlock(String blockType, int elevation, String biome) {
        this.blockType = blockType;
        this.elevation = elevation;
        this.biome = biome;
    }
}
