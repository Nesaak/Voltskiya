package com.voltskiya.core.mobs.scanning.groupUtils;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;

public class UtilsMaterial {
    private static Collection<Material> leaves = Arrays.asList(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES
    );

    public static Collection<Material> getLeaves() {
        return leaves;
    }
}
