package com.voltskiya.core.mobs.commands.paint;


import org.bukkit.Material;
import org.bukkit.block.Biome;

public class PaintersBlock {
    public final int blockType;
    public final int elevation;
    public final int biome;

    public PaintersBlock(String blockType, int elevation, String biome) {
        this.blockType = Material.valueOf(blockType).ordinal();
        this.elevation = elevation;
        this.biome = Biome.valueOf(biome).ordinal();
    }
}
