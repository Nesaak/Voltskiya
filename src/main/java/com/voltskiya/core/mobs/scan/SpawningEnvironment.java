package com.voltskiya.core.mobs.scan;

import org.bukkit.Material;
import org.bukkit.block.Biome;

/**
 * just a class to hold data
 */
public class SpawningEnvironment {
    public final Biome biome;
    public final Material blockTypeOn;
    public final int elevation;
    public final int emptyAboveHeight;
    // add stuff as you need it

    public SpawningEnvironment(Biome biome, Material blockType, int elevation, int emptyAboveHeight) {
        this.biome = biome;
        this.blockTypeOn = blockType;
        this.elevation = elevation;
        this.emptyAboveHeight = emptyAboveHeight;
    }
}
