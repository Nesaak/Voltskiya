package com.voltskiya.core.mobs.checks.groupUtils;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;

public class UtilsMaterial {
    private static final Collection<Material> leaves = Arrays.asList(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES
    );
    private static final Collection<Material> wood = Arrays.asList(
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG,

            Material.ACACIA_WOOD,
            Material.BIRCH_WOOD,
            Material.DARK_OAK_WOOD,
            Material.JUNGLE_WOOD,
            Material.OAK_WOOD,
            Material.SPRUCE_WOOD
    );

    public static Collection<Material> getLeaves() {
        return leaves;
    }

    public static Collection<Material> getWood() {
        return wood;
    }
}
